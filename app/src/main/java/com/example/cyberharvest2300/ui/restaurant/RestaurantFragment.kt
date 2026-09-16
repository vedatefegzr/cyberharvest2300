package com.example.cyberharvest2300.ui.restaurant
import com.example.cyberharvest2300.data.game.RestaurantData
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
import com.example.cyberharvest2300.data.game.ItemData
import com.example.cyberharvest2300.data.game.Recipe
import com.example.cyberharvest2300.data.game.RestaurantOrder
import com.example.cyberharvest2300.data.local.AppDatabase
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
import com.example.cyberharvest2300.viewmodel.DayCycleViewModel
import com.example.cyberharvest2300.viewmodel.DayCycleViewModelFactory
import com.example.cyberharvest2300.viewmodel.RestaurantViewModel
import com.example.cyberharvest2300.viewmodel.RestaurantViewModelFactory
import kotlinx.coroutines.launch

class RestaurantFragment : Fragment() {

    private var _binding:
            FragmentRestaurantBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var viewModel:
            RestaurantViewModel

    private lateinit var dayCycleViewModel:
            DayCycleViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentRestaurantBinding.inflate(
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
        setupDayCycleViewModel()
        setupButtons()
        observeViewModel()
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

        val playerProfileRepository =
            PlayerProfileRepository(
                database.playerProfileDao()
            )

        val creatureProgressRepository =
            CreatureProgressRepository(
                database.playerCreatureProgressDao()
            )

        val weaponProgressRepository =
            WeaponProgressRepository(
                database.playerWeaponProgressDao()
            )

        val dailyOrderRepository =
            DailyOrderRepository(
                database.dailyOrderDao()
            )

        val customerProgressRepository =
            CustomerProgressRepository(
                database.customerProgressDao()
            )

        val unlockConditionChecker =
            UnlockConditionChecker(
                playerProfileRepository =
                    playerProfileRepository,

                creatureProgressRepository =
                    creatureProgressRepository,

                weaponProgressRepository =
                    weaponProgressRepository
            )

        val factory =
            RestaurantViewModelFactory(
                inventoryRepository =
                    inventoryRepository,

                playerProfileRepository =
                    playerProfileRepository,

                unlockConditionChecker =
                    unlockConditionChecker,

                dailyOrderRepository =
                    dailyOrderRepository,

                customerProgressRepository =
                    customerProgressRepository
            )

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[RestaurantViewModel::class.java]
    }

    private fun setupDayCycleViewModel() {

        val database =
            AppDatabase.getDatabase(
                requireContext()
            )

        val repository =
            PlayerProfileRepository(
                database.playerProfileDao()
            )

        val factory =
            DayCycleViewModelFactory(
                repository
            )

        dayCycleViewModel =
            ViewModelProvider(
                this,
                factory
            )[DayCycleViewModel::class.java]
    }

    private fun setupButtons() {

        binding.btnCook.setOnClickListener {
            viewModel.cookSelectedRecipe()
        }

        binding.btnNewCustomer.setOnClickListener {
            viewModel.createOrder()
        }

        binding.btnServe.setOnClickListener {

            val order =
                viewModel.orders.value.firstOrNull()

            if (order != null) {
                viewModel.serveOrder(order)
            }
        }

        binding.btnCloseRestaurant.setOnClickListener {

            if (
                !viewModel.isRestaurantOpen.value
            ) {
                return@setOnClickListener
            }

            binding.btnCloseRestaurant.isEnabled =
                false

            binding.tvRestaurantInfo.text =
                "Closing restaurant..."

            dayCycleViewModel.startNight { success ->

                if (!isAdded) {
                    return@startNight
                }

                if (!success) {

                    binding.btnCloseRestaurant
                        .isEnabled = true

                    binding.tvRestaurantInfo.text =
                        "Could not start the night."

                    return@startNight
                }

                binding.tvRestaurantInfo.text =
                    "Night has started. Hunting time."

                val parent =
                    parentFragment

                if (parent is HubFragment) {
                    parent.openHunting()
                }
            }
        }
    }

    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                launch {

                    viewModel.playerProfile
                        .collect { profile ->

                            renderPlayerStats(
                                profile
                            )

                            renderRestaurantState(
                                profile?.timePhase ==
                                        "DAY"
                            )
                        }
                }

                launch {

                    viewModel.recipes
                        .collect { recipes ->

                            renderRecipes(
                                recipes
                            )
                        }
                }

                launch {

                    viewModel.selectedRecipeId
                        .collect {

                            renderSelectedRecipe()

                            renderRecipes(
                                viewModel.recipes.value
                            )
                        }
                }

                launch {

                    viewModel.inventory
                        .collect {

                            renderFoodInventory()
                        }
                }

                launch {

                    viewModel.orders
                        .collect { orders ->

                            renderOrders(
                                orders
                            )
                        }
                }

                launch {

                    viewModel.message
                        .collect { message ->

                            binding.tvRestaurantInfo
                                .text = message
                        }
                }

                launch {

                    viewModel.cookResult
                        .collect { result ->

                            if (result != null) {

                                renderFoodInventory()

                                binding.tvRestaurantInfo
                                    .text =
                                    result.message

                                viewModel
                                    .clearCookResult()
                            }
                        }
                }

                launch {

                    viewModel.serveResult
                        .collect { result ->

                            if (result != null) {

                                if (
                                    result.moneyEarned > 0
                                ) {

                                    binding.tvReward.text =
                                        "Last reward: " +
                                                "+${result.moneyEarned} ₡"
                                }

                                if (
                                    result.reputationEarned > 0
                                ) {

                                    binding.tvReputationReward
                                        .text =
                                        "Last reputation: " +
                                                "+${result.reputationEarned}"
                                }

                                if (
                                    result.restaurantXpEarned > 0
                                ) {

                                    binding.tvRestaurantInfo
                                        .text =
                                        result.message +
                                                "  +" +
                                                result.restaurantXpEarned +
                                                " Restaurant XP"

                                    if (
                                        result.restaurantLeveledUp
                                    ) {

                                        binding.tvRestaurantInfo
                                            .text =
                                            result.message +
                                                    "\nRESTAURANT LEVEL UP!"
                                    }
                                }

                                viewModel
                                    .clearServeResult()
                            }
                        }
                }
            }
        }
    }

    private fun renderRecipes(
        recipes: List<Recipe>
    ) {

        if (_binding == null) {
            return
        }

        binding.recipeContainer
            .removeAllViews()

        if (recipes.isEmpty()) {

            val emptyText =
                TextView(requireContext())

            emptyText.text =
                "NO RECIPES UNLOCKED"

            emptyText.textSize =
                16f

            emptyText.setTextColor(
                Color.LTGRAY
            )

            binding.recipeContainer
                .addView(emptyText)

            binding.btnCook.isEnabled =
                false

            return
        }

        recipes.forEach { recipe ->

            val card =
                LinearLayout(
                    requireContext()
                )

            card.orientation =
                LinearLayout.VERTICAL

            card.setPadding(
                20,
                20,
                20,
                20
            )

            val selected =
                recipe.id ==
                        viewModel
                            .selectedRecipeId
                            .value

            if (selected) {

                card.setBackgroundColor(
                    Color.rgb(
                        20,
                        45,
                        55
                    )
                )
            }

            val name =
                TextView(
                    requireContext()
                )

            name.text =
                if (selected) {
                    "▶ ${recipe.name}"
                } else {
                    recipe.name
                }

            name.textSize =
                18f

            name.setTextColor(
                if (selected) {
                    Color.CYAN
                } else {
                    Color.WHITE
                }
            )

            name.setTypeface(
                null,
                android.graphics.Typeface.BOLD
            )

            card.addView(name)

            val ingredients =
                TextView(
                    requireContext()
                )

            ingredients.text =
                buildIngredientsText(
                    recipe
                )

            ingredients.textSize =
                14f

            ingredients.setTextColor(
                Color.LTGRAY
            )

            card.addView(
                ingredients
            )

            val sellPrice =
                TextView(
                    requireContext()
                )

            sellPrice.text =
                "Sell: ${recipe.sellPrice} ₡"

            sellPrice.textSize =
                14f

            sellPrice.setTextColor(
                Color.GREEN
            )

            card.addView(
                sellPrice
            )

            val selectButton =
                Button(
                    requireContext()
                )

            selectButton.text =
                if (selected) {
                    "SELECTED"
                } else {
                    "SELECT"
                }

            selectButton.isEnabled =
                !selected

            selectButton.setOnClickListener {
                viewModel
                    .selectRecipe(
                        recipe.id
                    )
            }

            card.addView(
                selectButton
            )

            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.bottomMargin =
                16

            binding.recipeContainer
                .addView(
                    card,
                    params
                )
        }
    }

    private fun buildIngredientsText(
        recipe: Recipe
    ): String {

        return buildString {

            append(
                "Ingredients: "
            )

            recipe.ingredients
                .forEachIndexed {
                        index,
                        ingredient ->

                    if (index > 0) {
                        append("   ")
                    }

                    val item =
                        ItemData.getById(
                            ingredient.itemId
                        )

                    append(
                        "${item?.name ?: ingredient.itemId} " +
                                "x${ingredient.quantity}"
                    )
                }
        }
    }

    private fun renderSelectedRecipe() {

        val recipe =
            viewModel.getRecipe(
                viewModel
                    .selectedRecipeId
                    .value
                    ?: ""
            )

        if (recipe == null) {

            binding.tvSelectedRecipe.text =
                "SELECTED: None"

            binding.btnCook.isEnabled =
                false

            return
        }

        binding.tvSelectedRecipe.text =
            "SELECTED: ${recipe.name}"

        binding.btnCook.isEnabled =
            viewModel
                .isRestaurantOpen
                .value
    }

    private fun renderFoodInventory() {

        if (_binding == null) {
            return
        }

        val foodItems =
            viewModel.inventory.value
                .filter { item ->

                    !item.isSecured &&
                            ItemData
                                .getById(
                                    item.itemId
                                )
                                ?.type ==
                            com.example.cyberharvest2300.data.game.ItemType.FOOD
                }

        binding.tvFoodStock.text =
            if (foodItems.isEmpty()) {

                "No prepared food."

            } else {

                buildString {

                    foodItems.forEach { item ->

                        val data =
                            ItemData.getById(
                                item.itemId
                            )

                        append(
                            "${data?.name ?: item.itemId}"
                        )

                        append("    x")
                        append(item.quantity)
                        append("\n")
                    }
                }
            }
    }

    private fun renderPlayerStats(
        profile:
        com.example.cyberharvest2300.data.local.entity.PlayerProfile?
    ) {

        if (profile == null) {

            binding.tvPlayerStats.text =
                "Player profile not found."

            return
        }

        val capacity =
            RestaurantProgressionEngine
                .getCustomerCapacity(
                    profile.restaurantLevel
                )

        binding.tvPlayerStats.text =
            "Day ${profile.day}    " +
                    "Money: ${profile.money} ₡    " +
                    "Reputation: ${profile.reputation}\n" +
                    "Restaurant Lv.${profile.restaurantLevel}    " +
                    "XP: ${profile.restaurantXp}    " +
                    "Customers: $capacity"
    }

    private fun renderRestaurantState(
        isOpen: Boolean
    ) {

        binding.tvStatus.text =
            if (isOpen) {
                "OPEN — DAY"
            } else {
                "CLOSED — NIGHT"
            }

        binding.btnCook.isEnabled =
            isOpen &&
                    viewModel
                        .selectedRecipeId
                        .value != null

        binding.btnNewCustomer.isEnabled =
            isOpen

        binding.btnCloseRestaurant.isEnabled =
            isOpen

        binding.btnServe.isEnabled =
            isOpen &&
                    viewModel
                        .orders
                        .value
                        .isNotEmpty()
    }

    private fun renderOrders(
        orders: List<RestaurantOrder>
    ) {

        if (orders.isEmpty()) {

            binding.tvOrder.text =
                "No active orders."

            binding.btnServe.isEnabled =
                false

            return
        }

        val firstOrder =
            orders.first()

        val customer =
            RestaurantData.getCustomer(
                firstOrder.customerId
            )

        val recipe =
            viewModel.getRecipe(
                firstOrder.recipeId
            )

        binding.tvOrder.text =
            "ACTIVE CUSTOMERS: ${orders.size}\n\n" +
                    "CUSTOMER\n\n" +
                    "${customer?.name ?: "Unknown"}\n\n" +
                    "Wants: " +
                    "${recipe?.name ?: "Unknown"}\n\n" +
                    "Reward: " +
                    "${firstOrder.reward} ₡\n" +
                    "Reputation: " +
                    "+${firstOrder.reputationReward}"

        binding.btnServe.isEnabled =
            viewModel.isRestaurantOpen.value
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}