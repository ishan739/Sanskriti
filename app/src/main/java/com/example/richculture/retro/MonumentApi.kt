package com.example.richculture.retro

import com.example.richculture.Data.Monument
import retrofit2.http.GET
import retrofit2.http.Path

interface MonumentApi {
    @GET("monument/{id}")
    suspend fun getMonumentById(@Path("id") id: Int): Monument

    @GET("monument/district/{district}")
    suspend fun getMonumentsByDistrict(@Path("district") district: String): List<Monument>
}