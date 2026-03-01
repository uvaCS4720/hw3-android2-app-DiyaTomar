package edu.nd.pmcburne.hwapp.one.model

data class GameUiModel(
    val id: String,
    val homeName: String,
    val awayName: String,
    val homeScore: String?,
    val awayScore: String?,
    val homeWinner: Boolean,
    val awayWinner: Boolean,
    val startTime: String,
    val period: String,
    val clock: String,
    val finalMessage: String,
    val status: GameDisplayStatus
)
