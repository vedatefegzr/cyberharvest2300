
package com.example.cyberharvest2300.ui.inventory

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.data.game.ItemData
import com.example.cyberharvest2300.data.local.AppDatabase
import com.example.cyberharvest2300.data.local.entity.InventoryItem
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.databinding.FragmentInventoryBinding
import com.example.cyberharvest2300.viewmodel.InventoryViewModel
import com.example.cyberharvest2300.viewmodel.InventoryViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: InventoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentInventoryBinding.inflate(
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
        observeInventory()
        observeMoney()
    }

    private fun setupViewModel() {

        val database =
            AppDatabase.getDatabase(
                requireContext()
            )

        val inventoryRepository =
            InventoryRepository(
                inventoryDao =
                    database.inventoryDao(),

                playerProfileDao =
                    database.playerProfileDao()
            )

        val factory =
            InventoryViewModelFactory(
                inventoryRepository
            )

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[InventoryViewModel::class.java]
    }

    private fun observeInventory() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel
                    .getAllItems()
                    .collect { items ->

                        renderInventory(items)
                    }
            }
        }
    }

    private fun observeMoney() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                val database =
                    AppDatabase.getDatabase(
                        requireContext()
                    )

                PlayerProfileRepository(
                    database.playerProfileDao()
                )
                    .getPlayerProfile()
                    .collect { profile ->

                        binding.tvMoney.text =
                            "MONEY: ${profile?.money ?: 0} ₡"
                    }
            }
        }
    }

    private fun renderInventory(
        items: List<InventoryItem>
    ) {

        binding.inventoryContainer
            .removeAllViews()

        if (items.isEmpty()) {

            binding.tvEmpty.visibility =
                View.VISIBLE

            return
        }

        binding.tvEmpty.visibility =
            View.GONE

        items.forEach { item ->

            val itemData =
                ItemData.getById(
                    item.itemId
                )

            val row =
                LinearLayout(
                    requireContext()
                )

            row.orientation =
                LinearLayout.HORIZONTAL

            row.setPadding(
                0,
                20,
                0,
                20
            )

            val info =
                TextView(
                    requireContext()
                )

            info.text =
                buildString {

                    append(
                        itemData?.name
                            ?: "Unknown Item"
                    )

                    append(
                        " ×${item.quantity}"
                    )

                    append(
                        "\n${itemData?.type ?: "UNKNOWN"}"
                    )

                    append(
                        "\n${itemData?.rarity ?: "UNKNOWN"}"
                    )

                    if (item.isSecured) {
                        append("\nSECURED")
                    }
                }

            info.setTextColor(
                Color.WHITE
            )

            info.textSize =
                16f

            val infoParams =
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )

            row.addView(
                info,
                infoParams
            )

            if (!item.isSecured) {

                val sellButton =
                    Button(
                        requireContext()
                    )

                sellButton.text =
                    "SELL 1\n+${itemData?.sellValue ?: 0} ₡"

                sellButton.setOnClickListener {

                    val sellValue =
                        itemData?.sellValue
                            ?: 0

                    viewModel.sellOneItem(
                        item = item,
                        sellValue = sellValue
                    ) { success ->

                        if (!success) {
                            return@sellOneItem
                        }
                    }
                }

                row.addView(
                    sellButton
                )
            }

            binding.inventoryContainer
                .addView(row)

            val divider =
                View(
                    requireContext()
                )

            divider.setBackgroundColor(
                Color.DKGRAY
            )

            binding.inventoryContainer
                .addView(
                    divider,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    )
                )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

