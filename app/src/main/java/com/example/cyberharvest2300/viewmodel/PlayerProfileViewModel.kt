package com.example.cyberharvest2300.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cyberharvest2300.data.local.entity.PlayerProfile
import com.example.cyberharvest2300.data.repository.GameResetRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlayerProfileViewModel(
    private val repository: PlayerProfileRepository,
    private val gameResetRepository: GameResetRepository
) : ViewModel() {

    fun savePlayerProfile(
        characterName: String,
        restaurantName: String,
        startingClass: String,
        onResult: (Boolean) -> Unit
    ) {

        val profile =
            when (startingClass) {

                "CHEF" -> PlayerProfile(
                    characterName = characterName,
                    restaurantName = restaurantName,
                    startingClass = startingClass,
                    money = 300,
                    reputation = 1,
                    attack = 1
                )

                "HUNTER" -> PlayerProfile(
                    characterName = characterName,
                    restaurantName = restaurantName,
                    startingClass = startingClass,
                    money = 300,
                    reputation = 0,
                    attack = 2
                )

                "MERCHANT" -> PlayerProfile(
                    characterName = characterName,
                    restaurantName = restaurantName,
                    startingClass = startingClass,
                    money = 500,
                    reputation = 0,
                    attack = 1
                )

                else -> {
                    onResult(false)
                    return
                }
            }

        viewModelScope.launch {

            try {

                gameResetRepository
                    .resetAndCreateNewGame(
                        playerProfile = profile
                    )

                onResult(true)

            } catch (e: Exception) {

                onResult(false)
            }
        }
    }

    fun getPlayerProfile(): Flow<PlayerProfile?> {
        return repository.getPlayerProfile()
    }
}

class PlayerProfileViewModelFactory(
    private val repository: PlayerProfileRepository,
    private val gameResetRepository: GameResetRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                PlayerProfileViewModel::class.java
            )
        ) {

            return PlayerProfileViewModel(
                repository = repository,
                gameResetRepository = gameResetRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}