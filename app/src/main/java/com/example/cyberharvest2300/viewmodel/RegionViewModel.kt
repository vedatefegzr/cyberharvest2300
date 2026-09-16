package com.example.cyberharvest2300.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cyberharvest2300.data.game.RegionData
import com.example.cyberharvest2300.data.local.entity.RegionState
import com.example.cyberharvest2300.data.repository.RegionRepository
import com.example.cyberharvest2300.domain.unlock.UnlockConditionChecker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RegionViewModel(
    private val repository: RegionRepository,
    private val unlockConditionChecker: UnlockConditionChecker
) : ViewModel() {

    fun getAllStates(): Flow<List<RegionState>> {
        return repository.getAllStates()
    }

    fun checkAndUnlockRegions() {

        viewModelScope.launch {

            RegionData.all.forEach { region ->

                if (region.unlockConditions.isEmpty()) {
                    return@forEach
                }

                val currentState =
                    repository.getState(
                        region.id
                    )

                if (
                    currentState != null &&
                    currentState.isUnlocked
                ) {
                    return@forEach
                }

                val canUnlock =
                    unlockConditionChecker
                        .areConditionsMet(
                            region.unlockConditions
                        )

                if (canUnlock) {

                    repository.unlockRegion(
                        region.id
                    )
                }
            }
        }
    }

    fun unlockRegion(
        regionId: String
    ) {

        viewModelScope.launch {

            val region =
                RegionData.getById(
                    regionId
                )
                    ?: return@launch

            val conditionsMet =
                unlockConditionChecker
                    .areConditionsMet(
                        region.unlockConditions
                    )

            if (!conditionsMet) {
                return@launch
            }

            repository.unlockRegion(
                regionId
            )
        }
    }

    fun checkUnlock(
        regionId: String,
        onResult: (Boolean) -> Unit
    ) {

        viewModelScope.launch {

            val region =
                RegionData.getById(
                    regionId
                )

            if (region == null) {
                onResult(false)
                return@launch
            }

            val conditionsMet =
                unlockConditionChecker
                    .areConditionsMet(
                        region.unlockConditions
                    )

            onResult(
                conditionsMet
            )
        }
    }

    fun clearAllStates() {

        viewModelScope.launch {
            repository.clearAllStates()
        }
    }
}

class RegionViewModelFactory(
    private val repository: RegionRepository,
    private val unlockConditionChecker: UnlockConditionChecker
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                RegionViewModel::class.java
            )
        ) {

            return RegionViewModel(
                repository =
                    repository,

                unlockConditionChecker =
                    unlockConditionChecker
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}