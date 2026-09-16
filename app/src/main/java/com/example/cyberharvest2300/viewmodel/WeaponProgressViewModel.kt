package com.example.cyberharvest2300.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cyberharvest2300.data.local.entity.PlayerWeaponProgress
import com.example.cyberharvest2300.data.repository.WeaponProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WeaponProgressViewModel(
    private val repository: WeaponProgressRepository
) : ViewModel() {

    fun getAllProgress(): Flow<List<PlayerWeaponProgress>> {
        return repository.getAllProgress()
    }

    fun getEquippedWeapon(
        onResult: (PlayerWeaponProgress?) -> Unit
    ) {
        viewModelScope.launch {

            val weapon =
                repository.getEquippedWeapon()

            onResult(weapon)
        }
    }

    fun addWeapon(
        weaponId: String
    ) {
        viewModelScope.launch {
            repository.addWeapon(weaponId)
        }
    }

    fun equipWeapon(
        weaponId: String
    ) {
        viewModelScope.launch {
            repository.equipWeapon(weaponId)
        }
    }

    fun upgradeWeapon(
        weaponId: String
    ) {
        viewModelScope.launch {
            repository.upgradeWeapon(weaponId)
        }
    }

    fun clearAllProgress() {
        viewModelScope.launch {
            repository.clearAllProgress()
        }
    }
}

class WeaponProgressViewModelFactory(
    private val repository: WeaponProgressRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                WeaponProgressViewModel::class.java
            )
        ) {
            return WeaponProgressViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}