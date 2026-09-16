package com.example.cyberharvest2300.data.game

data class Creature(
    val id: String,
    val name: String,
    val hp: Int,
    val attack: Int,
    val regionId: String,
    val weakness: Element? = null,
    val resistance: Element? = null,
    val lootTable: List<LootDrop>
)

object CreatureData {

    val all = listOf(

        // =====================================================
        // NEON FIELDS
        // =====================================================

        Creature(
            id = GameIds.Creatures.CYBER_RAT,
            name = "Cyber Rat",
            hp = 30,
            attack = 5,
            regionId = GameIds.Regions.NEON_FIELDS,
            weakness = Element.THERMAL,
            resistance = null,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.CYBER_RAT_MEAT,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.SCRAP,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 3
                ),
                LootDrop(
                    itemId = GameIds.Items.DAMAGED_CIRCUIT,
                    rarity = Rarity.RARE,
                    dropChance = 0.15f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        Creature(
            id = GameIds.Creatures.NEON_CROW,
            name = "Neon Crow",
            hp = 42,
            attack = 7,
            regionId = GameIds.Regions.NEON_FIELDS,
            weakness = Element.ELECTRIC,
            resistance = Element.PHYSICAL,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.CYBER_RAT_MEAT,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.RAVEN_MEAT,
                    rarity = Rarity.COMMON,
                    dropChance = 0.75f,
                    minQty = 1,
                    maxQty = 1
                ),
                LootDrop(
                    itemId = GameIds.Items.RAVEN_FEATHER,
                    rarity = Rarity.UNCOMMON,
                    dropChance = 0.35f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.DAMAGED_CIRCUIT,
                    rarity = Rarity.RARE,
                    dropChance = 0.20f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        // =====================================================
        // RUSTLANDS
        // =====================================================

        Creature(
            id = GameIds.Creatures.SCRAP_WOLF,
            name = "Scrap Wolf",
            hp = 60,
            attack = 9,
            regionId = GameIds.Regions.RUSTLANDS,
            weakness = Element.THERMAL,
            resistance = Element.PHYSICAL,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.WOLF_MEAT,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.SCRAP_FANG,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.WOLF_PELT,
                    rarity = Rarity.RARE,
                    dropChance = 0.15f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        Creature(
            id = GameIds.Creatures.RUST_BOAR,
            name = "Rust Boar",
            hp = 110,
            attack = 14,
            regionId = GameIds.Regions.RUSTLANDS,
            weakness = Element.ELECTRIC,
            resistance = Element.PHYSICAL,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.BOAR_MEAT,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 3
                ),
                LootDrop(
                    itemId = GameIds.Items.IRON_PLATE,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.BOAR_TUSK,
                    rarity = Rarity.UNCOMMON,
                    dropChance = 0.35f,
                    minQty = 1,
                    maxQty = 1
                ),
                LootDrop(
                    itemId = GameIds.Items.RUST_CORE,
                    rarity = Rarity.RARE,
                    dropChance = 0.15f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        Creature(
            id = GameIds.Creatures.SCRAP_VULTURE,
            name = "Scrap Vulture",
            hp = 85,
            attack = 12,
            regionId = GameIds.Regions.RUSTLANDS,
            weakness = Element.ELECTRIC,
            resistance = Element.PHYSICAL,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.RAVEN_MEAT,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.RAVEN_FEATHER,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 3
                ),
                LootDrop(
                    itemId = GameIds.Items.IRON_PLATE,
                    rarity = Rarity.COMMON,
                    dropChance = 0.50f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.RUST_CORE,
                    rarity = Rarity.RARE,
                    dropChance = 0.20f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        // =====================================================
        // ELITE
        // =====================================================

        Creature(
            id = GameIds.Creatures.ALPHA_SCRAP_WOLF,
            name = "Alpha Scrap Wolf",
            hp = 105,
            attack = 17,
            regionId = GameIds.Regions.RUSTLANDS,
            weakness = Element.THERMAL,
            resistance = Element.PHYSICAL,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.WOLF_MEAT,
                    rarity = Rarity.UNCOMMON,
                    dropChance = 1.0f,
                    minQty = 2,
                    maxQty = 4
                ),
                LootDrop(
                    itemId = GameIds.Items.SCRAP_FANG,
                    rarity = Rarity.UNCOMMON,
                    dropChance = 1.0f,
                    minQty = 2,
                    maxQty = 4
                ),
                LootDrop(
                    itemId = GameIds.Items.WOLF_PELT,
                    rarity = Rarity.RARE,
                    dropChance = 0.65f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.RUST_CORE,
                    rarity = Rarity.RARE,
                    dropChance = 0.30f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        // =====================================================
        // TOXIC WASTES
        // =====================================================

        Creature(
            id = GameIds.Creatures.TOXIC_HOUND,
            name = "Toxic Hound",
            hp = 125,
            attack = 18,
            regionId = GameIds.Regions.TOXIC_WASTES,
            weakness = Element.ELECTRIC,
            resistance = Element.TOXIC,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.TOXIC_MEAT,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 3
                ),
                LootDrop(
                    itemId = GameIds.Items.TOXIC_GLAND,
                    rarity = Rarity.UNCOMMON,
                    dropChance = 0.75f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.TOXIC_CORE,
                    rarity = Rarity.RARE,
                    dropChance = 0.20f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        Creature(
            id = GameIds.Creatures.MUTANT_STAG,
            name = "Mutant Stag",
            hp = 155,
            attack = 21,
            regionId = GameIds.Regions.TOXIC_WASTES,
            weakness = Element.THERMAL,
            resistance = Element.PHYSICAL,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.TOXIC_MEAT,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 2,
                    maxQty = 4
                ),
                LootDrop(
                    itemId = GameIds.Items.MUTANT_HIDE,
                    rarity = Rarity.UNCOMMON,
                    dropChance = 0.80f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.TOXIC_GLAND,
                    rarity = Rarity.RARE,
                    dropChance = 0.35f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        // BALANCING:
        // HP 185 -> 150
        // ATK 24 -> 14
        // CRYO -> THERMAL
        Creature(
            id = GameIds.Creatures.ACID_CRAWLER,
            name = "Acid Crawler",
            hp = 150,
            attack = 14,
            regionId = GameIds.Regions.TOXIC_WASTES,
            weakness = Element.THERMAL,
            resistance = Element.TOXIC,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.TOXIC_MEAT,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 3
                ),
                LootDrop(
                    itemId = GameIds.Items.ACID_FANG,
                    rarity = Rarity.UNCOMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 3
                ),
                LootDrop(
                    itemId = GameIds.Items.TOXIC_CORE,
                    rarity = Rarity.RARE,
                    dropChance = 0.30f,
                    minQty = 1,
                    maxQty = 1
                ),
                LootDrop(
                    itemId = GameIds.Items.CORRUPTED_CIRCUIT,
                    rarity = Rarity.EPIC,
                    dropChance = 0.10f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        // =====================================================
        // ELITE
        // =====================================================

        // BALANCING:
        // HP 230 -> 190
        // ATK 29 -> 14
        Creature(
            id = GameIds.Creatures.TOXIC_ALPHA_HOUND,
            name = "Toxic Alpha Hound",
            hp = 190,
            attack = 14,
            regionId = GameIds.Regions.TOXIC_WASTES,
            weakness = Element.ELECTRIC,
            resistance = Element.TOXIC,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.TOXIC_MEAT,
                    rarity = Rarity.UNCOMMON,
                    dropChance = 1.0f,
                    minQty = 3,
                    maxQty = 5
                ),
                LootDrop(
                    itemId = GameIds.Items.TOXIC_GLAND,
                    rarity = Rarity.RARE,
                    dropChance = 1.0f,
                    minQty = 2,
                    maxQty = 3
                ),
                LootDrop(
                    itemId = GameIds.Items.TOXIC_CORE,
                    rarity = Rarity.RARE,
                    dropChance = 0.70f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.CORRUPTED_CIRCUIT,
                    rarity = Rarity.EPIC,
                    dropChance = 0.30f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        // =====================================================
        // IRON WASTES
        // =====================================================

        // BALANCING:
        // HP 250 -> 220
        // ATK 31 -> 14
        Creature(
            id = GameIds.Creatures.IRON_BEAST,
            name = "Iron Beast",
            hp = 220,
            attack = 14,
            regionId = GameIds.Regions.IRON_WASTES,
            weakness = Element.ELECTRIC,
            resistance = Element.PHYSICAL,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.STEEL_PLATE,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 3
                ),
                LootDrop(
                    itemId = GameIds.Items.MAGNETIC_CORE,
                    rarity = Rarity.UNCOMMON,
                    dropChance = 0.70f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.MACHINE_HEART,
                    rarity = Rarity.RARE,
                    dropChance = 0.20f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        // BALANCING:
        // HP 215 -> 190
        // ATK 28 -> 13
        Creature(
            id = GameIds.Creatures.STEEL_RAVEN,
            name = "Steel Raven",
            hp = 190,
            attack = 13,
            regionId = GameIds.Regions.IRON_WASTES,
            weakness = Element.THERMAL,
            resistance = Element.PHYSICAL,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.STEEL_PLATE,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.TITANIUM_SHARD,
                    rarity = Rarity.UNCOMMON,
                    dropChance = 0.70f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.ENERGY_CORE,
                    rarity = Rarity.RARE,
                    dropChance = 0.20f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        // BALANCING:
        // HP 330 -> 260
        // ATK 37 -> 13
        Creature(
            id = GameIds.Creatures.WAR_MACHINE,
            name = "War Machine",
            hp = 260,
            attack = 13,
            regionId = GameIds.Regions.IRON_WASTES,
            weakness = Element.THERMAL,
            resistance = Element.PHYSICAL,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.STEEL_PLATE,
                    rarity = Rarity.COMMON,
                    dropChance = 1.0f,
                    minQty = 2,
                    maxQty = 4
                ),
                LootDrop(
                    itemId = GameIds.Items.MAGNETIC_CORE,
                    rarity = Rarity.UNCOMMON,
                    dropChance = 0.85f,
                    minQty = 1,
                    maxQty = 3
                ),
                LootDrop(
                    itemId = GameIds.Items.MACHINE_HEART,
                    rarity = Rarity.RARE,
                    dropChance = 0.40f,
                    minQty = 1,
                    maxQty = 1
                ),
                LootDrop(
                    itemId = GameIds.Items.ENERGY_CORE,
                    rarity = Rarity.EPIC,
                    dropChance = 0.15f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        // =====================================================
        // ELITE
        // =====================================================

        // BALANCING:
        // HP 450 -> 340
        // ATK 45 -> 15
        Creature(
            id = GameIds.Creatures.ELITE_WAR_MACHINE,
            name = "Elite War Machine",
            hp = 340,
            attack = 15,
            regionId = GameIds.Regions.IRON_WASTES,
            weakness = Element.ELECTRIC,
            resistance = Element.PHYSICAL,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.STEEL_PLATE,
                    rarity = Rarity.UNCOMMON,
                    dropChance = 1.0f,
                    minQty = 3,
                    maxQty = 6
                ),
                LootDrop(
                    itemId = GameIds.Items.MAGNETIC_CORE,
                    rarity = Rarity.RARE,
                    dropChance = 1.0f,
                    minQty = 2,
                    maxQty = 4
                ),
                LootDrop(
                    itemId = GameIds.Items.MACHINE_HEART,
                    rarity = Rarity.EPIC,
                    dropChance = 0.75f,
                    minQty = 1,
                    maxQty = 2
                ),
                LootDrop(
                    itemId = GameIds.Items.ENERGY_CORE,
                    rarity = Rarity.EPIC,
                    dropChance = 0.40f,
                    minQty = 1,
                    maxQty = 1
                )
            )
        ),

        // =====================================================
        // FINAL BOSS
        // =====================================================

        // BALANCING:
        // HP 700 -> 600
        // ATK 55 -> 9
        Creature(
            id = GameIds.Creatures.IRON_TITAN,
            name = "Iron Titan",
            hp = 600,
            attack = 9,
            regionId = GameIds.Regions.IRON_WASTES,
            weakness = Element.ELECTRIC,
            resistance = Element.PHYSICAL,
            lootTable = listOf(
                LootDrop(
                    itemId = GameIds.Items.STEEL_PLATE,
                    rarity = Rarity.RARE,
                    dropChance = 1.0f,
                    minQty = 5,
                    maxQty = 8
                ),
                LootDrop(
                    itemId = GameIds.Items.TITANIUM_SHARD,
                    rarity = Rarity.EPIC,
                    dropChance = 1.0f,
                    minQty = 3,
                    maxQty = 5
                ),
                LootDrop(
                    itemId = GameIds.Items.MACHINE_HEART,
                    rarity = Rarity.EPIC,
                    dropChance = 1.0f,
                    minQty = 2,
                    maxQty = 3
                ),
                LootDrop(
                    itemId = GameIds.Items.ENERGY_CORE,
                    rarity = Rarity.LEGENDARY,
                    dropChance = 1.0f,
                    minQty = 1,
                    maxQty = 2
                )
            )
        )
    )

    fun getById(
        id: String
    ): Creature? {
        return all.find {
            it.id == id
        }
    }

    fun getByRegion(
        regionId: String
    ): List<Creature> {
        return all.filter {
            it.regionId == regionId
        }
    }
}