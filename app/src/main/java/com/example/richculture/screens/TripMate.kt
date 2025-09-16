package com.example.richculture.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.richculture.Data.Place
import com.example.richculture.ViewModels.TravelPlannerUiState
import com.example.richculture.ViewModels.TravelPlannerViewModel
import org.koin.androidx.compose.koinViewModel

// Mock data classes for previewing and compilation
// You can remove these if they are already defined in your project.
// namespace com.example.richculture.Data {
//     data class Place(
//         val location: String,
//         val duration: String,
//         val budget: String,
//         val opening_time: String,
//         val closing_time: String,
//         val right_time_to_visit: String
//     )
//     data class TripResponse(
//         val places: List<Place>,
//         val optimized_route: List<String>
//     )
// }
//
// namespace com.example.richculture.ViewModels {
//     sealed interface TravelPlannerUiState {
//         object Empty : TravelPlannerUiState
//         object Loading : TravelPlannerUiState
//         data class Success(val tripResponse: com.example.richculture.Data.TripResponse) : TravelPlannerUiState
//         data class Error(val message: String) : TravelPlannerUiState
//     }
//     // Mock ViewModel as well
//     class TravelPlannerViewModel : androidx.lifecycle.ViewModel() {
//         val uiState = kotlinx.coroutines.flow.MutableStateFlow<TravelPlannerUiState>(TravelPlannerUiState.Empty)
//         fun fetchTripPlan(destination: String) {}
//     }
// }


@Composable
fun TripMateScreen(
    navController: NavController,
    viewModel: TravelPlannerViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val primaryColor = Color(0xFF6A1B9A) // Rich Purple
    val accentColor = Color(0xFF8E24AA)  // Lighter Purple

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F7FC) // Light lavender background
    ) {
        Scaffold(
            topBar = {
                TripMateTopBar(navController = navController)
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    AiPlannerInputCard(
                        isLoading = uiState is TravelPlannerUiState.Loading,
                        primaryColor = primaryColor,
                        accentColor = accentColor,
                        onGenerateClick = viewModel::fetchTripPlan
                    )
                }

                item {
                    AnimatedContent(
                        targetState = uiState,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(600)) + slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(600, easing = EaseOutCubic)
                            ) togetherWith fadeOut(animationSpec = tween(300))
                        }, label = "results"
                    ) { state ->
                        when (state) {
                            is TravelPlannerUiState.Success -> {
                                TripResults(
                                    tripResponse = state.tripResponse,
                                    primaryColor = primaryColor,
                                    accentColor = accentColor
                                )
                            }
                            is TravelPlannerUiState.Error -> {
                                ErrorState(message = state.message, onRetry = { /* TODO */ })
                            }
                            is TravelPlannerUiState.Loading -> {
                                LoadingState(primaryColor = primaryColor)
                            }
                            is TravelPlannerUiState.Empty -> {
                                // Empty state, show nothing below the input card
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
fun TripMateTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "TripMate AI ✨",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun AiPlannerInputCard(
    isLoading: Boolean,
    primaryColor: Color,
    accentColor: Color,
    onGenerateClick: (String) -> Unit
) {
    var destination by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Plan Your Next Heritage Trip",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter Destination") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black,
                    focusedBorderColor = primaryColor,
                    focusedLeadingIconColor = primaryColor
                )
            )
            Button(
                onClick = { onGenerateClick(destination) },
                enabled = !isLoading && destination.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(listOf(primaryColor, accentColor)),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Generate Plan", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TripResults(
    tripResponse: com.example.richculture.Data.TripResponse,
    primaryColor: Color,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Places to Visit Section
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Places to Visit", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            tripResponse.places.forEach { place ->
                PlaceCard(place = place, primaryColor = primaryColor)
            }
        }

        // Optimized Route Section
        if (tripResponse.optimized_route.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Optimized Route", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                RouteTimeline(
                    route = tripResponse.optimized_route,
                    primaryColor = primaryColor,
                    accentColor = accentColor
                )
            }
        }
    }
}

@Composable
fun PlaceCard(place: Place, primaryColor: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(place.location, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Budget: ${place.budget} • Duration: ${place.duration}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(
                    text = "${place.opening_time} - ${place.closing_time}",
                    icon = Icons.Default.AddCircle,
                    color = primaryColor
                )
                InfoChip(
                    text = place.right_time_to_visit,
                    icon = Icons.Default.AddCircle,
                    color = Color(0xFFFFA000) // Amber
                )
            }
        }
    }
}

@Composable
fun InfoChip(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Text(text, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}


@Composable
fun RouteTimeline(route: List<String>, primaryColor: Color, accentColor: Color) {
    Column(modifier = Modifier.padding(start = 8.dp)) {
        route.forEachIndexed { index, place ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Timeline Graphics Column
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                if (index == 0) primaryColor else accentColor,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index == 0) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Start", tint = Color.White, modifier = Modifier.size(12.dp))
                        } else {
                            Text((index + 1).toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (index < route.lastIndex) {
                        // Dotted line
                        Canvas(modifier = Modifier.height(40.dp).width(2.dp)) {
                            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(center.x, 0f),
                                end = Offset(center.x, size.height),
                                strokeWidth = 2.dp.toPx(),
                                pathEffect = pathEffect
                            )
                        }
                    }
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = place,
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun LoadingState(primaryColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(color = primaryColor)
        Text(
            "Generating your trip...",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), // Light Red
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFFB71C1C), modifier = Modifier.size(32.dp))
            Text("Oops, something went wrong!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(message, color = Color.Gray, textAlign = TextAlign.Center)
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("Try Again")
            }
        }
    }
}
