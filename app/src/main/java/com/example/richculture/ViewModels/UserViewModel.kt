package com.example.richculture.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.LoginRequest
import com.example.richculture.Data.SignupRequest
import com.example.richculture.retro.RetrofitInstance.userApi
import com.example.richculture.utility.SessionManager
import kotlinx.coroutines.launch

class UserViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    val currentUser = sessionManager.currentUser

    init {
        sessionManager.getCurrentUserToken()?.let { token ->
            getUserProfile(token)
        }
    }

    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userApi.signup(SignupRequest(name, email, password))
                if (response.isSuccessful && response.body() != null) {
                    val signupResponse = response.body()!!
                    sessionManager.login(signupResponse.user, signupResponse.token)
                } else {
                    _error.postValue("Signup failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userApi.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    sessionManager.login(loginResponse.user, loginResponse.token)
                } else {
                    _error.postValue("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    private fun getUserProfile(token: String) {
        viewModelScope.launch {
            try {
                val user = userApi.getProfile("Bearer $token")
                sessionManager.restoreSession(user)
            } catch (e: Exception) {
                sessionManager.logout()
            }
        }
    }

    // ✅ NEW: Logout function that calls the SessionManager
    fun logout() {
        sessionManager.logout()
    }
}

