package com.example.cyberharvest2300.data.repository

import com.example.cyberharvest2300.data.local.dao.DailyOrderDao
import com.example.cyberharvest2300.data.local.entity.DailyOrder
import kotlinx.coroutines.flow.Flow

class DailyOrderRepository(
    private val dao: DailyOrderDao
) {

    suspend fun saveOrder(
        order: DailyOrder
    ): Long {
        return dao.insertOrder(order)
    }

    suspend fun getOrdersForDay(
        day: Int
    ): List<DailyOrder> {
        return dao.getOrdersForDay(day)
    }

    fun observeOrdersForDay(
        day: Int
    ): Flow<List<DailyOrder>> {
        return dao.observeOrdersForDay(day)
    }

    suspend fun getOrderById(
        orderId: Long
    ): DailyOrder? {
        return dao.getOrderById(orderId)
    }

    suspend fun clearOrdersForDay(
        day: Int
    ) {
        dao.clearOrdersForDay(day)
    }

    suspend fun clearAllOrders() {
        dao.clearAllOrders()
    }
}