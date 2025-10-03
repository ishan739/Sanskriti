package com.example.richculture.retro

import com.example.richculture.Data.ChatRequest
import com.example.richculture.Data.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatApi {
    // ✅ THE FIX: This header tells the server we are sending JSON.
    // This is automatically handled by Postman, but must be explicit in Retrofit.
    @Headers("Content-Type: application/json")
    @POST("chat")
    suspend fun sendMessage(
        @Body request: ChatRequest
    ): ChatResponse
}
