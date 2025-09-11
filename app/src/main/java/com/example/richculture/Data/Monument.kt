package com.example.richculture.Data // Using your specified package name

import com.google.gson.annotations.SerializedName

data class Monument(
    // Added annotations to map JSON keys to your class properties
    @SerializedName("_id")
    val mongoId: String, // It's safer to have a separate property for the MongoDB ID

    @SerializedName("id")
    val numericId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("district")
    val district: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("cover")
    val cover: String, // This now correctly maps to the 'cover' field in the JSON

    @SerializedName("furl")
    val furl: String,

    @SerializedName("surl")
    val surl: String,

    @SerializedName("turl")
    val turl: String
)


