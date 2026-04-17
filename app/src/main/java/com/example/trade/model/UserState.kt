package com.example.trade.model


data class UserState(
    val balance: Double = 0.0,
    val portfolio: Map<String, Double> = emptyMap()
) {

    fun getCoinAmount(coinId: String): Double {
        return portfolio[coinId] ?: 0.0
    }


    fun hasEnoughBalance(amount: Double): Boolean {
        return balance >= amount
    }


    fun hasEnoughCoins(coinId: String, amount: Double): Boolean {
        return getCoinAmount(coinId) >= amount
    }
}