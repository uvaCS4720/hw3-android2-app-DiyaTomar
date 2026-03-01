package edu.nd.pmcburne.hwapp.one.data.local

import androidx.room.Entity

@Entity(
    tableName = "games",
    primaryKeys = ["gameId", "gender", "dateKey"]
)

// defines the structure of the table
data class GameEntity(
    val gameId: String,
    val gender: String,
    val dateKey: String,
    val homeName: String,
    val awayName: String,
    val homeScore: String?,
    val awayScore: String?,
    val homeWinner: Boolean,
    val awayWinner: Boolean,
    val gameState: String,
    val startTime: String,
    val startEpoch: Long?,
    val currentPeriod: String?,
    val contestClock: String?,
    val finalMessage: String?
)
