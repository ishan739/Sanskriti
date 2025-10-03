package com.example.richculture.retro

import com.example.richculture.Data.Scanner
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ScannerApi {
    @Multipart
    @POST("predict/up")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<Scanner>
}