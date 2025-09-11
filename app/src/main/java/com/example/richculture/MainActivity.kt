package com.example.richculture

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import com.example.richculture.ViewModels.MainViewModel
import com.example.richculture.navigation.MainAppScaffold // Import the scaffold
import com.example.richculture.ui.theme.RichCultureTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            RichCultureTheme {
                // ✅ Pass the ViewModel created by Koin to the Scaffold
                val mainViewModel: MainViewModel = koinViewModel()
                MainAppScaffold(mainViewModel = mainViewModel)
            }
        }
    }
}

