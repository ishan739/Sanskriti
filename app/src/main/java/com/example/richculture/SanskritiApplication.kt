package com.example.richculture

import android.app.Application
import com.example.richculture.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

// This class runs before any of your screens. It's the main power switch.
class SanskritiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // This line turns on the Koin "machine" and loads all your recipes.
        startKoin {
            androidContext(this@SanskritiApplication)
            modules(appModule)
        }
    }
}

