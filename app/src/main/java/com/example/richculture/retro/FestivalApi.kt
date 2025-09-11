package com.example.richculture.retro

import com.example.richculture.Data.Festival
import retrofit2.http.GET
import retrofit2.http.Path

interface FestivalApi {

    @GET("festival/{id}")
    suspend fun getFestivalById(@Path("id") id: Int): Festival

    // Corrected function name to match what the ViewModel will call
    @GET("festival/religion/{religion}")
    suspend fun getFestivalByReligion(@Path("religion") religion: String): List<Festival>

    // Corrected function name to match what the ViewModel will call
    @GET("festival/region/{region}")
    suspend fun getFestivalByRegion(@Path("region") region: String): List<Festival>
}