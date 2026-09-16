package com.example.cyberharvest2300.domain.loot

import com.example.cyberharvest2300.data.game.Creature
import kotlin.random.Random

data class LootResult(
    val itemId: String,
    val quantity: Int
)

class LootCalculator {

    fun calculateLoot(
        creature: Creature
    ): List<LootResult> {

        val results = mutableListOf<LootResult>()

        for (drop in creature.lootTable) {

            val roll = Random.nextFloat()

            if (roll <= drop.dropChance) {

                val quantity =
                    Random.nextInt(
                        from = drop.minQty,
                        until = drop.maxQty + 1
                    )

                results.add(
                    LootResult(
                        itemId = drop.itemId,
                        quantity = quantity
                    )
                )
            }
        }

        return results
    }
}
