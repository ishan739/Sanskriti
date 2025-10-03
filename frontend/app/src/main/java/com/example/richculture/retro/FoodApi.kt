package com.example.richculture.retro

import com.example.richculture.Data.Food
import retrofit2.http.GET
import retrofit2.http.Path

interface FoodApi {

    // Get all food items
    @GET("food")
    suspend fun getAllFoods(): List<Food>

    // Get food by ID
    @GET("food/{id}")
    suspend fun getFoodById(@Path("id") id: Int): List<Food>

    // Get foods by type (Veg / Non-Veg)
    @GET("food/type/{value}")
    suspend fun getFoodsByType(@Path("value") type: String): List<Food>

    // Get foods by region (North / South / East / West)
    @GET("food/region/{value}")
    suspend fun getFoodsByRegion(@Path("value") region: String): List<Food>
}
