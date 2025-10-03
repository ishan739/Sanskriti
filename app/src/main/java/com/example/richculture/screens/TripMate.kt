package com.example.richculture.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.example.richculture.Data.Event
import com.example.richculture.Data.Place
import com.example.richculture.ViewModels.EventsViewModel
import com.example.richculture.ViewModels.TravelPlannerUiState
import com.example.richculture.ViewModels.TravelPlannerViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.richculture.R

// Famous Indian Cities Data
data class FamousCity(
    val name: String,
    val state: String,
    val emoji: String,
    val gradient: Pair<Color, Color>
)

val famousCities = listOf(
    FamousCity("Delhi", "National Capital", "🏛️", Pair(Color(0xFF667eea), Color(0xFF764ba2))),
    FamousCity("Agra", "Uttar Pradesh", "🕌", Pair(Color(0xFFf093fb), Color(0xFFf5576c))),
    FamousCity("Jaipur", "Rajasthan", "🏰", Pair(Color(0xFF4facfe), Color(0xFF00f2fe))),
    FamousCity("Varanasi", "Uttar Pradesh", "🕉️", Pair(Color(0xFFfa709a), Color(0xFFfee140))),
    FamousCity("Mumbai", "Maharashtra", "🏙️", Pair(Color(0xFF6a11cb), Color(0xFF2575fc))),
    FamousCity("Goa", "Goa", "🏖️", Pair(Color(0xFFf8cdda), Color(0xFF1d2b64))),
    FamousCity("Kerala", "Kerala", "🌴", Pair(Color(0xFF56ab2f), Color(0xFFa8e6cf))),
    FamousCity("Udaipur", "Rajasthan", "🏰", Pair(Color(0xFFee9ca7), Color(0xFFffdde1)))
)

@Composable
fun TripMateScreen(
    navController: NavController,
    travelViewModel: TravelPlannerViewModel = koinViewModel(),
    eventsViewModel: EventsViewModel = koinViewModel()
) {
    val uiState by travelViewModel.uiState.collectAsState()
    val events by eventsViewModel.events.collectAsState()
    val eventsLoading by eventsViewModel.loading.collectAsState()

    val primaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
    )
    val accentGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4facfe), Color(0xFF00f2fe))
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFF)
    ) {
        Scaffold(
            topBar = {
                ModernTripMateTopBar(navController = navController)
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    ModernAiPlannerInputCard(
                        isLoading = uiState is TravelPlannerUiState.Loading,
                        primaryGradient = primaryGradient,
                        accentGradient = accentGradient,
                        onGenerateClick = { destination ->
                            travelViewModel.fetchTripPlan(destination)
                            eventsViewModel.fetchEvents(destination)
                        }
                    )
                }

                if (uiState is TravelPlannerUiState.Empty) {
                    item {
                        FamousCitiesSection { cityName ->
                            travelViewModel.fetchTripPlan(cityName)
                            eventsViewModel.fetchEvents(cityName)
                        }
                    }
                }

                item {
                    AnimatedContent(
                        targetState = uiState,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(800)) + slideInVertically(
                                initialOffsetY = { it / 3 },
                                animationSpec = tween(800, easing = EaseOutCubic)
                            ) togetherWith fadeOut(animationSpec = tween(400))
                        }, label = "results"
                    ) { state ->
                        when (state) {
                            is TravelPlannerUiState.Success -> {
                                Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
                                    ModernTripResults(
                                        tripResponse = state.tripResponse,
                                        primaryGradient = primaryGradient,
                                        accentGradient = accentGradient
                                    )

                                    ModernEventsSection(
                                        events = events,
                                        isLoading = eventsLoading
                                    )
                                }
                            }
                            is TravelPlannerUiState.Error -> {
                                ModernErrorState(message = state.message, onRetry = { })
                            }
                            is TravelPlannerUiState.Loading -> {
                                ModernLoadingState()
                            }
                            is TravelPlannerUiState.Empty -> {
                                // Empty state handled above
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
fun ModernTripMateTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "TripMate",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = Color(0xFF2D3748)
                )
                Text("AI", fontSize = 16.sp, color = Color(0xFF667eea), fontWeight = FontWeight.Bold)
                Text("✨", fontSize = 18.sp)
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF4A5568)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun ModernAiPlannerInputCard(
    isLoading: Boolean,
    primaryGradient: Brush,
    accentGradient: Brush,
    onGenerateClick: (String) -> Unit
) {
    var destination by remember { mutableStateOf("") }
    val shimmerColors = listOf(
        Color(0xFFE2E8F0),
        Color(0xFFF7FAFC),
        Color(0xFFE2E8F0)
    )

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Discover India's Heritage",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A202C)
                    )
                    Text(
                        "Let AI craft your perfect cultural journey",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF718096),
                        fontWeight = FontWeight.Medium
                    )
                }

                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    label = { Text("Where do you want to explore?") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF667eea)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = Color(0xFF2D3748),
                        focusedTextColor = Color(0xFF2D3748),
                        focusedBorderColor = Color(0xFF667eea),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedLeadingIconColor = Color(0xFF667eea),
                        focusedLabelColor = Color(0xFF667eea)
                    )
                )

                Button(
                    onClick = { if (destination.isNotBlank()) onGenerateClick(destination) },
                    enabled = !isLoading && destination.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = if (isLoading) Brush.linearGradient(shimmerColors) else primaryGradient,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    "Crafting your journey...",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_search),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp) // adjust size as needed
                                )
                                Text(
                                    "Generate Plan",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FamousCitiesSection(onCityClick: (String) -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Popular Destinations",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3748)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(famousCities) { city ->
                FamousCityCard(city = city, onClick = { onCityClick(city.name) })
            }
        }
    }
}

@Composable
fun FamousCityCard(city: FamousCity, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "scale"
    )

    Card(
        modifier = Modifier
            .width(140.dp)
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Brush.linearGradient(listOf(city.gradient.first, city.gradient.second)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    city.emoji,
                    fontSize = 32.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    city.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    city.state,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

@Composable
fun ModernTripResults(
    tripResponse: com.example.richculture.Data.TripResponse,
    primaryGradient: Brush,
    accentGradient: Brush
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Places Section
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFF667eea), CircleShape)
                )
                Text(
                    "Places to Visit",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2D3748)
                )
            }

            tripResponse.places.forEachIndexed { index, place ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { 50 },
                        animationSpec = tween(600, delayMillis = index * 100)
                    ) + fadeIn(animationSpec = tween(600, delayMillis = index * 100))
                ) {
                    ModernPlaceCard(place = place, primaryGradient = primaryGradient)
                }
            }
        }

        // Optimized Route Section
        if (tripResponse.optimized_route.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFF4facfe), CircleShape)
                    )
                    Text(
                        "Optimized Route",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2D3748)
                    )
                }
                ModernRouteTimeline(
                    route = tripResponse.optimized_route,
                    primaryGradient = primaryGradient,
                    accentGradient = accentGradient
                )
            }
        }
    }
}

@Composable
fun ModernPlaceCard(place: Place, primaryGradient: Brush) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        place.location,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Budget: ${place.budget} • Duration: ${place.duration}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF718096),
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = {
                        try {
                            val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(place.location)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            // Fallback to browser maps if Google Maps not available
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://maps.google.com/maps?q=${Uri.encode(place.location)}"))
                                context.startActivity(browserIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Unable to open maps", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(primaryGradient, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Open in Maps",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Column (verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ModernInfoChip(
                    text = "${place.opening_time} - ${place.closing_time}",
                    icon = painterResource(id = R.drawable.ic_time),
                    gradient = Brush.linearGradient(listOf(Color(0xFF667eea), Color(0xFF764ba2)))
                )
                ModernInfoChip(
                    text = place.right_time_to_visit,
                    icon = painterResource(id = R.drawable.ic_day),
                    gradient = Brush.linearGradient(listOf(Color(0xFFFF9800), Color(0xFFFF5722)))
                )
            }
        }
    }
}

@Composable
fun ModernInfoChip(text: String, icon: Painter, gradient: Brush) {
    Box(
        modifier = Modifier
            .background(gradient, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
            Text(
                text,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ModernRouteTimeline(route: List<String>, primaryGradient: Brush, accentGradient: Brush) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            route.forEachIndexed { index, place ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    if (index == 0) primaryGradient else accentGradient,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${index + 1}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        if (index < route.lastIndex) {
                            Canvas(modifier = Modifier.height(48.dp).width(3.dp)) {
                                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                                drawLine(
                                    color = Color(0xFFE2E8F0),
                                    start = Offset(center.x, 0f),
                                    end = Offset(center.x, size.height),
                                    strokeWidth = 3.dp.toPx(),
                                    pathEffect = pathEffect
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(20.dp))
                    Text(
                        text = place,
                        modifier = Modifier.padding(vertical = 20.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernEventsSection(
    events: List<Event>,
    isLoading: Boolean
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color(0xFFFF6B6B), CircleShape)
            )
            Text(
                "Upcoming Events",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2D3748)
            )
        }

        when {
            isLoading -> {
                ExtendedEventsLoadingState()
            }
            events.isEmpty() -> {
                EmptyEventsState()
            }
            else -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(events) { event ->
                        EventCard(
                            event = event,
                            onClick = { openChromeCustomTab(context, event.bookingLink) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        event.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748),
                        maxLines = 2
                    )
                }

            }

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF4ECDC4), Color(0xFF44A08D))),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_share),
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Book Now",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExtendedEventsLoadingState() {
    var currentMessageIndex by remember { mutableStateOf(0) }
    val loadingMessages = listOf(
        "Searching for cultural events...",
        "Connecting with local organizers...",
        "Finding the best experiences...",
        "Checking availability and prices...",
        "Curating personalized recommendations...",
        "Almost there, finalizing your events..."
    )

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(30000)
            currentMessageIndex = (currentMessageIndex + 1) % loadingMessages.size
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "events_loading")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFFFF6B6B),
                                    Color(0xFF4ECDC4),
                                    Color(0xFF45B7D1),
                                    Color(0xFFFF6B6B)
                                )
                            ),
                            CircleShape
                        )
                        .graphicsLayer { rotationZ = rotationAngle },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(85.dp)
                            .background(Color.White, CircleShape)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .scale(pulseScale)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFF6B6B), Color(0xFFFF6B6B).copy(alpha = 0.3f))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            AnimatedContent(
                targetState = loadingMessages[currentMessageIndex],
                transitionSpec = {
                    fadeIn(animationSpec = tween(1000)) togetherWith
                            fadeOut(animationSpec = tween(1000))
                }, label = "message_transition"
            ) { message ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Discovering Events",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                    Text(
                        message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF718096),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "This may take a few minutes...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9CA3AF),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(5) { index ->
                        val animatedScale by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1.2f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "dot_$index"
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .scale(animatedScale)
                                .background(
                                    Color(0xFF4ECDC4).copy(alpha = 0.7f),
                                    CircleShape
                                )
                        )
                    }
                }

                Text(
                    "Great things are worth the wait!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF667eea),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EmptyEventsState() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFFFF9800), Color(0xFFFF5722))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                "No Events Found",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )
            Text(
                "We couldn't find any upcoming events in this location at the moment.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF718096),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ModernLoadingState() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFF667eea),
                                    Color(0xFF764ba2),
                                    Color(0xFF4facfe),
                                    Color(0xFF00f2fe),
                                    Color(0xFF667eea)
                                )
                            ),
                            CircleShape
                        )
                        .graphicsLayer { rotationZ = rotationAngle },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = Color(0xFF667eea),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Crafting Your Journey",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                    Text(
                        "Our AI is analyzing destinations, planning routes, and discovering hidden gems for your perfect heritage experience...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF718096),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ModernErrorState(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFFFF6B6B), Color(0xFFEE5A52))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Something went wrong",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
                Text(
                    message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF718096),
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(listOf(Color(0xFFFF6B6B), Color(0xFFEE5A52))),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            "Try Again",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private fun openChromeCustomTab(context: Context, url: String) {
    try {
        val builder = CustomTabsIntent.Builder()

        builder.setStartAnimations(
            context,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        builder.setExitAnimations(
            context,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        builder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
        builder.setShowTitle(true)

        val closeIcon = ContextCompat.getDrawable(
            context,
            android.R.drawable.ic_menu_close_clear_cancel
        )?.toBitmap()
        closeIcon?.let { builder.setCloseButtonIcon(it) }

        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))

    } catch (e: Exception) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}