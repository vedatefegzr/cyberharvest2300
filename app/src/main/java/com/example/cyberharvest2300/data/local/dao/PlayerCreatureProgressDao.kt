package com.example.cyberharvest2300.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cyberharvest2300.data.local.entity.PlayerCreatureProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerCreatureProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(
        progress: PlayerCreatureProgress
    )

    @Query("SELECT * FROM player_creature_progress")
    fun getAllProgress(): Flow<List<PlayerCreatureProgress>>

    @Query("""
        SELECT * FROM player_creature_progress
        WHERE creatureId = :creatureId
        LIMIT 1
    """)
    suspend fun getProgress(
        creatureId: String
    ): PlayerCreatureProgress?

    @Query("DELETE FROM player_creature_progress")
    suspend fun clearAllProgress()
}