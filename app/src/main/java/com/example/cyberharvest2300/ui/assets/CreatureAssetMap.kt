package com.example.cyberharvest2300.ui.assets

import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.data.game.GameIds

object CreatureAssetMap {

    private val assets = mapOf(
        GameIds.Creatures.CYBER_RAT to R.drawable.cyber_rat,
        GameIds.Creatures.NEON_CROW to R.drawable.neon_crow,
        GameIds.Creatures.SCRAP_WOLF to R.drawable.scrap_wolf,
        GameIds.Creatures.RUST_BOAR to R.drawable.rust_boar,
        GameIds.Creatures.SCRAP_VULTURE to R.drawable.scrap_vulture,
        GameIds.Creatures.ALPHA_SCRAP_WOLF to R.drawable.alpha_scrap_wolf,
        GameIds.Creatures.TOXIC_HOUND to R.drawable.toxic_hound,
        GameIds.Creatures.MUTANT_STAG to R.drawable.mutant_stag,
        GameIds.Creatures.ACID_CRAWLER to R.drawable.acid_crawler,
        GameIds.Creatures.TOXIC_ALPHA_HOUND to R.drawable.toxic_alpha_hound,
        GameIds.Creatures.IRON_BEAST to R.drawable.iron_beast,
        GameIds.Creatures.STEEL_RAVEN to R.drawable.steel_raven,
        GameIds.Creatures.WAR_MACHINE to R.drawable.war_machine,
        GameIds.Creatures.ELITE_WAR_MACHINE to R.drawable.elite_war_machine,
        GameIds.Creatures.IRON_TITAN to R.drawable.iron_titan
    )

    fun getAsset(creatureId: String): Int? {
        return assets[creatureId]
    }
}