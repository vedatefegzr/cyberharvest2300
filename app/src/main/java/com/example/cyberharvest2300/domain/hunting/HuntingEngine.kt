package com.example.cyberharvest2300.domain.hunting

import com.example.cyberharvest2300.data.game.Creature
import com.example.cyberharvest2300.data.game.CreatureData
import com.example.cyberharvest2300.data.game.GameIds
import com.example.cyberharvest2300.data.game.GameTime
import com.example.cyberharvest2300.data.game.WeaponData
import com.example.cyberharvest2300.data.repository.CreatureProgressRepository
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.data.repository.WeaponProgressRepository
import com.example.cyberharvest2300.domain.combat.CombatEngine
import com.example.cyberharvest2300.domain.combat.CombatResult
import com.example.cyberharvest2300.domain.loot.LootCalculator
import com.example.cyberharvest2300.domain.loot.LootResult
import kotlinx.coroutines.flow.first

data class HuntResult(
    val success: Boolean,
    val message: String,
    val creature: Creature? = null
)

data class HuntVictoryResult(
    val creature: Creature,
    val loot: List<LootResult>
)

class HuntingEngine(

    private val playerProfileRepository:
    PlayerProfileRepository,

    private val creatureProgressRepository:
    CreatureProgressRepository,

    private val weaponProgressRepository:
    WeaponProgressRepository,

    private val inventoryRepository:
    InventoryRepository,

    private val lootCalculator:
    LootCalculator
) {

    private var currentCreature:
            Creature? = null

    private var combatEngine:
            CombatEngine? = null

    suspend fun startHunt(
        regionId: String
    ): HuntResult {

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return HuntResult(
                    success = false,
                    message = "Player profile bulunamadı."
                )

        if (player.timePhase != GameTime.NIGHT) {

            return HuntResult(
                success = false,
                message = "It is daytime. Hunting is closed."
            )
        }

        if (currentCreature != null) {

            return HuntResult(
                success = false,
                message = "Already in combat."
            )
        }

        val creatures =
            CreatureData.getByRegion(
                regionId
            )

        if (creatures.isEmpty()) {

            return HuntResult(
                success = false,
                message = "Bu bölgede yaratık bulunamadı."
            )
        }

        if (player.health <= 0) {

            return HuntResult(
                success = false,
                message = "Canın tükenmiş. Yeni gün başlamasını bekle."
            )
        }

        val creature =
            selectCreature(creatures)

        currentCreature =
            creature

        creatureProgressRepository
            .recordEncounter(
                creature.id
            )

        val equippedWeapon =
            weaponProgressRepository
                .getEquippedWeapon()

        val weapon =
            equippedWeapon?.let {
                WeaponData.getById(
                    it.weaponId
                )
            }

        val weaponAttack =
            if (
                equippedWeapon != null &&
                weapon != null
            ) {

                weapon.baseAttack +
                        (
                                equippedWeapon.level - 1
                                ) * 2

            } else {
                0
            }

        val totalPlayerAttack =
            player.attack +
                    weaponAttack

        val playerElement =
            weapon?.element
                ?: com.example.cyberharvest2300.data.game.Element.PHYSICAL

        combatEngine =
            CombatEngine(

                playerMaxHp =
                    player.health,

                playerAttack =
                    totalPlayerAttack,

                playerDefense =
                    player.defense,

                playerElement =
                    playerElement,

                enemyMaxHp =
                    creature.hp,

                enemyAttack =
                    creature.attack,

                enemyWeakness =
                    creature.weakness,

                enemyResistance =
                    creature.resistance
            )

        return HuntResult(
            success = true,
            message =
                "${creature.name} ile karşılaştın!",
            creature = creature
        )
    }

    /*
     * =========================================================
     * TUTORIAL HUNT
     * =========================================================
     *
     * Bu fonksiyon SADECE TutorialFragment tarafından kullanılır.
     *
     * Normal startHunt() sistemine dokunmaz.
     * Normal Cyber Rat verisini değiştirmez.
     * Sadece savaş için geçici bir Creature kopyası oluşturur.
     */
    suspend fun startTutorialHunt(): HuntResult {

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return HuntResult(
                    success = false,
                    message = "Player profile bulunamadı."
                )

        if (player.timePhase != GameTime.NIGHT) {

            return HuntResult(
                success = false,
                message = "It is daytime. Hunting is closed."
            )
        }

        if (currentCreature != null) {

            return HuntResult(
                success = false,
                message = "Already in combat."
            )
        }

        if (player.health <= 0) {

            return HuntResult(
                success = false,
                message = "Canın tükenmiş."
            )
        }

        val cyberRat =
            CreatureData
                .getByRegion(
                    GameIds.Regions.NEON_FIELDS
                )
                .find {
                    it.id ==
                            GameIds.Creatures.CYBER_RAT
                }
                ?: return HuntResult(
                    success = false,
                    message = "Cyber Rat bulunamadı."
                )

        /*
         * Gerçek CreatureData değişmiyor.
         *
         * Sadece tutorial için geçici kopya.
         */
        val tutorialCreature =
            cyberRat.copy(
                hp = 10,
                attack = 1
            )

        currentCreature =
            tutorialCreature

        /*
         * Bestiary'de gerçek Cyber Rat
         * karşılaşması olarak kaydediliyor.
         */
        creatureProgressRepository
            .recordEncounter(
                cyberRat.id
            )

        val equippedWeapon =
            weaponProgressRepository
                .getEquippedWeapon()

        val weapon =
            equippedWeapon?.let {
                WeaponData.getById(
                    it.weaponId
                )
            }

        val weaponAttack =
            if (
                equippedWeapon != null &&
                weapon != null
            ) {

                weapon.baseAttack +
                        (
                                equippedWeapon.level - 1
                                ) * 2

            } else {
                0
            }

        val totalPlayerAttack =
            player.attack +
                    weaponAttack

        val playerElement =
            weapon?.element
                ?: com.example.cyberharvest2300.data.game.Element.PHYSICAL

        combatEngine =
            CombatEngine(

                playerMaxHp =
                    player.health,

                playerAttack =
                    totalPlayerAttack,

                playerDefense =
                    player.defense,

                playerElement =
                    playerElement,

                enemyMaxHp =
                    tutorialCreature.hp,

                enemyAttack =
                    tutorialCreature.attack,

                enemyWeakness =
                    tutorialCreature.weakness,

                enemyResistance =
                    tutorialCreature.resistance
            )

        return HuntResult(
            success = true,
            message =
                "${tutorialCreature.name} ile karşılaştın!",
            creature =
                tutorialCreature
        )
    }

    suspend fun attack():
            CombatResult? {

        val engine =
            combatEngine
                ?: return null

        val result =
            engine.attack()

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()

        if (player != null) {

            playerProfileRepository
                .updatePlayerProfile(
                    player.copy(
                        health =
                            result.playerHp
                    )
                )
        }

        if (
            result.combatEnded &&
            !result.playerWon
        ) {

            /*
             * Oyuncu öldüğünde mevcut av temizlenir.
             * Oyuncu aynı gece tekrar avlanabilir.
             * Yeni bir av için HP tekrar max'a alınır.
             */

            if (player != null) {

                playerProfileRepository
                    .updatePlayerProfile(
                        player.copy(
                            health =
                                player.maxHealth
                        )
                    )
            }

            combatEngine = null
            currentCreature = null
        }

        return result
    }

    suspend fun finishVictory():
            HuntVictoryResult? {

        val creature =
            currentCreature
                ?: return null

        creatureProgressRepository
            .recordKill(
                creature.id
            )

        val loot =
            lootCalculator
                .calculateLoot(
                    creature
                )

        for (lootResult in loot) {

            inventoryRepository.addItem(
                itemId =
                    lootResult.itemId,

                quantity =
                    lootResult.quantity
            )
        }

        combatEngine = null
        currentCreature = null

        return HuntVictoryResult(
            creature = creature,
            loot = loot
        )
    }

    private fun selectCreature(
        creatures: List<Creature>
    ): Creature {

        val boss =
            creatures.find {
                it.id == GameIds.Creatures.IRON_TITAN
            }

        val elites =
            creatures.filter {
                it.id == GameIds.Creatures.ALPHA_SCRAP_WOLF ||
                        it.id == GameIds.Creatures.TOXIC_ALPHA_HOUND ||
                        it.id == GameIds.Creatures.ELITE_WAR_MACHINE
            }

        val normalCreatures =
            creatures.filter {
                it.id != GameIds.Creatures.IRON_TITAN &&
                        it.id != GameIds.Creatures.ALPHA_SCRAP_WOLF &&
                        it.id != GameIds.Creatures.TOXIC_ALPHA_HOUND &&
                        it.id != GameIds.Creatures.ELITE_WAR_MACHINE
            }

        val roll =
            kotlin.random.Random.nextInt(100)

        // Boss: %3
        if (boss != null && roll < 3) {
            return boss
        }

        // Elite: %10
        if (elites.isNotEmpty() && roll in 3..12) {
            return elites.random()
        }

        // Normal
        if (normalCreatures.isNotEmpty()) {
            return normalCreatures.random()
        }

        // Eğer normal yaratık yoksa elite seç.
        if (elites.isNotEmpty()) {
            return elites.random()
        }

        // Eğer sadece boss varsa boss seç.
        if (boss != null) {
            return boss
        }

        // startHunt() zaten boş listeyi kontrol ettiği için
        // buraya normal şartlarda gelinmemeli.
        return creatures.random()
    }

    fun getCurrentCreature():
            Creature? =
        currentCreature

    fun getPlayerHp(): Int =
        combatEngine?.getPlayerHp()
            ?: 0

    fun getEnemyHp(): Int =
        combatEngine?.getEnemyHp()
            ?: 0

    fun clearHunt() {

        combatEngine = null

        currentCreature = null
    }
}