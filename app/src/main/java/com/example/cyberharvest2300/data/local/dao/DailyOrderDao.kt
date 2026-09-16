package com.example.cyberharvest2300.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cyberharvest2300.data.local.entity.DailyOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyOrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(
        order: DailyOrder
    ): Long

    @Query("""
        SELECT * FROM daily_orders
        WHERE day = :day
        ORDER BY id ASC
    """)
    suspend fun getOrdersForDay(
        day: Int
    ): List<DailyOrder>

    @Query("""
        SELECT * FROM daily_orders
        WHERE day = :day
        ORDER BY id ASC
    """)
    fun observeOrdersForDay(
        day: Int
    ): Flow<List<DailyOrder>>

    @Query("""
        SELECT * FROM daily_orders
        WHERE id = :orderId
        LIMIT 1
    """)
    suspend fun getOrderById(
        orderId: Long
    ): DailyOrder?

    @Query("""
        DELETE FROM daily_orders
        WHERE day = :day
    """)
    suspend fun clearOrdersForDay(
        day: Int
    )

    @Query("""
        DELETE FROM daily_orders
    """)
    suspend fun clearAllOrders()
}