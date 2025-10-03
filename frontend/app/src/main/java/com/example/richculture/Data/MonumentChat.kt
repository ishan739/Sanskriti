package com.example.richculture.Data


data class MonumentChatRequest(
    val message: String,
    val place_name: String,
    val conversation_id: String
)

data class MonumentChatResponse(
    val response: String,
    val conversation_id: String
)
