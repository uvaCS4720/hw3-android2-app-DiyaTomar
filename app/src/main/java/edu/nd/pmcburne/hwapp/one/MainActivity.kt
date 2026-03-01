package edu.nd.pmcburne.hwapp.one

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.nd.pmcburne.hwapp.one.data.local.AppDatabase
import edu.nd.pmcburne.hwapp.one.data.remote.NetworkModule
import edu.nd.pmcburne.hwapp.one.data.repository.ScoresRepository
import edu.nd.pmcburne.hwapp.one.ui.scores.ScoresScreen
import edu.nd.pmcburne.hwapp.one.ui.scores.ScoresViewModel
import edu.nd.pmcburne.hwapp.one.ui.scores.ScoresViewModelFactory
import edu.nd.pmcburne.hwapp.one.ui.theme.HWStarterRepoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = ScoresRepository(
            api = NetworkModule.api,
            db = AppDatabase.getInstance(applicationContext)
        )

        setContent {
            HWStarterRepoTheme {
                val vm: ScoresViewModel = viewModel(factory = ScoresViewModelFactory(repository))
                val state by vm.uiState.collectAsStateWithLifecycle()

                ScoresScreen(
                    uiState = state,
                    onDateChanged = vm::onDateChanged,
                    onGenderChanged = vm::onGenderChanged,
                    onRefresh = vm::onManualRefresh,
                    modifier = Modifier
                )
            }
        }
    }
}
