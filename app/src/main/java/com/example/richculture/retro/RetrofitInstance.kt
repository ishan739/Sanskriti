package com.example.richculture.retro


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val monumentApi: MonumentApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://sanskriti-p2v9.onrender.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MonumentApi::class.java)
    }

    val festivalApi: FestivalApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://sanskriti-p2v9.onrender.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FestivalApi::class.java)
    }

    val storyApi: StoryApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://sanskriti-p2v9.onrender.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StoryApi::class.java)
    }

    val foodApi: FoodApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://sanskriti-p2v9.onrender.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FoodApi::class.java)
    }
}




object NewsApiClient {
    val api: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }
}
