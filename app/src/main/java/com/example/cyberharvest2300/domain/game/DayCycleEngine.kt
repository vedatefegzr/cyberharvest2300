package com.example.cyberharvest2300.domain.game

import com.example.cyberharvest2300.data.game.GameTime
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import kotlinx.coroutines.flow.first

data class TimeChangeResult(
    val success: Boolean,
    val message: String,
    val day: Int,
    val phase: String
)

class DayCycleEngine(
    private val playerProfileRepository:
    PlayerProfileRepository
) {

    suspend fun startNight():
            TimeChangeResult {

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return TimeChangeResult(
                    success = false,
                    message =
                        "Player profile bulunamadı.",
                    day = 0,
                    phase =
                        GameTime.DAY
                )

        if (
            player.timePhase ==
            GameTime.NIGHT
        ) {

            return TimeChangeResult(
                success = false,
                message =
                    "Zaten gece.",
                day =
                    player.day,
                phase =
                    GameTime.NIGHT
            )
        }

        val updatedPlayer =
            player.copy(
                timePhase =
                    GameTime.NIGHT
            )

        playerProfileRepository
            .updatePlayerProfile(
                updatedPlayer
            )

        return TimeChangeResult(
            success = true,
            message =
                "Gece başladı. Avlanma zamanı.",
            day =
                updatedPlayer.day,
            phase =
                updatedPlayer.timePhase
        )
    }

    suspend fun startDay():
            TimeChangeResult {

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return TimeChangeResult(
                    success = false,
                    message =
                        "Player profile bulunamadı.",
                    day = 0,
                    phase =
                        GameTime.NIGHT
                )

        if (
            player.timePhase ==
            GameTime.DAY
        ) {

            return TimeChangeResult(
                success = false,
                message =
                    "Zaten gündüz.",
                day =
                    player.day,
                phase =
                    GameTime.DAY
            )
        }

        val updatedPlayer =
            player.copy(

                day =
                    player.day + 1,

                timePhase =
                    GameTime.DAY,

                health =
                    player.maxHealth
            )

        playerProfileRepository
            .updatePlayerProfile(
                updatedPlayer
            )

        return TimeChangeResult(
            success = true,
            message =
                "Yeni gün başladı. Canın yenilendi.",
            day =
                updatedPlayer.day,
            phase =
                updatedPlayer.timePhase
        )
    }

    suspend fun getCurrentPhase():
            String {

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()

        return player?.timePhase
            ?: GameTime.DAY
    }
}