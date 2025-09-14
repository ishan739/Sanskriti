package com.example.richculture.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.richculture.ViewModels.StoryViewModel
import com.example.richculture.ViewModels.UserViewModel
import com.example.richculture.navigate.ExploreItem
import com.example.richculture.navigate.QuickAction
import com.example.richculture.navigate.Screen
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.TimeUnit

// --- VIBRANT THEME COLORS ---
private val vibrantTopColor = Color(0xFFFFF3E0)
private val vibrantBottomColor = Color(0xFFF3E5F5)
private val screenBackgroundBrush = Brush.verticalGradient(listOf(vibrantTopColor, vibrantBottomColor))
private val primaryTextColor = Color(0xFF3C2F2F)
private val secondaryTextColor = Color(0xFF7D6E6E)
private val accentPink = Color(0xFFEC407A)
private val topBarBrush = Brush.verticalGradient(listOf(Color(0xFFFFD180), Color(0xFFFFA726)))

// --- ✅ UPDATED DATA SOURCE WITH 4 QUICK ACTIONS ---
val quickActions = listOf(
    QuickAction("AR Scan", "Scan monuments", R.drawable.ic_scan, Screen.ARScanAction.route, listOf(Color(0xFF26A69A), Color(0xFF00796B))),
    QuickAction("Calendar", "Festivals", R.drawable.ic_calendar, Screen.FestiveCalendar.route, listOf(Color(0xFFFFA726), Color(0xFFF57C00))),
    QuickAction("Hub", "Share memories", R.drawable.ic_hub, Screen.CommunityWall.route, listOf(Color(0xFFEC407A), Color(0xFFD81B60))),
    // --- ✅ NEW 4TH ITEM ADDED ---
    QuickAction("AzadiChat", "Chat with Leaders", R.drawable.ic_chatbot_avatar, Screen.AzadiChat.route, listOf(Color(0xFF42A5F5), Color(0xFF1E88E5)))
)

val exploreItems = listOf(
    ExploreItem("Heritage Explorer", R.drawable.ic_heritage, R.drawable.ic_heri, Screen.HeritageExplorer.route, emptyList()),
    ExploreItem("Arts & Traditions", R.drawable.ic_arts, R.drawable.ic_arts_trad, Screen.ArtsAndTraditions.route, emptyList()),
    ExploreItem("Festivals & Food", R.drawable.ic_festival, R.drawable.ic_diwali, Screen.FestivalsAndFood.route, emptyList()),
    ExploreItem("Sacred Stories", R.drawable.ic_story, R.drawable.ic_books, Screen.Stories.route, emptyList())
)

@Composable
fun HomeScreen(
    navController: NavController,
    storyViewModel: StoryViewModel = viewModel(),
    userViewModel: UserViewModel = koinViewModel() // ✅ Inject UserViewModel
) {
    val currentUser by userViewModel.currentUser.collectAsState()

    val storyOfTheDay by storyViewModel.storyOfTheDay.collectAsState()
    val isLoading by storyViewModel.isStoryOfTheDayLoading.collectAsState()
    val isPlaying by storyViewModel.isPlaying.collectAsState()
    val currentlyPlaying by storyViewModel.currentlyPlayingStory.collectAsState()

    var showStoryDialog by remember { mutableStateOf(false) }
    var selectedStory by remember { mutableStateOf<Story?>(null) }

    if (showStoryDialog && selectedStory != null) {
        StoryDetailDialogHome(story = selectedStory!!, viewModel = storyViewModel, onDismiss = { showStoryDialog = false })
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackgroundBrush),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { MainTopAppBar(navController, userName = currentUser?.name) } // ✅ Pass user name
        item { SectionHeader("Quick Actions") }
        item { QuickActionsGrid(navController) }
        item { SectionHeader("Explore Heritage") }
        item { ExploreGrid(navController) }
        item { SectionHeader("Highlight") }
        item {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accentPink)
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

@Composable
fun MainTopAppBar(navController: NavController, userName: String?) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = BottomArcShapeHome(arcHeight = 30.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(topBarBrush)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // ✅ Display personalized greeting
                Text("Namaste ${userName ?: ""}!", style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White))
                Text("✨ Discover India's timeless heritage", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.White.copy(alpha = 0.9f))
                }
                IconButton(onClick = {
                    // ✅ Protect the profile route
                    if (userName != null) {
                        navController.navigate(Screen.Profile.route)
                    } else {
                        Toast.makeText(context, "Please log in to view your profile.", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Auth.route)
                    }
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = "Profile",
                        modifier = Modifier.size(32.dp)
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

// --- ✅ NEW GRID LAYOUT FOR QUICK ACTIONS ---
@Composable
fun QuickActionsGrid(navController: NavController) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionCard(action = quickActions[0], navController = navController, modifier = Modifier.weight(1f))
            QuickActionCard(action = quickActions[1], navController = navController, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            quickActions.getOrNull(2)?.let { QuickActionCard(action = it, navController = navController, modifier = Modifier.weight(1f)) }
            quickActions.getOrNull(3)?.let { QuickActionCard(action = it, navController = navController, modifier = Modifier.weight(1f)) }
        }
    }
}


@Composable
fun QuickActionCard(action: QuickAction, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            // --- ✅ ASPECT RATIO CHANGED TO MATCH EXPLORE SECTION ---
            .aspectRatio(1.25f)
            .clickable { navController.navigate(action.route) },
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(action.gradientColors))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-20).dp, y = (-30).dp)
                    .size(100.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = action.icon),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(action.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(action.subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                }
            }
        }
    }
}

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
            Image(painter = painterResource(id = item.imageResId), contentDescription = item.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)), startY = 100f))
            )
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Image(
                    painter = painterResource(id = item.icon),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = item.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

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
            AsyncImage(model = story.thumbnail, contentDescription = story.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)), startY = 150f))
            )
            Row(modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Highlight", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                    Text(text = story.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = onListenClick,
                    modifier = Modifier.size(52.dp).background(Color.White, CircleShape)
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

@Composable
fun StoryDetailDialogHome(story: Story, viewModel: StoryViewModel, onDismiss: () -> Unit) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentlyPlayingStory by viewModel.currentlyPlayingStory.collectAsState()
    val playbackPosition by viewModel.playbackPosition.collectAsState()
    val totalDuration by viewModel.totalDuration.collectAsState()

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.8f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box {
                        AsyncImage(model = story.thumbnail, contentDescription = story.title, modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)), contentScale = ContentScale.Crop)
                        IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    Column(modifier = Modifier.padding(20.dp).weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(story.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(story.story, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, lineHeight = 22.sp)
                    }
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Slider(value = if (totalDuration > 0) playbackPosition.toFloat() else 0f, onValueChange = { newPosition -> viewModel.seekTo(newPosition.toLong()) }, valueRange = 0f..(if (totalDuration > 0) totalDuration.toFloat() else 1f), modifier = Modifier.fillMaxWidth())
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(formatDuration(playbackPosition), style = MaterialTheme.typography.labelSmall)
                            Text(formatDuration(totalDuration), style = MaterialTheme.typography.labelSmall)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        IconButton(onClick = { viewModel.playStory(story) }, modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.primary, CircleShape)) {
                            Icon(
                                painter = if (isPlaying && currentlyPlayingStory?.id == story.id) painterResource(id = R.drawable.ic_pause) else painterResource(id = R.drawable.ic_play),
                                contentDescription = if (isPlaying && currentlyPlayingStory?.id == story.id) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDuration(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, seconds)
}

class BottomArcShapeHome(private val arcHeight: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val arcHeightPx = with(density) { arcHeight.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - arcHeightPx)
            quadraticBezierTo(size.width / 2, size.height, 0f, size.height - arcHeightPx)
            close()
        }
        return Outline.Generic(path)
    }
}

