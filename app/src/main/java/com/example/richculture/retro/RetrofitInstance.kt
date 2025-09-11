package com.example.richculture.retro

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://sanskriti-p2v9.onrender.com/api/"
    private const val CHAT_BASE_URL = "https://tour-guide-r6vq.onrender.com/"
    private val chatRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(CHAT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    val monumentApi: MonumentApi by lazy { retrofit.create(MonumentApi::class.java) }
    val festivalApi: FestivalApi by lazy { retrofit.create(FestivalApi::class.java) }
    val storyApi: StoryApi by lazy { retrofit.create(StoryApi::class.java) }
    val foodApi: FoodApi by lazy { retrofit.create(FoodApi::class.java) }
    val danceApi: DanceApi by lazy { retrofit.create(DanceApi::class.java) }
    val musicApi: MusicApi by lazy { retrofit.create(MusicApi::class.java) }
    val artApi: ArtApi by lazy { retrofit.create(ArtApi::class.java) }

    // ✅ New ChatApi with different base URL
    val chatApi: ChatApi by lazy { chatRetrofit.create(ChatApi::class.java) }

    val calendarApi: CalendarApi by lazy { retrofit.create(CalendarApi::class.java) }

}

