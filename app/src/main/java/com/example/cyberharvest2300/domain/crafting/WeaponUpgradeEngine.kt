package com.example.cyberharvest2300.domain.crafting

import com.example.cyberharvest2300.data.game.Weapon
import com.example.cyberharvest2300.data.game.WeaponData
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.WeaponProgressRepository

data class WeaponUpgradeResult(
    val success: Boolean,
    val message: String,
    val weapon: Weapon? = null,
    val newLevel: Int? = null
)

class WeaponUpgradeEngine(
    private val weaponProgressRepository: WeaponProgressRepository,
    private val inventoryRepository: InventoryRepository
) {

    suspend fun upgradeWeapon(
        weaponId: String
    ): WeaponUpgradeResult {

        val weapon =
            WeaponData.getById(
                weaponId
            )
                ?: return WeaponUpgradeResult(
                    success = false,
                    message = "Weapon bulunamadı."
                )

        val progress =
            weaponProgressRepository.getProgress(
                weaponId
            )
                ?: return WeaponUpgradeResult(
                    success = false,
                    message = "Bu silaha sahip değilsin."
                )

        if (!progress.isOwned) {

            return WeaponUpgradeResult(
                success = false,
                message = "Bu silaha sahip değilsin."
            )
        }

        val currentLevel =
            progress.level

        if (
            currentLevel >=
            weapon.maxLevel
        ) {

            return WeaponUpgradeResult(
                success = false,
                message = "Silah maksimum seviyede."
            )
        }

        val nextLevel =
            currentLevel + 1

        val upgradeCost =
            weapon.upgradeCosts[nextLevel]
                ?: return WeaponUpgradeResult(
                    success = false,
                    message = "Upgrade maliyeti bulunamadı."
                )

        /*
         * Önce bütün malzemeleri kontrol et.
         */
        for (ingredient in upgradeCost) {

            val quantity =
                inventoryRepository.getItemQuantity(
                    itemId = ingredient.itemId
                )

            if (
                quantity <
                ingredient.quantity
            ) {

                return WeaponUpgradeResult(
                    success = false,
                    message = "Yeterli malzeme yok."
                )
            }
        }

        /*
         * Malzemeleri düşür.
         */
        for (ingredient in upgradeCost) {

            val removed =
                inventoryRepository.removeItem(
                    itemId = ingredient.itemId,
                    quantity = ingredient.quantity
                )

            if (!removed) {

                return WeaponUpgradeResult(
                    success = false,
                    message = "Malzemeler düşürülemedi."
                )
            }
        }

        /*
         * Upgrade başarılı.
         */
        weaponProgressRepository.upgradeWeapon(
            weaponId
        )

        return WeaponUpgradeResult(
            success = true,
            message =
                "${weapon.name} Lv.$nextLevel oldu!",
            weapon = weapon,
            newLevel = nextLevel
        )
    }
}