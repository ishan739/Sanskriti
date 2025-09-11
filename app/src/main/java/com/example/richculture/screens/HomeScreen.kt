package com.example.richculture.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.richculture.Data.Story
import com.example.richculture.R
import com.example.richculture.ViewModel.StoryViewModel
import com.example.richculture.navigate.ExploreItem
import com.example.richculture.navigate.QuickAction
import com.example.richculture.navigate.Screen
import java.util.concurrent.TimeUnit

// --- NEW VIBRANT THEME COLORS ---
private val vibrantTopColor = Color(0xFFFFF3E0) // Light Orange
private val vibrantBottomColor = Color(0xFFF3E5F5) // Light Purple
private val screenBackgroundBrush = Brush.verticalGradient(listOf(vibrantTopColor, vibrantBottomColor))
private val primaryTextColor = Color(0xFF3C2F2F)
private val secondaryTextColor = Color(0xFF7D6E6E)
private val accentMarigold = Color(0xFFFFA726)
private val accentTeal = Color(0xFF26A69A)
private val accentPink = Color(0xFFEC407A)
private val cardBackgroundColor = Color.White
private val topBarBrush = Brush.verticalGradient(listOf(Color(0xFFFFD180), Color(0xFFFFA726))) // Lighter orange gradient

// --- DATA SOURCE FOR THE UI (Updated with new gradients) ---
val quickActions = listOf(
    QuickAction("AR Scan", "Scan monuments", R.drawable.ic_scan, Screen.ARScanAction.route, listOf(accentTeal, Color(0xFF00796B))),
    QuickAction("Calendar", "Festival dates", R.drawable.ic_calendar, Screen.FestiveCalendar.route, listOf(accentMarigold, Color(0xFFF57C00))),
    QuickAction("Hub", "Share memories", R.drawable.ic_community, Screen.CommunityWall.route, listOf(accentPink, Color(0xFFD81B60)))
)

val exploreItems = listOf(
    ExploreItem("Heritage Explorer", R.drawable.ic_heri, R.drawable.ic_heri, Screen.HeritageExplorer.route, emptyList()),
    ExploreItem("Arts & Traditions", R.drawable.ic_arts, R.drawable.ic_arts_trad, Screen.ArtsAndTraditions.route, emptyList()),
    ExploreItem("Festivals & Food", R.drawable.ic_camera, R.drawable.ic_diwali, Screen.FestivalsAndFood.route, emptyList()),
    ExploreItem("Sacred Stories", R.drawable.ic_profile, R.drawable.ic_books, Screen.Stories.route, emptyList())
)

// Updated Highlight Brush
private val highlightBrush = Brush.horizontalGradient(listOf(accentMarigold, accentPink))

@Composable
fun HomeScreen(
    navController: NavController,
    storyViewModel: StoryViewModel = viewModel()
) {
    val storyOfTheDay by storyViewModel.storyOfTheDay.collectAsState()
    val isLoading by storyViewModel.isStoryOfTheDayLoading.collectAsState()
    val isPlaying by storyViewModel.isPlaying.collectAsState()
    val currentlyPlaying by storyViewModel.currentlyPlayingStory.collectAsState()

    var showStoryDialog by remember { mutableStateOf(false) }
    var selectedStory by remember { mutableStateOf<Story?>(null) }

    if (showStoryDialog && selectedStory != null) {
        StoryDetailDialog(story = selectedStory!!, viewModel = storyViewModel, onDismiss = { showStoryDialog = false })
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackgroundBrush), // Using new vibrant gradient
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { MainTopAppBar(navController) } // Pass NavController for navigation
        item { SectionHeader("Quick Actions") }
        item { QuickActionsRow(navController) }
        item { SectionHeader("Explore Heritage") }
        item { ExploreGrid(navController) }
        item { SectionHeader("Highlight") }
        item {
            if (isLoading) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accentMarigold)
                }
            } else {
                storyOfTheDay?.let { story ->
                    StoryOfTheDayCard(
                        story = story,
                        isPlaying = isPlaying && currentlyPlaying?.id == story.id,
                        onListenClick = { storyViewModel.playStory(story) },
                        onClick = {
                            selectedStory = story
                            showStoryDialog = true
                        }
                    )
                }
            }
        }
    }
}

// --- UPDATED HEADER COMPOSABLE ---
@Composable
fun MainTopAppBar(navController: NavController) { // Now accepts NavController
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = BottomArcShape(arcHeight = 30.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(topBarBrush) // Apply the new gradient here
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Namaste Harshi!",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White // Change text to white for contrast
                    )
                )
                Text(
                    text = "✨ Discover India's timeless heritage",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f) // Slightly transparent white
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* TODO notification click */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White.copy(alpha = 0.9f) // White icon
                    )
                }
                IconButton(onClick = { navController.navigate("profile") }) { // Profile navigation added
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Profile",
                        tint = Color.White.copy(alpha = 0.9f) // White icon
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = primaryTextColor,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp)
    )
}

// --- QUICK ACTIONS ---
@Composable
fun QuickActionsRow(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        quickActions.forEach { action ->
            QuickActionCard(action = action, navController = navController)
        }
    }
}

@Composable
fun QuickActionCard(action: QuickAction, navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clickable { navController.navigate(action.route) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Brush.verticalGradient(action.gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = action.icon),
                contentDescription = action.title,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = action.title,
            fontWeight = FontWeight.Bold,
            color = primaryTextColor,
            fontSize = 15.sp
        )
        Text(
            text = action.subtitle,
            color = secondaryTextColor,
            fontSize = 13.sp
        )
    }
}

// --- EXPLORE HERITAGE ---
@Composable
fun ExploreGrid(navController: NavController) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ExploreItemCard(item = exploreItems[0], navController = navController, modifier = Modifier.weight(1f))
            ExploreItemCard(item = exploreItems[1], navController = navController, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            exploreItems.getOrNull(2)?.let { ExploreItemCard(item = it, navController = navController, modifier = Modifier.weight(1f)) }
            exploreItems.getOrNull(3)?.let { ExploreItemCard(item = it, navController = navController, modifier = Modifier.weight(1f)) }
        }
    }
}

@Composable
fun ExploreItemCard(item: ExploreItem, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .aspectRatio(0.75f)
            .clickable { navController.navigate(item.route) },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 100f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// --- NEWLY REDESIGNED HIGHLIGHT CARD ---
@Composable
fun StoryOfTheDayCard(story: Story, isPlaying: Boolean, onClick: () -> Unit, onListenClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = story.thumbnail,
                contentDescription = story.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient Scrim for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 150f
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Highlight", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                    Text(
                        text = story.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = onListenClick,
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = accentPink,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

// --- STORY DIALOG (as provided) ---


// --- HELPER & CUSTOM SHAPE ---
private fun formatDuration(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, seconds)
}

class BottomArcShape(private val arcHeight: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val arcHeightPx = with(density) { arcHeight.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - arcHeightPx)
            quadraticBezierTo(
                size.width / 2, size.height,
                0f, size.height - arcHeightPx
            )
            close()
        }
        return Outline.Generic(path)
    }
}

