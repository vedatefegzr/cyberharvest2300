package com.example.cyberharvest2300.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_orders")
data class DailyOrder(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val day: Int,

    val customerId: String,

    val recipeId: String,

    val reward: Int,

    val reputationReward: Int,

    val isCompleted: Boolean = false
)