package com.example.richculture.navigate

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.richculture.screens.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {

    // Define animations once to reuse for all screens
    val enterTransition = slideInHorizontally(
        initialOffsetX = { 1000 },
        animationSpec = tween(400)
    ) + fadeIn(animationSpec = tween(400))

    val exitTransition = slideOutHorizontally(
        targetOffsetX = { -1000 },
        animationSpec = tween(400)
    ) + fadeOut(animationSpec = tween(400))

    val popEnterTransition = slideInHorizontally(
        initialOffsetX = { -1000 },
        animationSpec = tween(400)
    ) + fadeIn(animationSpec = tween(400))

    val popExitTransition = slideOutHorizontally(
        targetOffsetX = { 1000 },
        animationSpec = tween(400)
    ) + fadeOut(animationSpec = tween(400))

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Bottom Nav Screens
        composable(
            route = Screen.Home.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { HomeScreen(navController) }

        composable(
            route = Screen.ARScan.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { ArScanScreen() }

        composable(
            route = Screen.Stories.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { StoriesScreen(navController) }

        composable(
            route = Screen.Bazaar.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { BazaarScreen(navController) }

        composable(
            route = Screen.Chatbot.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { ChatbotScreen(navController) }

        composable(
            route = Screen.Profile.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { ProfileScreen(navController) }

        composable(
            route = Screen.Order.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { CartScreen(navController) }

        // Explore Sanskriti Screens
        composable(
            route = Screen.HeritageExplorer.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { HeritageExplorerScreen(navController) }

        composable(
            route = Screen.Trip.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { TripMateScreen(navController) }

        composable(
            route = Screen.ArtsAndTraditions.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { ArtsAndTraditionsScreen(navController) }

        composable(
            route = Screen.FestivalsAndFood.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { FestivalsAndFoodScreen(navController) }

        // Quick Action Screens
        composable(
            route = Screen.ARScanAction.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { ArScanScreen() } // same as ARScan

        composable(
            route = Screen.FestiveCalendar.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { FestiveCalendarScreen(navController) }

        composable(
            route = Screen.CommunityWall.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { CommunityWallScreen(navController) }
    }
}