package com.example.richculture.Data

data class Holiday(
    val name: String,
    val description: String,
    val country: Country,
    val date: DateInfo,
    val type: List<String>,
    val primary_type: String,
    val canonical_url: String,
    val urlid: String,
    val locations: String,
    val states: String
)

data class Country(
    val id: String,
    val name: String
)

data class DateInfo(
    val iso: String,
    val datetime: DateTime,
    val timezone: TimeZoneInfo? = null // Only present in some cases (like Equinox)
)

data class DateTime(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int? = null,   // Nullable → only present for events with exact time
    val minute: Int? = null,
    val second: Int? = null
)

data class TimeZoneInfo(
    val offset: String,
    val zoneabb: String,
    val zoneoffset: Int,
    val zonedst: Int,
    val zonetotaloffset: Int
)

