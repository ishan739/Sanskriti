package com.example.richculture.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.richculture.R
import com.example.richculture.navigate.Screen
import com.example.richculture.utility.PrefManager
import kotlinx.coroutines.launch

// Data class to hold info for each onboarding page
data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val buttonText: String
)

@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    val prefManager = remember { PrefManager(context) }
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(R.drawable.ic_diwali, "Discover India's Rich Cultural Heritage", "Explore Heritage"),
        OnboardingPage(R.drawable.ic_diwali, "Plan Your Next Adventure", "I Am Curious"),
        OnboardingPage(R.drawable.ic_diwali, "Connect with a Vibrant Community", "I Understand")
    )

    // ✅ FIX 1: Pass the page count to rememberPagerState
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Box(modifier = Modifier.fillMaxSize()) {
        // ✅ FIX 2: The 'count' parameter is removed from HorizontalPager.
        // The state now controls the page count.
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val page = pages[pageIndex]
            OnboardingPageContent(
                page = page,
                isLastPage = pageIndex == pages.size - 1,
                onNext = {
                    scope.launch {
                        if (pageIndex < pages.size - 1) {
                            pagerState.animateScrollToPage(pageIndex + 1)
                        }
                    }
                },
                onFinish = {
                    prefManager.setOnboardingComplete()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // ✅ FIX 3: Replaced the removed 'HorizontalPagerIndicator' with a custom Row-based indicator.
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 150.dp)
                .height(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pages.size) { iteration ->
                val color by animateColorAsState(
                    targetValue = if (pagerState.currentPage == iteration) Color(0xFFFF9700) else Color.White.copy(alpha = 0.5f),
                    animationSpec = tween(300),
                    label = "indicator_color"
                )
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    isLastPage: Boolean,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Your app logo
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )

            Text(
                text = "Sanskriti",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = page.title,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = if (isLastPage) onFinish else onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9700))
            ) {
                Text(text = page.buttonText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

