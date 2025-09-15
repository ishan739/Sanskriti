package com.example.richculture.retro
import CommunityApi
import UserApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitInstance {
    private const val BASE_URL = "https://sanskriti-p2v9.onrender.com/api/"
    private const val CHAT_BASE_URL = "https://tour-guide-r6vq.onrender.com/"
    private const val BHAGAT_URL = "https://bhagat-3ijm.onrender.com/"
    private const val BOSE_URL = "https://bose.onrender.com/"
    private const val KALAM_URL = "https://kalam-jgzi.onrender.com/"
    private const val VIVEKANANDA_URL = "https://vivekananda-ac4o.onrender.com/"
    private const val GANDHI_URL = "https://gandhi-hg9i.onrender.com/"

    private const val TRAVEL_PLANNER_URL = "https://travel-planner-os5y.onrender.com/"


    // ✅ NEW: Create a logging interceptor to see network traffic in Logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // This logs headers and the request/response body
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            // ✅ NEW: Add the logger to our client
            .addInterceptor(loggingInterceptor)
            .build()
    }


    // Helper function to avoid repeating code
    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    // ----- Retrofit instances using the helper -----
    private val travelPlannerRetrofit by lazy { createRetrofit(TRAVEL_PLANNER_URL) }

    private val retrofit by lazy { createRetrofit(BASE_URL) }
    private val chatRetrofit by lazy { createRetrofit(CHAT_BASE_URL) }
    private val bhagatRetrofit by lazy { createRetrofit(BHAGAT_URL) }
    private val boseRetrofit by lazy { createRetrofit(BOSE_URL) }
    private val kalamRetrofit by lazy { createRetrofit(KALAM_URL) }
    private val vivekanandaRetrofit by lazy { createRetrofit(VIVEKANANDA_URL) }
    private val gandhiRetrofit by lazy { createRetrofit(GANDHI_URL) }


    // ----- Normal APIs -----
    val monumentApi: MonumentApi by lazy { retrofit.create(MonumentApi::class.java) }
    val festivalApi: FestivalApi by lazy { retrofit.create(FestivalApi::class.java) }
    val storyApi: StoryApi by lazy { retrofit.create(StoryApi::class.java) }
    val foodApi: FoodApi by lazy { retrofit.create(FoodApi::class.java) }
    val danceApi: DanceApi by lazy { retrofit.create(DanceApi::class.java) }
    val musicApi: MusicApi by lazy { retrofit.create(MusicApi::class.java) }
    val artApi: ArtApi by lazy { retrofit.create(ArtApi::class.java) }
    val calendarApi: CalendarApi by lazy { retrofit.create(CalendarApi::class.java) }

    // ----- Chat APIs -----
    val chatApi: ChatApi by lazy { chatRetrofit.create(ChatApi::class.java) }
    val bhagatApi: ChatApi by lazy { bhagatRetrofit.create(ChatApi::class.java) }
    val boseApi: ChatApi by lazy { boseRetrofit.create(ChatApi::class.java) }
    val kalamApi: ChatApi by lazy { kalamRetrofit.create(ChatApi::class.java) }
    val vivekanandaApi: ChatApi by lazy { vivekanandaRetrofit.create(ChatApi::class.java) }
    val gandhiApi: ChatApi by lazy { gandhiRetrofit.create(ChatApi::class.java) }

//    Community Api

    val communityApi: CommunityApi by lazy { retrofit.create(CommunityApi::class.java) }

    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }

    val travelPlannerApi: TravelPlannerApi by lazy { travelPlannerRetrofit.create(TravelPlannerApi::class.java) }

    val scannerApi : ScannerApi by lazy { retrofit.create(ScannerApi::class.java) }


}