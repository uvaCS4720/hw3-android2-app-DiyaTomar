package edu.nd.pmcburne.hwapp.one.ui.scores

import edu.nd.pmcburne.hwapp.one.model.BasketballGender
import edu.nd.pmcburne.hwapp.one.model.GameUiModel
import java.time.LocalDate

data class ScoresUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedGender: BasketballGender = BasketballGender.MEN,
    val isLoading: Boolean = false,
    val games: List<GameUiModel> = emptyList(),
    val errorMessage: String? = null
)
