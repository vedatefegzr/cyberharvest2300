package com.example.cyberharvest2300.ui.assets

import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.data.game.GameIds

/**
 * Silah id'sini drawable kaynağına bağlar. Aynı desen: ItemAssetMap / CreatureAssetMap.
 * Görselleri değiştirmek için res/drawable altındaki aynı isimli dosyanın
 * üzerine yazman yeterli, kod tarafında değişiklik gerekmez.
 */
object WeaponAssetMap {

    private val assets = mapOf(
        GameIds.Weapons.SCRAP_PISTOL to R.drawable.scrap_pistol,
        GameIds.Weapons.FANG_KNIFE to R.drawable.fang_knife,
        GameIds.Weapons.TOXIC_RIFLE to R.drawable.toxic_rifle,
        GameIds.Weapons.PLASMA_RIFLE to R.drawable.plasma_rifle,
        GameIds.Weapons.HYDRA_CANNON to R.drawable.hydra_cannon
    )

    fun getAsset(weaponId: String): Int? {
        return assets[weaponId]
    }
}
