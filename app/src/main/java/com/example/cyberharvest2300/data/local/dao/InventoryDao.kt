package com.example.cyberharvest2300.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cyberharvest2300.data.local.entity.InventoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(
        item: InventoryItem
    )

    @Query("SELECT * FROM inventory_items")
    fun getAllItems(): Flow<List<InventoryItem>>

    @Query("""
        SELECT * FROM inventory_items
        WHERE itemId = :itemId
        AND isSecured = :isSecured
        LIMIT 1
    """)
    suspend fun getItem(
        itemId: String,
        isSecured: Boolean
    ): InventoryItem?

    @Query("""
        SELECT * FROM inventory_items
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun getItemById(
        id: Long
    ): InventoryItem?

    @Query("""
        SELECT quantity
        FROM inventory_items
        WHERE itemId = :itemId
        AND isSecured = 0
        LIMIT 1
    """)
    suspend fun getNormalItemQuantity(
        itemId: String
    ): Int?

    @Query("""
        UPDATE inventory_items
        SET quantity = quantity - :quantity
        WHERE id = :id
        AND isSecured = 0
        AND quantity >= :quantity
    """)
    suspend fun decreaseNormalItem(
        id: Long,
        quantity: Int
    )

    @Query("""
        UPDATE inventory_items
        SET quantity = quantity - :quantity
        WHERE id = :id
        AND isSecured = 1
        AND quantity >= :quantity
    """)
    suspend fun decreaseSecuredItem(
        id: Long,
        quantity: Int
    )

    @Query("""
        DELETE FROM inventory_items
        WHERE id = :id
    """)
    suspend fun deleteItemById(
        id: Long
    )

    @Query("DELETE FROM inventory_items")
    suspend fun clearInventory()
}