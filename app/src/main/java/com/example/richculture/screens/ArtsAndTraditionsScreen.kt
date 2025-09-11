package com.example.richculture.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.richculture.Data.Art
import com.example.richculture.Data.Dance
import com.example.richculture.Data.Music
import com.example.richculture.ViewModels.ArtViewModel
import com.example.richculture.ViewModels.DanceViewModel
import com.example.richculture.ViewModels.MusicViewModel

// --- DATA & VIEWMODELS ---

enum class ArtCategory { Dance, Music, Arts }

// --- NEW RADIANT COLORS ---
private val topBarBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFFF06292), Color(0xFFBA68C8)) // Radiant Pink to Purple
)

@Composable
fun ArtsAndTraditionsScreen(
    navController: NavController,
    danceViewModel: DanceViewModel = viewModel(),
    artViewModel: ArtViewModel = viewModel(),
    musicViewModel: MusicViewModel = viewModel()
) {
    var selectedCategory by remember { mutableStateOf(ArtCategory.Dance) }

    // Fetch data when the screen is first composed
    LaunchedEffect(Unit) {
        danceViewModel.fetchAllDances()
        artViewModel.fetchAllArts()
        musicViewModel.fetchAllMusic()
    }

    // Collect data from ViewModels
    val danceList by danceViewModel.danceList.collectAsState()
    val artList by artViewModel.artList.collectAsState()
    val musicList by musicViewModel.musicList.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFEFBF6), Color(0xFFF0EBE3))
                )
            )
    ) {
        Scaffold(
            topBar = { ArtsTopAppBar(navController) },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    CategoryTabs(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                }

                item {
                    AnimatedContent(
                        targetState = selectedCategory,
                        label = "Category Content",
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith
                                    fadeOut(animationSpec = tween(300))
                        }
                    ) { category ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            when (category) {
                                ArtCategory.Dance -> {
                                    if (danceList.isEmpty()) LoadingIndicator()
                                    else danceList.forEach { dance -> DanceCard(dance = dance) }
                                }
                                ArtCategory.Music -> {
                                    if (musicList.isEmpty()) LoadingIndicator()
                                    else musicList.forEach { music -> MusicCard(music = music) }
                                }
                                ArtCategory.Arts -> {
                                    if (artList.isEmpty()) LoadingIndicator()
                                    else artList.forEach { art -> ArtCard(art = art) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArtsTopAppBar(navController: NavController) {
    // A custom Top App Bar implementation using a Card to support a custom shape.
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = BottomArcShapeArts(arcHeight = 30.dp),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(topBarBrush)
                .padding(top = 8.dp, bottom = 38.dp) // Padding to account for arc and status bar
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    "Arts & Traditions 🎭",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}


@Composable
fun CategoryTabs(selectedCategory: ArtCategory, onCategorySelected: (ArtCategory) -> Unit) {
    val categories = ArtCategory.values()
    val activeColor = when (selectedCategory) {
        ArtCategory.Dance -> Color(0xFFE91E63)
        ArtCategory.Music -> Color(0xFF1E88E5)
        ArtCategory.Arts -> Color(0xFFFB8C00)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categories.forEach { category ->
            val isSelected = selectedCategory == category
            Button(
                onClick = { onCategorySelected(category) },
                modifier = Modifier.weight(1f),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) activeColor else Color.White,
                    contentColor = if (isSelected) Color.White else Color(0xFF5D4037)
                ),
                elevation = if (isSelected) ButtonDefaults.buttonElevation(4.dp) else ButtonDefaults.buttonElevation(2.dp),
                border = if (!isSelected) BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null
            ) {
                Text(category.name)
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 50.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFFE91E63)) // Using the Dance category color
    }
}

// --- DYNAMIC CARDS ---

@Composable
fun DanceCard(dance: Dance) {
    var expanded by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.clickable { expanded = !expanded },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize() // Animate size change
        ) {
            AsyncImage(
                model = dance.imageurl,
                contentDescription = dance.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(dance.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = dance.description,
                color = Color.Gray,
                lineHeight = 20.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { uriHandler.openUri(dance.wikiurl) },
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text("Learn More")
            }
        }
    }
}

@Composable
fun ArtCard(art: Art) {
    var expanded by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.clickable { expanded = !expanded },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize()
        ) {
            AsyncImage(model = art.imageurl, contentDescription = art.name, modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.height(16.dp))
            Text(art.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = art.description,
                color = Color.Gray,
                lineHeight = 20.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { uriHandler.openUri(art.wikiurl) },
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text("Learn More")
            }
        }
    }
}

@Composable
fun MusicCard(music: Music) {
    var expanded by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.clickable { expanded = !expanded },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize()
        ) {
            AsyncImage(model = music.imageurl, contentDescription = music.name, modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.height(16.dp))
            Text(music.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = music.description,
                color = Color.Gray,
                lineHeight = 20.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { uriHandler.openUri(music.wikiurl) },
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text("Learn More")
            }
        }
    }
}

// --- Custom Shape for Top App Bar ---
class BottomArcShapeArts(private val arcHeight: Dp) : Shape {
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

