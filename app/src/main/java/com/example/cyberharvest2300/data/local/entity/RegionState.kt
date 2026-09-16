package com.example.cyberharvest2300.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "region_state")
data class RegionState(

    @PrimaryKey
    val regionId: String,

    val isUnlocked: Boolean = false
)