package com.example.cyberharvest2300.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cyberharvest2300.data.game.Weapon
import com.example.cyberharvest2300.data.game.WeaponData
import com.example.cyberharvest2300.data.local.entity.InventoryItem
import com.example.cyberharvest2300.data.local.entity.PlayerWeaponProgress
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.WeaponProgressRepository
import com.example.cyberharvest2300.domain.crafting.WeaponCraftResult
import com.example.cyberharvest2300.domain.crafting.WeaponCraftingEngine
import com.example.cyberharvest2300.domain.crafting.WeaponUpgradeEngine
import com.example.cyberharvest2300.domain.crafting.WeaponUpgradeResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeaponUpgradeViewModel(
    private val weaponProgressRepository: WeaponProgressRepository,
    private val inventoryRepository: InventoryRepository,
    private val weaponUpgradeEngine: WeaponUpgradeEngine,
    private val weaponCraftingEngine: WeaponCraftingEngine
) : ViewModel() {

    private val _weapons =
        MutableStateFlow<List<Weapon>>(emptyList())

    val weapons: StateFlow<List<Weapon>> =
        _weapons.asStateFlow()

    private val _weaponProgress =
        MutableStateFlow<List<PlayerWeaponProgress>>(
            emptyList()
        )

    val weaponProgress:
            StateFlow<List<PlayerWeaponProgress>> =
        _weaponProgress.asStateFlow()

    private val _inventory =
        MutableStateFlow<List<InventoryItem>>(
            emptyList()
        )

    val inventory:
            StateFlow<List<InventoryItem>> =
        _inventory.asStateFlow()

    private val _upgradeResult =
        MutableStateFlow<WeaponUpgradeResult?>(null)

    val upgradeResult:
            StateFlow<WeaponUpgradeResult?> =
        _upgradeResult.asStateFlow()

    private val _craftResult =
        MutableStateFlow<WeaponCraftResult?>(null)

    val craftResult:
            StateFlow<WeaponCraftResult?> =
        _craftResult.asStateFlow()

    private var observersStarted = false

    fun loadWeapons() {

        _weapons.value =
            WeaponData.all

        if (observersStarted) {
            return
        }

        observersStarted = true

        viewModelScope.launch {

            launch {

                weaponProgressRepository
                    .getAllProgress()
                    .collect { progress ->

                        _weaponProgress.value =
                            progress
                    }
            }

            launch {

                inventoryRepository
                    .getAllItems()
                    .collect { items ->

                        _inventory.value =
                            items
                    }
            }
        }
    }

    fun getProgress(
        weaponId: String
    ): PlayerWeaponProgress? {

        return _weaponProgress.value.find {
            it.weaponId == weaponId
        }
    }

    fun getWeapon(
        weaponId: String
    ): Weapon? {

        return WeaponData.getById(
            weaponId
        )
    }

    fun getItemQuantity(
        itemId: String
    ): Int {

        return _inventory.value
            .filter {
                it.itemId == itemId &&
                        !it.isSecured
            }
            .sumOf {
                it.quantity
            }
    }

    fun upgradeWeapon(
        weaponId: String
    ) {

        viewModelScope.launch {

            val result =
                weaponUpgradeEngine.upgradeWeapon(
                    weaponId
                )

            _upgradeResult.value =
                result
        }
    }

    fun clearUpgradeResult() {
        _upgradeResult.value = null
    }

    fun craftWeapon(
        weaponId: String
    ) {

        viewModelScope.launch {

            val result =
                weaponCraftingEngine.craftWeapon(
                    weaponId
                )

            _craftResult.value =
                result
        }
    }

    fun clearCraftResult() {
        _craftResult.value = null
    }

    fun equipWeapon(
        weaponId: String
    ) {

        viewModelScope.launch {

            weaponProgressRepository.equipWeapon(
                weaponId
            )
        }
    }
}

class WeaponUpgradeViewModelFactory(
    private val weaponProgressRepository: WeaponProgressRepository,
    private val inventoryRepository: InventoryRepository,
    private val weaponUpgradeEngine: WeaponUpgradeEngine,
    private val weaponCraftingEngine: WeaponCraftingEngine
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                WeaponUpgradeViewModel::class.java
            )
        ) {

            return WeaponUpgradeViewModel(
                weaponProgressRepository =
                    weaponProgressRepository,

                inventoryRepository =
                    inventoryRepository,

                weaponUpgradeEngine =
                    weaponUpgradeEngine,

                weaponCraftingEngine =
                    weaponCraftingEngine
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}