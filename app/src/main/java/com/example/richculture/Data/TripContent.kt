package com.example.richculture.Data

data class TripResponse(
    val conversation_id: String,
    val places: List<Place>,
    val optimized_route: List<String>
)

data class Place(
    val budget: String,
    val duration: String,
    val location: String,
    val opening_time: String,
    val closing_time: String,
    val right_time_to_visit: String
)



data class TripRequest(
    val message: String,
    val role: String,
    val conversation_id: String
)