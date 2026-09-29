package com.example.cyberharvest2300.domain.restaurant

import com.example.cyberharvest2300.data.game.GameTime
import com.example.cyberharvest2300.data.game.ItemData
import com.example.cyberharvest2300.data.game.Recipe
import com.example.cyberharvest2300.data.game.RecipeData
import com.example.cyberharvest2300.data.game.RestaurantData
import com.example.cyberharvest2300.data.game.RestaurantOrder
import com.example.cyberharvest2300.data.local.entity.DailyOrder
import com.example.cyberharvest2300.data.repository.CustomerProgressRepository
import com.example.cyberharvest2300.data.repository.DailyOrderRepository
import com.example.cyberharvest2300.data.repository.InventoryRepository
import com.example.cyberharvest2300.data.repository.PlayerProfileRepository
import com.example.cyberharvest2300.domain.achievement.Achievement
import com.example.cyberharvest2300.domain.achievement.AchievementChecker
import com.example.cyberharvest2300.domain.game.GameOverChecker
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
    val restaurantLeveledUp: Boolean = false,
    val newlyUnlockedAchievements: List<Achievement> = emptyList()
)

data class BuyIngredientResult(
    val success: Boolean,
    val message: String,
    val itemId: String? = null,
    val quantity: Int = 0,
    val totalCost: Int = 0
)

class RestaurantEngine(
    private val inventoryRepository: InventoryRepository,
    private val playerProfileRepository: PlayerProfileRepository,
    private val unlockConditionChecker: UnlockConditionChecker,
    private val dailyOrderRepository: DailyOrderRepository,
    private val customerProgressRepository: CustomerProgressRepository,
    private val restaurantProgressionEngine: RestaurantProgressionEngine
) {

    // Restoran tarafında sadece restoran/gün/para tabanlı achievement'lar
    // kontrol edilir (kill sayısı bu engine'in erişebildiği bir veri değil,
    // onlar HuntingEngine.finishVictory() içinde kontrol edilir).
    private val achievementChecker =
        AchievementChecker(
            playerProfileRepository = playerProfileRepository
        )

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

        if (player.isGameOver) {
            return CookResult(
                false,
                "Oyun bitti."
            )
        }

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

        // Önceki günlerden kalan, servis edilmemiş siparişlerin
        // ayrılmış yemeğini envantere geri ver ve siparişi sil.
        dailyOrderRepository
            .getExpiredOrders(player.day)
            .forEach { old ->

                RecipeData.getById(old.recipeId)?.let { recipe ->
                    inventoryRepository.releaseSecuredItem(
                        itemId = recipe.resultItemId,
                        quantity = recipe.resultQuantity
                    )
                }

                dailyOrderRepository.deleteOrder(old.id)
            }

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

        if (player.isGameOver) {
            return null
        }

        if (player.timePhase != GameTime.DAY) {
            return null
        }

        val orders =
            dailyOrderRepository
                .getOrdersForDay(player.day)

        val customerCapacity =
            RestaurantProgressionEngine
                .getCustomerCapacity(
                    level = player.restaurantLevel,
                    perkIds = player.restaurantPerkIds
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

        if (player.isGameOver) {
            return ServeResult(
                false,
                "Oyun bitti."
            )
        }

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

        val activePerks =
            RestaurantPerks.parse(
                player.restaurantPerkIds
            )

        val reputationBonusPercent =
            activePerks.sumOf {
                it.reputationBonusPercent
            }

        val moneyReward =
            recipe.sellPrice

        val baseReputationReward =
            savedOrder.reputationReward

        val reputationReward =
            baseReputationReward +
                    (
                            baseReputationReward *
                                    reputationBonusPercent / 100f
                            ).toInt()

        val updatedPlayer =
            GameOverChecker.evaluate(
                player.copy(
                    money =
                        player.money +
                                moneyReward,

                    reputation =
                        player.reputation +
                                reputationReward
                )
            )

        playerProfileRepository
            .updatePlayerProfile(
                updatedPlayer
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
            if (!updatedPlayer.isGameOver) {
                restaurantProgressionEngine
                    .addRestaurantXp(
                        xpReward
                    )
            } else {
                null
            }

        val newlyUnlocked =
            if (!updatedPlayer.isGameOver) {
                achievementChecker.checkAndUnlock()
            } else {
                emptyList()
            }

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
                progressionResult?.leveledUp == true,

            newlyUnlockedAchievements =
                newlyUnlocked
        )
    }

    /*
     * =========================================================
     * MALZEME SATIN ALMA
     * =========================================================
     *
     * Paranın gerçek bir harcama noktası olması için eklendi.
     * Eksik pişirme malzemesini avlanmayı beklemeden,
     * parayla satın alabiliyorsun.
     *
     * Fiyat = ItemData.sellValue * BUY_PRICE_MULTIPLIER
     * (satış fiyatının üstüne standart bir kar marjı),
     * PERK_SUPPLIER seçildiyse bu fiyat üzerinden ek indirim uygulanır.
     */
    suspend fun buyIngredient(
        itemId: String,
        quantity: Int
    ): BuyIngredientResult {

        if (quantity <= 0) {
            return BuyIngredientResult(
                success = false,
                message = "Geçersiz miktar."
            )
        }

        val player =
            playerProfileRepository
                .getPlayerProfile()
                .first()
                ?: return BuyIngredientResult(
                    success = false,
                    message = "Player profile bulunamadı."
                )

        if (player.isGameOver) {
            return BuyIngredientResult(
                success = false,
                message = "Oyun bitti."
            )
        }

        if (player.timePhase != GameTime.DAY) {
            return BuyIngredientResult(
                success = false,
                message = "Restaurant is closed at night."
            )
        }

        val item =
            ItemData.getById(itemId)
                ?: return BuyIngredientResult(
                    success = false,
                    message = "Item not found."
                )

        val activePerks =
            RestaurantPerks.parse(
                player.restaurantPerkIds
            )

        val discountPercent =
            activePerks
                .sumOf { it.priceDiscountPercent }
                .coerceAtMost(30) // 30'da alış fiyatı satışın hep üstünde kalır

        val basePrice =
            item.sellValue * BUY_PRICE_MULTIPLIER

        val discountedPrice =
            basePrice * (1f - discountPercent / 100f)

        val unitPrice =
            discountedPrice
                .toInt()
                .coerceAtLeast(1)

        val totalCost =
            unitPrice * quantity

        if (player.money < totalCost) {
            return BuyIngredientResult(
                success = false,
                message = "Yeterli paran yok."
            )
        }

        // Alışveriş game over sebebi olmamalı; evaluate çağrılmaz.
        playerProfileRepository
            .updatePlayerProfile(
                player.copy(
                    money =
                        player.money - totalCost
                )
            )

        inventoryRepository.addItem(
            itemId = itemId,
            quantity = quantity
        )

        return BuyIngredientResult(
            success = true,
            message =
                "${item.name} x$quantity satın alındı.",
            itemId = itemId,
            quantity = quantity,
            totalCost = totalCost
        )
    }

    companion object {

        // Satın alma fiyatı, satış fiyatının kaç katı olsun.
        // 2.0f -> 1.8f: PERK_SUPPLIER ile birlikte oynanınca
        // fiyatlar çok agresif hissettiriyordu (bkz. ekonomi
        // dengeleme notları). Playtest sonrası tekrar ayarla.
        private const val BUY_PRICE_MULTIPLIER = 1.8f
    }
}
