package com.example.cyberharvest2300.ui.restaurant

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
import com.example.cyberharvest2300.data.game.ItemData
import com.example.cyberharvest2300.data.game.ItemType
import com.example.cyberharvest2300.data.game.Recipe
import com.example.cyberharvest2300.data.game.RestaurantData
import com.example.cyberharvest2300.data.game.RestaurantOrder
import com.example.cyberharvest2300.data.local.AppDatabase
import com.example.cyberharvest2300.data.local.entity.PlayerProfile
import com.example.cyberharvest2300.data.repository.CreatureProgressRepository
import com.example.cyberharvest2300.data.repository.CustomerProgressRepository
import com.example.cyberharvest2300.data.repository.DailyOrderRepository
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.data.repository.WeaponProgressRepository
import com.example.cyberharvest2300.databinding.FragmentRestaurantBinding
import com.example.cyberharvest2300.domain.restaurant.RestaurantProgressionEngine
import com.example.cyberharvest2300.domain.unlock.UnlockConditionChecker
import com.example.cyberharvest2300.ui.hub.HubFragment
import com.example.cyberharvest2300.ui.restaurant.adapter.RecipeAdapter
import com.example.cyberharvest2300.ui.restaurant.adapter.RecipeCardUiModel
import com.example.cyberharvest2300.viewmodel.DayCycleViewModel
import com.example.cyberharvest2300.viewmodel.DayCycleViewModelFactory
import com.example.cyberharvest2300.viewmodel.RestaurantViewModel
import com.example.cyberharvest2300.viewmodel.RestaurantViewModelFactory
import kotlinx.coroutines.launch

class RestaurantFragment : Fragment() {

    private var _binding: FragmentRestaurantBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RestaurantViewModel
    private lateinit var dayCycleViewModel: DayCycleViewModel
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupDayCycleViewModel()
        setupRecipeRecyclerView()
        setupButtons()
        observeViewModel()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())

        val inventoryRepository = InventoryRepository(
            inventoryDao = database.inventoryDao(),
            playerProfileDao = database.playerProfileDao()
        )
        val playerProfileRepository = PlayerProfileRepository(database.playerProfileDao())
        val creatureProgressRepository = CreatureProgressRepository(database.playerCreatureProgressDao())
        val weaponProgressRepository = WeaponProgressRepository(database.playerWeaponProgressDao())
        val dailyOrderRepository = DailyOrderRepository(database.dailyOrderDao())
        val customerProgressRepository = CustomerProgressRepository(database.customerProgressDao())

        val unlockConditionChecker = UnlockConditionChecker(
            playerProfileRepository = playerProfileRepository,
            creatureProgressRepository = creatureProgressRepository,
            weaponProgressRepository = weaponProgressRepository
        )

        val factory = RestaurantViewModelFactory(
            inventoryRepository = inventoryRepository,
            playerProfileRepository = playerProfileRepository,
            unlockConditionChecker = unlockConditionChecker,
            dailyOrderRepository = dailyOrderRepository,
            customerProgressRepository = customerProgressRepository
        )

        viewModel = ViewModelProvider(this, factory)[RestaurantViewModel::class.java]
    }

    private fun setupDayCycleViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = PlayerProfileRepository(database.playerProfileDao())
        val factory = DayCycleViewModelFactory(repository)
        dayCycleViewModel = ViewModelProvider(this, factory)[DayCycleViewModel::class.java]
    }

    private fun setupRecipeRecyclerView() {
        recipeAdapter = RecipeAdapter(onSelect = { recipeId -> viewModel.selectRecipe(recipeId) })
        binding.rvRecipes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecipes.adapter = recipeAdapter
    }

    private fun setupButtons() {
        binding.btnCook.setOnClickListener { viewModel.cookSelectedRecipe() }

        binding.btnNewCustomer.setOnClickListener { viewModel.createOrder() }

        binding.btnServe.setOnClickListener {
            val order = viewModel.orders.value.firstOrNull()
            if (order != null) viewModel.serveOrder(order)
        }

        binding.btnCloseRestaurant.setOnClickListener {
            if (!viewModel.isRestaurantOpen.value) return@setOnClickListener

            binding.btnCloseRestaurant.isEnabled = false
            binding.tvRestaurantInfo.text = "Closing restaurant..."

            dayCycleViewModel.startNight { success ->
                if (!isAdded) return@startNight

                if (!success) {
                    binding.btnCloseRestaurant.isEnabled = true
                    binding.tvRestaurantInfo.text = "Could not start the night."
                    return@startNight
                }

                binding.tvRestaurantInfo.text = "Night has started. Hunting time."

                val parent = parentFragment
                if (parent is HubFragment) parent.openHunting()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.playerProfile.collect { profile ->
                        renderPlayerStats(profile)
                        renderRestaurantState(profile?.timePhase == "DAY")
                    }
                }

                launch { viewModel.recipes.collect { renderRecipes() } }

                launch {
                    viewModel.selectedRecipeId.collect {
                        renderSelectedRecipe()
                        renderRecipes()
                    }
                }

                launch { viewModel.inventory.collect { renderFoodInventory() } }

                launch { viewModel.orders.collect { orders -> renderOrders(orders) } }

                launch {
                    viewModel.message.collect { message ->
                        binding.tvRestaurantInfo.text = message
                    }
                }

                launch {
                    viewModel.cookResult.collect { result ->
                        if (result != null) {
                            renderFoodInventory()
                            binding.tvRestaurantInfo.text = result.message
                            viewModel.clearCookResult()
                        }
                    }
                }

                launch {
                    viewModel.serveResult.collect { result ->
                        if (result != null) {
                            if (result.moneyEarned > 0) {
                                binding.tvReward.text = "Last reward: +${result.moneyEarned} ₡"
                            }
                            if (result.reputationEarned > 0) {
                                binding.tvReputationReward.text = "Last reputation: +${result.reputationEarned}"
                            }
                            if (result.restaurantXpEarned > 0) {
                                binding.tvRestaurantInfo.text =
                                    result.message + "  +" + result.restaurantXpEarned + " Restaurant XP"

                                if (result.restaurantLeveledUp) {
                                    binding.tvRestaurantInfo.text = result.message + "\nRESTAURANT LEVEL UP!"
                                }
                            }
                            viewModel.clearServeResult()
                        }
                    }
                }
            }
        }
    }

    /**
     * Tarif kartlarını (RecyclerView) günceller. Eski kodda burada elle
     * LinearLayout/TextView/Button inşa eden ~150 satırlık bir blok vardı;
     * artık sadece state -> UI model dönüşümü yapıp adapter'a veriyoruz.
     */
    private fun renderRecipes() {
        if (_binding == null) return

        val recipes = viewModel.recipes.value

        if (recipes.isEmpty()) {
            binding.rvRecipes.visibility = View.GONE
            binding.tvNoRecipes.visibility = View.VISIBLE
            binding.btnCook.isEnabled = false
            return
        }

        binding.rvRecipes.visibility = View.VISIBLE
        binding.tvNoRecipes.visibility = View.GONE

        val selectedId = viewModel.selectedRecipeId.value
        val items = recipes.map { recipe ->
            RecipeCardUiModel(recipe = recipe, isSelected = recipe.id == selectedId)
        }

        recipeAdapter.submitList(items)
    }

    private fun renderSelectedRecipe() {
        val recipe = viewModel.getRecipe(viewModel.selectedRecipeId.value ?: "")

        if (recipe == null) {
            binding.tvSelectedRecipe.text = "SELECTED: None"
            binding.btnCook.isEnabled = false
            return
        }

        binding.tvSelectedRecipe.text = "SELECTED: ${recipe.name}"
        binding.btnCook.isEnabled = viewModel.isRestaurantOpen.value
    }

    private fun renderFoodInventory() {
        if (_binding == null) return

        val foodItems = viewModel.inventory.value.filter { item ->
            !item.isSecured && ItemData.getById(item.itemId)?.type == ItemType.FOOD
        }

        binding.tvFoodStock.text = if (foodItems.isEmpty()) {
            "No prepared food."
        } else {
            buildString {
                foodItems.forEach { item ->
                    val data = ItemData.getById(item.itemId)
                    append("${data?.name ?: item.itemId}")
                    append("    x")
                    append(item.quantity)
                    append("\n")
                }
            }
        }
    }

    private fun renderPlayerStats(profile: PlayerProfile?) {
        if (profile == null) {
            binding.tvPlayerStats.text = "Player profile not found."
            return
        }

        val capacity = RestaurantProgressionEngine.getCustomerCapacity(profile.restaurantLevel)

        binding.tvPlayerStats.text =
            "Day ${profile.day}    Money: ${profile.money} ₡    Reputation: ${profile.reputation}\n" +
                "Restaurant Lv.${profile.restaurantLevel}    XP: ${profile.restaurantXp}    Customers: $capacity"
    }

    private fun renderRestaurantState(isOpen: Boolean) {
        binding.tvStatus.text = if (isOpen) "OPEN — DAY" else "CLOSED — NIGHT"
        binding.btnCook.isEnabled = isOpen && viewModel.selectedRecipeId.value != null
        binding.btnNewCustomer.isEnabled = isOpen
        binding.btnCloseRestaurant.isEnabled = isOpen
        binding.btnServe.isEnabled = isOpen && viewModel.orders.value.isNotEmpty()
    }

    private fun renderOrders(orders: List<RestaurantOrder>) {
        if (orders.isEmpty()) {
            binding.tvOrder.text = "No active orders."
            binding.btnServe.isEnabled = false
            return
        }

        val firstOrder = orders.first()
        val customer = RestaurantData.getCustomer(firstOrder.customerId)
        val recipe = viewModel.getRecipe(firstOrder.recipeId)

        binding.tvOrder.text =
            "ACTIVE CUSTOMERS: ${orders.size}\n\n" +
                "CUSTOMER\n\n${customer?.name ?: "Unknown"}\n\n" +
                "Wants: ${recipe?.name ?: "Unknown"}\n\n" +
                "Reward: ${firstOrder.reward} ₡\n" +
                "Reputation: +${firstOrder.reputationReward}"

        binding.btnServe.isEnabled = viewModel.isRestaurantOpen.value
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
