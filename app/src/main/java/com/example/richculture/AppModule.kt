package com.example.richculture

import com.example.richculture.ViewModels.*
import com.example.richculture.utility.PrefManager
import com.example.richculture.utility.ScanHistoryManager
import com.example.richculture.utility.SessionManager
import com.example.richculture.utility.TokenManager
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

    // ✅ UPDATED: Teach Koin that ScannerViewModel now needs ScanHistoryManager.
    viewModel { ScannerViewModel(get()) }
}

