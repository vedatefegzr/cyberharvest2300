package com.example.cyberharvest2300.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberharvest2300.data.local.entity.InventoryItem
import com.example.cyberharvest2300.data.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val repository: InventoryRepository
) : ViewModel() {

    fun getAllItems(): Flow<List<InventoryItem>> {
        return repository.getAllItems()
    }

    fun addItem(
        itemId: String,
        quantity: Int,
        isSecured: Boolean = false
    ) {
        viewModelScope.launch {
            repository.addItem(
                itemId = itemId,
                quantity = quantity,
                isSecured = isSecured
            )
        }
    }

    fun sellOneItem(
        item: InventoryItem,
        sellValue: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success =
                repository.sellOneItem(
                    inventoryItemId = item.id,
                    sellValue = sellValue
                )

            onResult(success)
        }
    }

    fun clearInventory() {
        viewModelScope.launch {
            repository.clearInventory()
        }
    }
}