package com.example.cyberharvest2300.domain.game

import com.example.cyberharvest2300.data.local.entity.PlayerProfile

/*
 * =========================================================
 * GAME OVER SEBEPLERI
 * =========================================================
 * PlayerProfile.gameOverReason alanında string olarak saklanır
 * (Room'da enum yerine string kullanmak migration'ları kolaylaştırır).
 */
object GameOverReason {
    const val NONE = "NONE"
    const val DEATH = "DEATH"                         // can 0'a düştü
    const val BANKRUPTCY = "BANKRUPTCY"                // para VE itibar tükendi
    const val REPUTATION_COLLAPSE = "REPUTATION_COLLAPSE" // itibar tabana vurdu
}

/*
 * =========================================================
 * GAME OVER CHECKER
 * =========================================================
 * Tek sorumluluğu: bir PlayerProfile'ın oyunu bitirecek bir
 * duruma düşüp düşmediğine karar vermek.
 *
 * Bu sınıf state DEĞİŞTİRMEZ, sadece PlayerProfile.copy() ile
 * yeni (gerekirse isGameOver=true işaretlenmiş) bir kopya döner.
 * Kaydetmek çağıran Engine'in sorumluluğundadır - bkz.
 * HuntingEngine.attack() ve RestaurantEngine içindeki kullanım.
 *
 * Eşik değerleri playtest sonucuna göre ayarlanmalı; şu anki
 * değerler başlangıç tahminidir.
 */
object GameOverChecker {

    // İtibar bu değerin altına/eşitine düşerse restoran güvenilirliğini
    // tamamen kaybeder sayılır.
    const val REPUTATION_FLOOR = -10

    fun evaluate(
        profile: PlayerProfile
    ): PlayerProfile {

        // Zaten bitmiş bir oyunu tekrar değerlendirip
        // sebebini değiştirmiyoruz.
        if (profile.isGameOver) {
            return profile
        }

        val reason: String? = when {

            profile.health <= 0 ->
                GameOverReason.DEATH

            profile.reputation <= REPUTATION_FLOOR ->
                GameOverReason.REPUTATION_COLLAPSE

            profile.money <= 0 && profile.reputation <= 0 ->
                GameOverReason.BANKRUPTCY

            else -> null
        }

        if (reason == null) {
            return profile
        }

        return profile.copy(
            isGameOver = true,
            gameOverReason = reason
        )
    }
}
