package com.example.richculture.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
        Screen.Chatbot,
        Screen.Trip,
        Screen.Bazaar
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(bottom = 5.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(60.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
                .clip(CircleShape)
                // ✅ Use a semi-transparent background for the glass effect
                .background(Color.White.copy(alpha = 0.65f))
                .border(
                    BorderStroke(1.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.3f)))),
                    CircleShape
                ),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val isSelected = currentRoute == screen.route
                UltraModernNavBarItem(
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

// UltraModernNavBarItem function remains the same...
@Composable
private fun UltraModernNavBarItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "scale"
    )
    val indicatorHeight by animateDpAsState(
        targetValue = if (isSelected) 4.dp else 0.dp,
        animationSpec = spring(),
        label = "indicator_height"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "glow_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse_anim"
    )
    val glowRadius by animateFloatAsState(
        targetValue = if (isSelected) 25f * pulse else 0f,
        animationSpec = tween(500),
        label = "glow_radius"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) screen.color ?: MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.8f),
        animationSpec = tween(300),
        label = "icon_color"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) screen.color ?: MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(300),
        label = "text_color"
    )
    val textStyleWithShadow = TextStyle(
        shadow = Shadow(color = Color.Black.copy(alpha = 0.3f), offset = Offset(1f, 2f), blurRadius = 4f)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(top = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .height(indicatorHeight)
                .width(20.dp)
                .background(screen.color ?: Color.Transparent, CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(65.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .shadow(12.dp, CircleShape, spotColor = screen.color ?: Color.Transparent)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                        .border(
                            1.dp,
                            screen.gradient ?: Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                            CircleShape
                        )
                )
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawBehind {
                        if (glowRadius > 0) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf((screen.color ?: Color.Transparent).copy(alpha = 0.4f), Color.Transparent),
                                    radius = glowRadius * size.minDimension / 2,
                                ),
                                radius = glowRadius * size.minDimension / 2
                            )
                        }
                    }
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = screen.drawableResId),
                    contentDescription = screen.title,
                    tint = Color.Black,
                    modifier = Modifier.size(26.dp)
                )

                AnimatedVisibility(visible = isSelected) {
                    Text(
                        text = screen.title,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        style = textStyleWithShadow
                    )
                }
            }
        }
    }
}

