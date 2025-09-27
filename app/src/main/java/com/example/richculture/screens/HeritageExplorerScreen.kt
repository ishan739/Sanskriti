package com.example.richculture.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.richculture.Data.Monument
import com.example.richculture.R
import com.example.richculture.ViewModels.MonumentChatViewModel
import com.example.richculture.ViewModels.MonumentViewModel
import com.example.richculture.screens.composables.ChatMessage
import com.example.richculture.utility.allDistricts
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs
import kotlin.math.sin

// Enhanced glassmorphic modifiers
fun Modifier.glassmorphic(
    backgroundColor: Color = Color.Black.copy(alpha = 0.35f),
    borderColor: Color = Color.White.copy(alpha = 0.2f)
) = this
    .clip(RoundedCornerShape(16.dp))
    .background(backgroundColor)
    .border(1.dp, borderColor, RoundedCornerShape(16.dp))

fun Modifier.softGlass() = this
    .clip(RoundedCornerShape(24.dp))
    .background(
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f),
                Color.White.copy(alpha = 0.85f)
            )
        )
    )
    .shadow(
        elevation = 8.dp,
        shape = RoundedCornerShape(24.dp),
        ambientColor = Color.Black.copy(alpha = 0.1f),
        spotColor = Color.Black.copy(alpha = 0.1f)
    )

@Composable
fun HeritageExplorerScreen(
    navController: NavController,
    viewModel: MonumentViewModel = viewModel(),
    chatViewModel: MonumentChatViewModel = koinViewModel()
) {
    val monuments by viewModel.displayedMonuments.collectAsState()
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    var selectedMonument by remember { mutableStateOf<Monument?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Gradient Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF5F7FA),
                            Color(0xFFE8EDF5),
                            Color(0xFFDDE4F0)
                        )
                    )
                )
        )

        Scaffold(
            topBar = {
                ModernHeader(
                    title = "Heritage Explorer",
                    subtitle = "Discover India's timeless marvels",
                    onBackClicked = { navController.popBackStack() }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    ModernSearchBar(
                        searchQuery = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { district -> viewModel.fetchByDistrict(district) }
                    )
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ModernLoadingIndicator()
                        }
                    }
                } else if (error != null) {
                    item {
                        ErrorCard(error = error!!)
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank()) "Search Results" else "Featured Monuments",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A2E)
                            )
                            Badge(
                                containerColor = Color(0xFF6C63FF).copy(alpha = 0.1f),
                                contentColor = Color(0xFF6C63FF)
                            ) {
                                Text(
                                    "${monuments.size} Sites",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    items(monuments) { monument ->
                        ModernMonumentCard(
                            monument = monument,
                            onClick = { selectedMonument = monument }
                        )
                    }
                }
            }
        }

        // Full screen monument detail dialog
        AnimatedVisibility(
            visible = selectedMonument != null,
            enter = fadeIn(tween(300)) + slideInVertically(tween(300)),
            exit = fadeOut(tween(300)) + slideOutVertically(tween(300))
        ) {
            selectedMonument?.let { monument ->
                UltraModernMonumentDialog(
                    monument = monument,
                    chatViewModel = chatViewModel,
                    onDismiss = { selectedMonument = null }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UltraModernMonumentDialog(
    monument: Monument,
    chatViewModel: MonumentChatViewModel,
    onDismiss: () -> Unit
) {
    var showChat by remember { mutableStateOf(false) }
    val chatResponse by chatViewModel.chatResponse.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()
    val conversationHistory = remember { mutableStateListOf<ChatMessage>() }
    var text by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Animation states
    val chatTransition = updateTransition(showChat, label = "chat")
    val fabScale by chatTransition.animateFloat(
        label = "fab",
        transitionSpec = { spring(dampingRatio = 0.6f) }
    ) { if (it) 0f else 1f }

    LaunchedEffect(chatResponse) {
        chatResponse?.let {
            conversationHistory.add(ChatMessage(it, isUser = false))
            chatViewModel.clearResponse()
            coroutineScope.launch {
                delay(100)
                listState.animateScrollToItem(conversationHistory.size - 1)
            }
        }
    }

    LaunchedEffect(showChat) {
        if (showChat) {
            chatViewModel.resetChatState()
            conversationHistory.clear()
            conversationHistory.add(
                ChatMessage(
                    "✨ Welcome! I'm ${monument.name}'s digital avatar. What would you like to know about my history, architecture, or cultural significance?",
                    isUser = false
                )
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = if (showChat) Color(0xFF0A0E27) else Color(0xFFF5F7FA),
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                // Main Content
                AnimatedContent(
                    targetState = showChat,
                    transitionSpec = {
                        (fadeIn(tween(400)) + slideInHorizontally(tween(400)) { it / 2 }) togetherWith
                                (fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 2 })
                    },
                    label = "content_switch"
                ) { isChat ->
                    if (isChat) {
                        ModernChatInterface(
                            monument = monument,
                            history = conversationHistory,
                            isLoading = isLoading,
                            listState = listState,
                            text = text,
                            onTextChange = { text = it },
                            onSendMessage = {
                                if (text.isNotBlank()) {
                                    conversationHistory.add(ChatMessage(text, isUser = true))
                                    chatViewModel.sendMessage(text, placeName = monument.name)
                                    coroutineScope.launch {
                                        delay(50)
                                        listState.animateScrollToItem(conversationHistory.size)
                                    }
                                    text = ""
                                }
                            },
                            onBack = { showChat = false }
                        )
                    } else {
                        ImmersiveMonumentDetails(
                            monument = monument,
                            onClose = onDismiss
                        )
                    }
                }

                // Floating Action Button for Chat
                if (!showChat) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(24.dp)
                            .navigationBarsPadding()
                            .scale(fabScale)
                    ) {
                        LargeFloatingActionButton(
                            onClick = { showChat = true },
                            containerColor = Color(0xFF6C63FF),
                            contentColor = Color.White,
                            elevation = FloatingActionButtonDefaults.elevation(12.dp),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chat),
                                contentDescription = "Chat",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernChatInterface(
    monument: Monument,
    history: List<ChatMessage>,
    isLoading: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    text: String,
    onTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E27),
                        Color(0xFF151933),
                        Color(0xFF1E2341)
                    )
                )
            )
    ) {
        // Modern Chat Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF1E2341).copy(alpha = 0.5f),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = monument.cover,
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF6C63FF), CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(monument.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF4CAF50), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Assistant Active", fontSize = 12.sp, color = Color(0xFF4CAF50))
                    }
                }
            }
        }

        // Chat Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(history) { message ->
                ModernChatBubble(
                    message = message.text,
                    isUser = message.isUser
                )
            }
            if (isLoading) {
                item {
                    AITypingIndicator()
                }
            }
        }

        // Modern Input Field
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding(),
            color = Color(0xFF1E2341),
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF2A2F4F)
                ) {
                    TextField(
                        value = text,
                        onValueChange = onTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Ask about history, architecture...",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 14.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF6C63FF),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 3,
                        textStyle = TextStyle(fontSize = 14.sp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = onSendMessage,
                    modifier = Modifier.size(48.dp),
                    containerColor = Color(0xFF6C63FF),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernHeader(title: String, subtitle: String, onBackClicked: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, tint = Color.Black, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernSearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val filteredDistricts = remember(searchQuery) {
        if (searchQuery.isEmpty()) emptyList() else allDistricts.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    ExposedDropdownMenuBox(
        expanded = expanded && filteredDistricts.isNotEmpty(),
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newValue ->
                onQueryChange(newValue)
                expanded = true
            },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            placeholder = { Text("Search by district (e.g., Agra)") },
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded && filteredDistricts.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            filteredDistricts.take(5).forEach { district ->
                DropdownMenuItem(
                    text = { Text(district, color = Color.Black) },
                    onClick = {
                        onQueryChange(district)
                        expanded = false
                        onSearch(district)
                    }
                )
            }
        }
    }
}

@Composable
fun ModernMonumentCard(monument: Monument, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            AsyncImage(
                model = monument.cover,
                contentDescription = monument.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(monument.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(monument.location, fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = monument.description,
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ModernLoadingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(color = Color(0xFF6C63FF))
        Text("Fetching Monuments...", color = Color.Gray)
    }
}

@Composable
fun ErrorCard(error: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.width(12.dp))
            Text(error, color = Color.Red.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun ModernChatBubble(
    message: String,
    isUser: Boolean
) {
    val bubbleShape = RoundedCornerShape(
        topStart = 18.dp,
        topEnd = 18.dp,
        bottomStart = if (isUser) 18.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 18.dp
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF6C63FF), Color(0xFF8B7FFF))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Surface(
            shape = bubbleShape,
            color = if (isUser) Color(0xFF6C63FF) else Color(0xFF2A2F4F),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
fun AITypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val dot1 = infiniteTransition.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 1200; 0f at 0; 1f at 200; 0f at 400; 0f at 1200 }), label = "dot1")
    val dot2 = infiniteTransition.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 1200; 0f at 200; 1f at 400; 0f at 600; 0f at 1200 }), label = "dot2")
    val dot3 = infiniteTransition.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 1200; 0f at 400; 1f at 600; 0f at 800; 0f at 1200 }), label = "dot3")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(18.dp)).background(Color(0xFF2A2F4F)).padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text("AI is thinking", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(8.dp).scale(dot1.value).background(Color(0xFF6C63FF), CircleShape))
            Box(modifier = Modifier.size(8.dp).scale(dot2.value).background(Color(0xFF6C63FF), CircleShape))
            Box(modifier = Modifier.size(8.dp).scale(dot3.value).background(Color(0xFF6C63FF), CircleShape))
        }
    }
}

@Composable
fun ImmersiveMonumentDetails(
    monument: Monument,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    // Separate cover photo from museum photos
    val museumImages = listOf(monument.furl, monument.surl, monument.turl).filter { it.isNotBlank() }
    val museumPagerState = rememberPagerState(pageCount = { museumImages.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // --- Cover Image Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            AsyncImage(
                model = monument.cover,
                contentDescription = monument.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.8f)
                            ),
                            startY = 200f
                        )
                    )
            )

            // --- Top Controls ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
                IconButton(
                    onClick = {
                        try {
                            val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(monument.name)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            context.startActivity(mapIntent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, "Google Maps is not installed.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_map),
                        contentDescription = "Open in Maps",
                        tint = Color.White
                    )
                }
            }
        }

        // --- Monument Information Text Content ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = monument.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = monument.location,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Divider()

            Text(
                text = "About this Monument",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )

            Text(
                text = monument.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray,
                lineHeight = 24.sp
            )
        }

        // --- Museum Section ---
        if (museumImages.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Divider()

                Text(
                    text = "Museum",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )

                // Museum Image Gallery
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    HorizontalPager(
                        state = museumPagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = museumImages[page],
                            contentDescription = "Museum image ${page + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }

                    // Museum Pager Indicator
                    if (museumImages.size > 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(museumImages.size) { iteration ->
                                val isSelected = museumPagerState.currentPage == iteration
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) Color.White
                                            else Color.White.copy(alpha = 0.5f)
                                        )
                                        .size(if (isSelected) 8.dp else 6.dp)
                                        .animateContentSize()
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add some bottom padding for better scrolling experience
        Spacer(modifier = Modifier.height(24.dp))
    }
}
