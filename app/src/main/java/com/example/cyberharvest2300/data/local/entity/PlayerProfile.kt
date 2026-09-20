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

    val timePhase: String = "DAY",

    // =====================================================
    // GAME OVER
    // =====================================================
    // isGameOver true olduğunda oyun akışı durur; UI bunu
    // dinleyip bir Game Over ekranına yönlendirmeli.
    // gameOverReason -> GameOverChecker.GameOverReason sabitlerinden biri.
    val isGameOver: Boolean = false,

    val gameOverReason: String = "NONE",

    // =====================================================
    // RESTAURANT LEVEL-UP PERK SEÇİMİ
    // =====================================================
    // Bir seviye atlandığında true olur; UI bir perk seçim
    // ekranı göstermeli. Oyuncu seçim yapınca false'a döner.
    val pendingPerkChoice: Boolean = false,

    // Oyuncunun şu ana kadar seçtiği perk id'leri, virgülle ayrılmış.
    // Örn: "PERK_CAPACITY,PERK_SUPPLIER"
    val restaurantPerkIds: String = "",

    // =====================================================
    // ACHIEVEMENTS
    // =====================================================
    // Kilidi açılmış achievement id'leri, virgülle ayrılmış.
    val unlockedAchievementIds: String = ""
)
