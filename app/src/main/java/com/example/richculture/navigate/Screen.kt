package com.example.richculture.navigate

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.richculture.R

/**
 * A unified sealed class to define all navigation routes in the app.
 *
 * @param route The unique string route for navigation.
 * @param title The user-facing title of the screen.
 * @param icon The ImageVector icon for the screen.
 * @param color The unique theme color, primarily for bottom navigation items. Null for other screens.
 * @param gradient The unique theme gradient, primarily for bottom navigation items. Null for other screens.
 */

sealed class Screen(
    val route: String,
    val title: String,
    val drawableResId: Int,
    val color: Color? = null,
    val gradient: Brush? = null
) {
    // --- Bottom Navigation Bar Items ---
    // These have non-null colors and gradients for the advanced UI.

    object Home : Screen(
        "home", "Home", R.drawable.ic_home, Color(0xFFFF9700),
        Brush.verticalGradient(listOf(Color(0xFF523201), Color(0xFFAF2700)))
    )
    object Stories : Screen(
        "stories", "Stories",R.drawable.ic_profile , Color(0xFF66BB6A),
        Brush.verticalGradient(listOf(Color(0xFFA5D6A7), Color(0xFF81C784)))
    )
    object Trip : Screen(
        "trip", "TripMate", R.drawable.ic_chatbot_avatar, Color(0xFF7E57C2),
        Brush.verticalGradient(listOf(Color(0xFFB39DDB), Color(0xFF9575CD)))
    )
    object Bazaar : Screen(
        "bazaar", "Bazaar", R.drawable.ic_bazaar, Color(0xFFEC407A),
        Brush.verticalGradient(listOf(Color(0xFFF8BBD0), Color(0xFFF48FB1)))
    )
    object Chatbot : Screen(
        "chatbot", "Chatbot", R.drawable.ic_chat, Color(0xFF42A5F5),
        Brush.verticalGradient(listOf(Color(0xFF90CAF9), Color(0xFF64B5F6)))
    )

    // --- Other App Screens ---
    // These do not need colors or gradients for the bottom nav.

    object Profile : Screen("profile", "Profile", R.drawable.ic_profile)
    object Order : Screen("orders", "Orders", R.drawable.ic_cart)
    object ARScan : Screen("arscan", "AR Scan", R.drawable.ic_scan)

    // Explore Sanskriti Screens
    object HeritageExplorer : Screen("heritage_explorer", "Heritage Explorer", R.drawable.ic_heri)
    object ArtsAndTraditions : Screen("arts_traditions", "Arts & Traditions", R.drawable.ic_community)
    object FestivalsAndFood : Screen("festivals_food", "Festivals & Food", R.drawable.ic_camera)

    // Quick Action Screens
    object ARScanAction : Screen("arscan_action", "AR Scan", R.drawable.ic_scan)
    object FestiveCalendar : Screen("festive_calendar", "Festive Calendar", R.drawable.ic_calendar)
    object CommunityWall : Screen("community_wall", "Community Wall", R.drawable.ic_community)
}

