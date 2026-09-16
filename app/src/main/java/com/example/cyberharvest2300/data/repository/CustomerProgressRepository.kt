package com.example.cyberharvest2300.data.repository

import com.example.cyberharvest2300.data.local.dao.CustomerProgressDao
import com.example.cyberharvest2300.data.local.entity.CustomerProgress
import kotlinx.coroutines.flow.Flow

class CustomerProgressRepository(
    private val dao: CustomerProgressDao
) {

    suspend fun getProgress(
        customerId: String
    ): CustomerProgress? {
        return dao.getProgress(customerId)
    }

    fun getAllProgress(): Flow<List<CustomerProgress>> {
        return dao.getAllProgress()
    }

    suspend fun saveProgress(
        progress: CustomerProgress
    ) {
        dao.insertProgress(progress)
    }

    suspend fun addVisit(
        customerId: String,
        relationshipGain: Int = 1
    ) {

        val existing =
            dao.getProgress(customerId)

        if (existing == null) {

            dao.insertProgress(
                CustomerProgress(
                    customerId = customerId,
                    relationship = relationshipGain.coerceAtMost(100),
                    visits = 1
                )
            )

        } else {

            dao.insertProgress(
                existing.copy(
                    relationship =
                        (
                                existing.relationship +
                                        relationshipGain
                                ).coerceAtMost(100),

                    visits =
                        existing.visits + 1
                )
            )
        }
    }

    suspend fun clearAllProgress() {
        dao.clearAllProgress()
    }
}