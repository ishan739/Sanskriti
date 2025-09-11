package com.example.richculture.retro


import com.example.richculture.Data.Story
import retrofit2.http.GET
import retrofit2.http.Path

interface StoryApi {

    // Get all stories
    @GET("story")
    suspend fun getStories(): List<Story>

    // Get story by ID
    @GET("story/{id}")
    suspend fun getStoryById(@Path("id") id: Int): Story

    // Get stories by category name
    @GET("story/category/{name}")
    suspend fun getStoriesByCategory(@Path("name") category: String): List<Story>
}
