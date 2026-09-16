package com.example.cyberharvest2300.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cyberharvest2300.data.local.entity.RegionState
import kotlinx.coroutines.flow.Flow

@Dao
interface RegionStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertState(
        state: RegionState
    )

    @Query("SELECT * FROM region_state")
    fun getAllStates(): Flow<List<RegionState>>

    @Query("""
        SELECT * FROM region_state
        WHERE regionId = :regionId
        LIMIT 1
    """)
    suspend fun getState(
        regionId: String
    ): RegionState?

    @Query("DELETE FROM region_state")
    suspend fun clearAllStates()
}