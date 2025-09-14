package com.example.richculture.Data

data class PostResponse(
    val _id: String,
    val author: Author,
    val media: String,
    val caption: String,
    val location: String,
    val likes: List<String>,
    val comments: List<Comment>,
    val createdAt: String,
    val updatedAt: String
)

data class Author(
    val _id: String,
    val name: String,
    val profileImage: String
)

data class Comment(
    val _id: String,
    val message: String,
    val author: Author,
    val createdAt: String
)
