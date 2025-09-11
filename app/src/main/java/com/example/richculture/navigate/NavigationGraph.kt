package com.example.richculture.navigate

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.richculture.screens.*
import com.example.richculture.utility.cubeEnterTransition
import com.example.richculture.utility.cubeExitTransition
import com.example.richculture.utility.cubePopEnterTransition
import com.example.richculture.utility.cubePopExitTransition

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- Onboarding & Auth Screens (No special transition) ---
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }
        composable(Screen.Auth.route) {
            AuthScreen(navController)
        }

        // --- Main App Screens with CUBE TRANSITION ---
        val allScreens = listOf(
            Screen.Home, Screen.Stories, Screen.Bazaar, Screen.Chatbot, Screen.Profile,
            Screen.Order, Screen.HeritageExplorer, Screen.Trip, Screen.ArtsAndTraditions,
            Screen.FestivalsAndFood, Screen.ARScanAction, Screen.FestiveCalendar, Screen.CommunityWall
        )

        // Using a loop makes the NavHost cleaner and more maintainable
        allScreens.forEach { screen ->
            composable(
                route = screen.route,
                enterTransition = { cubeEnterTransition() },
                exitTransition = { cubeExitTransition() },
                popEnterTransition = { cubePopEnterTransition() },
                popExitTransition = { cubePopExitTransition() }
            ) {
                // Determine which screen composable to show based on the route
                when (screen) {
                    Screen.Home -> HomeScreen(navController)
                    Screen.Stories -> StoriesScreen(navController)
                    Screen.Bazaar -> BazaarScreen(navController)
                    Screen.Chatbot -> ChatbotScreen(navController)
                    Screen.Profile -> ProfileScreen(navController)
                    Screen.Order -> CartScreen(navController)
                    Screen.HeritageExplorer -> HeritageExplorerScreen(navController)
                    Screen.Trip -> TripMateScreen(navController)
                    Screen.ArtsAndTraditions -> ArtsAndTraditionsScreen(navController)
                    Screen.FestivalsAndFood -> FestivalsAndFoodScreen(navController)
                    Screen.ARScanAction -> ArScanScreen(navController)
                    Screen.FestiveCalendar -> FestiveCalendarScreen(navController)
                    Screen.CommunityWall -> CommunityWallScreen(navController)
                    // Add cases for any other screens here
                    else -> {}
                }
            }
        }
    }
}

