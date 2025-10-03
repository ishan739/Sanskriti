package com.example.richculture.Data

data class AzadiChatRequest(
    val message: String,
    val leader_name: String,
    val conversation_id: String
)

data class AzadiChatResponse(
    val response: String,
    val conversation_id: String
)