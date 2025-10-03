package com.example.richculture.retro

import com.example.richculture.Data.Art
import retrofit2.http.GET
import retrofit2.http.Path

interface ArtApi {

    @GET("art")
    suspend fun getAllArts(): List<Art>

    @GET("art/{id}")
    suspend fun getArtById(@Path("id") id: Int): Art
}