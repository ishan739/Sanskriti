package com.example.richculture.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

// ✅ Using the more detailed data class to support different UI for the final page
private data class OnboardingPageInfo(
    val title: String,
    val subtitle: String,
    val buttonText: String,
    val backgroundResId: Int,
    val isFinalPage: Boolean = false
)

// ✅ Using the more detailed content for the three onboarding screens
private val onboardingPages = listOf(
    OnboardingPageInfo(
        title = "Sanskriti",
        subtitle = "Discover India's Rich Cultural Heritage",
        buttonText = "Explore Heritage",
        backgroundResId = R.drawable.ic_hawa // TODO: Replace with your background image
    ),
    OnboardingPageInfo(
        title = "Uncover Timeless Stories",
        subtitle = "Dive into the rich tapestry of Indian folklore, mythology, and history. Every legend has a lesson, every story a soul.",
        buttonText = "I Am Curious",
        backgroundResId = R.drawable.ic_kathak // TODO: Replace with your background image
    ),
    OnboardingPageInfo(
        title = "Welcome to Sanskriti",
        subtitle = "Embark on a journey through India's magnificent cultural heritage. Explore monuments, traditions, festivals, and connect with our rich history.",
        buttonText = "Begin Your Journey",
        backgroundResId = R.drawable.ic_hawa2, // TODO: Replace with your background image
        isFinalPage = true
    )
)

@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    val prefManager = PrefManager(context)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val page = onboardingPages[pageIndex]

            // ✅ THE MAIN CHANGE: We now switch between two different UI layouts
            if (page.isFinalPage) {
                // Use the card-based UI for the last page
                FinalOnboardingPageUI(page = page) {
                    prefManager.setOnboardingComplete()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            } else {
                // Use the standard full-screen UI for the first two pages
                StandardOnboardingPageUI(page = page) {
                    scope.launch {
                        pagerState.animateScrollToPage(pageIndex + 1)
                    }
                }
            }
        }

        // Pager Indicator - only shown for the standard pages
        if (!pagerState.currentPage.equals(onboardingPages.size - 1)) {
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
                    .height(20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // We only show indicators for the first two pages
                repeat(onboardingPages.size - 1) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color(0xFFFF9700) else Color.White.copy(alpha = 0.5f)
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(12.dp)
                    )
                }
            }
        }
    }
}

// ✅ This is the UI for the first and second pages
@Composable
private fun StandardOnboardingPageUI(page: OnboardingPageInfo, onButtonClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = page.backgroundResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_main), // TODO: Replace with your app logo
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
                text = page.subtitle,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9700)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(page.buttonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ✅ This is the special UI for the final page
@Composable
private fun FinalOnboardingPageUI(page: OnboardingPageInfo, onButtonClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = page.backgroundResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_main), // TODO: Replace with your app logo
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(page.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(page.subtitle, textAlign = TextAlign.Center, color = Color.Gray)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onButtonClick,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFFFF6F00), Color(0xFFEC407A))),
                                shape = RoundedCornerShape(50)
                            ),
                        contentPadding = PaddingValues()
                    ) {
                        Text(page.buttonText, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

