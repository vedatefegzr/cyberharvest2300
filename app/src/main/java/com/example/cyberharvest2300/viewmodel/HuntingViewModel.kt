package com.example.cyberharvest2300.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cyberharvest2300.data.game.Creature
import com.example.cyberharvest2300.data.game.GameIds
import com.example.cyberharvest2300.data.game.Region
import com.example.cyberharvest2300.data.game.RegionData
import com.example.cyberharvest2300.data.local.entity.PlayerProfile
import com.example.cyberharvest2300.data.local.entity.RegionState
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.data.repository.RegionRepository
import com.example.cyberharvest2300.domain.combat.CombatResult
import com.example.cyberharvest2300.domain.hunting.HuntResult
import com.example.cyberharvest2300.domain.hunting.HuntVictoryResult
import com.example.cyberharvest2300.domain.hunting.HuntingEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HuntingViewModel(
    private val huntingEngine: HuntingEngine,
    private val regionRepository: RegionRepository,
    private val playerProfileRepository: PlayerProfileRepository
) : ViewModel() {

    private val _regions =
        MutableStateFlow<List<Region>>(emptyList())

    val regions: StateFlow<List<Region>> =
        _regions.asStateFlow()

    private val _regionStates =
        MutableStateFlow<List<RegionState>>(emptyList())

    val regionStates: StateFlow<List<RegionState>> =
        _regionStates.asStateFlow()

    private val _selectedRegionId =
        MutableStateFlow<String?>(null)

    val selectedRegionId: StateFlow<String?> =
        _selectedRegionId.asStateFlow()

    private val _currentCreature =
        MutableStateFlow<Creature?>(null)

    val currentCreature: StateFlow<Creature?> =
        _currentCreature.asStateFlow()

    private val _huntResult =
        MutableStateFlow<HuntResult?>(null)

    val huntResult: StateFlow<HuntResult?> =
        _huntResult.asStateFlow()

    private val _combatResult =
        MutableStateFlow<CombatResult?>(null)

    val combatResult: StateFlow<CombatResult?> =
        _combatResult.asStateFlow()

    private val _victoryResult =
        MutableStateFlow<HuntVictoryResult?>(null)

    val victoryResult: StateFlow<HuntVictoryResult?> =
        _victoryResult.asStateFlow()

    private val _playerProfile =
        MutableStateFlow<PlayerProfile?>(null)

    val playerProfile: StateFlow<PlayerProfile?> =
        _playerProfile.asStateFlow()

    private var observersStarted = false

    fun loadRegions() {

        _regions.value = RegionData.all

        if (observersStarted) {
            return
        }

        observersStarted = true

        viewModelScope.launch {

            playerProfileRepository
                .getPlayerProfile()
                .collect { profile ->

                    _playerProfile.value = profile
                }
        }

        viewModelScope.launch {

            regionRepository
                .getAllStates()
                .collect { states ->

                    updateRegionStates(states)

                    if (_selectedRegionId.value == null) {

                        val firstUnlockedRegion =
                            RegionData.all.firstOrNull { region ->

                                region.id ==
                                        GameIds.Regions.NEON_FIELDS ||

                                        states.any { state ->

                                            state.regionId ==
                                                    region.id &&

                                                    state.isUnlocked
                                        }
                            }

                        _selectedRegionId.value =
                            firstUnlockedRegion?.id
                    }
                }
        }
    }

    fun updateRegionStates(
        states: List<RegionState>
    ) {

        _regionStates.value = states

        val selected =
            _selectedRegionId.value

        if (
            selected != null &&
            !isRegionUnlocked(selected)
        ) {

            val fallback =
                RegionData.all.firstOrNull { region ->

                    isRegionUnlocked(region.id)
                }

            _selectedRegionId.value =
                fallback?.id
        }
    }

    fun selectRegion(
        regionId: String
    ) {

        if (isRegionUnlocked(regionId)) {

            _selectedRegionId.value =
                regionId
        }
    }

    fun isRegionUnlocked(
        regionId: String
    ): Boolean {

        if (
            regionId ==
            GameIds.Regions.NEON_FIELDS
        ) {
            return true
        }

        return _regionStates.value.any { state ->

            state.regionId == regionId &&
                    state.isUnlocked
        }
    }

    fun startHunt() {

        if (_currentCreature.value != null) {
            return
        }

        val regionId =
            _selectedRegionId.value
                ?: return

        viewModelScope.launch {

            val result =
                huntingEngine.startHunt(
                    regionId
                )

            _huntResult.value =
                result

            if (result.success) {

                _currentCreature.value =
                    result.creature
            }
        }
    }

    fun attack() {

        if (_currentCreature.value == null) {
            return
        }

        viewModelScope.launch {

            val result =
                huntingEngine.attack()
                    ?: return@launch

            _combatResult.value =
                result

            if (
                result.combatEnded &&
                result.playerWon
            ) {

                val victory =
                    huntingEngine.finishVictory()

                _victoryResult.value =
                    victory

                _currentCreature.value =
                    null
            }

            if (
                result.combatEnded &&
                !result.playerWon
            ) {

                _currentCreature.value =
                    null
            }
        }
    }

    fun clearCombatResult() {

        _combatResult.value =
            null
    }

    fun clearVictoryResult() {

        _victoryResult.value =
            null
    }

    fun clearHunt() {

        huntingEngine.clearHunt()

        _currentCreature.value =
            null

        _huntResult.value =
            null

        _combatResult.value =
            null

        _victoryResult.value =
            null
    }
}

class HuntingViewModelFactory(
    private val huntingEngine: HuntingEngine,
    private val regionRepository: RegionRepository,
    private val playerProfileRepository: PlayerProfileRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                HuntingViewModel::class.java
            )
        ) {

            return HuntingViewModel(
                huntingEngine =
                    huntingEngine,

                regionRepository =
                    regionRepository,

                playerProfileRepository =
                    playerProfileRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}