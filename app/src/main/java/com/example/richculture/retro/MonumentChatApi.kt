package com.example.richculture.retro

import com.example.richculture.Data.MonumentChatRequest
import com.example.richculture.Data.MonumentChatResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MonumentChatApi {
    @POST("chat")
    @Headers("Content-Type: application/json")
    suspend fun sendMessage(@Body request: MonumentChatRequest): MonumentChatResponse
}