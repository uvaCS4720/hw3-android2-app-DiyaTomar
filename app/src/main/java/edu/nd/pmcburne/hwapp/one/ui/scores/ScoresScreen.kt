package edu.nd.pmcburne.hwapp.one.ui.scores

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.nd.pmcburne.hwapp.one.model.BasketballGender
import edu.nd.pmcburne.hwapp.one.model.GameDisplayStatus
import edu.nd.pmcburne.hwapp.one.model.GameUiModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoresScreen(
    uiState: ScoresUiState,
    onDateChanged: (LocalDate) -> Unit,
    onGenderChanged: (BasketballGender) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    if (showDatePicker) {
        val initialMillis = uiState.selectedDate
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
        val datePickerState = androidx.compose.material3.rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = datePickerState.selectedDateMillis
                        if (selected != null) {
                            val date = Instant.ofEpochMilli(selected)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            onDateChanged(date)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { showDatePicker = true },
                    label = { Text(uiState.selectedDate.format(formatter)) }
                )
                Button(onClick = onRefresh) {
                    Text("Refresh")
                }
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.height(24.dp), strokeWidth = 2.dp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.selectedGender == BasketballGender.MEN,
                    onClick = { onGenderChanged(BasketballGender.MEN) },
                    label = { Text("Men") }
                )
                FilterChip(
                    selected = uiState.selectedGender == BasketballGender.WOMEN,
                    onClick = { onGenderChanged(BasketballGender.WOMEN) },
                    label = { Text("Women") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.games.isEmpty()) {
                Text(
                    text = "No games available for this date.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.games, key = { it.id }) { game ->
                        GameCard(game = game)
                    }
                }
            }
        }
    }
}

@Composable
private fun GameCard(game: GameUiModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TeamRow(
                name = game.awayName,
                score = when (game.status) {
                    GameDisplayStatus.UPCOMING -> "--"
                    else -> game.awayScore ?: "--"
                },
                isWinner = game.awayWinner && game.status == GameDisplayStatus.FINAL
            )
            TeamRow(
                name = game.homeName,
                score = when (game.status) {
                    GameDisplayStatus.UPCOMING -> "--"
                    else -> game.homeScore ?: "--"
                },
                isWinner = game.homeWinner && game.status == GameDisplayStatus.FINAL
            )

            Text(
                text = statusText(game),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TeamRow(name: String, score: String, isWinner: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = score,
            fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun statusText(game: GameUiModel): String {
    return when (game.status) {
        GameDisplayStatus.UPCOMING -> "Starts at ${game.startTime}"
        GameDisplayStatus.FINAL -> "Final"
        GameDisplayStatus.LIVE -> {
            val period = game.period.ifBlank { game.finalMessage }
            if (period.equals("HALFTIME", ignoreCase = true)) {
                "Halftime"
            } else {
                val clock = game.clock.ifBlank { "--:--" }
                "$period • $clock"
            }
        }
    }
}
