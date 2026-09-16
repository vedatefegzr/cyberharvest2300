package com.example.cyberharvest2300.ui.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.databinding.FragmentCharacterCreationBinding

class CharacterCreationFragment : Fragment() {

    private var _binding: FragmentCharacterCreationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCharacterCreationBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {

            val characterName =
                binding.etCharacterName.text.toString().trim()

            val restaurantName =
                binding.etRestaurantName.text.toString().trim()

            if (characterName.isEmpty()) {
                binding.etCharacterName.error = "Enter your name"
                return@setOnClickListener
            }

            if (restaurantName.isEmpty()) {
                binding.etRestaurantName.error = "Enter restaurant name"
                return@setOnClickListener
            }

            val bundle = Bundle().apply {
                putString("characterName", characterName)
                putString("restaurantName", restaurantName)
            }

            findNavController().navigate(
                R.id.action_characterCreationFragment_to_loreFragment,
                bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}