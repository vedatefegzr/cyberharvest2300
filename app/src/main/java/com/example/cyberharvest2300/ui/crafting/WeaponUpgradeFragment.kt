package com.example.cyberharvest2300.ui.crafting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cyberharvest2300.data.local.AppDatabase
import com.example.cyberharvest2300.data.repository.CreatureProgressRepository
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.data.repository.WeaponProgressRepository
import com.example.cyberharvest2300.databinding.FragmentWeaponUpgradeBinding
import com.example.cyberharvest2300.domain.crafting.WeaponCraftingEngine
import com.example.cyberharvest2300.domain.crafting.WeaponUpgradeEngine
import com.example.cyberharvest2300.domain.unlock.UnlockConditionChecker
import com.example.cyberharvest2300.ui.crafting.adapter.WeaponCardAdapter
import com.example.cyberharvest2300.ui.crafting.adapter.WeaponCardUiModel
import com.example.cyberharvest2300.viewmodel.WeaponUpgradeViewModel
import com.example.cyberharvest2300.viewmodel.WeaponUpgradeViewModelFactory
import kotlinx.coroutines.launch

class WeaponUpgradeFragment : Fragment() {

    private var _binding: FragmentWeaponUpgradeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WeaponUpgradeViewModel
    private lateinit var adapter: WeaponCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeaponUpgradeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        observeViewModel()

        viewModel.loadWeapons()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())

        val weaponProgressRepository = WeaponProgressRepository(database.playerWeaponProgressDao())

        val inventoryRepository = InventoryRepository(
            inventoryDao = database.inventoryDao(),
            playerProfileDao = database.playerProfileDao()
        )

        val playerProfileRepository = PlayerProfileRepository(database.playerProfileDao())

        val creatureProgressRepository = CreatureProgressRepository(database.playerCreatureProgressDao())

        val unlockConditionChecker = UnlockConditionChecker(
            playerProfileRepository = playerProfileRepository,
            creatureProgressRepository = creatureProgressRepository,
            weaponProgressRepository = weaponProgressRepository
        )

        val weaponUpgradeEngine = WeaponUpgradeEngine(
            weaponProgressRepository = weaponProgressRepository,
            inventoryRepository = inventoryRepository
        )

        val weaponCraftingEngine = WeaponCraftingEngine(
            weaponProgressRepository = weaponProgressRepository,
            inventoryRepository = inventoryRepository,
            unlockConditionChecker = unlockConditionChecker
        )

        val factory = WeaponUpgradeViewModelFactory(
            weaponProgressRepository = weaponProgressRepository,
            inventoryRepository = inventoryRepository,
            weaponUpgradeEngine = weaponUpgradeEngine,
            weaponCraftingEngine = weaponCraftingEngine
        )

        viewModel = ViewModelProvider(this, factory)[WeaponUpgradeViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = WeaponCardAdapter(
            onCraft = { weaponId -> viewModel.craftWeapon(weaponId) },
            onUpgrade = { weaponId -> viewModel.upgradeWeapon(weaponId) },
            onEquip = { weaponId -> viewModel.equipWeapon(weaponId) }
        )

        binding.rvWeapons.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWeapons.adapter = adapter
        // Silah sayısı sabit ve az olduğu için setHasFixedSize gerekmiyor,
        // ama liste büyürse performans için açılabilir:
        // binding.rvWeapons.setHasFixedSize(true)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.weapons.collect { renderList() }
                }

                launch {
                    viewModel.weaponProgress.collect { renderList() }
                }

                launch {
                    viewModel.inventory.collect { renderList() }
                }

                launch {
                    viewModel.upgradeResult.collect { result ->
                        result ?: return@collect
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                        viewModel.clearUpgradeResult()
                    }
                }

                launch {
                    viewModel.craftResult.collect { result ->
                        result ?: return@collect
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                        viewModel.clearCraftResult()
                    }
                }
            }
        }
    }

    /**
     * weapons + weaponProgress + inventory state'lerini tek bir liste
     * modeline birleştirip adapter'a gönderir. DiffUtil sayesinde
     * sadece gerçekten değişen kartlar yeniden çizilir.
     */
    private fun renderList() {
        if (_binding == null) return

        val ownedQuantities: Map<String, Int> = viewModel.inventory.value
            .filter { !it.isSecured }
            .groupBy { it.itemId }
            .mapValues { (_, items) -> items.sumOf { it.quantity } }

        val items = viewModel.weapons.value.map { weapon ->
            WeaponCardUiModel(
                weapon = weapon,
                progress = viewModel.getProgress(weapon.id),
                ownedQuantities = ownedQuantities
            )
        }

        adapter.submitList(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
