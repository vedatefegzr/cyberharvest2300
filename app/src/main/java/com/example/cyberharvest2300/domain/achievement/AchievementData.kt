package com.example.cyberharvest2300.domain.achievement

import com.example.cyberharvest2300.data.local.entity.PlayerProfile

/*
 * =========================================================
 * ACHIEVEMENT
 * =========================================================
 * condition: (profile, totalKills) -> Boolean
 * totalKills, CreatureProgressRepository üzerinden hesaplanan
 * toplam öldürme sayısıdır (AchievementChecker'a bakınız).
 */
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val moneyReward: Int = 0,
    val reputationReward: Int = 0,
    val condition: (profile: PlayerProfile, totalKills: Int) -> Boolean
)

object AchievementData {

    val all: List<Achievement> = listOf(

        Achievement(
            id = "ACH_FIRST_BLOOD",
            name = "İlk Kan",
            description = "İlk yaratığını avla.",
            moneyReward = 20,
            condition = { _, kills -> kills >= 1 }
        ),

        Achievement(
            id = "ACH_HUNTER_10",
            name = "Deneyimli Avcı",
            description = "Toplam 10 yaratık avla.",
            moneyReward = 100,
            reputationReward = 2,
            condition = { _, kills -> kills >= 10 }
        ),

        Achievement(
            id = "ACH_HUNTER_50",
            name = "Kasap",
            description = "Toplam 50 yaratık avla.",
            moneyReward = 400,
            reputationReward = 5,
            condition = { _, kills -> kills >= 50 }
        ),

        Achievement(
            id = "ACH_FIRST_SERVE",
            name = "Açılış Günü",
            description = "İlk siparişini başarıyla servis et.",
            moneyReward = 15,
            condition = { profile, _ -> profile.restaurantXp >= 20 }
        ),

        Achievement(
            id = "ACH_RESTAURANT_MAX",
            name = "Efsanevi Mutfak",
            description = "Restoranını 10. seviyeye ulaştır.",
            moneyReward = 500,
            reputationReward = 10,
            condition = { profile, _ -> profile.restaurantLevel >= 10 }
        ),

        Achievement(
            id = "ACH_SURVIVOR_10",
            name = "Hayatta Kalan",
            description = "Restoranını 10 gün boyunca ayakta tut.",
            moneyReward = 150,
            condition = { profile, _ -> profile.day >= 10 }
        ),

        Achievement(
            id = "ACH_RICH_1000",
            name = "Zengin Tüccar",
            description = "1000₡ biriktir.",
            reputationReward = 3,
            condition = { profile, _ -> profile.money >= 1000 }
        )
    )

    fun getById(
        id: String
    ): Achievement? {

        return all.find {
            it.id == id
        }
    }
}
