package com.example.cyberharvest2300.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cyberharvest2300.data.game.Recipe
import com.example.cyberharvest2300.data.game.RestaurantData
import com.example.cyberharvest2300.data.game.RestaurantOrder
import com.example.cyberharvest2300.data.local.entity.InventoryItem
import com.example.cyberharvest2300.data.local.entity.PlayerProfile
import com.example.cyberharvest2300.data.repository.CustomerProgressRepository
import com.example.cyberharvest2300.data.repository.DailyOrderRepository
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.domain.restaurant.RestaurantEngine
import com.example.cyberharvest2300.domain.restaurant.RestaurantProgressionEngine
import com.example.cyberharvest2300.domain.restaurant.CookResult
import com.example.cyberharvest2300.domain.restaurant.ServeResult
import com.example.cyberharvest2300.domain.unlock.UnlockConditionChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RestaurantViewModel(
    private val engine: RestaurantEngine,
    private val playerProfileRepository: PlayerProfileRepository,
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    private val _recipes =
        MutableStateFlow<List<Recipe>>(emptyList())

    val recipes: StateFlow<List<Recipe>> =
        _recipes.asStateFlow()

    private val _selectedRecipeId =
        MutableStateFlow<String?>(null)

    val selectedRecipeId:
            StateFlow<String?> =
        _selectedRecipeId.asStateFlow()

    private val _orders =
        MutableStateFlow<List<RestaurantOrder>>(emptyList())

    val orders:
            StateFlow<List<RestaurantOrder>> =
        _orders.asStateFlow()

    private val _message =
        MutableStateFlow(
            "Restaurant ready."
        )

    val message:
            StateFlow<String> =
        _message.asStateFlow()

    private val _cookResult =
        MutableStateFlow<CookResult?>(null)

    val cookResult:
            StateFlow<CookResult?> =
        _cookResult.asStateFlow()

    private val _serveResult =
        MutableStateFlow<ServeResult?>(null)

    val serveResult:
            StateFlow<ServeResult?> =
        _serveResult.asStateFlow()

    private val _playerProfile =
        MutableStateFlow<PlayerProfile?>(null)

    val playerProfile:
            StateFlow<PlayerProfile?> =
        _playerProfile.asStateFlow()

    private val _inventory =
        MutableStateFlow<List<InventoryItem>>(
            emptyList()
        )

    val inventory:
            StateFlow<List<InventoryItem>> =
        _inventory.asStateFlow()

    private val _isRestaurantOpen =
        MutableStateFlow(false)

    val isRestaurantOpen:
            StateFlow<Boolean> =
        _isRestaurantOpen.asStateFlow()

    init {
        observePlayerProfile()
        observeInventory()
        loadRecipes()
        loadOrders()
    }

    private fun observePlayerProfile() {

        viewModelScope.launch {

            playerProfileRepository
                .getPlayerProfile()
                .collect { profile ->

                    _playerProfile.value =
                        profile

                    _isRestaurantOpen.value =
                        profile?.timePhase == "DAY"

                    if (profile != null) {
                        loadOrders()
                    }
                }
        }
    }

    private fun observeInventory() {

        viewModelScope.launch {

            inventoryRepository
                .getAllItems()
                .collect { items ->

                    _inventory.value =
                        items
                }
        }
    }

    private fun loadRecipes() {

        viewModelScope.launch {

            val availableRecipes =
                engine.getAvailableRecipes()

            _recipes.value =
                availableRecipes

            if (
                _selectedRecipeId.value == null &&
                availableRecipes.isNotEmpty()
            ) {
                _selectedRecipeId.value =
                    availableRecipes.first().id
            }
        }
    }

    private fun loadOrders() {

        viewModelScope.launch {

            _orders.value =
                engine.getCurrentOrders()
        }
    }

    fun selectRecipe(
        recipeId: String
    ) {

        if (
            _recipes.value.any {
                it.id == recipeId
            }
        ) {
            _selectedRecipeId.value =
                recipeId
        }
    }

    fun cookSelectedRecipe() {

        val recipeId =
            _selectedRecipeId.value
                ?: return

        viewModelScope.launch {

            val result =
                engine.cookRecipe(
                    recipeId
                )

            _cookResult.value =
                result

            _message.value =
                result.message
        }
    }

    fun createOrder() {

        viewModelScope.launch {

            if (!_isRestaurantOpen.value) {

                _message.value =
                    "Restaurant is closed at night."

                return@launch
            }

            val order =
                engine.createDailyOrder()

            if (order == null) {

                val profile =
                    _playerProfile.value

                if (profile != null) {

                    val capacity =
                        RestaurantProgressionEngine
                            .getCustomerCapacity(
                                profile.restaurantLevel
                            )

                    val activeOrders =
                        _orders.value.size

                    _message.value =
                        if (
                            activeOrders >=
                            capacity
                        ) {
                            "Today's customer capacity is full."
                        } else {
                            "No prepared food is available for a customer."
                        }
                }

                return@launch
            }

            _message.value =
                buildOrderMessage(order)

            loadOrders()
        }
    }

    fun serveOrder(
        order: RestaurantOrder
    ) {

        viewModelScope.launch {

            val result =
                engine.serveOrder(
                    order
                )

            _serveResult.value =
                result

            _message.value =
                result.message

            if (result.success) {
                loadOrders()
            }
        }
    }

    fun clearCookResult() {
        _cookResult.value = null
    }

    fun clearServeResult() {
        _serveResult.value = null
    }

    fun getInventoryQuantity(
        itemId: String
    ): Int {

        return _inventory.value
            .filter {
                it.itemId == itemId &&
                        !it.isSecured
            }
            .sumOf {
                it.quantity
            }
    }

    fun getRecipe(
        recipeId: String
    ): Recipe? {

        return _recipes.value.find {
            it.id == recipeId
        }
    }

    fun getCustomerName(
        customerId: String
    ): String {

        return RestaurantData
            .getCustomer(
                customerId
            )
            ?.name
            ?: "Unknown"
    }

    private fun buildOrderMessage(
        order: RestaurantOrder
    ): String {

        val customer =
            RestaurantData.getCustomer(
                order.customerId
            )

        val recipe =
            _recipes.value.find {
                it.id == order.recipeId
            }

        return "${customer?.name ?: "Customer"} " +
                "ordered ${recipe?.name ?: "food"}."
    }
}

class RestaurantViewModelFactory(
    private val inventoryRepository:
    InventoryRepository,

    private val playerProfileRepository:
    PlayerProfileRepository,

    private val unlockConditionChecker:
    UnlockConditionChecker,

    private val dailyOrderRepository:
    DailyOrderRepository,

    private val customerProgressRepository:
    CustomerProgressRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                RestaurantViewModel::class.java
            )
        ) {

            val progressionEngine =
                RestaurantProgressionEngine(
                    playerProfileRepository
                )

            val restaurantEngine =
                RestaurantEngine(
                    inventoryRepository =
                        inventoryRepository,

                    playerProfileRepository =
                        playerProfileRepository,

                    unlockConditionChecker =
                        unlockConditionChecker,

                    dailyOrderRepository =
                        dailyOrderRepository,

                    customerProgressRepository =
                        customerProgressRepository,

                    restaurantProgressionEngine =
                        progressionEngine
                )

            return RestaurantViewModel(
                engine =
                    restaurantEngine,

                playerProfileRepository =
                    playerProfileRepository,

                inventoryRepository =
                    inventoryRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}