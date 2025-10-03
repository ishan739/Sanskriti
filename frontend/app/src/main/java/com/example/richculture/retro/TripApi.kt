package com.example.richculture.retro

import com.example.richculture.Data.TripRequest
import com.example.richculture.Data.TripResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TripApi {
    @POST("chat")
    suspend fun getPlaces(@Body request: TripRequest): TripResponse
}