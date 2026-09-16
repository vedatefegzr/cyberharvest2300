
package com.example.cyberharvest2300.domain.restaurant

import com.example.cyberharvest2300.data.game.GameTime
import com.example.cyberharvest2300.data.game.Recipe
import com.example.cyberharvest2300.data.game.RecipeData
import com.example.cyberharvest2300.data.game.RestaurantData
import com.example.cyberharvest2300.data.game.RestaurantOrder
import com.example.cyberharvest2300.data.local.entity.DailyOrder
import com.example.cyberharvest2300.data.repository.CustomerProgressRepository
import com.example.cyberharvest2300.data.repository.DailyOrderRepository
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.domain.unlock.UnlockConditionChecker
import kotlinx.coroutines.flow.first

data class CookResult(
    val success: Boolean,
    val message: String,
    val recipe: Recipe? = null
)

data class ServeResult(
    val success: Boolean,
    val message: String,
    val moneyEarned: Int = 0,
    val reputationEarned: Int = 0,
    val restaurantXpEarned: Int = 0,
    val restaurantLeveledUp: Boolean = false
)

class RestaurantEngine(
    private val inventoryRepository: InventoryRepository,
    private val playerProfileRepository: PlayerProfileRepository,
    private val unlockConditionChecker: UnlockConditionChecker,
    private val dailyOrderRepository: DailyOrderRepository,
    private val customerProgressRepository: CustomerProgressRepository,
    private val restaurantProgressionEngine: RestaurantProgressionEngine
) {

    suspend fun getAvailableRecipes(): List<Recipe> {

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return emptyList()

        if (player.timePhase != GameTime.DAY) {
            return emptyList()
        }

        return RecipeData.all.filter { recipe ->

            unlockConditionChecker
                .areConditionsMet(
                    recipe.unlockConditions
                )
        }
    }

    suspend fun cookRecipe(
        recipeId: String
    ): CookResult {

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return CookResult(
                    false,
                    "Player profile not found."
                )

        if (player.timePhase != GameTime.DAY) {
            return CookResult(
                false,
                "Restaurant is closed at night."
            )
        }

        val recipe =
            RecipeData.getById(recipeId)
                ?: return CookResult(
                    false,
                    "Recipe not found."
                )

        val unlocked =
            unlockConditionChecker
                .areConditionsMet(
                    recipe.unlockConditions
                )

        if (!unlocked) {
            return CookResult(
                false,
                "This recipe is still locked.",
                recipe
            )
        }

        for (ingredient in recipe.ingredients) {

            val quantity =
                inventoryRepository
                    .getItemQuantity(
                        itemId =
                            ingredient.itemId
                    )

            if (quantity < ingredient.quantity) {
                return CookResult(
                    false,
                    "Not enough ingredients.",
                    recipe
                )
            }
        }

        for (ingredient in recipe.ingredients) {

            val removed =
                inventoryRepository.removeItem(
                    itemId =
                        ingredient.itemId,
                    quantity =
                        ingredient.quantity
                )

            if (!removed) {
                return CookResult(
                    false,
                    "Could not consume ingredients.",
                    recipe
                )
            }
        }

        inventoryRepository.addItem(
            itemId =
                recipe.resultItemId,
            quantity =
                recipe.resultQuantity
        )

        return CookResult(
            true,
            "${recipe.name} cooked successfully.",
            recipe
        )
    }

    suspend fun getCurrentOrders(): List<RestaurantOrder> {

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return emptyList()

        val orders =
            dailyOrderRepository
                .getOrdersForDay(player.day)

        return orders
            .filter { !it.isCompleted }
            .map { order ->

                RestaurantOrder(
                    id =
                        order.id,

                    customerId =
                        order.customerId,

                    recipeId =
                        order.recipeId,

                    reward =
                        order.reward,

                    reputationReward =
                        order.reputationReward
                )
            }
    }

    suspend fun createDailyOrder(): RestaurantOrder? {

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return null

        if (player.timePhase != GameTime.DAY) {
            return null
        }

        val orders =
            dailyOrderRepository
                .getOrdersForDay(player.day)

        val customerCapacity =
            RestaurantProgressionEngine
                .getCustomerCapacity(
                    player.restaurantLevel
                )

        val activeOrders =
            orders.count {
                !it.isCompleted
            }

        if (activeOrders >= customerCapacity) {
            return null
        }

        val possibleRecipes =
            getAvailableRecipes()
                .filter { recipe ->

                    val preparedQuantity =
                        inventoryRepository
                            .getItemQuantity(
                                itemId =
                                    recipe.resultItemId
                            )

                    preparedQuantity >=
                            recipe.resultQuantity
                }

        if (possibleRecipes.isEmpty()) {
            return null
        }

        val order =
            RestaurantData
                .createRandomOrder(
                    possibleRecipes
                )
                ?: return null

        val recipe =
            RecipeData.getById(
                order.recipeId
            )
                ?: return null

        /*
         * Sipariş oluşturulduğu anda yemeği
         * normal inventory'den secured inventory'ye taşıyoruz.
         *
         * Böylece oyuncu siparişe ayrılmış
         * yemeği satamaz veya başka yerde kullanamaz.
         */
        val secured =
            inventoryRepository.secureItem(
                itemId =
                    recipe.resultItemId,

                quantity =
                    recipe.resultQuantity
            )

        if (!secured) {
            return null
        }

        val orderId =
            dailyOrderRepository.saveOrder(
                DailyOrder(
                    day =
                        player.day,

                    customerId =
                        order.customerId,

                    recipeId =
                        order.recipeId,

                    reward =
                        order.reward,

                    reputationReward =
                        order.reputationReward,

                    isCompleted =
                        false
                )
            )

        return order.copy(
            id = orderId
        )
    }

    suspend fun serveOrder(
        order: RestaurantOrder
    ): ServeResult {

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return ServeResult(
                    false,
                    "Player profile not found."
                )

        if (player.timePhase != GameTime.DAY) {
            return ServeResult(
                false,
                "Restaurant is closed at night."
            )
        }

        val savedOrder =
            dailyOrderRepository
                .getOrderById(order.id)
                ?: return ServeResult(
                    false,
                    "Order not found."
                )

        if (
            savedOrder.day != player.day ||
            savedOrder.isCompleted
        ) {
            return ServeResult(
                false,
                "This order is no longer active."
            )
        }

        val recipe =
            RecipeData.getById(
                savedOrder.recipeId
            )
                ?: return ServeResult(
                    false,
                    "Recipe not found."
                )

        /*
         * Siparişe ayrılmış yemek artık
         * secured inventory'de bulunuyor.
         */
        val consumed =
            inventoryRepository
                .consumeSecuredItem(
                    itemId =
                        recipe.resultItemId,

                    quantity =
                        recipe.resultQuantity
                )

        if (!consumed) {
            return ServeResult(
                false,
                "Prepared food is not available."
            )
        }

        val moneyReward =
            recipe.sellPrice

        val reputationReward =
            savedOrder.reputationReward

        playerProfileRepository
            .updatePlayerProfile(
                player.copy(
                    money =
                        player.money +
                                moneyReward,

                    reputation =
                        player.reputation +
                                reputationReward
                )
            )

        dailyOrderRepository.saveOrder(
            savedOrder.copy(
                isCompleted = true
            )
        )

        customerProgressRepository
            .addVisit(
                customerId =
                    savedOrder.customerId,

                relationshipGain =
                    1
            )

        val xpReward = 20

        val progressionResult =
            restaurantProgressionEngine
                .addRestaurantXp(
                    xpReward
                )

        return ServeResult(
            success = true,

            message =
                "${recipe.name} served successfully.",

            moneyEarned =
                moneyReward,

            reputationEarned =
                reputationReward,

            restaurantXpEarned =
                xpReward,

            restaurantLeveledUp =
                progressionResult?.leveledUp == true
        )
    }
}
