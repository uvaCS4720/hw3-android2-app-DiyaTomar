package edu.nd.pmcburne.hwapp.one.ui.scores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hwapp.one.data.local.GameEntity
import edu.nd.pmcburne.hwapp.one.data.repository.ScoresRepository
import edu.nd.pmcburne.hwapp.one.model.BasketballGender
import edu.nd.pmcburne.hwapp.one.model.GameDisplayStatus
import edu.nd.pmcburne.hwapp.one.model.GameUiModel
import java.time.LocalDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// The ViewModel for the Scores screen
// Handles view change based on date, gender, and refresh

class ScoresViewModel(
    private val repository: ScoresRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScoresUiState())
    val uiState: StateFlow<ScoresUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        observeSelectedQuery()
        refresh()
    }

    fun onDateChanged(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, errorMessage = null) }
        observeSelectedQuery()
        refresh()
    }

    fun onGenderChanged(gender: BasketballGender) {
        _uiState.update { it.copy(selectedGender = gender, errorMessage = null) }
        observeSelectedQuery()
        refresh()
    }

    fun onManualRefresh() {
        _uiState.update { it.copy(errorMessage = null) }
        refresh()
    }

    private fun observeSelectedQuery() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            repository.observeGames(
                _uiState.value.selectedGender,
                _uiState.value.selectedDate
            ).collect { entities ->
                _uiState.update { it.copy(games = entities.map { entity -> entity.toUiModel() }) }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                repository.refresh(_uiState.value.selectedGender, _uiState.value.selectedDate)
            }.onFailure {
                _uiState.update {
                    it.copy(errorMessage = "Could not update scores. Showing saved data if available.")
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun GameEntity.toUiModel(): GameUiModel {
        val normalizedState = gameState.lowercase()
        val normalizedFinalMessage = (finalMessage ?: "").lowercase()
        val status = when {
            normalizedState == "pre" -> GameDisplayStatus.UPCOMING
            normalizedState.contains("final") || normalizedState == "post" || normalizedFinalMessage.contains("final") -> {
                GameDisplayStatus.FINAL
            }

            else -> GameDisplayStatus.LIVE
        }

        return GameUiModel(
            id = gameId,
            homeName = homeName,
            awayName = awayName,
            homeScore = homeScore,
            awayScore = awayScore,
            homeWinner = homeWinner,
            awayWinner = awayWinner,
            startTime = startTime,
            period = currentPeriod.orEmpty(),
            clock = contestClock.orEmpty(),
            finalMessage = finalMessage.orEmpty(),
            status = status
        )
    }
}

class ScoresViewModelFactory(
    private val repository: ScoresRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoresViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScoresViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
