package com.example.richculture.retro

import CommunityApi
import UserApi
import com.example.richculture.Data.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    // --- URLs for all our different services ---
    private const val BASE_URL = "https://sanskriti-p2v9.onrender.com/api/"
    private const val TOUR_GUIDE_CHAT_URL = "https://tour-guide-tvmf.onrender.com/"
    private const val AZADI_CHAT_URL = "https://bose-glq9.onrender.com/"
    private const val TRAVEL_PLANNER_URL = "https://travel-planner-os5y.onrender.com/"
    private const val MONUMENT_CHAT_URL = "https://kalam-0bny.onrender.com/"


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    // --- Retrofit instances for each service ---
    private val retrofit by lazy { createRetrofit(BASE_URL) }
    private val tourGuideChatRetrofit by lazy { createRetrofit(TOUR_GUIDE_CHAT_URL) }
    private val travelPlannerRetrofit by lazy { createRetrofit(TRAVEL_PLANNER_URL) }
    private val azadiChatRetrofit by lazy { createRetrofit(AZADI_CHAT_URL) }
    private val monumentChatRetrofit by lazy { createRetrofit(MONUMENT_CHAT_URL) }


    // --- ✅ CORRECTED: All APIs are now present and correct ---

    // APIs from the main BASE_URL
    val monumentApi: MonumentApi by lazy { retrofit.create(MonumentApi::class.java) }
    val festivalApi: FestivalApi by lazy { retrofit.create(FestivalApi::class.java) }
    val storyApi: StoryApi by lazy { retrofit.create(StoryApi::class.java) }
    val foodApi: FoodApi by lazy { retrofit.create(FoodApi::class.java) }
    val danceApi: DanceApi by lazy { retrofit.create(DanceApi::class.java) }
    val musicApi: MusicApi by lazy { retrofit.create(MusicApi::class.java) }
    val artApi: ArtApi by lazy { retrofit.create(ArtApi::class.java) }
    val calendarApi: CalendarApi by lazy { retrofit.create(CalendarApi::class.java) }
    val communityApi: CommunityApi by lazy { retrofit.create(CommunityApi::class.java) }
    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
    val scannerApi : ScannerApi by lazy { retrofit.create(ScannerApi::class.java) }

    // APIs from their specific URLs
    val travelPlannerApi: TravelPlannerApi by lazy { travelPlannerRetrofit.create(TravelPlannerApi::class.java) }
    val chatApi: ChatApi by lazy { tourGuideChatRetrofit.create(ChatApi::class.java) } // General Assistant
    val azadiChatApi: AzadiChatApi by lazy { azadiChatRetrofit.create(AzadiChatApi::class.java) } // Chat with Leaders
    val monumentChatApi: MonumentChatApi by lazy { monumentChatRetrofit.create(MonumentChatApi::class.java) } // Chat with Monuments
}

