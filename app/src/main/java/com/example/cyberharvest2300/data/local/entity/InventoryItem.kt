package com.example.cyberharvest2300.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemId: String,
    val quantity: Int,
    val isSecured: Boolean = false
)