package com.example.cyberharvest2300.data.game

object RestaurantData {

    val customers =
        listOf(

            RestaurantCustomer(
                id = "CUSTOMER_WORKER",
                name = "Wasteland Worker"
            ),

            RestaurantCustomer(
                id = "CUSTOMER_HUNTER",
                name = "Night Hunter"
            ),

            RestaurantCustomer(
                id = "CUSTOMER_TRADER",
                name = "Scrap Trader"
            )
        )

    fun createRandomOrder(
        availableRecipes: List<Recipe>
    ): RestaurantOrder? {

        if (availableRecipes.isEmpty()) {
            return null
        }

        if (customers.isEmpty()) {
            return null
        }

        val customer =
            customers.random()

        val recipe =
            availableRecipes.random()

        return RestaurantOrder(
            customerId = customer.id,
            recipeId = recipe.id,
            reward = recipe.sellPrice,
            reputationReward = 1
        )
    }

    fun getCustomer(
        customerId: String
    ): RestaurantCustomer? {

        return customers.find {
            it.id == customerId
        }
    }
}