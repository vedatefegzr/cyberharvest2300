package com.example.cyberharvest2300.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfile(

    @PrimaryKey
    val id: Int = 1,

    val characterName: String,

    val restaurantName: String,

    val startingClass: String,

    val money: Int,

    val reputation: Int,

    val attack: Int,

    val health: Int = 100,

    val maxHealth: Int = 100,

    val defense: Int = 0,

    val day: Int = 1,

    val restaurantLevel: Int = 1,

    val restaurantXp: Int = 0,

    val timePhase: String = "DAY"
)