package edu.nd.pmcburne.hwapp.one.data.repository

import androidx.room.withTransaction
import edu.nd.pmcburne.hwapp.one.data.local.AppDatabase
import edu.nd.pmcburne.hwapp.one.data.local.GameEntity
import edu.nd.pmcburne.hwapp.one.data.remote.NcaaApiService
import edu.nd.pmcburne.hwapp.one.data.remote.ScoreboardResponse
import edu.nd.pmcburne.hwapp.one.model.BasketballGender
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class ScoresRepository(
    private val api: NcaaApiService,
    private val db: AppDatabase
) {
    fun observeGames(gender: BasketballGender, date: LocalDate): Flow<List<GameEntity>> {
        return db.gameDao().observeGames(gender.apiValue, date.toString())
    }

    suspend fun refresh(gender: BasketballGender, date: LocalDate) {
        val response = api.getScoreboard(
            gender = gender.apiValue,
            year = date.year.toString(),
            month = "%02d".format(date.monthValue),
            day = "%02d".format(date.dayOfMonth)
        )
        val mapped = response.toEntities(gender, date)
        db.withTransaction {
            db.gameDao().deleteByGenderAndDate(gender.apiValue, date.toString())
            db.gameDao().upsertAll(mapped)
        }
    }

    private fun ScoreboardResponse.toEntities(
        gender: BasketballGender,
        date: LocalDate
    ): List<GameEntity> {
        return games.map { it.game }.map { game ->
            GameEntity(
                gameId = game.gameID,
                gender = gender.apiValue,
                dateKey = date.toString(),
                homeName = game.home.names.short,
                awayName = game.away.names.short,
                homeScore = game.home.score.ifBlank { null },
                awayScore = game.away.score.ifBlank { null },
                homeWinner = game.home.winner,
                awayWinner = game.away.winner,
                gameState = game.gameState,
                startTime = game.startTime,
                startEpoch = game.startTimeEpoch.toLongOrNull(),
                currentPeriod = game.currentPeriod.ifBlank { null },
                contestClock = game.contestClock.ifBlank { null },
                finalMessage = game.finalMessage.ifBlank { null }
            )
        }
    }
}
