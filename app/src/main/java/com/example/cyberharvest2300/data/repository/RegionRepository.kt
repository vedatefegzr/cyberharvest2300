package com.example.cyberharvest2300.data.repository

import com.example.cyberharvest2300.data.local.dao.RegionStateDao
import com.example.cyberharvest2300.data.local.entity.RegionState
import kotlinx.coroutines.flow.Flow

class RegionRepository(
    private val dao: RegionStateDao
) {

    fun getAllStates(): Flow<List<RegionState>> {
        return dao.getAllStates()
    }

    suspend fun getState(
        regionId: String
    ): RegionState? {

        return dao.getState(regionId)
    }

    suspend fun unlockRegion(
        regionId: String
    ) {

        val existing =
            dao.getState(regionId)

        if (existing == null) {

            dao.insertState(
                RegionState(
                    regionId = regionId,
                    isUnlocked = true
                )
            )

        } else {

            dao.insertState(
                existing.copy(
                    isUnlocked = true
                )
            )
        }
    }

    suspend fun clearAllStates() {
        dao.clearAllStates()
    }
}