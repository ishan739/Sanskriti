package com.example.richculture.Data

import com.google.gson.annotations.SerializedName

data class Event(
    val name: String,
    @SerializedName("booking_link") val bookingLink: String
)

data class EventRequest(
    val city: String
)

