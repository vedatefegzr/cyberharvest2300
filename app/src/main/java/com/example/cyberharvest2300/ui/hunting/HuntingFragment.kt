
package com.example.cyberharvest2300.ui.hunting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.cyberharvest2300.data.game.ItemData
import com.example.cyberharvest2300.data.game.Region
import com.example.cyberharvest2300.data.local.AppDatabase
import com.example.cyberharvest2300.data.repository.CreatureProgressRepository
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.data.repository.RegionRepository
import com.example.cyberharvest2300.data.repository.WeaponProgressRepository
import com.example.cyberharvest2300.databinding.FragmentHuntingBinding
import com.example.cyberharvest2300.domain.combat.CombatResult
import com.example.cyberharvest2300.domain.hunting.HuntingEngine
import com.example.cyberharvest2300.domain.loot.LootCalculator
import com.example.cyberharvest2300.domain.unlock.UnlockConditionChecker
import com.example.cyberharvest2300.ui.assets.CreatureAssetMap
import com.example.cyberharvest2300.ui.hub.HubFragment
import com.example.cyberharvest2300.viewmodel.DayCycleViewModel
import com.example.cyberharvest2300.viewmodel.DayCycleViewModelFactory
import com.example.cyberharvest2300.viewmodel.HuntingViewModel
import com.example.cyberharvest2300.viewmodel.HuntingViewModelFactory
import com.example.cyberharvest2300.viewmodel.RegionViewModel
import com.example.cyberharvest2300.viewmodel.RegionViewModelFactory
import kotlinx.coroutines.launch

class HuntingFragment : Fragment() {

    private var _binding: FragmentHuntingBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var viewModel: HuntingViewModel

    private lateinit var regionViewModel: RegionViewModel

    private lateinit var dayCycleViewModel: DayCycleViewModel

    private var regionIds = emptyList<String>()

    private var spinnerUpdating = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentHuntingBinding.inflate(
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

        setupViewModel()
        setupRegionViewModel()
        setupDayCycleViewModel()

        setupRegionSpinner()
        setupButtons()

        observeHunting()
        observeRegions()
        observeDayCycle()

        viewModel.loadRegions()

        regionViewModel
            .checkAndUnlockRegions()
    }

    private fun setupViewModel() {

        val database =
            AppDatabase.getDatabase(
                requireContext()
            )

        val playerProfileRepository =
            PlayerProfileRepository(
                database.playerProfileDao()
            )

        val creatureProgressRepository =
            CreatureProgressRepository(
                database.playerCreatureProgressDao()
            )

        val weaponProgressRepository =
            WeaponProgressRepository(
                database.playerWeaponProgressDao()
            )

        val inventoryRepository =
            InventoryRepository(
                inventoryDao =
                    database.inventoryDao(),
                playerProfileDao =
                    database.playerProfileDao()
            )

        val regionRepository =
            RegionRepository(
                database.regionStateDao()
            )

        val huntingEngine =
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
                    LootCalculator()
            )

        val factory =
            HuntingViewModelFactory(
                huntingEngine =
                    huntingEngine,
                regionRepository =
                    regionRepository,
                playerProfileRepository =
                    playerProfileRepository
            )

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[HuntingViewModel::class.java]
    }

    private fun setupRegionViewModel() {

        val database =
            AppDatabase.getDatabase(
                requireContext()
            )

        val playerProfileRepository =
            PlayerProfileRepository(
                database.playerProfileDao()
            )

        val creatureProgressRepository =
            CreatureProgressRepository(
                database.playerCreatureProgressDao()
            )

        val weaponProgressRepository =
            WeaponProgressRepository(
                database.playerWeaponProgressDao()
            )

        val regionRepository =
            RegionRepository(
                database.regionStateDao()
            )

        val unlockConditionChecker =
            UnlockConditionChecker(
                playerProfileRepository =
                    playerProfileRepository,
                creatureProgressRepository =
                    creatureProgressRepository,
                weaponProgressRepository =
                    weaponProgressRepository
            )

        val factory =
            RegionViewModelFactory(
                repository =
                    regionRepository,
                unlockConditionChecker =
                    unlockConditionChecker
            )

        regionViewModel =
            ViewModelProvider(
                this,
                factory
            )[RegionViewModel::class.java]
    }

    private fun setupDayCycleViewModel() {

        val database =
            AppDatabase.getDatabase(
                requireContext()
            )

        val playerProfileRepository =
            PlayerProfileRepository(
                database.playerProfileDao()
            )

        val factory =
            DayCycleViewModelFactory(
                playerProfileRepository
            )

        dayCycleViewModel =
            ViewModelProvider(
                this,
                factory
            )[DayCycleViewModel::class.java]
    }

    private fun setupRegionSpinner() {

        binding.spinnerRegions
            .onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    if (spinnerUpdating) {
                        return
                    }

                    if (position !in regionIds.indices) {
                        return
                    }

                    viewModel.selectRegion(
                        regionIds[position]
                    )
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    private fun setupButtons() {

        binding.btnHunt
            .setOnClickListener {

                if (!dayCycleViewModel.isNight()) {

                    binding.tvStatus.text =
                        "It is daytime. Hunting is closed."

                    binding.btnHunt.isEnabled =
                        false

                    return@setOnClickListener
                }

                binding.btnHunt.isEnabled =
                    false

                binding.btnAttack.isEnabled =
                    false

                binding.btnReturnRestaurant.isEnabled =
                    false

                binding.tvStatus.text =
                    "Searching for a creature..."

                viewModel.startHunt()
            }

        binding.btnAttack
            .setOnClickListener {

                binding.btnAttack.isEnabled =
                    false

                viewModel.attack()
            }

        binding.btnReturnRestaurant
            .setOnClickListener {

                if (!dayCycleViewModel.isNight()) {
                    return@setOnClickListener
                }

                binding.btnHunt.isEnabled =
                    false

                binding.btnAttack.isEnabled =
                    false

                binding.btnReturnRestaurant.isEnabled =
                    false

                binding.tvStatus.text =
                    "Returning to restaurant..."

                dayCycleViewModel
                    .startDay { success ->

                        if (!isAdded) {
                            return@startDay
                        }

                        if (!success) {

                            binding.tvStatus.text =
                                "Could not start the new day."

                            binding.btnReturnRestaurant.isEnabled =
                                dayCycleViewModel.isNight()

                            return@startDay
                        }

                        binding.tvStatus.text =
                            "A new day has begun."

                        binding.root.postDelayed({

                            if (!isAdded) {
                                return@postDelayed
                            }

                            val parent =
                                parentFragment

                            if (parent is HubFragment) {
                                parent.openRestaurant()
                            }

                        }, 500)
                    }
            }
    }

    private fun observeDayCycle() {

        viewLifecycleOwner
            .lifecycleScope
            .launch {

                viewLifecycleOwner
                    .repeatOnLifecycle(
                        Lifecycle.State.STARTED
                    ) {

                        dayCycleViewModel
                            .phase
                            .collect { phase ->

                                if (phase != "NIGHT") {

                                    binding.btnHunt.isEnabled =
                                        false

                                    binding.btnAttack.isEnabled =
                                        false

                                    binding.btnReturnRestaurant.isEnabled =
                                        false

                                    binding.tvStatus.text =
                                        "It is daytime. Hunting is closed."

                                } else {

                                    if (
                                        viewModel
                                            .currentCreature
                                            .value == null
                                    ) {

                                        binding.btnHunt.isEnabled =
                                            true

                                        binding.btnReturnRestaurant.isEnabled =
                                            true
                                    }
                                }
                            }
                    }
            }
    }

    private fun observeRegions() {

        viewLifecycleOwner
            .lifecycleScope
            .launch {

                viewLifecycleOwner
                    .repeatOnLifecycle(
                        Lifecycle.State.STARTED
                    ) {

                        regionViewModel
                            .getAllStates()
                            .collect { states ->

                                viewModel
                                    .updateRegionStates(
                                        states
                                    )
                            }
                    }
            }
    }

    private fun observeHunting() {

        viewLifecycleOwner
            .lifecycleScope
            .launch {

                viewLifecycleOwner
                    .repeatOnLifecycle(
                        Lifecycle.State.STARTED
                    ) {

                        launch {

                            viewModel
                                .regions
                                .collect { regions ->

                                    renderRegionSpinner(
                                        regions
                                    )
                                }
                        }

                        launch {

                            viewModel
                                .selectedRegionId
                                .collect { selectedRegionId ->

                                    if (selectedRegionId == null) {
                                        return@collect
                                    }

                                    val index =
                                        regionIds
                                            .indexOf(
                                                selectedRegionId
                                            )

                                    if (index >= 0) {

                                        spinnerUpdating =
                                            true

                                        binding.spinnerRegions
                                            .setSelection(
                                                index
                                            )

                                        spinnerUpdating =
                                            false
                                    }
                                }
                        }

                        launch {

                            viewModel
                                .currentCreature
                                .collect { creature ->

                                    if (creature == null) {

                                        binding.ivCreature.visibility =
                                            View.GONE

                                        binding.tvCreature.text =
                                            "No creature"

                                        binding.progressEnemyHp.progress =
                                            0

                                        return@collect
                                    }

                                    binding.ivCreature.visibility =
                                        View.VISIBLE

                                    binding.tvCreature.text =
                                        creature.name

                                    val creatureImage =
                                        CreatureAssetMap.getAsset(
                                            creature.id
                                        )

                                    if (creatureImage != null) {

                                        binding.ivCreature
                                            .setImageResource(
                                                creatureImage
                                            )
                                    }

                                    binding.progressEnemyHp.max =
                                        creature.hp

                                    binding.progressEnemyHp.progress =
                                        creature.hp
                                }
                        }

                        launch {

                            viewModel
                                .huntResult
                                .collect { result ->

                                    result
                                        ?: return@collect

                                    if (!result.success) {

                                        binding.tvStatus.text =
                                            result.message

                                        binding.btnHunt.isEnabled =
                                            dayCycleViewModel
                                                .isNight()

                                        binding.btnReturnRestaurant.isEnabled =
                                            dayCycleViewModel
                                                .isNight()

                                        return@collect
                                    }

                                    val creature =
                                        result.creature
                                            ?: return@collect

                                    val playerProfile =
                                        viewModel
                                            .playerProfile
                                            .value

                                    val playerHealth =
                                        playerProfile
                                            ?.health
                                            ?: 0

                                    val playerMaxHealth =
                                        playerProfile
                                            ?.maxHealth
                                            ?: 100

                                    binding.ivCreature.visibility =
                                        View.VISIBLE

                                    binding.tvCreature.text =
                                        creature.name

                                    val creatureImage =
                                        CreatureAssetMap.getAsset(
                                            creature.id
                                        )

                                    if (creatureImage != null) {

                                        binding.ivCreature
                                            .setImageResource(
                                                creatureImage
                                            )
                                    }

                                    binding.progressEnemyHp.max =
                                        creature.hp

                                    binding.progressEnemyHp.progress =
                                        creature.hp

                                    binding.progressPlayerHp.max =
                                        playerMaxHealth

                                    binding.progressPlayerHp.progress =
                                        playerHealth

                                    binding.tvStatus.text =
                                        "${creature.name} encountered!"

                                    binding.btnHunt.isEnabled =
                                        false

                                    binding.btnAttack.isEnabled =
                                        true

                                    binding.btnReturnRestaurant.isEnabled =
                                        false
                                }
                        }

                        launch {

                            viewModel
                                .combatResult
                                .collect { result ->

                                    result
                                        ?: return@collect

                                    binding.progressPlayerHp.max =
                                        viewModel
                                            .playerProfile
                                            .value
                                            ?.maxHealth
                                            ?: 100

                                    binding.progressPlayerHp.progress =
                                        result.playerHp

                                    binding.progressEnemyHp.progress =
                                        result.enemyHp
                                            .coerceAtLeast(0)

                                    if (result.combatEnded) {

                                        binding.btnAttack.isEnabled =
                                            false

                                        if (result.playerWon) {

                                            binding.tvStatus.text =
                                                buildCombatMessage(
                                                    result
                                                )

                                        } else {

                                            binding.tvStatus.text =
                                                "You were defeated.\n" +
                                                        "Your HP has been restored."

                                            binding.btnHunt.isEnabled =
                                                dayCycleViewModel
                                                    .isNight()

                                            binding.btnReturnRestaurant.isEnabled =
                                                dayCycleViewModel
                                                    .isNight()
                                        }

                                    } else {

                                        binding.tvStatus.text =
                                            buildCombatMessage(
                                                result
                                            )

                                        binding.btnAttack.isEnabled =
                                            true
                                    }

                                    viewModel
                                        .clearCombatResult()
                                }
                        }

                        launch {

                            viewModel
                                .victoryResult
                                .collect { result ->

                                    result
                                        ?: return@collect

                                    binding.tvStatus.text =
                                        buildString {

                                            append(
                                                "${result.creature.name} defeated!\n\n"
                                            )

                                            append(
                                                "LOOT:\n"
                                            )

                                            if (result.loot.isEmpty()) {

                                                append(
                                                    "No loot found.\n"
                                                )

                                            } else {

                                                result
                                                    .loot
                                                    .forEach { loot ->

                                                        val itemName =
                                                            ItemData
                                                                .getById(
                                                                    loot.itemId
                                                                )
                                                                ?.name
                                                                ?: loot.itemId

                                                        append(
                                                            "$itemName ×${loot.quantity}\n"
                                                        )
                                                    }
                                            }

                                            append(
                                                "\nYou can continue hunting."
                                            )
                                        }

                                    binding.progressEnemyHp.progress =
                                        0

                                    binding.btnAttack.isEnabled =
                                        false

                                    binding.btnHunt.isEnabled =
                                        dayCycleViewModel
                                            .isNight()

                                    binding.btnReturnRestaurant.isEnabled =
                                        dayCycleViewModel
                                            .isNight()

                                    viewModel
                                        .clearVictoryResult()
                                }
                        }
                    }
            }
    }

    private fun buildCombatMessage(
        result: CombatResult
    ): String {

        val message =
            StringBuilder()

        message.append(
            "You dealt ${result.damageDealt} damage."
        )

        if (result.criticalHit) {

            message.append(
                " CRITICAL HIT!"
            )
        }

        if (result.weaknessHit) {

            message.append(
                " WEAKNESS!"
            )
        }

        if (result.resistanceHit) {

            message.append(
                " RESISTANCE!"
            )
        }

        if (result.damageTaken > 0) {

            message.append(
                "\nEnemy dealt ${result.damageTaken} damage."
            )
        }

        return message.toString()
    }

    private fun renderRegionSpinner(
        regions: List<Region>
    ) {

        val unlockedRegions =
            regions.filter { region ->

                viewModel
                    .isRegionUnlocked(
                        region.id
                    )
            }

        regionIds =
            unlockedRegions.map {
                it.id
            }

        val regionNames =
            unlockedRegions.map {
                it.name
            }

        spinnerUpdating =
            true

        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                regionNames
            )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spinnerRegions
            .adapter =
            adapter

        spinnerUpdating =
            false

        if (regionNames.isEmpty()) {

            binding.tvStatus.text =
                "No hunting regions available."

            binding.btnHunt.isEnabled =
                false

            binding.btnReturnRestaurant.isEnabled =
                false
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}
