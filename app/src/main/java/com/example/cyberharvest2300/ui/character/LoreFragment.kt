package com.example.cyberharvest2300.ui.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.databinding.FragmentLoreBinding

class LoreFragment : Fragment() {

    private var _binding: FragmentLoreBinding? = null
    private val binding get() = _binding!!

    private var characterName: String = ""
    private var restaurantName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        characterName =
            arguments?.getString("characterName") ?: ""

        restaurantName =
            arguments?.getString("restaurantName") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoreBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnContinue.setOnClickListener {

            val bundle = Bundle().apply {
                putString("characterName", characterName)
                putString("restaurantName", restaurantName)
            }

            findNavController().navigate(
                R.id.action_loreFragment_to_classSelectionFragment,
                bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}