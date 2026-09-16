
package com.example.cyberharvest2300.ui.hub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.data.local.AppDatabase
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.databinding.FragmentHubBinding
import com.example.cyberharvest2300.ui.bestiary.BestiaryFragment
import com.example.cyberharvest2300.ui.crafting.CraftingFragment
import com.example.cyberharvest2300.ui.hunting.HuntingFragment
import com.example.cyberharvest2300.ui.inventory.InventoryFragment
import com.example.cyberharvest2300.ui.restaurant.RestaurantFragment
import com.example.cyberharvest2300.viewmodel.DayCycleViewModel
import com.example.cyberharvest2300.viewmodel.DayCycleViewModelFactory
import kotlinx.coroutines.launch

class HubFragment : Fragment() {

    private var _binding: FragmentHubBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var dayCycleViewModel:
            DayCycleViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentHubBinding.inflate(
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

        setupDayCycleViewModel()
        setupBottomNavigation()
        observeDayCycle()

        if (savedInstanceState == null) {
            openRestaurant()
        }
    }

    private fun setupDayCycleViewModel() {

        val database =
            AppDatabase.getDatabase(
                requireContext()
            )

        val playerProfileRepository =
            PlayerProfileRepository(
                database.playerProfileDao()
            )

        val factory =
            DayCycleViewModelFactory(
                playerProfileRepository
            )

        dayCycleViewModel =
            ViewModelProvider(
                this,
                factory
            )[DayCycleViewModel::class.java]
    }

    private fun setupBottomNavigation() {

        binding.bottomNavigation
            .setOnItemSelectedListener { item ->

                when (item.itemId) {

                    R.id.nav_restaurant -> {

                        if (!dayCycleViewModel.isDay()) {
                            false
                        } else {
                            openRestaurant()
                            true
                        }
                    }

                    R.id.nav_inventory -> {

                        openInventory()
                        true
                    }

                    R.id.nav_crafting -> {

                        if (!dayCycleViewModel.isDay()) {
                            false
                        } else {
                            openCrafting()
                            true
                        }
                    }

                    R.id.nav_bestiary -> {

                        openBestiary()
                        true
                    }

                    R.id.nav_hunting -> {

                        if (!dayCycleViewModel.isNight()) {
                            false
                        } else {
                            openHunting()
                            true
                        }
                    }

                    else -> false
                }
            }

        binding.bottomNavigation.selectedItemId =
            R.id.nav_restaurant
    }

    private fun observeDayCycle() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                dayCycleViewModel.phase.collect { phase ->

                    val isDay =
                        phase == "DAY"

                    val menu =
                        binding.bottomNavigation.menu

                    menu.findItem(
                        R.id.nav_restaurant
                    ).isEnabled = isDay

                    menu.findItem(
                        R.id.nav_crafting
                    ).isEnabled = isDay

                    menu.findItem(
                        R.id.nav_hunting
                    ).isEnabled = !isDay

                    if (
                        !isDay &&
                        binding.bottomNavigation
                            .selectedItemId ==
                        R.id.nav_restaurant
                    ) {

                        binding.bottomNavigation
                            .selectedItemId =
                            R.id.nav_hunting
                    }

                    if (
                        isDay &&
                        binding.bottomNavigation
                            .selectedItemId ==
                        R.id.nav_hunting
                    ) {

                        binding.bottomNavigation
                            .selectedItemId =
                            R.id.nav_restaurant
                    }
                }
            }
        }
    }

    /*
     * Her yeni Hub sekmesine geçerken
     * eski child back stack temizlenir.
     *
     * Böylece WeaponUpgrade gibi
     * geçici ekranlar eski sekmeye geri dönmez.
     */
    private fun clearChildBackStack() {

        childFragmentManager
            .popBackStackImmediate()
    }

    fun openRestaurant() {

        clearChildBackStack()

        childFragmentManager
            .beginTransaction()
            .replace(
                R.id.hubContainer,
                RestaurantFragment()
            )
            .commit()
    }

    private fun openInventory() {

        clearChildBackStack()

        childFragmentManager
            .beginTransaction()
            .replace(
                R.id.hubContainer,
                InventoryFragment()
            )
            .commit()
    }

    private fun openCrafting() {

        clearChildBackStack()

        childFragmentManager
            .beginTransaction()
            .replace(
                R.id.hubContainer,
                CraftingFragment()
            )
            .commit()
    }

    private fun openBestiary() {

        clearChildBackStack()

        childFragmentManager
            .beginTransaction()
            .replace(
                R.id.hubContainer,
                BestiaryFragment()
            )
            .commit()
    }

    fun openHunting() {

        clearChildBackStack()

        childFragmentManager
            .beginTransaction()
            .replace(
                R.id.hubContainer,
                HuntingFragment()
            )
            .commit()
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}

