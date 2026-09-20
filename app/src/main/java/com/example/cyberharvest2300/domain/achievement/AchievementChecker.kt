package com.example.cyberharvest2300.domain.achievement

import com.example.cyberharvest2300.data.repository.CreatureProgressRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import kotlinx.coroutines.flow.first

/*
 * =========================================================
 * ACHIEVEMENT CHECKER
 * =========================================================
 * Belirli bir aksiyondan sonra (av bitişi, sipariş servisi,
 * yeni gün vs.) çağrılır. O anki PlayerProfile + toplam avlanma
 * sayısını AchievementData.all listesindeki koşullarla karşılaştırır,
 * henüz açılmamış ama koşulu sağlanan achievement'ları açar,
 * ödüllerini (para/itibar) oyuncuya verir ve yeni açılanları döner.
 *
 * creatureProgressRepository opsiyoneldir: sadece kill sayısına
 * bağlı achievement'ları kontrol edebilmek için gereklidir. Bu
 * repository'ye erişimi olmayan çağıranlar (ör. RestaurantEngine)
 * null geçebilir; o durumda kill tabanlı achievement'lar
 * kontrol edilmez, diğerleri (restoran seviyesi, gün, para vs.)
 * yine de kontrol edilir.
 */
class AchievementChecker(
    private val playerProfileRepository: PlayerProfileRepository,
    private val creatureProgressRepository: CreatureProgressRepository? = null
) {

    suspend fun checkAndUnlock(): List<Achievement> {

        val profile =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return emptyList()

        // Oyun bittiyse yeni achievement açmanın bir anlamı yok.
        if (profile.isGameOver) {
            return emptyList()
        }

        val totalKills =
            creatureProgressRepository
                ?.getAllProgress()
                ?.first()
                ?.sumOf { it.killCount }
                ?: 0

        val alreadyUnlocked =
            profile.unlockedAchievementIds
                .split(",")
                .filter { it.isNotBlank() }
                .toSet()

        val newlyUnlocked =
            mutableListOf<Achievement>()

        var moneyGain = 0
        var reputationGain = 0

        for (achievement in AchievementData.all) {

            if (achievement.id in alreadyUnlocked) {
                continue
            }

            val met =
                achievement.condition(
                    profile,
                    totalKills
                )

            if (met) {
                newlyUnlocked += achievement
                moneyGain += achievement.moneyReward
                reputationGain += achievement.reputationReward
            }
        }

        if (newlyUnlocked.isEmpty()) {
            return emptyList()
        }

        val updatedIds =
            (alreadyUnlocked + newlyUnlocked.map { it.id })
                .joinToString(",")

        playerProfileRepository
            .updatePlayerProfile(
                profile.copy(
                    unlockedAchievementIds = updatedIds,
                    money = profile.money + moneyGain,
                    reputation = profile.reputation + reputationGain
                )
            )

        return newlyUnlocked
    }
}
