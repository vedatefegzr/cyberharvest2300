package com.example.cyberharvest2300.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cyberharvest2300.data.local.entity.PlayerWeaponProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerWeaponProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(
        progress: PlayerWeaponProgress
    )

    @Query("SELECT * FROM player_weapon_progress")
    fun getAllProgress(): Flow<List<PlayerWeaponProgress>>

    @Query("""
        SELECT * FROM player_weapon_progress
        WHERE weaponId = :weaponId
        LIMIT 1
    """)
    suspend fun getProgress(
        weaponId: String
    ): PlayerWeaponProgress?

    @Query("""
        SELECT * FROM player_weapon_progress
        WHERE isEquipped = 1
        LIMIT 1
    """)
    suspend fun getEquippedWeapon(): PlayerWeaponProgress?

    @Query("DELETE FROM player_weapon_progress")
    suspend fun clearAllProgress()
}