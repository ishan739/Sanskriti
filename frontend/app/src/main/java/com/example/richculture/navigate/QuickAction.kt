package com.example.richculture.navigate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter


data class QuickAction(
    val title: String,
    val subtitle: String,
    val icon: Int,
    val route: String,
    // ✅ CHANGED: Storing the List of Colors directly is more flexible
    val gradientColors: List<Color>
)

data class ExploreItem(
    val title: String,
    val icon: Int,
    val imageResId: Int,
    val route: String,
    // ✅ CHANGED: Storing the List of Colors directly
    val gradientColors: List<Color>
)

// --- Dummy Data with Contextual Icons & Images ---

