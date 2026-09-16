package com.example.cyberharvest2300.data.game

data class Region(
    val id: String,
    val name: String,
    val creatureIds: List<String>,
    val unlockConditions: List<UnlockCondition> = emptyList()
)

object RegionData {

    val all = listOf(

        // =====================================================
        // 1. NEON FIELDS
        // =====================================================

        Region(
            id = GameIds.Regions.NEON_FIELDS,
            name = "Neon Fields",
            creatureIds = listOf(
                GameIds.Creatures.CYBER_RAT,
                GameIds.Creatures.NEON_CROW
            ),
            unlockConditions = emptyList()
        ),

        // =====================================================
        // 2. RUSTLANDS
        // =====================================================

        Region(
            id = GameIds.Regions.RUSTLANDS,
            name = "Rustlands",
            creatureIds = listOf(
                GameIds.Creatures.SCRAP_WOLF,
                GameIds.Creatures.RUST_BOAR,
                GameIds.Creatures.SCRAP_VULTURE,
                GameIds.Creatures.ALPHA_SCRAP_WOLF
            ),
            unlockConditions = listOf(
                UnlockCondition.MinReputation(3),
                UnlockCondition.MinMoney(200)
            )
        ),

        // =====================================================
        // 3. TOXIC WASTES
        // =====================================================

        Region(
            id = GameIds.Regions.TOXIC_WASTES,
            name = "Toxic Wastes",
            creatureIds = listOf(
                GameIds.Creatures.TOXIC_HOUND,
                GameIds.Creatures.MUTANT_STAG,
                GameIds.Creatures.ACID_CRAWLER,
                GameIds.Creatures.TOXIC_ALPHA_HOUND
            ),
            unlockConditions = listOf(
                UnlockCondition.MinReputation(6),
                UnlockCondition.MinRestaurantLevel(2)
            )
        ),

        // =====================================================
        // 4. IRON WASTES
        // =====================================================

        Region(
            id = GameIds.Regions.IRON_WASTES,
            name = "Iron Wastes",
            creatureIds = listOf(
                GameIds.Creatures.IRON_BEAST,
                GameIds.Creatures.STEEL_RAVEN,
                GameIds.Creatures.WAR_MACHINE,
                GameIds.Creatures.ELITE_WAR_MACHINE,
                GameIds.Creatures.IRON_TITAN
            ),
            unlockConditions = listOf(
                UnlockCondition.MinReputation(10),
                UnlockCondition.MinRestaurantLevel(4),
                UnlockCondition.WeaponLevel(
                    weaponId = GameIds.Weapons.SCRAP_PISTOL,
                    level = 2
                )
            )
        )
    )

    fun getById(id: String): Region? {
        return all.find {
            it.id == id
        }
    }

    fun getCreatures(regionId: String): List<Creature> {

        val region =
            getById(regionId)
                ?: return emptyList()

        return region.creatureIds.mapNotNull {
            CreatureData.getById(it)
        }
    }
}