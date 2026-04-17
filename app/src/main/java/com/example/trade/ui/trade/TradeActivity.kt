package com.example.trade.ui.trade

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.trade.databinding.ActivityTradeBinding
import com.example.trade.ui.auth.AuthViewModel
import com.example.trade.ui.main.MainViewModel
import kotlinx.coroutines.launch

class TradeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTradeBinding
    private lateinit var vm: TradeViewModel
    private val mainViewModel = MainViewModel()

    private val coinId: String by lazy {
        intent.getStringExtra("coinId") ?: "bitcoin"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTradeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        vm = TradeViewModel(coinId)


        binding.tvCoinName.text = coinId.replaceFirstChar { it.uppercase() }


        lifecycleScope.launch {
            vm.currentPrice.collect { price ->
                binding.tvPrice.text = "$%.2f".format(price)
            }
        }


        lifecycleScope.launch {
            mainViewModel.userState.collect { state ->
                binding.tvBalance.text = "$%.2f".format(state.balance)
            }
        }


        binding.btnBack.setOnClickListener {
            finish()
        }


        binding.btnBuy.setOnClickListener {
            val amt = binding.edtAmount.text.toString().toDoubleOrNull()
            if (amt != null && amt > 0) {
                vm.trade(amountUsd = amt, isBuy = true)
                binding.edtAmount.text?.clear()
            }
        }


        binding.btnSell.setOnClickListener {
            val amt = binding.edtAmount.text.toString().toDoubleOrNull()
            if (amt != null && amt > 0) {
                vm.trade(amountUsd = amt, isBuy = false)
                binding.edtAmount.text?.clear()
            }
        }
    }
}