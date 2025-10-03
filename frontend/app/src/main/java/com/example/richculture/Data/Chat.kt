package com.example.richculture.Data

// Request model
data class ChatRequest(
    val message: String,
    val conversation_id: String? = null // optional, in case you don’t want context tracking
)

// Response model
data class ChatResponse(
    val response: String,
    val conversation_id: String
)

