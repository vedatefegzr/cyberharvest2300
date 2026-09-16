package com.example.cyberharvest2300.domain.unlock

import com.example.cyberharvest2300.data.game.UnlockCondition
import com.example.cyberharvest2300.data.repository.CreatureProgressRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.data.repository.WeaponProgressRepository
import kotlinx.coroutines.flow.first

class UnlockConditionChecker(
    private val playerProfileRepository: PlayerProfileRepository,
    private val creatureProgressRepository: CreatureProgressRepository,
    private val weaponProgressRepository: WeaponProgressRepository
) {

    suspend fun areConditionsMet(
        conditions: List<UnlockCondition>
    ): Boolean {

        if (conditions.isEmpty()) {
            return true
        }

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return false

        for (condition in conditions) {

            val conditionMet =
                when (condition) {

                    is UnlockCondition.MinReputation -> {
                        player.reputation >= condition.value
                    }

                    is UnlockCondition.MinMoney -> {
                        player.money >= condition.value
                    }

                    is UnlockCondition.MinRestaurantLevel -> {
                        player.restaurantLevel >= condition.value
                    }

                    is UnlockCondition.CreatureKilled -> {

                        val progress =
                            creatureProgressRepository
                                .getProgress(
                                    condition.creatureId
                                )

                        val killCount =
                            progress?.killCount ?: 0

                        killCount >= condition.times
                    }

                    is UnlockCondition.WeaponLevel -> {

                        val progress =
                            weaponProgressRepository
                                .getProgress(
                                    condition.weaponId
                                )

                        val level =
                            progress?.level ?: 0

                        level >= condition.level
                    }
                }

            if (!conditionMet) {
                return false
            }
        }

        return true
    }
}