package com.example.richculture.di

import com.example.richculture.ViewModels.*
import com.example.richculture.utility.PrefManager
import com.example.richculture.utility.ScanHistoryManager
import com.example.richculture.utility.SessionManager
import com.example.richculture.utility.TokenManager
import com.example.richculture.viewmodel.BazaarViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Singletons
    single { PrefManager(androidContext()) }
    single { TokenManager(androidContext()) }
    single { SessionManager(androidContext()) }
    single { ScanHistoryManager(androidContext()) }

    // ViewModels
    viewModel { MainViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { CommunityViewModel() }
    viewModel { TravelPlannerViewModel() }
    viewModel { ScannerViewModel(get()) }
    viewModel { StoryViewModel(get()) }

    // ✅ CRITICAL FIX: Added the missing recipes for both chat ViewModels
    viewModel { AzadiChatViewModel() }
    viewModel { ChatbotViewModel() }
    viewModel { MonumentViewModel() }
    viewModel { MonumentChatViewModel() }
    viewModel { BazaarViewModel() }
}
