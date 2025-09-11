package com.example.richculture.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.richculture.R

// --- New Data Models for the Redesigned Screen ---

enum class CommunityTab { Memories, Recipes }

data class CommunityPost(
    val id: Int,
    val userName: String,
    val userInitials: String,
    val location: String,
    val timestamp: String,
    val imageResId: Int,
    val caption: String,
    val likes: Int,
    val comments: Int
)

data class Recipe(
    val id: Int,
    val name: String,
    val cuisine: String,
    val imageResId: Int,
    val timeMin: Int,
    val difficulty: String,
    val rating: Float,
    val likes: Int
)

// --- Dummy Data ---

val communityPosts = listOf(
    CommunityPost(1, "Priya Sharma", "PS", "Taj Mahal, Agra", "2 hours ago", R.drawable.ic_tajmahal, "Mesmerized by the eternal symbol of love! The intricate marble work is breathtaking. #TajMahal #IndianHeritage", 48, 12),
    CommunityPost(2, "Raj Patel", "RP", "Holi Festival, Mathura", "1 day ago", R.drawable.ic_janmashtami, "Colors of joy at the Holi festival! An unforgettable experience.", 102, 25)
)

val recipes = listOf(
    Recipe(1, "Masala Chai", "North Indian", R.drawable.ic_pattachitra, 15, "Easy", 4.8f, 89),
    Recipe(2, "Butter Chicken", "Punjabi", R.drawable.ic_blue_pottery, 45, "Medium", 4.9f, 156),
    Recipe(3, "Samosa", "Street Food", R.drawable.ic_rajasthani_scarf, 60, "Medium", 4.7f, 92)
)


@Composable
fun CommunityWallScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(CommunityTab.Memories) }

    Scaffold(
        topBar = { CommunityTopAppBar(navController) },
        containerColor = Color(0xFFF4F7FA) // Soft off-white background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                MainTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            }

            item {
                AnimatedContent(targetState = selectedTab, label = "Tab Animation") { tab ->
                    when (tab) {
                        CommunityTab.Memories -> MemoriesContent()
                        CommunityTab.Recipes -> RecipesContent()
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityTopAppBar(navController: NavController) {
    Box {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF81C784), Color(0xFF2E7D32))
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Community Hub 🌏", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Share memories & discover recipes", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun MainTabs(selectedTab: CommunityTab, onTabSelected: (CommunityTab) -> Unit) {
    Card(
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(6.dp)) {
            TabButton(
                text = "Memories",
                icon = Icons.Default.AddCircle,
                isSelected = selectedTab == CommunityTab.Memories,
                activeColor = Color(0xFF7E57C2),
                modifier = Modifier.weight(1f)
            ) { onTabSelected(CommunityTab.Memories) }

            TabButton(
                text = "Recipes",
                icon = Icons.Default.AddCircle,
                isSelected = selectedTab == CommunityTab.Recipes,
                activeColor = Color(0xFF388E3C),
                modifier = Modifier.weight(1f)
            ) { onTabSelected(CommunityTab.Recipes) }
        }
    }
}

@Composable
fun MemoriesContent() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = { /* Share Memory */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFAB47BC), Color(0xFF7E57C2))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Share", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Your Memory", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        communityPosts.forEach { post ->
            MemoryPostCard(post = post)
        }
    }
}

@Composable
fun RecipesContent() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RecipeFilterChips()
        recipes.forEach { recipe ->
            RecipeCard(recipe = recipe)
        }
    }
}


@Composable
fun MemoryPostCard(post: CommunityPost) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(post.userInitials, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(post.userName, fontWeight = FontWeight.Bold)
                    Text("${post.location} • ${post.timestamp}", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Image(
                painter = painterResource(id = post.imageResId),
                contentDescription = post.caption,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(post.caption, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    PostActionButton(icon = Icons.Default.FavoriteBorder, text = "${post.likes}")
                    PostActionButton(icon = Icons.Default.AddCircle, text = "${post.comments}")
                    PostActionButton(icon = Icons.Default.Share, text = "Share")
                }
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = recipe.imageResId),
                contentDescription = recipe.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Text(recipe.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = Color.Gray)
                }
                Text(recipe.cuisine, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    MetaInfos(icon = Icons.Default.AddCircle, text = "${recipe.timeMin} mins")
                    MetaInfos(icon = Icons.Default.AddCircle, text = recipe.difficulty)
                    MetaInfos(icon = Icons.Default.Star, text = "${recipe.rating}")
                }
                Text("${recipe.likes} likes", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* View Recipe */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                    shape = CircleShape
                ) {
                    Text("View Recipe")
                }
            }
        }
    }
}

@Composable
fun RecipeFilterChips() {
    val filters = listOf("All", "Breakfast", "Main Course", "Snacks")
    var selectedFilter by remember { mutableStateOf("All") }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filters) { filter ->
            val isSelected = selectedFilter == filter
            FilterChip(
                selected = isSelected,
                onClick = { selectedFilter = filter },
                label = { Text(filter) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF388E3C),
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun TabButton(text: String, icon: ImageVector, isSelected: Boolean, activeColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) activeColor else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        elevation = if (isSelected) ButtonDefaults.buttonElevation(2.dp) else null
    ) {
        Icon(icon, contentDescription = text, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun PostActionButton(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, contentDescription = text, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Text(text, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun MetaInfos(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}