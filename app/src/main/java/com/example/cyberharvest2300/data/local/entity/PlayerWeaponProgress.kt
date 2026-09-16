package com.example.cyberharvest2300.data.local.entity

import androidx.room.Entity

@Entity(tableName = "player_weapon_progress")
data class PlayerWeaponProgress(

    @androidx.room.PrimaryKey
    val weaponId: String,

    val level: Int = 1,

    val isOwned: Boolean = false,

    val isEquipped: Boolean = false
)