package edu.nd.pmcburne.hwapp.one.data.remote

import com.google.gson.annotations.SerializedName

data class ScoreboardResponse(
    @SerializedName("updated_at")
    val updatedAt: String?,
    val games: List<GameWrapper>
)

data class GameWrapper(
    val game: ApiGame
)

data class ApiGame(
    val gameID: String,
    val gameState: String,
    val startDate: String,
    val startTime: String,
    val startTimeEpoch: String,
    val currentPeriod: String,
    val contestClock: String,
    val finalMessage: String,
    val home: ApiTeam,
    val away: ApiTeam
)

data class ApiTeam(
    val score: String,
    val winner: Boolean,
    val names: ApiNames
)

data class ApiNames(
    val short: String
)
