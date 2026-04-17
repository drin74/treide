package com.example.trade.ui.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthViewModel {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Log.d("AUTH", "Вход успешен: $email")
            true
        } catch (e: Exception) {
            Log.e("AUTH", "Ошибка входа: ${e.message}", e)
            false
        }
    }

    suspend fun register(email: String, password: String): Boolean {
        return try {

            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return false


            val userData = hashMapOf(
                "balance" to 10000.0,
                "portfolio" to emptyMap<String, Double>(),
                "email" to email,
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("users")
                .document(userId)
                .set(userData)
                .await()

            Log.d("AUTH", "Регистрация успешна: $email с балансом \$10,000")
            true
        } catch (e: Exception) {
            Log.e("AUTH", "Ошибка регистрации: ${e.message}", e)
            false
        }
    }

    fun logout() {
        auth.signOut()
    }
}