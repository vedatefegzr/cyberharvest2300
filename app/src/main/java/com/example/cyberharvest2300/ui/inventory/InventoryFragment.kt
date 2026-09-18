package com.example.cyberharvest2300.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.data.local.AppDatabase
import com.example.cyberharvest2300.data.local.entity.InventoryItem
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.databinding.FragmentInventoryBinding
import com.example.cyberharvest2300.ui.inventory.adapter.InventoryAdapter
import com.example.cyberharvest2300.viewmodel.InventoryViewModel
import com.example.cyberharvest2300.viewmodel.InventoryViewModelFactory
import kotlinx.coroutines.launch

class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        observeInventory()
        observeMoney()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val inventoryRepository = InventoryRepository(
            inventoryDao = database.inventoryDao(),
            playerProfileDao = database.playerProfileDao()
        )
        val factory = InventoryViewModelFactory(inventoryRepository)
        viewModel = ViewModelProvider(this, factory)[InventoryViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = InventoryAdapter(
            onSell = { item ->
                val itemData = com.example.cyberharvest2300.data.game.ItemData.getById(item.itemId)
                val sellValue = itemData?.sellValue ?: 0
                viewModel.sellOneItem(item = item, sellValue = sellValue) { /* state akışıyla zaten güncellenir */ }
            }
        )
        binding.rvInventory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInventory.adapter = adapter
    }

    private fun observeInventory() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getAllItems().collect { items -> renderInventory(items) }
            }
        }
    }

    private fun observeMoney() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val database = AppDatabase.getDatabase(requireContext())
                PlayerProfileRepository(database.playerProfileDao())
                    .getPlayerProfile()
                    .collect { profile ->
                        binding.tvMoney.text = getString(
                            R.string.inventory_money_format,
                            profile?.money ?: 0
                        )
                    }
            }
        }
    }

    private fun renderInventory(items: List<InventoryItem>) {
        if (_binding == null) return

        binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        binding.rvInventory.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE

        adapter.submitList(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
