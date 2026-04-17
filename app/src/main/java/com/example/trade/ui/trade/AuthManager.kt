package com.example.trade

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object AuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    suspend fun ensureUserLoggedIn(): Boolean {

        if (auth.currentUser != null) {
            Log.d("AUTH", "Пользователь уже авторизован: ${auth.currentUser?.email}")
            return true
        }

        try {

            val email = "user@test.com"
            val password = "123456"

            Log.d("AUTH", "Создаём нового пользователя...")

            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")


            val userData = hashMapOf(
                "balance" to 10000.0,
                "portfolio" to emptyMap<String, Double>(),
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("users")
                .document(userId)
                .set(userData)
                .await()

            Log.d("AUTH", "✅ Пользователь создан с балансом \$10,000")
            return true

        } catch (e: Exception) {
            Log.e("AUTH", "❌ Ошибка регистрации: ${e.message}", e)
            return false
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}