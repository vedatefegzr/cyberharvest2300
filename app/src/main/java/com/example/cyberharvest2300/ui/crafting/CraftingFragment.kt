package com.example.cyberharvest2300.ui.crafting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.databinding.FragmentCraftingBinding

class CraftingFragment : Fragment() {

    private var _binding: FragmentCraftingBinding? = null

    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentCraftingBinding.inflate(
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

        binding.btnWeaponWorkshop.setOnClickListener {

            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.hubContainer,
                    WeaponUpgradeFragment()
                )
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}