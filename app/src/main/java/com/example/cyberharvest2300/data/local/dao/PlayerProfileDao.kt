package com.example.cyberharvest2300.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cyberharvest2300.data.local.entity.PlayerProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerProfile(playerProfile: PlayerProfile)

    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun getPlayerProfile(): Flow<PlayerProfile?>

    @Query("DELETE FROM player_profile")
    suspend fun deletePlayerProfile()
}