package com.example.trade.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trade.data.network.PriceDto
import com.example.trade.data.repository.TradeRepository
import com.example.trade.model.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repo = TradeRepository()

    private val _prices = MutableStateFlow<Map<String, PriceDto>>(emptyMap())
    val prices: StateFlow<Map<String, PriceDto>> = _prices

    val userState: StateFlow<UserState> = repo.observeUserState().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UserState()
    ) as StateFlow<UserState>

    init {
        loadPrices()
    }

    private fun loadPrices() {
        viewModelScope.launch {
            try {
                _prices.value = repo.fetchPrices()
            } catch (e: Exception) {
                Log.e("Trade", "Ошибка загрузки цен", e)
            }
        }
    }
}