package com.example.richculture.retro

import com.example.richculture.Data.ChatRequest
import com.example.richculture.Data.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {

    @POST("chat")
    suspend fun sendMessage(
        @Body request: ChatRequest
    ) : ChatResponse
}