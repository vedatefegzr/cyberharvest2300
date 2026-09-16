package com.example.cyberharvest2300.data.game

data class RestaurantCustomer(
    val id: String,
    val name: String
)

data class RestaurantOrder(
    val id: Long = 0,
    val customerId: String,
    val recipeId: String,
    val reward: Int,
    val reputationReward: Int
)