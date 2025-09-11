package com.example.richculture

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SanskritiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // This is where Koin is started for the whole app
        startKoin {
            androidContext(this@SanskritiApplication)
            modules(appModule)
        }
    }
}

