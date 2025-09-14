package com.example.richculture.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.LoginRequest
import com.example.richculture.Data.SignupRequest
import com.example.richculture.retro.RetrofitInstance.userApi
import com.example.richculture.utility.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // ✅ NEW: State to track the progress of the profile update
    private val _isUpdatingProfile = MutableStateFlow(false)
    val isUpdatingProfile: StateFlow<Boolean> = _isUpdatingProfile.asStateFlow()

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

    // ✅ UPDATED: Update Profile function now handles loading state and refreshes data
    fun updateUserProfile(
        token: String,
        profileImage: MultipartBody.Part?,
        name: RequestBody?,
        bio: RequestBody?,
        gender: RequestBody?
    ) {
        viewModelScope.launch {
            _isUpdatingProfile.value = true
            try {
                val response = userApi.updateProfile("Bearer $token", profileImage, name, bio, gender)
                // On success, update the session with the new user data
                sessionManager.restoreSession(response.user)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _isUpdatingProfile.value = false
            }
        }
    }
}

