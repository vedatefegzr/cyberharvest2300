package com.example.cyberharvest2300.ui.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.data.local.AppDatabase
import com.example.cyberharvest2300.data.repository.GameResetRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.databinding.FragmentClassSelectionBinding
import com.example.cyberharvest2300.viewmodel.PlayerProfileViewModel
import com.example.cyberharvest2300.viewmodel.PlayerProfileViewModelFactory
class ClassSelectionFragment : Fragment() {

    private var _binding: FragmentClassSelectionBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var viewModel: PlayerProfileViewModel

    private var characterName: String = ""
    private var restaurantName: String = ""

    private var selectedClass: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        characterName =
            arguments?.getString(
                "characterName"
            ) ?: ""

        restaurantName =
            arguments?.getString(
                "restaurantName"
            ) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentClassSelectionBinding.inflate(
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
        setupClassSelection()
        setupStartGame()
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

        val gameResetRepository =
            GameResetRepository(
                database
            )

        val factory =
            PlayerProfileViewModelFactory(
                repository =
                    playerProfileRepository,

                gameResetRepository =
                    gameResetRepository
            )

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[PlayerProfileViewModel::class.java]
    }

    private fun setupClassSelection() {

        binding.btnChef.setOnClickListener {

            selectedClass =
                "CHEF"
        }

        binding.btnHunter.setOnClickListener {

            selectedClass =
                "HUNTER"
        }

        binding.btnMerchant.setOnClickListener {

            selectedClass =
                "MERCHANT"
        }
    }

    private fun setupStartGame() {

        binding.btnStartGame
            .setOnClickListener {

                val selected =
                    selectedClass

                if (selected == null) {

                    binding.btnStartGame.error =
                        "Choose your starting origin first"

                    return@setOnClickListener
                }

                binding.btnStartGame.isEnabled =
                    false

                viewModel.savePlayerProfile(
                    characterName =
                        characterName,

                    restaurantName =
                        restaurantName,

                    startingClass =
                        selected
                ) { success ->

                    if (!isAdded) {
                        return@savePlayerProfile
                    }

                    if (!success) {

                        binding.btnStartGame.isEnabled =
                            true

                        binding.btnStartGame.error =
                            "Could not start new game."

                        return@savePlayerProfile
                    }

                    findNavController().navigate(
                        R.id.action_classSelectionFragment_to_tutorialFragment
                    )
                }
            }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}