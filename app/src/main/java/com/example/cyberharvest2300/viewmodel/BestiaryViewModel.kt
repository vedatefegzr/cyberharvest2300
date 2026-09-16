package com.example.cyberharvest2300.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cyberharvest2300.data.game.Creature
import com.example.cyberharvest2300.data.game.CreatureData
import com.example.cyberharvest2300.data.local.entity.PlayerCreatureProgress
import com.example.cyberharvest2300.data.repository.CreatureProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BestiaryViewModel(
    private val repository: CreatureProgressRepository
) : ViewModel() {

    private val _creatures =
        MutableStateFlow<List<Creature>>(emptyList())

    val creatures: StateFlow<List<Creature>> =
        _creatures.asStateFlow()

    private val _progress =
        MutableStateFlow<List<PlayerCreatureProgress>>(
            emptyList()
        )

    val progress:
            StateFlow<List<PlayerCreatureProgress>> =
        _progress.asStateFlow()

    private var progressObserverStarted =
        false

    fun loadBestiary() {

        _creatures.value =
            CreatureData.all

        if (progressObserverStarted) {
            return
        }

        progressObserverStarted = true

        viewModelScope.launch {

            repository
                .getAllProgress()
                .collect { progressList ->

                    _progress.value =
                        progressList
                }
        }
    }

    fun getProgress(
        creatureId: String
    ): PlayerCreatureProgress? {

        return _progress.value.find {
            it.creatureId == creatureId
        }
    }
}

class BestiaryViewModelFactory(
    private val repository: CreatureProgressRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                BestiaryViewModel::class.java
            )
        ) {

            return BestiaryViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}