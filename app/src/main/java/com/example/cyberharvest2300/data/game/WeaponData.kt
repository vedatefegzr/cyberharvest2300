package com.example.cyberharvest2300.data.game

data class Weapon(
    val id: String,
    val name: String,
    val baseAttack: Int,
    val element: Element,
    val maxLevel: Int = 3,
    val craftCost: List<Ingredient> = emptyList(),
    val unlockConditions: List<UnlockCondition> = emptyList(),
    val upgradeCosts: Map<Int, List<Ingredient>>
)

object WeaponData {

    val all = listOf(

        Weapon(
            id = GameIds.Weapons.SCRAP_PISTOL,
            name = "Scrap Pistol",
            baseAttack = 10,
            element = Element.PHYSICAL,
            maxLevel = 3,

            craftCost = emptyList(),

            unlockConditions = emptyList(),

            upgradeCosts = mapOf(

                2 to listOf(
                    Ingredient(
                        itemId = GameIds.Items.SCRAP,
                        quantity = 5
                    )
                ),

                3 to listOf(
                    Ingredient(
                        itemId = GameIds.Items.SCRAP,
                        quantity = 10
                    ),
                    Ingredient(
                        itemId = GameIds.Items.DAMAGED_CIRCUIT,
                        quantity = 2
                    )
                )
            )
        ),

        Weapon(
            id = GameIds.Weapons.FANG_KNIFE,
            name = "Fang Knife",
            baseAttack = 11,
            element = Element.PHYSICAL,
            maxLevel = 3,

            craftCost = listOf(
                Ingredient(
                    itemId = GameIds.Items.SCRAP_FANG,
                    quantity = 5
                ),
                Ingredient(
                    itemId = GameIds.Items.SCRAP,
                    quantity = 3
                )
            ),

            unlockConditions = listOf(
                UnlockCondition.MinReputation(3)
            ),

            upgradeCosts = mapOf(

                2 to listOf(
                    Ingredient(
                        itemId = GameIds.Items.SCRAP_FANG,
                        quantity = 5
                    )
                ),

                3 to listOf(
                    Ingredient(
                        itemId = GameIds.Items.SCRAP_FANG,
                        quantity = 10
                    ),
                    Ingredient(
                        itemId = GameIds.Items.WOLF_PELT,
                        quantity = 2
                    )
                )
            )
        ),

        Weapon(
            id = GameIds.Weapons.TOXIC_RIFLE,
            name = "Toxic Rifle",
            baseAttack = 16,
            element = Element.TOXIC,
            maxLevel = 3,

            craftCost = listOf(
                Ingredient(
                    itemId = GameIds.Items.RUST_CORE,
                    quantity = 3
                ),
                Ingredient(
                    itemId = GameIds.Items.DAMAGED_CIRCUIT,
                    quantity = 2
                )
            ),

            unlockConditions = listOf(
                UnlockCondition.MinReputation(6)
            ),

            upgradeCosts = mapOf(

                2 to listOf(
                    Ingredient(
                        itemId = GameIds.Items.RUST_CORE,
                        quantity = 3
                    )
                ),

                3 to listOf(
                    Ingredient(
                        itemId = GameIds.Items.RUST_CORE,
                        quantity = 6
                    ),
                    Ingredient(
                        itemId = GameIds.Items.DAMAGED_CIRCUIT,
                        quantity = 3
                    )
                )
            )
        ),

        Weapon(
            id = GameIds.Weapons.PLASMA_RIFLE,
            name = "Plasma Rifle",
            baseAttack = 22,
            element = Element.THERMAL,
            maxLevel = 3,

            craftCost = listOf(
                Ingredient(
                    itemId = GameIds.Items.IRON_PLATE,
                    quantity = 5
                ),
                Ingredient(
                    itemId = GameIds.Items.RUST_CORE,
                    quantity = 3
                ),
                Ingredient(
                    itemId = GameIds.Items.DAMAGED_CIRCUIT,
                    quantity = 3
                )
            ),

            unlockConditions = listOf(
                UnlockCondition.MinReputation(10)
            ),

            upgradeCosts = mapOf(

                2 to listOf(
                    Ingredient(
                        itemId = GameIds.Items.IRON_PLATE,
                        quantity = 5
                    ),
                    Ingredient(
                        itemId = GameIds.Items.RUST_CORE,
                        quantity = 2
                    )
                ),

                3 to listOf(
                    Ingredient(
                        itemId = GameIds.Items.IRON_PLATE,
                        quantity = 10
                    ),
                    Ingredient(
                        itemId = GameIds.Items.RUST_CORE,
                        quantity = 4
                    )
                )
            )
        ),

        Weapon(
            id = GameIds.Weapons.HYDRA_CANNON,
            name = "Hydra Cannon",
            baseAttack = 30,
            element = Element.ELECTRIC,
            maxLevel = 3,

            craftCost = listOf(
                Ingredient(
                    itemId = GameIds.Items.IRON_PLATE,
                    quantity = 8
                ),
                Ingredient(
                    itemId = GameIds.Items.DAMAGED_CIRCUIT,
                    quantity = 3
                )
            ),

            unlockConditions = listOf(
                UnlockCondition.MinReputation(15)
            ),

            upgradeCosts = mapOf(

                2 to listOf(
                    Ingredient(
                        itemId = GameIds.Items.IRON_PLATE,
                        quantity = 8
                    ),
                    Ingredient(
                        itemId = GameIds.Items.DAMAGED_CIRCUIT,
                        quantity = 2
                    )
                ),

                3 to listOf(
                    Ingredient(
                        itemId = GameIds.Items.IRON_PLATE,
                        quantity = 15
                    ),
                    Ingredient(
                        itemId = GameIds.Items.DAMAGED_CIRCUIT,
                        quantity = 4
                    )
                )
            )
        )
    )

    fun getById(
        id: String
    ): Weapon? {

        return all.find {
            it.id == id
        }
    }
}

