package com.example.richculture.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.richculture.ViewModels.MainViewModel
import com.example.richculture.navigate.NavigationGraph
import com.example.richculture.navigate.Screen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainAppScaffold(mainViewModel: MainViewModel) {
    val startDestination by mainViewModel.startDestination.collectAsState()

    Crossfade(targetState = startDestination, animationSpec = tween(500), label = "AppStartAnimation") { destination ->
        if (destination != null) {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val bottomNavRoutes = setOf(
                Screen.Home.route,
                Screen.Bazaar.route,
                Screen.Trip.route,
                Screen.CommunityWall.route
            )

            Scaffold(
                bottomBar = {
                    if (currentRoute in bottomNavRoutes) {
                        BottomNavBar(navController)
                    }
                },
                containerColor = Color.Transparent,
                // ✅ CRITICAL: Let Scaffold handle window insets properly for immersive mode
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->
                // ✅ FIXED: Use ALL padding values from Scaffold, including bottom padding
                NavigationGraph(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding), // This includes bottom padding for nav bar
                    startDestination = destination
                )
            }
        } else {
            // This is the loading state UI shown before the ViewModel decides the destination.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFF8F5)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}