package com.example.richculture.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.richculture.R
import com.example.richculture.navigate.Screen
import com.example.richculture.utility.PrefManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Data class to hold info for each onboarding page
private data class OnboardingPageInfo(
    val backgroundResId: Int,
    val title: String,
    val subtitle: String,
    val buttonText: String? = null // Button is optional for the auto-advancing splash
)

@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    val prefManager = remember { PrefManager(context) }
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPageInfo(
            backgroundResId = R.drawable.ic_tajmahal, // Replace with your high-res image
            title = "Sanskriti",
            subtitle = "Explore, Experience & Embrace\nIndia's Heritage"
        ),
        OnboardingPageInfo(
            backgroundResId = R.drawable.ic_hawa, // Replace with your high-res image
            title = "Architectural Wonders",
            subtitle = "Journey through India's magnificent architectural wonders and historical landmarks.",
            buttonText = "Next"
        ),
        OnboardingPageInfo(
            backgroundResId = R.drawable.ic_kathak, // Replace with your high-res image
            title = "Vibrant Traditions",
            subtitle = "Join the colorful festivals that bring communities together in joy and tradition.",
            buttonText = "Get Started"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    // ✅ This LaunchedEffect handles the auto-scroll from the first page
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == 0) {
            delay(2500) // Wait for 2.5 seconds on the intro splash
            scope.launch {
                pagerState.animateScrollToPage(1)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = pagerState.currentPage != 0 // Disable scroll on the first page
        ) { pageIndex ->
            val page = pages[pageIndex]
            OnboardingPageUI(
                page = page,
                onButtonClick = {
                    if (pageIndex < pages.size - 1) {
                        scope.launch {
                            pagerState.animateScrollToPage(pageIndex + 1)
                        }
                    } else {
                        // This is the last page, finish onboarding
                        prefManager.setOnboardingComplete()
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun OnboardingPageUI(
    page: OnboardingPageInfo,
    onButtonClick: () -> Unit
) {
    val serifFontFamily = FontFamily(Font(R.font.playfair_display_regular)) // Ensure you have this font in res/font

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = page.backgroundResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // A subtle gradient overlay to make text more readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 800f
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = page.title,
                color = Color.White,
                fontSize = 42.sp,
                fontFamily = serifFontFamily,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 50.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = page.subtitle,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Only show the button if buttonText is provided
            if (page.buttonText != null) {
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        text = page.buttonText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

