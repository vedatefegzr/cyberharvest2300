package com.example.cyberharvest2300.data.repository

import com.example.cyberharvest2300.data.local.dao.InventoryDao
import com.example.cyberharvest2300.data.local.dao.PlayerProfileDao
import com.example.cyberharvest2300.data.local.entity.InventoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class InventoryRepository(
    private val inventoryDao: InventoryDao,
    private val playerProfileDao: PlayerProfileDao
) {

    fun getAllItems(): Flow<List<InventoryItem>> {
        return inventoryDao.getAllItems()
    }

    suspend fun addItem(
        itemId: String,
        quantity: Int,
        isSecured: Boolean = false
    ) {

        if (quantity <= 0) {
            return
        }

        val existingItem =
            inventoryDao.getItem(
                itemId,
                isSecured
            )

        if (existingItem == null) {

            inventoryDao.insertItem(
                InventoryItem(
                    itemId = itemId,
                    quantity = quantity,
                    isSecured = isSecured
                )
            )

        } else {

            inventoryDao.insertItem(
                existingItem.copy(
                    quantity =
                        existingItem.quantity + quantity
                )
            )
        }
    }

    suspend fun getItemQuantity(
        itemId: String,
        isSecured: Boolean = false
    ): Int {

        if (isSecured) {
            return inventoryDao
                .getItem(
                    itemId,
                    true
                )
                ?.quantity
                ?: 0
        }

        return inventoryDao
            .getNormalItemQuantity(itemId)
            ?: 0
    }

    suspend fun removeItem(
        itemId: String,
        quantity: Int,
        isSecured: Boolean = false
    ): Boolean {

        if (quantity <= 0) {
            return false
        }

        if (isSecured) {
            return false
        }

        val currentQuantity =
            inventoryDao
                .getNormalItemQuantity(itemId)
                ?: return false

        if (currentQuantity < quantity) {
            return false
        }

        val item =
            inventoryDao.getItem(
                itemId,
                false
            )
                ?: return false

        inventoryDao.decreaseNormalItem(
            id = item.id,
            quantity = quantity
        )

        if (currentQuantity == quantity) {
            inventoryDao.deleteItemById(item.id)
        }

        return true
    }

    suspend fun secureItem(
        itemId: String,
        quantity: Int
    ): Boolean {

        if (quantity <= 0) {
            return false
        }

        val normalItem =
            inventoryDao.getItem(
                itemId,
                false
            )
                ?: return false

        if (normalItem.quantity < quantity) {
            return false
        }

        inventoryDao.decreaseNormalItem(
            id = normalItem.id,
            quantity = quantity
        )

        if (normalItem.quantity == quantity) {
            inventoryDao.deleteItemById(
                normalItem.id
            )
        }

        val securedItem =
            inventoryDao.getItem(
                itemId,
                true
            )

        if (securedItem == null) {

            inventoryDao.insertItem(
                InventoryItem(
                    itemId = itemId,
                    quantity = quantity,
                    isSecured = true
                )
            )

        } else {

            inventoryDao.insertItem(
                securedItem.copy(
                    quantity =
                        securedItem.quantity + quantity
                )
            )
        }

        return true
    }

    suspend fun consumeSecuredItem(
        itemId: String,
        quantity: Int
    ): Boolean {

        if (quantity <= 0) {
            return false
        }

        val securedItem =
            inventoryDao.getItem(
                itemId,
                true
            )
                ?: return false

        if (securedItem.quantity < quantity) {
            return false
        }

        inventoryDao.decreaseSecuredItem(
            id = securedItem.id,
            quantity = quantity
        )

        if (securedItem.quantity == quantity) {
            inventoryDao.deleteItemById(
                securedItem.id
            )
        }

        return true
    }

    suspend fun sellOneItem(
        inventoryItemId: Long,
        sellValue: Int
    ): Boolean {

        if (sellValue <= 0) {
            return false
        }

        val item =
            inventoryDao.getItemById(
                inventoryItemId
            )
                ?: return false

        if (item.isSecured) {
            return false
        }

        if (item.quantity <= 0) {
            return false
        }

        val player =
            playerProfileDao
                .getPlayerProfile()
                .first()
                ?: return false

        inventoryDao.decreaseNormalItem(
            id = item.id,
            quantity = 1
        )

        if (item.quantity == 1) {
            inventoryDao.deleteItemById(item.id)
        }

        playerProfileDao.insertPlayerProfile(
            player.copy(
                money =
                    player.money + sellValue
            )
        )

        return true
    }

    suspend fun clearInventory() {
        inventoryDao.clearInventory()
    }
}