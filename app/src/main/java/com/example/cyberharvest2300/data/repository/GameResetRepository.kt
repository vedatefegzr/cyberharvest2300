package com.example.cyberharvest2300.data.repository

import androidx.room.withTransaction
import com.example.cyberharvest2300.data.local.AppDatabase
import com.example.cyberharvest2300.data.local.entity.PlayerProfile

class GameResetRepository(
    private val database: AppDatabase
) {

    suspend fun resetAndCreateNewGame(
        playerProfile: PlayerProfile
    ) {

        database.withTransaction {

            database.playerProfileDao()
                .deletePlayerProfile()

            database.inventoryDao()
                .clearInventory()

            database.playerWeaponProgressDao()
                .clearAllProgress()

            database.regionStateDao()
                .clearAllStates()

            database.playerCreatureProgressDao()
                .clearAllProgress()

            database.dailyOrderDao()
                .clearAllOrders()

            database.customerProgressDao()
                .clearAllProgress()

            database.playerProfileDao()
                .insertPlayerProfile(
                    playerProfile
                )
        }
    }
}