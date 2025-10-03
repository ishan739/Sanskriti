package com.example.richculture.retro

import com.example.richculture.Data.AzadiChatRequest
import com.example.richculture.Data.AzadiChatResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface AzadiChatApi {
    @POST("chat")
    @Headers("Content-Type: application/json")
    suspend fun sendMessage(@Body request: AzadiChatRequest): AzadiChatResponse
}