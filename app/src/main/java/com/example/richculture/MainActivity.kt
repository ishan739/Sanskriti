package com.example.richculture

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import com.example.richculture.navigation.MainAppScaffold // Import the scaffold
import com.example.richculture.ui.theme.RichCultureTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            RichCultureTheme {
                // Now, MainActivity's only job is to call our main UI container.
                MainAppScaffold()
            }
        }
    }
}

