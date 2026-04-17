package com.example.trade.ui.trade

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trade.data.repository.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TradeViewModel(
    private val coinId: String
) : ViewModel() {

    private val repo = TradeRepository()
    private val _currentPrice = MutableStateFlow(0.0)
    val currentPrice: StateFlow<Double> = _currentPrice

    init {
        loadPrice()
    }

    private fun loadPrice() {
        viewModelScope.launch {
            try {
                val prices = repo.fetchPrices()
                _currentPrice.value = prices[coinId]?.usd ?: 0.0
            } catch (e: Exception) {
                Log.e("Trade", "Ошибка загрузки цены", e)
            }
        }
    }

    fun trade(amountUsd: Double, isBuy: Boolean) {
        viewModelScope.launch {
            try {
                repo.executeTrade(coinId, amountUsd, isBuy)

            } catch (e: Exception) {
                Log.e("Trade", "Ошибка торговли", e)

            }
        }
    }
}