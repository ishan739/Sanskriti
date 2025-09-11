package com.example.richculture.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.richculture.Data.Story
import com.example.richculture.ViewModels.StoryCategoryInfo
import com.example.richculture.ViewModels.StoryViewModel
import java.util.concurrent.TimeUnit
import com.example.richculture.R

// Gradients for category card backgrounds
private val categoryGradients = mapOf(
    "Mythology" to Brush.verticalGradient(listOf(Color(0xFF8E24AA), Color(0xFFB39DDB))),
    "Folk Tales" to Brush.verticalGradient(listOf(Color(0xFF388E3C), Color(0xFF81C784))),
    "Legends" to Brush.verticalGradient(listOf(Color(0xFFE64A19), Color(0xFFFF8A65))),
    "Epics" to Brush.verticalGradient(listOf(Color(0xFF1976D2), Color(0xFF64B5F6))),
    "Default" to Brush.verticalGradient(listOf(Color(0xFF5f72be), Color(0xFF9921e8)))
)

// Simple accent colors for the text tags
private val categoryAccentColors = mapOf(
    "Mythology" to Color(0xFF8E24AA),
    "Folk Tales" to Color(0xFF388E3C),
    "Legends" to Color(0xFFE64A19),
    "Epics" to Color(0xFF1976D2),
    "Default" to Color(0xFF5f72be)
)

@Composable
fun StoriesScreen(
    navController: NavController,
    viewModel: StoryViewModel = viewModel()
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val displayedStories by viewModel.displayedStories.collectAsState()
    val selectedCategoryName by viewModel.selectedCategory.collectAsState()
    var selectedStory by remember { mutableStateOf<Story?>(null) }

    Scaffold(
        topBar = { StoriesTopAppBar(navController) },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading && categories.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text(text = error!!, modifier = Modifier.align(Alignment.Center).padding(16.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = padding.calculateTopPadding() + 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        Sectione(title = "Story Categories") {
                            CategoryGrid(
                                categories = categories,
                                selectedCategory = selectedCategoryName,
                                onCategoryClick = { category -> viewModel.selectCategory(category) }
                            )
                        }
                    }
                    item {
                        val sectionTitle = selectedCategoryName?.let { "$it Stories" } ?: "All Stories"
                        Sectione(
                            title = sectionTitle,
                            action = {
                                if (selectedCategoryName != null) {
                                    TextButton(onClick = { viewModel.selectCategory(null) }) {
                                        Text("View All")
                                    }
                                }
                            }
                        ) {
                            FeaturedStoriesList(
                                stories = displayedStories,
                                onStoryClick = { story -> selectedStory = story },
                                onPlayClick = { story -> viewModel.playStory(story) }
                            )
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(visible = selectedStory != null, enter = fadeIn(), exit = fadeOut()) {
        selectedStory?.let { story ->
            StoryDetailDialog(story = story, viewModel = viewModel, onDismiss = { selectedStory = null })
        }
    }
}

@Composable
fun StoriesTopAppBar(navController: NavController) {
    Box {
        Spacer(modifier = Modifier.fillMaxWidth().height(110.dp).background(Brush.verticalGradient(listOf(Color(0xFF7E57C2), Color(0xFF6200EA))), shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)))
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Sacred Stories 📚", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Ancient tales that shaped our culture", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun Sectione(title: String, modifier: Modifier = Modifier, action: (@Composable () -> Unit)? = null, content: @Composable () -> Unit) {
    Column(modifier = modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            action?.invoke()
        }
        content()
    }
}

@Composable
fun CategoryGrid(
    categories: List<StoryCategoryInfo>,
    selectedCategory: String?,
    onCategoryClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        categories.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowItems.forEach { category ->
                    CategoryCard(
                        category = category,
                        modifier = Modifier.weight(1f),
                        isSelected = category.name == selectedCategory,
                        selectedCategory = selectedCategory,
                        onClick = { onCategoryClick(category.name) }
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: StoryCategoryInfo,
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    selectedCategory: String?,
    onClick: () -> Unit
) {
    val border = if (isSelected) BorderStroke(3.dp, Color.White) else null
    val alpha = if (isSelected || selectedCategory == null) 1f else 0.6f

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .alpha(alpha)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        border = border
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(categoryGradients[category.name] ?: categoryGradients["Default"]!!).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(model = category.firstThumbnailUrl, contentDescription = category.name, modifier = Modifier.size(40.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.height(8.dp))
            Text(category.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
            Text("${category.storyCount} Stories", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}


@Composable
fun FeaturedStoriesList(stories: List<Story>, onStoryClick: (Story) -> Unit, onPlayClick: (Story) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        stories.forEach { story ->
            FeaturedStoryItem(story = story, onStoryClick = onStoryClick, onPlayClick = onPlayClick)
        }
    }
}

@Composable
fun FeaturedStoryItem(story: Story, onStoryClick: (Story) -> Unit, onPlayClick: (Story) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onStoryClick(story) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = story.thumbnail, contentDescription = story.title, modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(story.title, fontWeight = FontWeight.Bold)
                Text(story.name, fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                val categoryColor = categoryAccentColors[story.category] ?: categoryAccentColors["Default"]!!
                Text(
                    text = story.category,
                    color = categoryColor,
                    fontSize = 10.sp,
                    modifier = Modifier.background(categoryColor.copy(alpha = 0.1f), CircleShape).padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            IconButton(onClick = { onPlayClick(story) }, modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun StoryDetailDialog(story: Story, viewModel: StoryViewModel, onDismiss: () -> Unit) {
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
                    Column(
                        modifier = Modifier.padding(20.dp).weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(story.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(story.story, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, lineHeight = 22.sp)
                    }
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Slider(
                            value = if (totalDuration > 0) playbackPosition.toFloat() else 0f,
                            onValueChange = { newPosition -> viewModel.seekTo(newPosition.toLong()) },
                            valueRange = 0f..(if (totalDuration > 0) totalDuration.toFloat() else 1f),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(formatDuration(playbackPosition), style = MaterialTheme.typography.labelSmall)
                            Text(formatDuration(totalDuration), style = MaterialTheme.typography.labelSmall)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        IconButton(
                            onClick = { viewModel.playStory(story) },
                            modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {

                            Icon(
                                painter = if (isPlaying && currentlyPlayingStory?.id == story.id) {
                                    painterResource(id = R.drawable.ic_pause)   // your pause image
                                } else {
                                    painterResource(id = R.drawable.ic_play)    // your play image
                                },
                                contentDescription = if (isPlaying && currentlyPlayingStory?.id == story.id) {
                                    "Pause"
                                } else {
                                    "Play"
                                },
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
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

