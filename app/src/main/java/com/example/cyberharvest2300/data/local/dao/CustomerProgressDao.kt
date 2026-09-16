package com.example.cyberharvest2300.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cyberharvest2300.data.local.entity.CustomerProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(
        progress: CustomerProgress
    )

    @Query("""
        SELECT * FROM customer_progress
        WHERE customerId = :customerId
        LIMIT 1
    """)
    suspend fun getProgress(
        customerId: String
    ): CustomerProgress?

    @Query("""
        SELECT * FROM customer_progress
        ORDER BY relationship DESC
    """)
    fun getAllProgress(): Flow<List<CustomerProgress>>

    @Query("""
        DELETE FROM customer_progress
    """)
    suspend fun clearAllProgress()
}