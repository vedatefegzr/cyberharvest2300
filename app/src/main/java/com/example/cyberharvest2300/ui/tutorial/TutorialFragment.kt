package com.example.cyberharvest2300.ui.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.data.game.GameIds
import com.example.cyberharvest2300.data.game.ItemData
import com.example.cyberharvest2300.data.game.WeaponData
import com.example.cyberharvest2300.data.local.AppDatabase
import com.example.cyberharvest2300.data.repository.CreatureProgressRepository
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.data.repository.WeaponProgressRepository
import com.example.cyberharvest2300.databinding.FragmentTutorialBinding
import com.example.cyberharvest2300.domain.game.DayCycleEngine
import com.example.cyberharvest2300.domain.hunting.HuntResult
import com.example.cyberharvest2300.domain.hunting.HuntingEngine
import com.example.cyberharvest2300.domain.loot.LootCalculator
import com.example.cyberharvest2300.viewmodel.WeaponProgressViewModel
import com.example.cyberharvest2300.viewmodel.WeaponProgressViewModelFactory
import kotlinx.coroutines.launch

class TutorialFragment : Fragment() {

    private var _binding: FragmentTutorialBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var playerProfileRepository: PlayerProfileRepository
    private lateinit var inventoryRepository: InventoryRepository
    private lateinit var weaponProgressRepository: WeaponProgressRepository
    private lateinit var creatureProgressRepository: CreatureProgressRepository

    private lateinit var dayCycleEngine: DayCycleEngine
    private lateinit var huntingEngine: HuntingEngine

    private lateinit var weaponProgressViewModel: WeaponProgressViewModel

    private val lootCalculator =
        LootCalculator()

    private var currentHuntResult: HuntResult? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentTutorialBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        setupRepositories()
        setupEngines()
        setupWeaponViewModel()

        showIntroduction()
    }

    private fun setupRepositories() {

        val database =
            AppDatabase.getDatabase(
                requireContext()
            )

        playerProfileRepository =
            PlayerProfileRepository(
                database.playerProfileDao()
            )

        inventoryRepository =
            InventoryRepository(
                inventoryDao =
                    database.inventoryDao(),
                playerProfileDao =
                    database.playerProfileDao()
            )

        weaponProgressRepository =
            WeaponProgressRepository(
                database.playerWeaponProgressDao()
            )

        creatureProgressRepository =
            CreatureProgressRepository(
                database.playerCreatureProgressDao()
            )
    }

    private fun setupEngines() {

        dayCycleEngine =
            DayCycleEngine(
                playerProfileRepository
            )

        huntingEngine =
            HuntingEngine(
                playerProfileRepository =
                    playerProfileRepository,

                creatureProgressRepository =
                    creatureProgressRepository,

                weaponProgressRepository =
                    weaponProgressRepository,

                inventoryRepository =
                    inventoryRepository,

                lootCalculator =
                    lootCalculator
            )
    }

    private fun setupWeaponViewModel() {

        val factory =
            WeaponProgressViewModelFactory(
                weaponProgressRepository
            )

        weaponProgressViewModel =
            ViewModelProvider(
                this,
                factory
            )[WeaponProgressViewModel::class.java]
    }

    private fun showIntroduction() {

        binding.tvTitle.text =
            "DAY 1"

        binding.tvSubtitle.text =
            "TUTORIAL"

        binding.tvStory.text =
            "Yeni hayatının ilk günü başladı.\n\n" +
                    "Restoranın küçük ve harap durumda.\n\n" +
                    "Ama bugün ilk müşterini ağırlayacaksın."

        binding.btnContinue.text =
            "CONTINUE"

        binding.btnContinue.setOnClickListener {
            showRestaurant()
        }
    }

    private fun showRestaurant() {

        binding.tvTitle.text =
            "RESTAURANT"

        binding.tvSubtitle.text =
            "DAY 1"

        binding.tvStory.text =
            "Kapı açıldı.\n\n" +
                    "İlk müşterin restorana girdi.\n\n" +
                    "Siparişini hazırlayıp müşteriye servis et."

        binding.btnContinue.text =
            "SERVE CUSTOMER"

        binding.btnContinue.setOnClickListener {
            serveCustomer()
        }
    }

    private fun serveCustomer() {

        binding.tvTitle.text =
            "RESTAURANT"

        binding.tvSubtitle.text =
            "DAY 1"

        binding.tvStory.text =
            "Müşterinin siparişini başarıyla hazırladın.\n\n" +
                    "Müşteriye servis yaptın.\n\n" +
                    "İlk müşterin memnun bir şekilde restorandan ayrıldı."

        binding.btnContinue.text =
            "CONTINUE"

        binding.btnContinue.setOnClickListener {
            showEvening()
        }
    }

    private fun showEvening() {

        binding.tvTitle.text =
            "EVENING"

        binding.tvSubtitle.text =
            "DAY 1"

        binding.tvStory.text =
            "Gün sona eriyor.\n\n" +
                    "Restoranı kapatmanın zamanı geldi.\n\n" +
                    "Ama yarın için malzemeye ihtiyacın var.\n\n" +
                    "Gece dışarı çıkıp avlanmalısın."

        binding.btnContinue.text =
            "PREPARE"

        binding.btnContinue.setOnClickListener {
            showPrepare()
        }
    }

    private fun showPrepare() {

        weaponProgressViewModel.addWeapon(
            GameIds.Weapons.SCRAP_PISTOL
        )

        weaponProgressViewModel.equipWeapon(
            GameIds.Weapons.SCRAP_PISTOL
        )

        val scrapPistol =
            WeaponData.getById(
                GameIds.Weapons.SCRAP_PISTOL
            )

        binding.tvTitle.text =
            "PREPARE"

        binding.tvSubtitle.text =
            "DAY 1"

        binding.tvStory.text =
            "Avlanmaya çıkmadan önce ekipmanlarını kontrol et.\n\n" +
                    "Eski bir ${scrapPistol?.name ?: "Scrap Pistol"} buldun.\n\n" +
                    "Silahını kuşandın ve gece avına hazırsın."

        binding.btnContinue.text =
            "CONTINUE"

        binding.btnContinue.setOnClickListener {

            startNight()
        }
    }

    private fun startNight() {

        viewLifecycleOwner.lifecycleScope.launch {

            val result =
                dayCycleEngine.startNight()

            if (!result.success) {

                binding.tvStory.text =
                    result.message

                return@launch
            }

            showHunting()
        }
    }

    private fun showHunting() {

        binding.tvTitle.text =
            "NEON FIELDS"

        binding.tvSubtitle.text =
            "NIGHT • DAY 1"

        binding.tvStory.text =
            "Neon Fields'a geldin.\n\n" +
                    "Etrafta terk edilmiş makineler ve mutant hayvanlar var.\n\n" +
                    "Bir ses duydun..."

        binding.btnContinue.text =
            "CONTINUE"

        binding.btnContinue.setOnClickListener {

            startHunt()
        }
    }

    private fun startHunt() {

        viewLifecycleOwner.lifecycleScope.launch {

            val result =
                huntingEngine.startTutorialHunt()

            currentHuntResult = result

            if (!result.success) {

                binding.tvStory.text =
                    result.message

                return@launch
            }

            showCyberRatEncounter()
        }
    }

    private fun showCyberRatEncounter() {

        val creature =
            huntingEngine.getCurrentCreature()

        if (creature == null) {
            return
        }

        binding.tvTitle.text =
            "ENCOUNTER"

        binding.tvSubtitle.text =
            creature.name

        binding.tvStory.text =
            "${creature.name} önüne çıktı!\n\n" +
                    "HP: ${creature.hp}\n" +
                    "Attack: ${creature.attack}\n\n" +
                    "Silahını hazırla."

        binding.btnContinue.text =
            "FIGHT"

        binding.btnContinue.setOnClickListener {

            showCombat()
        }
    }

    private fun showCombat() {

        val creature =
            huntingEngine.getCurrentCreature()
                ?: return

        binding.tvTitle.text =
            "COMBAT"

        binding.tvSubtitle.text =
            creature.name

        binding.tvStory.text =
            "PLAYER HP: ${huntingEngine.getPlayerHp()}\n" +
                    "${creature.name} HP: ${huntingEngine.getEnemyHp()}\n\n" +
                    "Scrap Pistol hazır.\n" +
                    "Saldırını gerçekleştir."

        binding.btnContinue.text =
            "ATTACK"

        binding.btnContinue.setOnClickListener {

            performAttack()
        }
    }

    private fun performAttack() {

        viewLifecycleOwner.lifecycleScope.launch {

            val result =
                huntingEngine.attack()

            if (result == null) {
                return@launch
            }

            if (result.combatEnded) {

                if (result.playerWon) {

                    finishVictory()
                } else {

                    showDefeat()
                }

                return@launch
            }

            val creature =
                huntingEngine.getCurrentCreature()

            if (creature == null) {
                return@launch
            }

            binding.tvTitle.text =
                "COMBAT"

            binding.tvSubtitle.text =
                creature.name

            binding.tvStory.text =
                "Sen ${result.damageDealt} hasar verdin.\n\n" +
                        "${creature.name} sana " +
                        "${result.damageTaken} hasar verdi.\n\n" +
                        "PLAYER HP: ${result.playerHp}\n" +
                        "${creature.name} HP: ${result.enemyHp}"

            binding.btnContinue.text =
                "ATTACK"

            binding.btnContinue.setOnClickListener {

                performAttack()
            }
        }
    }

    private suspend fun finishVictory() {

        val victoryResult =
            huntingEngine.finishVictory()
                ?: return

        val loot =
            victoryResult.loot

        binding.tvTitle.text =
            "VICTORY"

        binding.tvSubtitle.text =
            "${victoryResult.creature.name} DEFEATED"

        binding.tvStory.text =
            buildString {

                append(
                    "${victoryResult.creature.name} yenildi!\n\n"
                )

                append(
                    "Loot topladın.\n\n"
                )

                loot.forEach { lootResult ->

                    append(
                        "${getItemName(lootResult.itemId)} ×" +
                                "${lootResult.quantity}\n"
                    )
                }
            }

        binding.btnContinue.text =
            "START DAY 2"

        binding.btnContinue.setOnClickListener {

            startDayTwo()
        }
    }

    private fun getItemName(
        itemId: String
    ): String {

        return ItemData
            .getById(itemId)
            ?.name
            ?: itemId
    }

    private fun showDefeat() {

        binding.tvTitle.text =
            "DEFEAT"

        binding.tvSubtitle.text =
            "YOU DIED"

        binding.tvStory.text =
            "Cyber Rat seni yendi.\n\n" +
                    "Canın yenilendi.\n\n" +
                    "Tekrar hazırlanıp avlanabilirsin."

        binding.btnContinue.text =
            "TRY AGAIN"

        binding.btnContinue.setOnClickListener {

            startHunt()
        }
    }

    private fun startDayTwo() {

        viewLifecycleOwner.lifecycleScope.launch {

            val result =
                dayCycleEngine.startDay()

            if (!result.success) {

                binding.tvStory.text =
                    result.message

                return@launch
            }

            showDayTwo()
        }
    }

    private fun showDayTwo() {

        binding.tvTitle.text =
            "DAY 2"

        binding.tvSubtitle.text =
            "TUTORIAL COMPLETE"

        binding.tvStory.text =
            "Güneş yeniden doğdu.\n\n" +
                    "İlk geceni başarıyla atlattın.\n\n" +
                    "Artık avlanmayı ve restoranını yönetmeyi biliyorsun."

        binding.btnContinue.text =
            "CONTINUE"

        binding.btnContinue.setOnClickListener {

            finishTutorial()
        }
    }

    private fun finishTutorial() {

        binding.tvTitle.text =
            "WELCOME"

        binding.tvSubtitle.text =
            "CYBER-HARVEST 2300"

        binding.tvStory.text =
            "Tutorial tamamlandı.\n\n" +
                    "Artık kendi restoranını yönetebilirsin."

        binding.btnContinue.text =
            "ENTER RESTAURANT"

        binding.btnContinue.setOnClickListener {

            findNavController().navigate(
                R.id.action_tutorialFragment_to_hubFragment
            )
        }
    }

    override fun onDestroyView() {

        huntingEngine.clearHunt()

        super.onDestroyView()

        _binding = null
    }
}