package com.example.cyberharvest2300.domain.restaurant

import com.example.cyberharvest2300.data.local.entity.PlayerProfile
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import kotlinx.coroutines.flow.first

data class RestaurantProgressionResult(
    val profile: PlayerProfile,
    val leveledUp: Boolean,
    val oldLevel: Int,
    val newLevel: Int
)

class RestaurantProgressionEngine(
    private val playerProfileRepository: PlayerProfileRepository
) {

    companion object {

        // =====================================================
        // RESTAURANT LEVELS
        // FINAL: 10 LEVEL
        // =====================================================

        private val XP_THRESHOLDS =
            listOf(
                0,      // Level 1
                100,    // Level 2
                250,    // Level 3
                450,    // Level 4
                700,    // Level 5
                1000,   // Level 6
                1350,   // Level 7
                1750,   // Level 8
                2200,   // Level 9
                2700    // Level 10
            )

        fun getXpRequiredForLevel(
            level: Int
        ): Int {

            val safeLevel =
                level.coerceIn(
                    1,
                    XP_THRESHOLDS.size
                )

            return XP_THRESHOLDS[safeLevel - 1]
        }

        // =====================================================
        // CUSTOMER CAPACITY
        // =====================================================
        // perkIds verilirse (PlayerProfile.restaurantPerkIds),
        // PERK_CAPACITY seçimlerinden gelen ek kapasite de
        // hesaba katılır.

        fun getCustomerCapacity(
            level: Int,
            perkIds: String = ""
        ): Int {

            val baseCapacity =
                when {
                    level <= 2 -> 1
                    level <= 4 -> 2
                    level <= 6 -> 3
                    level <= 8 -> 4
                    else -> 5
                }

            val perkBonus =
                RestaurantPerks
                    .parse(perkIds)
                    .sumOf { it.extraCapacity }

            return baseCapacity + perkBonus
        }

        // =====================================================
        // XP TO NEXT LEVEL
        // =====================================================

        fun getXpToNextLevel(
            level: Int,
            currentXp: Int
        ): Int {

            if (level >= XP_THRESHOLDS.size) {
                return 0
            }

            return (
                    XP_THRESHOLDS[level] -
                            currentXp
                    ).coerceAtLeast(0)
        }
    }

    suspend fun addRestaurantXp(
        amount: Int
    ): RestaurantProgressionResult? {

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return null

        val oldLevel =
            player.restaurantLevel

        val newXp =
            player.restaurantXp +
                    amount.coerceAtLeast(0)

        val newLevel =
            calculateLevel(newXp)

        val leveledUp =
            newLevel > oldLevel

        val updatedPlayer =
            player.copy(
                restaurantXp = newXp,
                restaurantLevel = newLevel,

                /*
                 * Seviye atlandıysa oyuncuya bir perk seçimi
                 * borçlanılır. UI, pendingPerkChoice == true
                 * gördüğünde perk seçim ekranını göstermeli;
                 * choosePerk() çağrılana kadar bu true kalır
                 * (art arda birden fazla level atlanırsa bile
                 * tek bir bekleyen seçim yeterlidir - istersen
                 * bunu bir sayaca çevirebilirsin).
                 */
                pendingPerkChoice =
                    player.pendingPerkChoice || leveledUp
            )

        playerProfileRepository
            .updatePlayerProfile(
                updatedPlayer
            )

        return RestaurantProgressionResult(
            profile = updatedPlayer,
            leveledUp = leveledUp,
            oldLevel = oldLevel,
            newLevel = newLevel
        )
    }

    /*
     * Oyuncu level-up sonrası sunulan perk'lerden birini seçtiğinde
     * çağrılır. Perk id restaurantPerkIds listesine eklenir ve
     * bekleyen seçim bayrağı kapatılır.
     */
    suspend fun choosePerk(
        perkId: String
    ): PlayerProfile? {

        val perk =
            RestaurantPerks.getById(perkId)
                ?: return null

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return null

        if (!player.pendingPerkChoice) {
            return player
        }

        val currentIds =
            player.restaurantPerkIds
                .split(",")
                .filter { it.isNotBlank() }

        val updatedIds =
            (currentIds + perk.id)
                .joinToString(",")

        val updatedPlayer =
            player.copy(
                restaurantPerkIds = updatedIds,
                pendingPerkChoice = false
            )

        playerProfileRepository
            .updatePlayerProfile(
                updatedPlayer
            )

        return updatedPlayer
    }

    private fun calculateLevel(
        xp: Int
    ): Int {

        var level = 1

        for (index in XP_THRESHOLDS.indices) {

            if (xp >= XP_THRESHOLDS[index]) {
                level = index + 1
            }
        }

        return level.coerceAtMost(
            XP_THRESHOLDS.size
        )
    }
}
