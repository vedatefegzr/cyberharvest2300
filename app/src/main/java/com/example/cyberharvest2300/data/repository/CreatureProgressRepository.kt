package com.example.cyberharvest2300.data.repository

import com.example.cyberharvest2300.data.local.dao.PlayerCreatureProgressDao
import com.example.cyberharvest2300.data.local.entity.PlayerCreatureProgress
import kotlinx.coroutines.flow.Flow

class CreatureProgressRepository(
    private val dao: PlayerCreatureProgressDao
) {

    fun getAllProgress(): Flow<List<PlayerCreatureProgress>> {
        return dao.getAllProgress()
    }

    suspend fun getProgress(
        creatureId: String
    ): PlayerCreatureProgress? {

        return dao.getProgress(creatureId)
    }

    suspend fun recordEncounter(
        creatureId: String
    ) {

        val existing =
            dao.getProgress(creatureId)

        if (existing == null) {

            dao.insertProgress(
                PlayerCreatureProgress(
                    creatureId = creatureId,
                    encounterCount = 1
                )
            )

        } else {

            dao.insertProgress(
                existing.copy(
                    encounterCount =
                        existing.encounterCount + 1
                )
            )
        }
    }

    suspend fun recordKill(
        creatureId: String
    ) {

        val existing =
            dao.getProgress(creatureId)

        if (existing == null) {

            dao.insertProgress(
                PlayerCreatureProgress(
                    creatureId = creatureId,
                    encounterCount = 1,
                    killCount = 1
                )
            )

        } else {

            dao.insertProgress(
                existing.copy(
                    killCount =
                        existing.killCount + 1
                )
            )
        }
    }

    suspend fun setTracked(
        creatureId: String,
        tracked: Boolean
    ) {

        val existing =
            dao.getProgress(creatureId)
                ?: return

        dao.insertProgress(
            existing.copy(
                isTracked = tracked
            )
        )
    }

    suspend fun clearAllProgress() {
        dao.clearAllProgress()
    }
}