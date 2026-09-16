package com.example.cyberharvest2300.data.repository

import com.example.cyberharvest2300.data.local.dao.PlayerWeaponProgressDao
import com.example.cyberharvest2300.data.local.entity.PlayerWeaponProgress
import kotlinx.coroutines.flow.Flow

class WeaponProgressRepository(
    private val dao: PlayerWeaponProgressDao
) {

    fun getAllProgress(): Flow<List<PlayerWeaponProgress>> {
        return dao.getAllProgress()
    }

    suspend fun getProgress(
        weaponId: String
    ): PlayerWeaponProgress? {

        return dao.getProgress(weaponId)
    }

    suspend fun getEquippedWeapon(): PlayerWeaponProgress? {
        return dao.getEquippedWeapon()
    }

    suspend fun addWeapon(
        weaponId: String
    ) {

        val existing =
            dao.getProgress(weaponId)

        if (existing != null) {
            return
        }

        dao.insertProgress(
            PlayerWeaponProgress(
                weaponId = weaponId,
                level = 1,
                isOwned = true,
                isEquipped = false
            )
        )
    }

    suspend fun equipWeapon(
        weaponId: String
    ) {

        val weapon =
            dao.getProgress(weaponId)
                ?: return

        if (!weapon.isOwned) {
            return
        }

        val equippedWeapon =
            dao.getEquippedWeapon()

        if (equippedWeapon != null) {

            dao.insertProgress(
                equippedWeapon.copy(
                    isEquipped = false
                )
            )
        }

        dao.insertProgress(
            weapon.copy(
                isEquipped = true
            )
        )
    }

    suspend fun upgradeWeapon(
        weaponId: String
    ) {

        val weapon =
            dao.getProgress(weaponId)
                ?: return

        dao.insertProgress(
            weapon.copy(
                level = weapon.level + 1
            )
        )
    }

    suspend fun clearAllProgress() {
        dao.clearAllProgress()
    }
}