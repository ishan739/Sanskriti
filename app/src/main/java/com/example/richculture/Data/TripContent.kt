package com.example.richculture.Data

data class Place(
    val budget: String,
    val duration: String,
    val location: String,
    val opening_time: String,
    val closing_time: String,
    val right_time_to_visit: String
)

data class TravelPlannerResponse(
    val conversation_id: String,
    val places: List<Place>
)


data class TravelPlannerRequest(
    val message: String,
    val role: String,
    val conversation_id: String
)