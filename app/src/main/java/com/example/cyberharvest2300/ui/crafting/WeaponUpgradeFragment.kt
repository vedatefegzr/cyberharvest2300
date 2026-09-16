package com.example.cyberharvest2300.ui.crafting

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.cyberharvest2300.data.game.ItemData
import com.example.cyberharvest2300.data.game.UnlockCondition
import com.example.cyberharvest2300.data.local.AppDatabase
import com.example.cyberharvest2300.data.repository.CreatureProgressRepository
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.data.repository.WeaponProgressRepository
import com.example.cyberharvest2300.databinding.FragmentWeaponUpgradeBinding
import com.example.cyberharvest2300.domain.crafting.WeaponCraftingEngine
import com.example.cyberharvest2300.domain.crafting.WeaponUpgradeEngine
import com.example.cyberharvest2300.domain.unlock.UnlockConditionChecker
import com.example.cyberharvest2300.viewmodel.WeaponUpgradeViewModel
import com.example.cyberharvest2300.viewmodel.WeaponUpgradeViewModelFactory
import kotlinx.coroutines.launch

class WeaponUpgradeFragment : Fragment() {

    private var _binding: FragmentWeaponUpgradeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WeaponUpgradeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentWeaponUpgradeBinding.inflate(
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
        observeViewModel()

        viewModel.loadWeapons()
    }

    private fun setupViewModel() {

        val database =
            AppDatabase.getDatabase(
                requireContext()
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

        val playerProfileRepository =
            PlayerProfileRepository(
                database.playerProfileDao()
            )

        val creatureProgressRepository =
            CreatureProgressRepository(
                database.playerCreatureProgressDao()
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

        val weaponUpgradeEngine =
            WeaponUpgradeEngine(
                weaponProgressRepository =
                    weaponProgressRepository,

                inventoryRepository =
                    inventoryRepository
            )

        val weaponCraftingEngine =
            WeaponCraftingEngine(
                weaponProgressRepository =
                    weaponProgressRepository,

                inventoryRepository =
                    inventoryRepository,

                unlockConditionChecker =
                    unlockConditionChecker
            )

        val factory =
            WeaponUpgradeViewModelFactory(
                weaponProgressRepository =
                    weaponProgressRepository,

                inventoryRepository =
                    inventoryRepository,

                weaponUpgradeEngine =
                    weaponUpgradeEngine,

                weaponCraftingEngine =
                    weaponCraftingEngine
            )

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[WeaponUpgradeViewModel::class.java]
    }

    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                launch {
                    viewModel.weapons.collect {
                        renderWeapons()
                    }
                }

                launch {
                    viewModel.weaponProgress.collect {
                        renderWeapons()
                    }
                }

                launch {
                    viewModel.inventory.collect {
                        renderWeapons()
                    }
                }

                launch {
                    viewModel.upgradeResult.collect { result ->

                        result ?: return@collect

                        Toast.makeText(
                            requireContext(),
                            result.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        viewModel.clearUpgradeResult()
                    }
                }

                launch {
                    viewModel.craftResult.collect { result ->

                        result ?: return@collect

                        Toast.makeText(
                            requireContext(),
                            result.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        viewModel.clearCraftResult()
                    }
                }
            }
        }
    }

    private fun renderWeapons() {

        if (_binding == null) {
            return
        }

        if (binding.weaponContainer.childCount > 1) {

            binding.weaponContainer.removeViews(
                1,
                binding.weaponContainer.childCount - 1
            )
        }

        viewModel.weapons.value.forEach { weapon ->

            val progress =
                viewModel.getProgress(
                    weapon.id
                )

            val card =
                LinearLayout(
                    requireContext()
                )

            card.orientation =
                LinearLayout.VERTICAL

            card.setPadding(
                20,
                20,
                20,
                20
            )

            val name =
                TextView(
                    requireContext()
                )

            name.text =
                weapon.name

            name.textSize = 20f
            name.setTextColor(Color.WHITE)

            card.addView(name)

            /*
             * WEAPON SAHİP DEĞİL
             */
            if (
                progress == null ||
                !progress.isOwned
            ) {

                val locked =
                    TextView(
                        requireContext()
                    )

                locked.text =
                    "NOT OWNED"

                locked.textSize =
                    16f

                locked.setTextColor(
                    Color.LTGRAY
                )

                card.addView(
                    locked
                )

                weapon.unlockConditions.forEach { condition ->

                    val requirement =
                        TextView(
                            requireContext()
                        )

                    requirement.text =
                        when (condition) {

                            is UnlockCondition.MinReputation ->
                                "Required Reputation: ${condition.value}"

                            is UnlockCondition.MinMoney ->
                                "Required Money: ${condition.value}₡"

                            is UnlockCondition.MinRestaurantLevel ->
                                "Required Restaurant Level: ${condition.value}"

                            is UnlockCondition.CreatureKilled ->
                                "Required Kills: ${condition.times}"

                            is UnlockCondition.WeaponLevel ->
                                "Required Weapon Level: ${condition.level}"
                        }

                    requirement.textSize =
                        15f

                    requirement.setTextColor(
                        Color.LTGRAY
                    )

                    card.addView(
                        requirement
                    )
                }

                if (weapon.craftCost.isNotEmpty()) {

                    val costTitle =
                        TextView(
                            requireContext()
                        )

                    costTitle.text =
                        "CRAFT COST"

                    costTitle.textSize =
                        16f

                    costTitle.setPadding(
                        0,
                        12,
                        0,
                        0
                    )

                    card.addView(
                        costTitle
                    )

                    var canCraft =
                        true

                    weapon.craftCost.forEach { ingredient ->

                        val item =
                            ItemData.getById(
                                ingredient.itemId
                            )

                        val currentQuantity =
                            viewModel.getItemQuantity(
                                ingredient.itemId
                            )

                        if (
                            currentQuantity <
                            ingredient.quantity
                        ) {
                            canCraft = false
                        }

                        val cost =
                            TextView(
                                requireContext()
                            )

                        cost.text =
                            "${item?.name ?: ingredient.itemId}: " +
                                    "$currentQuantity / " +
                                    ingredient.quantity

                        cost.textSize =
                            15f

                        cost.setTextColor(
                            if (
                                currentQuantity >=
                                ingredient.quantity
                            ) {
                                Color.GREEN
                            } else {
                                Color.RED
                            }
                        )

                        card.addView(cost)
                    }

                    val craftButton =
                        Button(
                            requireContext()
                        )

                    craftButton.text =
                        "CRAFT"

                    craftButton.isEnabled =
                        canCraft

                    craftButton.setOnClickListener {

                        craftButton.isEnabled =
                            false

                        viewModel.craftWeapon(
                            weapon.id
                        )
                    }

                    card.addView(
                        craftButton
                    )
                }

            } else {

                /*
                 * WEAPON SAHİP
                 */

                val level =
                    TextView(
                        requireContext()
                    )

                level.text =
                    "Level: ${progress.level}"

                level.textSize =
                    16f

                level.setTextColor(
                    Color.WHITE
                )

                card.addView(level)

                val currentLevel =
                    progress.level

                val currentAttack =
                    weapon.baseAttack +
                            (currentLevel - 1) * 2

                val attack =
                    TextView(
                        requireContext()
                    )

                attack.text =
                    "Attack: $currentAttack"

                attack.textSize =
                    16f

                attack.setTextColor(
                    Color.CYAN
                )

                card.addView(attack)

                val equipButton =
                    Button(
                        requireContext()
                    )

                equipButton.text =
                    if (progress.isEquipped) {
                        "EQUIPPED"
                    } else {
                        "EQUIP"
                    }

                equipButton.isEnabled =
                    !progress.isEquipped

                equipButton.setOnClickListener {

                    equipButton.isEnabled =
                        false

                    viewModel.equipWeapon(
                        weapon.id
                    )
                }

                card.addView(
                    equipButton
                )

                /*
                 * MAX LEVEL
                 */

                if (
                    currentLevel >=
                    weapon.maxLevel
                ) {

                    val maxLevel =
                        TextView(
                            requireContext()
                        )

                    maxLevel.text =
                        "MAX LEVEL"

                    maxLevel.textSize =
                        16f

                    maxLevel.setTextColor(
                        Color.YELLOW
                    )

                    card.addView(
                        maxLevel
                    )

                } else {

                    /*
                     * NEXT LEVEL COST
                     */

                    val nextLevel =
                        currentLevel + 1

                    val upgradeCost =
                        weapon.upgradeCosts[
                            nextLevel
                        ]

                    if (upgradeCost != null) {

                        val costTitle =
                            TextView(
                                requireContext()
                            )

                        costTitle.text =
                            "UPGRADE COST"

                        costTitle.textSize =
                            16f

                        costTitle.setPadding(
                            0,
                            12,
                            0,
                            0
                        )

                        card.addView(
                            costTitle
                        )

                        var canUpgrade =
                            true

                        upgradeCost.forEach { ingredient ->

                            val item =
                                ItemData.getById(
                                    ingredient.itemId
                                )

                            val currentQuantity =
                                viewModel.getItemQuantity(
                                    ingredient.itemId
                                )

                            if (
                                currentQuantity <
                                ingredient.quantity
                            ) {
                                canUpgrade = false
                            }

                            val cost =
                                TextView(
                                    requireContext()
                                )

                            cost.text =
                                "${item?.name ?: ingredient.itemId}: " +
                                        "$currentQuantity / " +
                                        ingredient.quantity

                            cost.textSize =
                                15f

                            cost.setTextColor(
                                if (
                                    currentQuantity >=
                                    ingredient.quantity
                                ) {
                                    Color.GREEN
                                } else {
                                    Color.RED
                                }
                            )

                            card.addView(cost)
                        }

                        val upgradeButton =
                            Button(
                                requireContext()
                            )

                        upgradeButton.text =
                            "UPGRADE TO LV.$nextLevel"

                        upgradeButton.isEnabled =
                            canUpgrade

                        upgradeButton.setOnClickListener {

                            upgradeButton.isEnabled =
                                false

                            viewModel.upgradeWeapon(
                                weapon.id
                            )
                        }

                        card.addView(
                            upgradeButton
                        )
                    }
                }
            }

            val divider =
                View(
                    requireContext()
                )

            divider.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
                ).apply {
                    topMargin = 24
                    bottomMargin = 24
                }

            card.addView(divider)

            binding.weaponContainer.addView(
                card
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}