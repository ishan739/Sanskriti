package com.example.richculture.Data


data class Dance(
    val _id: String,
    val id: Int,
    val name: String,
    val type: String,
    val origin: String,
    val description: String,
    val imageurl: String,
    val videourl: String?,   // Sometimes empty
    val wikiurl: String,
    val __v: Int,
    val createdAt: String,
    val updatedAt: String
)
