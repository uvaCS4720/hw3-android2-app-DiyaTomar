package edu.nd.pmcburne.hwapp.one.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    // selecting games by gender and date and ordering them by start time
    @Query(
        """
        SELECT * FROM games
        WHERE gender = :gender AND dateKey = :dateKey
        ORDER BY CASE WHEN startEpoch IS NULL THEN 1 ELSE 0 END, startEpoch
        """
    )
    fun observeGames(gender: String, dateKey: String): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<GameEntity>)

    @Query("DELETE FROM games WHERE gender = :gender AND dateKey = :dateKey")
    suspend fun deleteByGenderAndDate(gender: String, dateKey: String)
}
