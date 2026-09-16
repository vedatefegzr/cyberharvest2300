
package com.example.cyberharvest2300.data.game

enum class TimePhase {
    DAY,
    NIGHT
}

object GameTime {

    const val DAY = "DAY"
    const val NIGHT = "NIGHT"

    fun getPhase(value: String): TimePhase {
        return when (value) {
            NIGHT -> TimePhase.NIGHT
            else -> TimePhase.DAY
        }
    }
}

