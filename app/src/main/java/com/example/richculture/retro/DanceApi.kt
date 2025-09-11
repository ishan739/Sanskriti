package com.example.richculture.retro

import com.example.richculture.Data.Dance
import retrofit2.http.GET
import retrofit2.http.Path

interface DanceApi {

    @GET("dance")
    suspend fun getAllDances(): List<Dance>

    @GET("dance/{id}")
    suspend fun getDanceById(@Path("id") id: Int): Dance
}
