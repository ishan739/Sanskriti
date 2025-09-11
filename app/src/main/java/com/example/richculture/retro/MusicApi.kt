package com.example.richculture.retro


import com.example.richculture.Data.Music
import retrofit2.http.GET
import retrofit2.http.Path

interface MusicApi {

    @GET("music")
    suspend fun getAllMusic(): List<Music>

    @GET("music/{id}")
    suspend fun getMusicById(@Path("id") id: Int): Music
}