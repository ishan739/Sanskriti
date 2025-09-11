package com.example.richculture.retro

import com.example.richculture.Data.Holiday
import retrofit2.http.GET
import retrofit2.http.Path

interface CalendarApi {

    // 🔹 All holidays for a year
    @GET("cal/year/{year}")
    suspend fun getHolidaysByYear(
        @Path("year") year: Int
    ): List<Holiday>

    // 🔹 Holidays for a specific month
    @GET("cal/month/{year}/{month}")
    suspend fun getHolidaysByMonth(
        @Path("year") year: Int,
        @Path("month") month: Int
    ): List<Holiday>

    // 🔹 Upcoming holidays
    @GET("cal/upcoming")
    suspend fun getUpcomingHolidays(): List<Holiday>
}
