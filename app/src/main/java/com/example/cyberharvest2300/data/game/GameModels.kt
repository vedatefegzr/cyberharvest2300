package com.example.cyberharvest2300.data.game

enum class ItemType {
    FOOD_MATERIAL,
    CRAFTING_MATERIAL,
    BIO_MATERIAL,
    MECHANICAL_PART,
    WEAPON_COMPONENT,
    FOOD
}

enum class Rarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY
}

enum class Element {
    PHYSICAL,
    TOXIC,
    THERMAL,
    ELECTRIC,
    CRYO
}

data class Ingredient(
    val itemId: String,
    val quantity: Int
)

data class LootDrop(
    val itemId: String,
    val rarity: Rarity,
    val dropChance: Float,
    val minQty: Int = 1,
    val maxQty: Int = 1
)
sealed class UnlockCondition {

    data class MinReputation(
        val value: Int
    ) : UnlockCondition()

    data class MinMoney(
        val value: Int
    ) : UnlockCondition()

    data class MinRestaurantLevel(
        val value: Int
    ) : UnlockCondition()

    data class CreatureKilled(
        val creatureId: String,
        val times: Int
    ) : UnlockCondition()

    data class WeaponLevel(
        val weaponId: String,
        val level: Int
    ) : UnlockCondition()
}