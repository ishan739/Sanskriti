package com.example.richculture

import com.example.richculture.ViewModels.CommunityViewModel
import com.example.richculture.ViewModels.MainViewModel
import com.example.richculture.ViewModels.UserViewModel
import com.example.richculture.utility.PrefManager
import com.example.richculture.utility.SessionManager
import com.example.richculture.utility.TokenManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Manages onboarding status
    single { PrefManager(androidContext()) }
    // Manages the raw auth token string
    single { TokenManager(androidContext()) }

    // ✅ NEW: Manages the global user session state (singleton)
    single { SessionManager(androidContext()) }

    // ViewModels
    viewModel { MainViewModel(get()) }
    // ✅ UPDATED: UserViewModel now depends on SessionManager
    viewModel { UserViewModel(get()) }
    viewModel { CommunityViewModel() }
}

