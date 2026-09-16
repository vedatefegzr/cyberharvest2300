package com.example.cyberharvest2300.data.game

data class Item(
    val id: String,
    val name: String,
    val type: ItemType,
    val rarity: Rarity,
    val sellValue: Int
)

object ItemData {

    val all = listOf(

        // =====================================================
        // NEON FIELDS
        // =====================================================

        Item(
            id = GameIds.Items.CYBER_RAT_MEAT,
            name = "Cyber Rat Meat",
            type = ItemType.FOOD_MATERIAL,
            rarity = Rarity.COMMON,
            sellValue = 10
        ),

        Item(
            id = GameIds.Items.SCRAP,
            name = "Scrap",
            type = ItemType.CRAFTING_MATERIAL,
            rarity = Rarity.COMMON,
            sellValue = 5
        ),

        Item(
            id = GameIds.Items.DAMAGED_CIRCUIT,
            name = "Damaged Circuit",
            type = ItemType.MECHANICAL_PART,
            rarity = Rarity.RARE,
            sellValue = 25
        ),

        Item(
            id = GameIds.Items.RAVEN_MEAT,
            name = "Raven Meat",
            type = ItemType.FOOD_MATERIAL,
            rarity = Rarity.COMMON,
            sellValue = 18
        ),

        Item(
            id = GameIds.Items.RAVEN_FEATHER,
            name = "Raven Feather",
            type = ItemType.BIO_MATERIAL,
            rarity = Rarity.UNCOMMON,
            sellValue = 20
        ),

        // =====================================================
        // RUSTLANDS
        // =====================================================

        Item(
            id = GameIds.Items.WOLF_MEAT,
            name = "Wolf Meat",
            type = ItemType.FOOD_MATERIAL,
            rarity = Rarity.COMMON,
            sellValue = 25
        ),

        Item(
            id = GameIds.Items.SCRAP_FANG,
            name = "Scrap Fang",
            type = ItemType.MECHANICAL_PART,
            rarity = Rarity.COMMON,
            sellValue = 15
        ),

        Item(
            id = GameIds.Items.WOLF_PELT,
            name = "Wolf Pelt",
            type = ItemType.BIO_MATERIAL,
            rarity = Rarity.RARE,
            sellValue = 50
        ),

        Item(
            id = GameIds.Items.BOAR_MEAT,
            name = "Boar Meat",
            type = ItemType.FOOD_MATERIAL,
            rarity = Rarity.COMMON,
            sellValue = 30
        ),

        Item(
            id = GameIds.Items.IRON_PLATE,
            name = "Iron Plate",
            type = ItemType.MECHANICAL_PART,
            rarity = Rarity.COMMON,
            sellValue = 20
        ),

        Item(
            id = GameIds.Items.RUST_CORE,
            name = "Rust Core",
            type = ItemType.MECHANICAL_PART,
            rarity = Rarity.RARE,
            sellValue = 60
        ),

        Item(
            id = GameIds.Items.BOAR_TUSK,
            name = "Boar Tusk",
            type = ItemType.BIO_MATERIAL,
            rarity = Rarity.UNCOMMON,
            sellValue = 35
        ),

        // =====================================================
        // TOXIC WASTES
        // =====================================================

        Item(
            id = GameIds.Items.TOXIC_MEAT,
            name = "Toxic Meat",
            type = ItemType.FOOD_MATERIAL,
            rarity = Rarity.COMMON,
            sellValue = 40
        ),

        Item(
            id = GameIds.Items.TOXIC_GLAND,
            name = "Toxic Gland",
            type = ItemType.BIO_MATERIAL,
            rarity = Rarity.UNCOMMON,
            sellValue = 55
        ),

        Item(
            id = GameIds.Items.TOXIC_CORE,
            name = "Toxic Core",
            type = ItemType.MECHANICAL_PART,
            rarity = Rarity.RARE,
            sellValue = 90
        ),

        Item(
            id = GameIds.Items.MUTANT_HIDE,
            name = "Mutant Hide",
            type = ItemType.BIO_MATERIAL,
            rarity = Rarity.UNCOMMON,
            sellValue = 65
        ),

        Item(
            id = GameIds.Items.ACID_FANG,
            name = "Acid Fang",
            type = ItemType.BIO_MATERIAL,
            rarity = Rarity.UNCOMMON,
            sellValue = 70
        ),

        Item(
            id = GameIds.Items.PLASMA_CELL,
            name = "Plasma Cell",
            type = ItemType.WEAPON_COMPONENT,
            rarity = Rarity.RARE,
            sellValue = 100
        ),

        Item(
            id = GameIds.Items.CORRUPTED_CIRCUIT,
            name = "Corrupted Circuit",
            type = ItemType.MECHANICAL_PART,
            rarity = Rarity.EPIC,
            sellValue = 150
        ),

        // =====================================================
        // IRON WASTES
        // =====================================================

        Item(
            id = GameIds.Items.STEEL_PLATE,
            name = "Steel Plate",
            type = ItemType.MECHANICAL_PART,
            rarity = Rarity.UNCOMMON,
            sellValue = 75
        ),

        Item(
            id = GameIds.Items.MAGNETIC_CORE,
            name = "Magnetic Core",
            type = ItemType.WEAPON_COMPONENT,
            rarity = Rarity.RARE,
            sellValue = 120
        ),

        Item(
            id = GameIds.Items.MACHINE_HEART,
            name = "Machine Heart",
            type = ItemType.MECHANICAL_PART,
            rarity = Rarity.EPIC,
            sellValue = 200
        ),

        Item(
            id = GameIds.Items.TITANIUM_SHARD,
            name = "Titanium Shard",
            type = ItemType.MECHANICAL_PART,
            rarity = Rarity.EPIC,
            sellValue = 180
        ),

        Item(
            id = GameIds.Items.ENERGY_CORE,
            name = "Energy Core",
            type = ItemType.WEAPON_COMPONENT,
            rarity = Rarity.LEGENDARY,
            sellValue = 300
        ),

        // =====================================================
        // FOOD
        // =====================================================

        Item(
            id = GameIds.Items.BASIC_MEAT_STEW,
            name = "Basic Meat Stew",
            type = ItemType.FOOD,
            rarity = Rarity.COMMON,
            sellValue = 30
        ),

        Item(
            id = GameIds.Items.CYBER_RAT_BURGER,
            name = "Cyber Rat Burger",
            type = ItemType.FOOD,
            rarity = Rarity.COMMON,
            sellValue = 45
        ),

        Item(
            id = GameIds.Items.WOLF_MEAT_SOUP,
            name = "Wolf Meat Soup",
            type = ItemType.FOOD,
            rarity = Rarity.UNCOMMON,
            sellValue = 55
        ),

        Item(
            id = GameIds.Items.RUST_BOAR_STEAK,
            name = "Rust Boar Steak",
            type = ItemType.FOOD,
            rarity = Rarity.UNCOMMON,
            sellValue = 70
        ),

        Item(
            id = GameIds.Items.TOXIC_HOUND_ROAST,
            name = "Toxic Hound Roast",
            type = ItemType.FOOD,
            rarity = Rarity.RARE,
            sellValue = 90
        ),

        Item(
            id = GameIds.Items.MUTANT_STAG_STEAK,
            name = "Mutant Stag Steak",
            type = ItemType.FOOD,
            rarity = Rarity.RARE,
            sellValue = 110
        ),

        Item(
            id = GameIds.Items.IRON_TITAN_FEAST,
            name = "Iron Titan Feast",
            type = ItemType.FOOD,
            rarity = Rarity.LEGENDARY,
            sellValue = 180
        )
    )

    fun getById(id: String): Item? {
        return all.find {
            it.id == id
        }
    }
}