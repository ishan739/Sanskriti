package com.example.richculture.Data


data class User(
    val role: String,
    val _id: String,
    val name: String,
    val email: String,
    val profileImage: String,
    val posts: List<String>,
    val cart: List<String>,
    val savedPlaces: List<String>,
    val isOtpVerified: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int,
    val bio: String?,
    val gender: String?
)

data class UpdateProfileResponse(
    val message: String,
    val user: User
)

// --- Request and Success Response Models ---

data class SignupRequest(val name: String, val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val message: String, val token: String, val user: User)
data class SignupResponse(val message: String, val token: String, val user: User)

// --- ✅ NEW: Password Reset Flow Models ---

data class SendOtpRequest(val email: String)
data class VerifyOtpRequest(val email: String, val otp: String)
data class ResetPasswordRequest(val email: String, val newPassword: String)

// A generic response for simple success messages from the API
data class GenericAuthResponse(val message: String)


// --- Error Response Models ---
data class LoginErrorResponse(val message: String)
data class SignupErrorResponse(val message: String, val details: List<String>?)




