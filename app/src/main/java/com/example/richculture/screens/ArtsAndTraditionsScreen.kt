package com.example.richculture.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.richculture.R

// --- New, Rich Data Models for the Redesigned Screen ---

enum class ArtCategory { Dance, Music, Arts }

data class ArtForm(
    val title: String,
    val description: String,
    val region: String,
    val imageResId: Int,
    val category: ArtCategory
)

// --- Dummy Data ---

val artsAndTraditionsData = listOf(
    ArtForm("Bharatanatyam", "Classical dance from Tamil Nadu expressing religious themes.", "Tamil Nadu", R.drawable.ic_arts, ArtCategory.Dance),
    ArtForm("Kathakali", "A stylized classical Indian dance-drama from Kerala.", "Kerala", R.drawable.ic_rajasthani_scarf, ArtCategory.Dance),
    ArtForm("Sitar", "A plucked stringed instrument, originating from the Indian subcontinent.", "North India", R.drawable.ic_blue_pottery, ArtCategory.Music),
    ArtForm("Tabla", "A pair of small hand drums from the Indian subcontinent.", "Hindustani", R.drawable.ic_rajasthani_scarf, ArtCategory.Music),
    ArtForm("Madhubani Painting", "A style of Indian painting, practiced in the Mithila region.", "Bihar", R.drawable.ic_madhubani, ArtCategory.Arts),
    ArtForm("Blue Pottery", "Widely recognized as a traditional craft of Jaipur.", "Jaipur", R.drawable.ic_ganesha_idol, ArtCategory.Arts)
)

@Composable
fun ArtsAndTraditionsScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf(ArtCategory.Dance) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF3E5F5), Color(0xFFEDE7F6), Color(0xFFE8EAF6))
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
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
                            fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) togetherWith
                                    fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
                        }
                    ) { category ->
                        val items = artsAndTraditionsData.filter { it.category == category }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            items.forEach { artForm ->
                                ArtFormCard(artForm = artForm)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtsTopAppBar(navController: NavController) {
    TopAppBar(
        title = {
            Column {
                Text("Arts & Traditions 🎭", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("The soul of Indian culture", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(Color.White.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        windowInsets = WindowInsets(0.dp),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun CategoryTabs(selectedCategory: ArtCategory, onCategorySelected: (ArtCategory) -> Unit) {
    val categories = listOf(ArtCategory.Dance, ArtCategory.Music, ArtCategory.Arts)

    Card(
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Row(modifier = Modifier.padding(6.dp)) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                val activeColor = when (category) {
                    ArtCategory.Dance -> Color(0xFFE91E63)
                    ArtCategory.Music -> Color(0xFF1E88E5)
                    ArtCategory.Arts -> Color(0xFFFB8C00)
                }

                Button(
                    onClick = { onCategorySelected(category) },
                    modifier = Modifier.weight(1f),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) activeColor else Color.Transparent,
                        contentColor = if (isSelected) Color.White else Color.Gray
                    ),
                    elevation = if (isSelected) ButtonDefaults.buttonElevation(4.dp) else null
                ) {
                    Text(category.name)
                }
            }
        }
    }
}

@Composable
fun ArtFormCard(artForm: ArtForm) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = artForm.imageResId),
                contentDescription = artForm.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(artForm.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(artForm.description, color = Color.Gray, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { /*TODO*/ },
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Text("Explore ${artForm.region}", color = Color.Black)
                }
                Button(
                    onClick = { /*TODO*/ },
                    shape = CircleShape,
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Text("Learn More")
                }
            }
        }
    }
}