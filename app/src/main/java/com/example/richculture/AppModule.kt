package com.example.richculture

import com.example.richculture.ViewModels.MainViewModel
import com.example.richculture.utility.PrefManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Tells Koin how to create a single instance of PrefManager
    single { PrefManager(androidContext()) }

    // Tells Koin how to create a MainViewModel and injects PrefManager into it
    viewModel { MainViewModel(get()) }
}
