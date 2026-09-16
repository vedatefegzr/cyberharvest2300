package com.example.cyberharvest2300.domain.combat

import com.example.cyberharvest2300.data.game.Element
import kotlin.random.Random

data class CombatResult(
    val playerHp: Int,
    val enemyHp: Int,

    val damageDealt: Int,
    val damageTaken: Int,

    val combatEnded: Boolean,
    val playerWon: Boolean,

    val criticalHit: Boolean = false,
    val weaknessHit: Boolean = false,
    val resistanceHit: Boolean = false
)

class CombatEngine(

    private val playerMaxHp: Int,

    private val playerAttack: Int,

    private val playerDefense: Int,

    private val playerElement: Element,

    private val enemyMaxHp: Int,

    private val enemyAttack: Int,

    private val enemyWeakness: Element?,

    private val enemyResistance: Element?
) {

    private var playerHp =
        playerMaxHp

    private var enemyHp =
        enemyMaxHp

    fun attack(): CombatResult {

        var damage =
            playerAttack

        val weaknessHit =
            enemyWeakness == playerElement

        val resistanceHit =
            enemyResistance == playerElement

        if (weaknessHit) {
            damage =
                (damage * 1.5f).toInt()
        }

        if (resistanceHit) {
            damage =
                (damage * 0.5f).toInt()
        }

        val criticalHit =
            Random.nextFloat() < 0.10f

        if (criticalHit) {
            damage *= 2
        }

        damage =
            damage.coerceAtLeast(1)

        enemyHp -= damage

        if (enemyHp <= 0) {

            enemyHp = 0

            return CombatResult(
                playerHp = playerHp,
                enemyHp = enemyHp,
                damageDealt = damage,
                damageTaken = 0,
                combatEnded = true,
                playerWon = true,
                criticalHit = criticalHit,
                weaknessHit = weaknessHit,
                resistanceHit = resistanceHit
            )
        }

        val damageTaken =
            calculateEnemyDamage()

        playerHp -= damageTaken

        if (playerHp <= 0) {

            playerHp = 0

            return CombatResult(
                playerHp = playerHp,
                enemyHp = enemyHp,
                damageDealt = damage,
                damageTaken = damageTaken,
                combatEnded = true,
                playerWon = false,
                criticalHit = criticalHit,
                weaknessHit = weaknessHit,
                resistanceHit = resistanceHit
            )
        }

        return CombatResult(
            playerHp = playerHp,
            enemyHp = enemyHp,
            damageDealt = damage,
            damageTaken = damageTaken,
            combatEnded = false,
            playerWon = false,
            criticalHit = criticalHit,
            weaknessHit = weaknessHit,
            resistanceHit = resistanceHit
        )
    }

    private fun calculateEnemyDamage(): Int {

        return (
                enemyAttack - playerDefense
                ).coerceAtLeast(1)
    }

    fun getPlayerHp(): Int =
        playerHp

    fun getEnemyHp(): Int =
        enemyHp
}