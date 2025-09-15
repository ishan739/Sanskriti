package com.example.richculture.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.richculture.Data.Place
import com.example.richculture.ViewModels.TravelPlannerUiState
import com.example.richculture.ViewModels.TravelPlannerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TripMateScreen(
    navController: NavHostController,
    viewModel: TravelPlannerViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    AiPlannerCard(
                        isLoading = uiState is TravelPlannerUiState.Loading,
                        onGenerateClick = { destination ->
                            viewModel.fetchPlaces(destination)
                        }
                    )
                }

                item {
                    // This animated visibility block handles all UI states smoothly
                    AnimatedVisibility(
                        visible = uiState !is TravelPlannerUiState.Empty,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (val state = uiState) {
                                is TravelPlannerUiState.Success -> {
                                    PlacesResultList(places = state.places)
                                }
                                is TravelPlannerUiState.Error -> {
                                    ErrorStates(message = state.message)
                                }
                                is TravelPlannerUiState.Loading -> {
                                    // The loading state is handled inside the AiPlannerCard button
                                }
                                is TravelPlannerUiState.Empty -> {
                                    // Handled by AnimatedVisibility
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
fun AiPlannerCard(isLoading: Boolean, onGenerateClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
        border = BorderStroke(1.dp, Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape)
                        .background(Brush.verticalGradient(listOf(Color(0xFF7E57C2), Color(0xFFAB47BC)))),
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter a city, e.g., 'Jaipur'")},
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7E57C2),
                    cursorColor = Color(0xFF7E57C2)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onGenerateClick(searchQuery) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading && searchQuery.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF7E57C2), Color(0xFF512DA8))),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Generate Plan", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PlacesResultList(places: List<Place>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Your AI-Generated Itinerary",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        places.forEach { place ->
            PlaceCard(place = place)
        }
    }
}

@Composable
fun PlaceCard(place: Place) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(place.location, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(icon = Icons.Default.AddCircle, title = "Budget", value = place.budget)
            InfoRow(icon = Icons.Default.AddCircle, title = "Duration", value = place.duration)
            InfoRow(icon = Icons.Default.AddCircle, title = "Timings", value = "${place.opening_time} - ${place.closing_time}")
            Spacer(modifier = Modifier.height(12.dp))
            SuggestionChip(text = place.right_time_to_visit)
        }
    }
}

@Composable
fun SuggestionChip(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.AddCircle, contentDescription = "Suggestion", tint = Color(0xFF388E3C))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Tip: $text", color = Color(0xFF388E3C), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun InfoRow(icon: ImageVector, title: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(80.dp))
        Text(value, color = Color.Gray)
    }
}

@Composable
fun ErrorStates(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0))
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Something Went Wrong", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(message, textAlign = TextAlign.Center, color = Color.DarkGray)
        }
    }
}


// --- No changes needed for the TopAppBar ---
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
