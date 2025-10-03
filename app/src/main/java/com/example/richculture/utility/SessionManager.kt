package com.example.richculture.utility

import android.content.Context
import com.example.richculture.Data.User
import com.example.richculture.navigate.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.navigation.NavController

// A singleton class to manage the user's session state across the app.
class SessionManager(context: Context) {
    private val tokenManager = TokenManager(context)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun login(user: User, token: String) {
        tokenManager.saveToken(token)
        _currentUser.value = user
    }

    fun logout() {
        tokenManager.clearToken()
        _currentUser.value = null
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }

    fun getCurrentUserToken(): String? {
        return tokenManager.getToken()
    }

    fun restoreSession(user: User?) {
        _currentUser.value = user
    }

    // ✅ NEW: Centralized navigation logic for after a logout
    fun navigateToAuth(navController: NavController) {
        navController.navigate(Screen.Auth.route) {
            // Clear the entire back stack so the user can't go "back" into the app
            popUpTo(0) {
                inclusive = true
            }
        }
    }
}

