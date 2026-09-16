package com.example.cyberharvest2300.domain.crafting

import com.example.cyberharvest2300.data.game.Weapon
import com.example.cyberharvest2300.data.game.WeaponData
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.WeaponProgressRepository
import com.example.cyberharvest2300.domain.unlock.UnlockConditionChecker

data class WeaponCraftResult(
    val success: Boolean,
    val message: String,
    val weapon: Weapon? = null
)

class WeaponCraftingEngine(
    private val weaponProgressRepository: WeaponProgressRepository,
    private val inventoryRepository: InventoryRepository,
    private val unlockConditionChecker: UnlockConditionChecker
) {

    suspend fun craftWeapon(
        weaponId: String
    ): WeaponCraftResult {

        val weapon =
            WeaponData.getById(
                weaponId
            )
                ?: return WeaponCraftResult(
                    success = false,
                    message = "Weapon bulunamadı."
                )

        val existingProgress =
            weaponProgressRepository.getProgress(
                weaponId
            )

        if (
            existingProgress != null &&
            existingProgress.isOwned
        ) {

            return WeaponCraftResult(
                success = false,
                message = "Bu silaha zaten sahipsin."
            )
        }

        val conditionsMet =
            unlockConditionChecker.areConditionsMet(
                weapon.unlockConditions
            )

        if (!conditionsMet) {

            return WeaponCraftResult(
                success = false,
                message = "Bu silahın kilidi henüz açılmadı."
            )
        }

        if (weapon.craftCost.isEmpty()) {

            return WeaponCraftResult(
                success = false,
                message = "Bu silah craft edilemez."
            )
        }

        for (ingredient in weapon.craftCost) {

            val quantity =
                inventoryRepository.getItemQuantity(
                    itemId = ingredient.itemId
                )

            if (
                quantity <
                ingredient.quantity
            ) {

                return WeaponCraftResult(
                    success = false,
                    message = "Yeterli malzeme yok."
                )
            }
        }

        for (ingredient in weapon.craftCost) {

            val removed =
                inventoryRepository.removeItem(
                    itemId = ingredient.itemId,
                    quantity = ingredient.quantity
                )

            if (!removed) {

                return WeaponCraftResult(
                    success = false,
                    message = "Malzemeler düşürülemedi."
                )
            }
        }

        weaponProgressRepository.addWeapon(
            weaponId
        )

        return WeaponCraftResult(
            success = true,
            message = "${weapon.name} üretildi!",
            weapon = weapon
        )
    }
}