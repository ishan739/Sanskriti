package com.example.richculture.Data

// ✅ User Signup Request
data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)

// ✅ Login Request
data class LoginRequest(
    val email: String,
    val password: String
)

// ✅ Login Response (structure is same as signup, so we reuse User)
data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)


// ✅ User object inside response
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


// for UPDATE profile API
typealias ProfileResponse = User

data class UpdateProfileResponse(
    val message: String,
    val user: User
)


// ✅ Signup Response
data class SignupResponse(
    val message: String,
    val token: String,
    val user: User,
    val details : String
)

// ✅ Post Upload Response

