package com.example.richculture.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// --- New, Rich Data Models for the Redesigned Screen ---

data class Feature(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val colorGradient: Brush
)

// --- Dummy Data with Vibrant Gradients and Contextual Icons ---

val features = listOf(
    Feature(Icons.Default.AddCircle, "AI-Powered Planning", "Personalized itineraries based on your preferences", Brush.verticalGradient(listOf(Color(0xFF7E57C2), Color(0xFF512DA8)))),
    Feature(Icons.Default.AddCircle, "Real-time Updates", "Live crowd levels, weather, and traffic info", Brush.verticalGradient(listOf(Color(0xFFFFCA28), Color(0xFFFFA000)))),
    Feature(Icons.Default.AddCircle, "Smart Routing", "Optimal paths and transport suggestions", Brush.verticalGradient(listOf(Color(0xFF66BB6A), Color(0xFF388E3C)))),
    Feature(Icons.Default.AddCircle, "Multi-layer Insights", "Historical context, architectural details & more", Brush.verticalGradient(listOf(Color(0xFFEC407A), Color(0xFFC2185B))))
)

@Composable
fun TripMateScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFEDE7F6), Color(0xFFF3E5F5))
                )
            )
    ) {
        Scaffold(
            topBar = { TripMateTopAppBar(navController) },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding()),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { TripPlannerTabs() }
                item { AiPlannerCard() }
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        features.chunked(2).forEach { rowItems ->
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                rowItems.forEach { feature ->
                                    FeatureCard(feature = feature, modifier = Modifier.weight(1f))
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
fun TripMateTopAppBar(navController: NavHostController) {
    Box {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF64B5F6), Color(0xFF1976D2))
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("TripMate AI ✨", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Your intelligent heritage companion", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
            IconButton(
                onClick = { /* Handle magic action */ },
                modifier = Modifier.background(
                    Brush.horizontalGradient(listOf(Color(0xFFFFA726), Color(0xFFF57C00))),
                    CircleShape
                )
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = "Suggestions", tint = Color.White)
            }
        }
    }
}

@Composable
fun TripPlannerTabs() {
    val tabs = listOf("AI Planner", "Explore", "Live Guide")
    var selectedTab by remember { mutableStateOf("AI Planner") }

    Card(
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
        modifier = Modifier.padding(horizontal = 16.dp),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Row(modifier = Modifier.padding(6.dp)) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab
                Button(
                    onClick = { selectedTab = tab },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color.White else Color.Transparent,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                    ),
                    elevation = if (isSelected) ButtonDefaults.buttonElevation(4.dp) else null
                ) {
                    Text(tab)
                }
            }
        }
    }
}

@Composable
fun AiPlannerCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
        border = BorderStroke(1.dp, Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF7E57C2), Color(0xFFAB47BC))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "AI Planner", tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("AI-Powered Trip Planning", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Let AI create your perfect heritage journey", color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    .border(1.dp, Color.White, CircleShape)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var searchQuery by remember { mutableStateOf("") }
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text("Where do you want to go?", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
                Button(
                    onClick = { /*TODO*/ },
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF7E57C2), Color(0xFF512DA8))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Next", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureCard(feature: Feature, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .aspectRatio(0.8f)
            .clickable { /*TODO*/ },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(feature.colorGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(feature.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Column {
                Text(feature.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(feature.description, color = Color.Gray, fontSize = 12.sp, lineHeight = 16.sp)
            }
        }
    }
}
