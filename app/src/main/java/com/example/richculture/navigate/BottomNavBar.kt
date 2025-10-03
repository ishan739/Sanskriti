package com.example.richculture.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.richculture.navigate.Screen

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.CommunityWall,
        Screen.Trip,
        Screen.Bazaar
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // This determines if the bottom bar should be shown.
    val shouldShowBottomBar = items.any { it.route == currentRoute }

    AnimatedVisibility(visible = shouldShowBottomBar) {
        // ✅ FIXED: Solid bottom nav that prevents content overlap
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // Fixed height for consistent spacing
                .background(Color.White) // Solid white background
            // No padding or elevation effects that cause floating
            ,
            horizontalArrangement = Arrangement.SpaceEvenly, // Even spacing
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val isSelected = currentRoute == screen.route
                SolidNavBarItem(
                    screen = screen,
                    isSelected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SolidNavBarItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // ✅ Color animation for selected state
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF2196F3) else Color.Gray, // Blue for selected, gray for unselected
        animationSpec = tween(300),
        label = "icon_color"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.Black else Color.Gray,
        animationSpec = tween(300),
        label = "text_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // No ripple effect
                onClick = onClick
            )
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        // ✅ Icon with orange dot indicator for active item
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = screen.drawableResId),
                contentDescription = screen.title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            // ✅ Orange dot indicator (matches your image)
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF9800)) // Orange dot
                        .offset(y = (-12).dp) // Position above icon
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ✅ Always show text (not animated visibility)
        Text(
            text = screen.title,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}