package com.example.cyberharvest2300.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cyberharvest2300.data.game.GameTime
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.domain.game.DayCycleEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DayCycleViewModel(
    private val engine: DayCycleEngine,
    private val playerProfileRepository: PlayerProfileRepository
) : ViewModel() {

    private val _day =
        MutableStateFlow(1)

    val day: StateFlow<Int> =
        _day.asStateFlow()

    private val _phase =
        MutableStateFlow(GameTime.DAY)

    val phase: StateFlow<String> =
        _phase.asStateFlow()

    private val _message =
        MutableStateFlow("")

    val message: StateFlow<String> =
        _message.asStateFlow()

    init {
        observePlayer()
    }

    private fun observePlayer() {

        viewModelScope.launch {

            playerProfileRepository
                .getPlayerProfile()
                .collect { player ->

                    if (player != null) {

                        _day.value =
                            player.day

                        _phase.value =
                            player.timePhase
                    }
                }
        }
    }

    fun startNight(
        onComplete: (Boolean) -> Unit = {}
    ) {

        viewModelScope.launch {

            val result =
                engine.startNight()

            _message.value =
                result.message

            onComplete(
                result.success &&
                        result.phase == GameTime.NIGHT
            )
        }
    }

    fun startDay(
        onComplete: (Boolean) -> Unit = {}
    ) {

        viewModelScope.launch {

            val result =
                engine.startDay()

            _message.value =
                result.message

            onComplete(
                result.success &&
                        result.phase == GameTime.DAY
            )
        }
    }

    fun isDay(): Boolean {
        return _phase.value == GameTime.DAY
    }

    fun isNight(): Boolean {
        return _phase.value == GameTime.NIGHT
    }
}

class DayCycleViewModelFactory(
    private val playerProfileRepository: PlayerProfileRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                DayCycleViewModel::class.java
            )
        ) {

            val engine =
                DayCycleEngine(
                    playerProfileRepository
                )

            return DayCycleViewModel(
                engine =
                    engine,

                playerProfileRepository =
                    playerProfileRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}