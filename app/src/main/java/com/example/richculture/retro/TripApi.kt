package com.example.richculture.retro

import com.example.richculture.Data.TravelPlannerRequest
import com.example.richculture.Data.TravelPlannerResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface TravelPlannerApi {

    @POST("chat")
    @Headers("Content-Type: application/json") // ✅ Added for safety
    suspend fun getPlaces(
        @Body request: TravelPlannerRequest
    ): TravelPlannerResponse
}
