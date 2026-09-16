package com.example.cyberharvest2300.ui.mainmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.data.local.AppDatabase
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.databinding.FragmentMainMenuBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var playerProfileRepository: PlayerProfileRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentMainMenuBinding.inflate(
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

        setupRepository()
        setupButtons()
        checkSaveGame()
    }

    private fun setupRepository() {

        val database =
            AppDatabase.getDatabase(
                requireContext()
            )

        playerProfileRepository =
            PlayerProfileRepository(
                database.playerProfileDao()
            )
    }

    private fun setupButtons() {

        binding.btnNewGame.setOnClickListener {

            findNavController().navigate(
                R.id.action_mainMenuFragment_to_characterCreationFragment
            )
        }

        binding.btnContinue.setOnClickListener {

            if (!binding.btnContinue.isEnabled) {
                return@setOnClickListener
            }

            findNavController().navigate(
                R.id.action_mainMenuFragment_to_hubFragment
            )
        }
    }

    private fun checkSaveGame() {

        viewLifecycleOwner.lifecycleScope.launch {

            val profile =
                playerProfileRepository
                    .getPlayerProfile()
                    .first()

            if (!isAdded) {
                return@launch
            }

            binding.btnContinue.isEnabled =
                profile != null
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}