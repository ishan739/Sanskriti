package com.example.richculture.retro


import com.example.richculture.Data.LoginRequest
import com.example.richculture.Data.LoginResponse
import com.example.richculture.Data.ProfileResponse
import com.example.richculture.Data.SignupRequest
import com.example.richculture.Data.SignupResponse
import com.example.richculture.Data.UpdateProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    // ✅ Signup
    @POST("user/signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>

    // ✅ Login
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("user/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): ProfileResponse

    @Multipart
    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part profileImage: MultipartBody.Part?,
        @Part("name") name: RequestBody?,
        @Part("bio") bio: RequestBody?,
        @Part("gender") gender: RequestBody?
    ): UpdateProfileResponse
}
