package com.example.cyberharvest2300.data.game

data class Recipe(
    val id: String,
    val name: String,
    val resultItemId: String,
    val resultQuantity: Int,
    val ingredients: List<Ingredient>,
    val sellPrice: Int,
    val unlockConditions: List<UnlockCondition> = emptyList(),
    val isSecret: Boolean = false
)

object RecipeData {

    val all = listOf(

        // =========================
        // BASIC MEAT STEW
        // =========================

        Recipe(
            id = GameIds.Recipes.BASIC_MEAT_STEW,
            name = "Basic Meat Stew",
            resultItemId = GameIds.Items.BASIC_MEAT_STEW,
            resultQuantity = 1,
            ingredients = listOf(
                Ingredient(
                    itemId = GameIds.Items.CYBER_RAT_MEAT,
                    quantity = 2
                ),
                Ingredient(
                    itemId = GameIds.Items.SCRAP,
                    quantity = 1
                )
            ),
            sellPrice = 30
        ),

        // =========================
        // CYBER RAT BURGER
        // =========================

        Recipe(
            id = GameIds.Recipes.CYBER_RAT_BURGER,
            name = "Cyber Rat Burger",
            resultItemId = GameIds.Items.CYBER_RAT_BURGER,
            resultQuantity = 1,
            ingredients = listOf(
                Ingredient(
                    itemId = GameIds.Items.CYBER_RAT_MEAT,
                    quantity = 3
                ),
                Ingredient(
                    itemId = GameIds.Items.SCRAP,
                    quantity = 2
                )
            ),
            sellPrice = 45,
            unlockConditions = listOf(
                UnlockCondition.MinReputation(2)
            )
        ),

        // =========================
        // WOLF MEAT SOUP
        // =========================

        Recipe(
            id = GameIds.Recipes.WOLF_MEAT_SOUP,
            name = "Wolf Meat Soup",
            resultItemId = GameIds.Items.WOLF_MEAT_SOUP,
            resultQuantity = 1,
            ingredients = listOf(
                Ingredient(
                    itemId = GameIds.Items.WOLF_MEAT,
                    quantity = 2
                ),
                Ingredient(
                    itemId = GameIds.Items.SCRAP,
                    quantity = 1
                )
            ),
            sellPrice = 55,
            unlockConditions = listOf(
                UnlockCondition.CreatureKilled(
                    creatureId = GameIds.Creatures.SCRAP_WOLF,
                    times = 2
                )
            )
        ),

        // =========================
        // RUST BOAR STEAK
        // =========================

        Recipe(
            id = GameIds.Recipes.RUST_BOAR_STEAK,
            name = "Rust Boar Steak",
            resultItemId = GameIds.Items.RUST_BOAR_STEAK,
            resultQuantity = 1,
            ingredients = listOf(
                Ingredient(
                    itemId = GameIds.Items.BOAR_MEAT,
                    quantity = 2
                ),
                Ingredient(
                    itemId = GameIds.Items.IRON_PLATE,
                    quantity = 1
                )
            ),
            sellPrice = 70,
            unlockConditions = listOf(
                UnlockCondition.CreatureKilled(
                    creatureId = GameIds.Creatures.RUST_BOAR,
                    times = 2
                )
            )
        ),

        // =========================
        // TOXIC HOUND ROAST
        // =========================

        Recipe(
            id = GameIds.Recipes.TOXIC_HOUND_ROAST,
            name = "Toxic Hound Roast",
            resultItemId = GameIds.Items.TOXIC_HOUND_ROAST,
            resultQuantity = 1,
            ingredients = listOf(
                Ingredient(
                    itemId = GameIds.Items.TOXIC_MEAT,
                    quantity = 2
                ),
                Ingredient(
                    itemId = GameIds.Items.TOXIC_GLAND,
                    quantity = 1
                )
            ),
            sellPrice = 90,
            unlockConditions = listOf(
                UnlockCondition.CreatureKilled(
                    creatureId = GameIds.Creatures.TOXIC_HOUND,
                    times = 2
                )
            )
        ),

        // =========================
        // MUTANT STAG STEAK
        // =========================

        Recipe(
            id = GameIds.Recipes.MUTANT_STAG_STEAK,
            name = "Mutant Stag Steak",
            resultItemId = GameIds.Items.MUTANT_STAG_STEAK,
            resultQuantity = 1,
            ingredients = listOf(
                Ingredient(
                    itemId = GameIds.Items.TOXIC_MEAT,
                    quantity = 2
                ),
                Ingredient(
                    itemId = GameIds.Items.MUTANT_HIDE,
                    quantity = 1
                )
            ),
            sellPrice = 110,
            unlockConditions = listOf(
                UnlockCondition.CreatureKilled(
                    creatureId = GameIds.Creatures.MUTANT_STAG,
                    times = 2
                )
            )
        ),

        // =========================
        // IRON TITAN FEAST
        // =========================

        Recipe(
            id = GameIds.Recipes.IRON_TITAN_FEAST,
            name = "Iron Titan Feast",
            resultItemId = GameIds.Items.IRON_TITAN_FEAST,
            resultQuantity = 1,
            ingredients = listOf(
                Ingredient(
                    itemId = GameIds.Items.BOAR_MEAT,
                    quantity = 3
                ),
                Ingredient(
                    itemId = GameIds.Items.STEEL_PLATE,
                    quantity = 2
                ),
                Ingredient(
                    itemId = GameIds.Items.ENERGY_CORE,
                    quantity = 1
                )
            ),
            sellPrice = 180,
            unlockConditions = listOf(
                UnlockCondition.CreatureKilled(
                    creatureId = GameIds.Creatures.IRON_TITAN,
                    times = 1
                )
            )
        )
    )

    fun getById(
        id: String
    ): Recipe? {
        return all.find {
            it.id == id
        }
    }
}