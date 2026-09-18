package com.example.cyberharvest2300.ui.bestiary

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
import com.example.cyberharvest2300.data.local.AppDatabase
import com.example.cyberharvest2300.data.repository.CreatureProgressRepository
import com.example.cyberharvest2300.databinding.FragmentBestiaryBinding
import com.example.cyberharvest2300.ui.bestiary.adapter.CreatureCardAdapter
import com.example.cyberharvest2300.ui.bestiary.adapter.CreatureCardUiModel
import com.example.cyberharvest2300.viewmodel.BestiaryViewModel
import com.example.cyberharvest2300.viewmodel.BestiaryViewModelFactory
import kotlinx.coroutines.launch

class BestiaryFragment : Fragment() {

    private var _binding: FragmentBestiaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BestiaryViewModel
    private lateinit var adapter: CreatureCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBestiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        observeBestiary()

        viewModel.loadBestiary()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = CreatureProgressRepository(database.playerCreatureProgressDao())
        val factory = BestiaryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[BestiaryViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = CreatureCardAdapter()
        binding.rvCreatures.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCreatures.adapter = adapter
    }

    private fun observeBestiary() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.creatures.collect { renderList() } }
                launch { viewModel.progress.collect { renderList() } }
            }
        }
    }

    private fun renderList() {
        if (_binding == null) return

        val items = viewModel.creatures.value.map { creature ->
            CreatureCardUiModel(
                creature = creature,
                progress = viewModel.getProgress(creature.id)
            )
        }

        adapter.submitList(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
