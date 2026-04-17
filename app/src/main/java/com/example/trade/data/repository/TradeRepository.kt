package com.example.trade.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.example.trade.data.network.CoinGeckoApi
import com.example.trade.data.network.NetworkModule
import com.example.trade.data.network.PriceDto
import com.example.trade.model.UserState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TradeRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val api: CoinGeckoApi = NetworkModule.api



    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun register(email: String, password: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()

        val initialData = hashMapOf(
            "balance" to 10000.0,
            "portfolio" to emptyMap<String, Double>()
        )
        db.collection("users").document(result.user!!.uid).set(initialData).await()
    }

    fun logout() {
        auth.signOut()
    }



    fun observeUserState(): Flow<UserState> = callbackFlow {
        val uid = auth.currentUser?.uid ?: run {
            close()
            return@callbackFlow
        }


        val registration: ListenerRegistration = db.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val balance = snapshot.getDouble("balance") ?: 0.0
                    @Suppress("UNCHECKED_CAST")
                    val portfolio = snapshot.get("portfolio") as? Map<String, Double> ?: emptyMap()

                    trySend(UserState(balance, portfolio))
                }
            }


        awaitClose {
            registration.remove()
        }
    }



    suspend fun fetchPrices(
        ids: String = "bitcoin,ethereum,solana,dogecoin"
    ): Map<String, PriceDto> {
        return api.getPrices(ids = ids, vs = "usd")
    }




    suspend fun executeTrade(
        currencyId: String,
        usdAmount: Double,
        isBuy: Boolean
    ) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")


        val prices = fetchPrices()
        val currentPrice = prices[currencyId]?.usd
            ?: throw Exception("Price not found for $currencyId")

        val cryptoAmount = usdAmount / currentPrice

        val userRef = db.collection("users").document(uid)


        val snapshot = userRef.get().await()
        val currentBalance = snapshot.getDouble("balance") ?: 0.0
        @Suppress("UNCHECKED_CAST")
        val portfolio = (snapshot.get("portfolio") as? MutableMap<String, Double>)?.toMutableMap()
            ?: mutableMapOf()

        if (isBuy) {

            if (currentBalance < usdAmount) {
                throw Exception("Недостаточно виртуальных средств")
            }

            userRef.update("balance", currentBalance - usdAmount).await()

            portfolio[currencyId] = (portfolio[currencyId] ?: 0.0) + cryptoAmount
        } else {

            val ownedAmount = portfolio[currencyId] ?: 0.0
            if (ownedAmount < cryptoAmount) {
                throw Exception("Недостаточно криптовалюты в портфеле")
            }

            portfolio[currencyId] = ownedAmount - cryptoAmount

            userRef.update("balance", currentBalance + usdAmount).await()
        }


        userRef.update("portfolio", portfolio).await()
    }
}