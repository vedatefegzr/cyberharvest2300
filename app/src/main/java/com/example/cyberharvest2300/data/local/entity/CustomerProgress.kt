package com.example.cyberharvest2300.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_progress")
data class CustomerProgress(

    @PrimaryKey
    val customerId: String,

    val relationship: Int = 0,

    val visits: Int = 0
)