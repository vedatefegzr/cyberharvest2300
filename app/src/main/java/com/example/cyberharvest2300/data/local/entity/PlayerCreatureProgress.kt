package com.example.cyberharvest2300.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_creature_progress")
data class PlayerCreatureProgress(

    @PrimaryKey
    val creatureId: String,

    val encounterCount: Int = 0,

    val killCount: Int = 0,

    val isTracked: Boolean = false
)