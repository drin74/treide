package com.example.trade.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


interface CoinGeckoApi {
    @GET("api/v3/simple/price")
    suspend fun getPrices(
        @Query("ids") ids: String,
        @Query("vs_currencies") vs: String = "usd"
    ): Map<String, PriceDto>
}

data class PriceDto(val usd: Double)


object NetworkModule {
    val api: CoinGeckoApi = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CoinGeckoApi::class.java)
}