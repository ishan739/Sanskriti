package com.example.richculture.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.LoginErrorResponse
import com.example.richculture.Data.LoginRequest
import com.example.richculture.Data.ResetPasswordRequest
import com.example.richculture.Data.SendOtpRequest
import com.example.richculture.Data.SignupErrorResponse
import com.example.richculture.Data.SignupRequest
import com.example.richculture.Data.VerifyOtpRequest
import com.example.richculture.retro.RetrofitInstance.userApi
import com.example.richculture.utility.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isUpdatingProfile = MutableStateFlow(false)
    val isUpdatingProfile: StateFlow<Boolean> = _isUpdatingProfile.asStateFlow()

    // ✅ NEW: States to manage the multi-step reset flow
    private val _otpSent = MutableLiveData<Boolean>()
    val otpSent: LiveData<Boolean> get() = _otpSent

    private val _otpVerified = MutableLiveData<Boolean>()
    val otpVerified: LiveData<Boolean> get() = _otpVerified

    private val _passwordResetSuccess = MutableLiveData<Boolean>()
    val passwordResetSuccess: LiveData<Boolean> get() = _passwordResetSuccess


    val currentUser = sessionManager.currentUser

    init {
        sessionManager.getCurrentUserToken()?.let { token -> getUserProfile(token) }
    }

    fun sendOtp(email: String) {
        viewModelScope.launch {
            try {
                val response = userApi.sendOtp(SendOtpRequest(email))
                if (response.isSuccessful) {
                    _otpSent.postValue(true)
                } else {
                    _error.postValue(response.message())
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    fun verifyOtp(email: String, otp: String) {
        viewModelScope.launch {
            try {
                val response = userApi.verifyOtp(VerifyOtpRequest(email, otp))
                if (response.isSuccessful) {
                    _otpVerified.postValue(true)
                } else {
                    _error.postValue("Invalid OTP. Please try again.")
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    fun resetPassword(email: String, newPassword: String) {
        viewModelScope.launch {
            try {
                val response = userApi.resetPassword(ResetPasswordRequest(email, newPassword))
                if (response.isSuccessful) {
                    _passwordResetSuccess.postValue(true)
                } else {
                    _error.postValue(response.message())
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    // Function to clear reset states when the user navigates away
    fun clearResetStates() {
        _otpSent.value = false
        _otpVerified.value = false
        _passwordResetSuccess.value = false
    }

    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userApi.signup(SignupRequest(name, email, password))
                if (response.isSuccessful && response.body() != null) {
                    val signupResponse = response.body()!!
                    sessionManager.login(signupResponse.user, signupResponse.token)
                } else {
                    // ✅ UPDATED: Use the specific SignupErrorResponse class
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, SignupErrorResponse::class.java)
                    _error.postValue(errorResponse.details?.firstOrNull() ?: errorResponse.message)
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
                    // ✅ UPDATED: Use the specific LoginErrorResponse class
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, LoginErrorResponse::class.java)
                    _error.postValue(errorResponse.message)
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

    fun logout() {
        sessionManager.logout()
    }

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
                sessionManager.restoreSession(response.user)
            } catch (e: Exception) {
                _error.postValue(e.message)
            } finally {
                _isUpdatingProfile.value = false
            }
        }
    }
}

