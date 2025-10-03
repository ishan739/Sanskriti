package com.example.richculture.navigate

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.richculture.R

sealed class Screen(
    val route: String,
    val title: String,
    val drawableResId: Int,
    val color: Color? = null,
    val gradient: Brush? = null
) {
    // --- Bottom Navigation Bar Items ---
    object Home : Screen("home", "Home", R.drawable.ic_home, Color(0xFFFF9700), Brush.verticalGradient(listOf(Color(0xFF523201), Color(0xFFAF2700))))
    object Stories : Screen("stories", "Stories",R.drawable.ic_profile , Color(0xFF66BB6A), Brush.verticalGradient(listOf(Color(0xFFA5D6A7), Color(0xFF81C784))))
    object Trip : Screen("trip", "TripMate", R.drawable.tripmate, Color(0xFF7E57C2), Brush.verticalGradient(listOf(Color(0xFFB39DDB), Color(0xFF9575CD))))
    object Bazaar : Screen("bazaar", "Bazaar", R.drawable.ic_shopping, Color(0xFFEC407A), Brush.verticalGradient(listOf(Color(0xFFF8BBD0), Color(0xFFF48FB1))))
    object Chatbot : Screen("chatbot", "Chatbot", R.drawable.chatbot, Color(0xFF42A5F5), Brush.verticalGradient(listOf(Color(0xFF90CAF9), Color(0xFF64B5F6))))

    // --- Other App Screens ---
    object Profile : Screen("profile", "Profile", R.drawable.ic_profile)
    object Order : Screen("orders", "Orders", R.drawable.ic_cart)
    object HeritageExplorer : Screen("heritage_explorer", "Heritage Explorer", R.drawable.ic_heri)
    object ArtsAndTraditions : Screen("arts_traditions", "Arts & Traditions", R.drawable.ic_community)
    object FestivalsAndFood : Screen("festivals_food", "Festivals & Food", R.drawable.ic_camera)
    object ARScanAction : Screen("arscan_action", "AR Scan", R.drawable.ic_scan)
    object Camera : Screen("camera_screen", "Camera", -1)
    object AzadiChat : Screen("azadi_chat", "Azadi Chat", R.drawable.azaadichat)
    object FestiveCalendar : Screen("festive_calendar", "Festive Calendar", R.drawable.ic_calendar)
    object CommunityWall : Screen("community_wall", "Community", R.drawable.community)

    // ✅ NEW: Route for creating a new post
    object CreatePost : Screen("create_post", "Create Post", -1)

    // --- Startup Screens ---
    object Onboarding : Screen("onboarding", "Onboarding", -1)
    object Auth : Screen("auth", "Authentication", -1)

    object WebView : Screen("webview", "WebView", -1)

    object Cart : Screen("cart", "Cart", -1)

}

