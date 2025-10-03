package com.example.richculture.utility

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val AUTH_TOKEN_KEY = "auth_token"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(AUTH_TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(AUTH_TOKEN_KEY, null)
    }

    fun clearToken() {
        prefs.edit().remove(AUTH_TOKEN_KEY).apply()
    }
}
