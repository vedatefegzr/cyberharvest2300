package com.example.cyberharvest2300.data.repository

import com.example.cyberharvest2300.data.local.dao.PlayerProfileDao
import com.example.cyberharvest2300.data.local.entity.PlayerProfile
import kotlinx.coroutines.flow.Flow

class PlayerProfileRepository(
    private val playerProfileDao: PlayerProfileDao
) {

    suspend fun savePlayerProfile(
        playerProfile: PlayerProfile
    ) {
        playerProfileDao.insertPlayerProfile(
            playerProfile
        )
    }

    fun getPlayerProfile(): Flow<PlayerProfile?> {
        return playerProfileDao.getPlayerProfile()
    }

    suspend fun updatePlayerProfile(
        playerProfile: PlayerProfile
    ) {
        playerProfileDao.insertPlayerProfile(
            playerProfile
        )
    }

    suspend fun deletePlayerProfile() {
        playerProfileDao.deletePlayerProfile()
    }
}