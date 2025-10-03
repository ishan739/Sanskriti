package com.example.richculture.utility

import android.app.Application
import com.example.richculture.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SanskritiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SanskritiApplication)
            // ✅ CRITICAL FIX: Load your recipe book (appModule) into Koin
            modules(appModule)
        }
    }
}