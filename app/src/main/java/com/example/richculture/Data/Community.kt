package com.example.richculture.Data

// ✅ NEW: Data class for sending a new comment to the API
data class AddCommentRequest(
    val message: String
)

// Post Upload Response
data class PostResponse(
    val _id: String,
    val author: Author?, // Made nullable for safety
    val media: String?,
    val caption: String?,
    val location: String?,
    val likes: List<String>,
    val comments: List<Comment>,
    val createdAt: String?,
    val updatedAt: String?
)

data class Author(
    val _id: String,
    val name: String?, // Made nullable for safety
    val profileImage: String
)

data class Comment(
    val _id: String,
    val message: String?,
    val author: Author?,
    val createdAt: String? // Made nullable for safety
)

