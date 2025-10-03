package com.example.richculture.retro

import com.example.richculture.Data.Event
import com.example.richculture.Data.EventRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface EventsApi {
    @POST("get-events")
    suspend fun getEvents(@Body request: EventRequest): List<Event>
}
