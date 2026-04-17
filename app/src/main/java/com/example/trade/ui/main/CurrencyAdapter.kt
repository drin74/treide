package com.example.trade.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trade.data.network.PriceDto
import com.example.trade.databinding.ItemCurrencyBinding

class CurrencyAdapter(
    private val onTradeClick: (String) -> Unit
) : RecyclerView.Adapter<CurrencyAdapter.VH>() {

    private var items = emptyList<CurrencyItem>()
    private var prices = emptyMap<String, PriceDto>()

    fun submitList(newItems: List<CurrencyItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun updatePrices(newPrices: Map<String, PriceDto>) {
        prices = newPrices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCurrencyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val price = prices[item.id]?.usd ?: 0.0

        holder.binding.tvCoinName.text = item.id.replaceFirstChar { it.uppercase() }
        holder.binding.tvCoinPrice.text = "$%.2f / %.4f шт.".format(price, item.amount)
        holder.binding.btnTrade.setOnClickListener {
            onTradeClick(item.id)
        }
    }

    override fun getItemCount() = items.size

    class VH(val binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root)
    data class CurrencyItem(val id: String, val amount: Double)
}

data class CurrencyItem(val id: String, val amount: Double)