package com.example.trade.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trade.AuthManager
import com.example.trade.databinding.ActivityMainBinding
import com.example.trade.ui.trade.TradeActivity

import com.example.trade.ui.auth.AuthActivity
import com.example.trade.ui.auth.AuthViewModel

import kotlinx.coroutines.launch
import com.example.trade.ui.main.CurrencyAdapter.CurrencyItem

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var vm: MainViewModel
    private lateinit var adapter: CurrencyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabLogout.setOnClickListener {
            AuthViewModel().logout()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            val success = AuthManager.ensureUserLoggedIn()
            if (!success) {

                android.widget.Toast.makeText(
                    this@MainActivity,
                    "Ошибка авторизации!",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }

        vm = MainViewModel()


        adapter = CurrencyAdapter { coinId ->

            val intent = Intent(this, TradeActivity::class.java)
            intent.putExtra("coinId", coinId)
            startActivity(intent)
        }


        binding.rvCurrencies.layoutManager = LinearLayoutManager(this)
        binding.rvCurrencies.adapter = adapter


        lifecycleScope.launch {
            vm.userState.collect { state ->
                binding.tvBalanceValue.text = "$%.2f".format(state.balance)
                adapter.submitList(buildCurrencyList(state.portfolio))
            }
        }


        lifecycleScope.launch {
            vm.prices.collect { prices ->
                adapter.updatePrices(prices)
            }
        }


        binding.fabLogout.setOnClickListener {
            finish()
        }

        binding.fabLogout.setOnClickListener {

            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()


            val intent = Intent(this, com.example.trade.ui.auth.AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun buildCurrencyList(portfolio: Map<String, Double>): List<CurrencyItem> {
        val ids = listOf("bitcoin", "ethereum", "solana", "dogecoin")
        return ids.map { id ->
            CurrencyItem(id, portfolio[id] ?: 0.0)
        }
    }
}

