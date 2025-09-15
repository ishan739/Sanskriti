package com.example.richculture.screens

import androidx.annotation.DrawableRes
import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.richculture.R
import com.example.richculture.navigate.Screen
import com.example.richculture.utility.PrefManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class MediaType {
    IMAGE, VIDEO
}

private data class OnboardingPageInfo(
    val mediaType: MediaType,
    @DrawableRes val imageResId: Int? = null,
    @RawRes val videoResId: Int? = null,
    val title: String,
    val subtitle: String,
    val buttonText: String? = null
)

@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    val prefManager = remember { PrefManager(context) }
    val scope = rememberCoroutineScope()
    var isMuted by remember { mutableStateOf(false) }

    val pages = listOf(
        OnboardingPageInfo(
            mediaType = MediaType.IMAGE,
            imageResId = R.drawable.ic_taj,
            title = "Sanskriti",
            subtitle = "Explore, Experience & Embrace\nIndia's Heritage"
        ),
        OnboardingPageInfo(
            mediaType = MediaType.VIDEO,
            videoResId = R.raw.ic_intro_video_1,
            title = "Architectural Wonders",
            subtitle = "Journey through India's magnificent architectural wonders and historical landmarks.",
            buttonText = "Next"
        ),
        OnboardingPageInfo(
            mediaType = MediaType.VIDEO,
            videoResId = R.raw.ic_intro_video_3,
            title = "Vibrant Traditions",
            subtitle = "Join the colorful festivals that bring communities together in joy and tradition.",
            buttonText = "Get Started"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    // ⏩ Auto-advance first page to second
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == 0) {
            delay(2500)
            scope.launch { pagerState.animateScrollToPage(1) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = pagerState.currentPage != 0
        ) { pageIndex ->
            val page = pages[pageIndex]
            OnboardingPageUI(
                page = page,
                isCurrentPage = pagerState.currentPage == pageIndex,
                isMuted = isMuted,
                onButtonClick = {
                    if (pageIndex < pages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pageIndex + 1) }
                    } else {
                        prefManager.setOnboardingComplete()
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // 🔊 Volume toggle button
        AnimatedVisibility(
            visible = pagerState.currentPage > 0,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { isMuted = !isMuted },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Image(
                    painter = painterResource(
                        id = if (isMuted) R.drawable.ic_volume_off else R.drawable.ic_volume_on
                    ),
                    contentDescription = "Toggle Sound"
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageUI(
    page: OnboardingPageInfo,
    isCurrentPage: Boolean,
    isMuted: Boolean,
    onButtonClick: () -> Unit
) {
    val serifFontFamily = FontFamily(Font(R.font.playfair_display_regular))

    Box(modifier = Modifier.fillMaxSize()) {
        when (page.mediaType) {
            MediaType.IMAGE -> {
                Image(
                    painter = painterResource(id = page.imageResId!!),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            MediaType.VIDEO -> {
                VideoBackground(
                    videoResId = page.videoResId!!,
                    isActive = isCurrentPage,
                    isMuted = isMuted
                )
            }
        }

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
            if (page.buttonText != null) {
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
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

@OptIn(UnstableApi::class)
@Composable
private fun VideoBackground(
    @RawRes videoResId: Int,
    isActive: Boolean,
    isMuted: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 🎥 Each video page gets its own player instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE // ✅ Loop current video forever
        }
    }

    // Only load + play when this page is active
    LaunchedEffect(isActive, isMuted) {
        if (isActive) {
            val mediaItem = MediaItem.fromUri("android.resource://${context.packageName}/$videoResId")
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            exoPlayer.volume = if (isMuted) 0f else 1f
        } else {
            exoPlayer.pause()
        }
    }

    // Release when not needed
    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> if (isActive) exoPlayer.play()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
