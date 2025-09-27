package com.example.richculture.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.richculture.Data.Story
import com.example.richculture.R
import com.example.richculture.ViewModels.ChatbotViewModel
import com.example.richculture.ViewModels.StoryViewModel
import com.example.richculture.ViewModels.UserViewModel
import com.example.richculture.navigate.ExploreItem
import com.example.richculture.navigate.QuickAction
import com.example.richculture.navigate.Screen
import com.example.richculture.screens.composables.ChatBubble
import com.example.richculture.screens.composables.ChatMessage
import com.example.richculture.screens.composables.MessageInputField
import kotlinx.coroutines.launch
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

// --- ✅ UPDATED DATA SOURCE (REMOVED AZADI CHAT FROM QUICK ACTIONS) ---
val quickActions = listOf(
    QuickAction("AR Scan", "Scan monuments", R.drawable.ic_scan, Screen.ARScanAction.route, listOf(Color(0xFF26A69A), Color(0xFF00796B))),
    QuickAction("Azadi Chat", "Chat With Leaders", R.drawable.ic_chatbot, Screen.AzadiChat.route, listOf(Color(0xFFEC407A), Color(0xFFD81B60)))

)

val exploreItems = listOf(
    ExploreItem("Heritage Explorer", R.drawable.ic_heritage, R.drawable.ic_heri, Screen.HeritageExplorer.route, emptyList()),
    ExploreItem("Arts & Traditions", R.drawable.ic_arts, R.drawable.ic_arts_trad, Screen.ArtsAndTraditions.route, emptyList()),
    ExploreItem("Festivals & Food", R.drawable.ic_festival, R.drawable.ic_diwali, Screen.FestivalsAndFood.route, emptyList()),
    ExploreItem("Sacred Stories", R.drawable.ic_story, R.drawable.ic_books, Screen.Stories.route, emptyList())
)

val upcomingEventsAction = QuickAction(
    "Upcoming Events",
    "Discover what's happening",
    R.drawable.ic_calendar, // You can use a different icon if preferred
    "", // No navigation route yet
    listOf(Color(0xFFFF6B35), Color(0xFFFF8E53))
)

@Composable
fun HomeScreen(
    navController: NavController,
    storyViewModel: StoryViewModel = viewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val storyOfTheDay by storyViewModel.storyOfTheDay.collectAsState()
    val isLoading by storyViewModel.isStoryOfTheDayLoading.collectAsState()

    // ✅ Get audio state from the refactored ViewModel
    val isPlaying by storyViewModel.isPlaying.collectAsState()
    val currentlyPlayingUrl by storyViewModel.currentlyPlayingStory.collectAsState()

    var showStoryDialog by remember { mutableStateOf(false) }
    var selectedStory by remember { mutableStateOf<Story?>(null) }

    // ✅ NEW: Floating Chatbot State
    var showChatbot by remember { mutableStateOf(false) }

    if (showStoryDialog && selectedStory != null) {
        StoryDetailDialogHome(story = selectedStory!!, viewModel = storyViewModel, onDismiss = { showStoryDialog = false })
    }

    // ✅ NEW: Chatbot Dialog
    if (showChatbot) {
        FloatingChatbotDialog(
            onDismiss = { showChatbot = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackgroundBrush)
                .statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { MainTopAppBar(navController, userName = currentUser?.name, profileImageUrl = currentUser?.profileImage) }
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
                            isPlaying = isPlaying && currentlyPlayingUrl == story.audiourl,
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

        // ✅ NEW: Floating Chatbot Button
        FloatingActionButton(
            onClick = { showChatbot = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .zIndex(1f)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.4f),
                    shape = CircleShape
                ),
            containerColor = Color(0x66FFFFFF), // semi-transparent purple
            shape = CircleShape
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_chatbot_avatar),
                contentDescription = "Open Chatbot",
                modifier = Modifier.size(32.dp)
            )
        }

    }
}

// ✅ NEW: Floating Chatbot Dialog Component
@Composable
fun FloatingChatbotDialog(
    onDismiss: () -> Unit,
    chatbotViewModel: ChatbotViewModel = koinViewModel()
) {
    val chatResponse by chatbotViewModel.chatResponse.collectAsState()
    val isLoading by chatbotViewModel.isLoading.collectAsState()
    val error by chatbotViewModel.error.collectAsState()

    var text by remember { mutableStateOf("") }
    val conversationHistory = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatResponse) {
        chatResponse?.let { response ->
            conversationHistory.add(ChatMessage(response, isUser = false))
            chatbotViewModel.clearResponse()
            coroutineScope.launch {
                if (conversationHistory.isNotEmpty()) {
                    listState.animateScrollToItem(conversationHistory.lastIndex)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        chatbotViewModel.resetChatState()
        conversationHistory.clear()
    }

    val quickQuestions = listOf(
        "Tell me about Taj Mahal",
        "What festivals are celebrated in December?",
        "Traditional food of Rajasthan"
    )

    val userBubbleColor = Color(0xFF6A1B9A)
    val buttonBrush = Brush.linearGradient(colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F6FC))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ✅ Chatbot Header
                ChatbotDialogHeader(onClose = onDismiss)

                // ✅ Chat Content
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (conversationHistory.isEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            AssistantWelcomeCard(
                                quickQuestions = quickQuestions,
                                onQuestionClick = { question ->
                                    text = question
                                }
                            )
                        }
                    }

                    items(conversationHistory) { message ->
                        ChatBubble(
                            isUser = message.isUser,
                            message = message.text,
                            userColor = userBubbleColor
                        )
                    }

                    if (isLoading) {
                        item {
                            ChatBubble(isUser = false, message = "Typing...", userColor = userBubbleColor)
                        }
                    }

                    error?.let { err ->
                        item {
                            Text(
                                text = "Error: $err",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                // ✅ Message Input
                MessageInputField(
                    text = text,
                    onTextChange = { newText -> text = newText },
                    onSendClick = {
                        if (text.isNotBlank()) {
                            conversationHistory.add(ChatMessage(text, isUser = true))
                            chatbotViewModel.sendMessage(text, "unique_conversation_id_123")
                            text = ""
                            coroutineScope.launch {
                                if (conversationHistory.isNotEmpty()) {
                                    listState.animateScrollToItem(conversationHistory.lastIndex)
                                }
                            }
                        }
                    },
                    buttonBrush = buttonBrush,
                    cardColors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
                    cardElevation = CardDefaults.cardElevation(0.dp),
                    cardBorder = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                    textFieldColors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color(0xFF8E2DE2),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholderText = "Ask about monuments, festivals..."
                )
            }
        }
    }
}

// ✅ NEW: Chatbot Dialog Header
@Composable
fun ChatbotDialogHeader(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Brush.linearGradient(colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "AI Cultural Assistant",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Ask me about Indian heritage",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}

// --- ALL EXISTING COMPOSABLES REMAIN THE SAME ---

@Composable
fun MainTopAppBar(navController: NavController, userName: String?, profileImageUrl: String?) {
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
                Text(
                    "Namaste ${userName ?: ""}",
                    style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text(
                    "✨ Discover India's timeless heritage",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Calendar icon (replaced notification)
                IconButton(
                    onClick = {
                        navController.navigate(Screen.FestiveCalendar.route)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "Calendar",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = {
                    if (userName != null) {
                        navController.navigate(Screen.Profile.route)
                    } else {
                        Toast.makeText(context, "Please log in to view your profile.", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Auth.route)
                    }
                }) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.ic_profile),
                        placeholder = painterResource(id = R.drawable.ic_profile)
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

// ✅ UPDATED: Now shows only 3 items in a row layout
@Composable
fun QuickActionsGrid(navController: NavController) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // First row - 2 cards (AR Scan + AzadiChat)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionCard(
                action = quickActions[0], // AR Scan
                navController = navController,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                action = quickActions[1], // AzadiChat
                navController = navController,
                modifier = Modifier.weight(1f)
            )
        }

        // Second row - 1 full-width card (Upcoming Events)
        UpcomingEventsCard(
            action = upcomingEventsAction,
            onClick = {
                // No navigation yet - you can add a toast or leave empty
                // Toast.makeText(context, "Coming Soon!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun UpcomingEventsCard(
    action: QuickAction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // Slightly taller for full-width design
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(action.gradientColors)) // Horizontal gradient for wide card
        ) {
            // Decorative background elements
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-20).dp)
                    .size(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side - Text content
                Column {
                    Text(
                        text = action.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = action.subtitle,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }

                // Right side - Icon
                Image(
                    painter = painterResource(id = action.icon),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}


@Composable
fun QuickActionCard(action: QuickAction, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { navController.navigate(action.route) },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(action.gradientColors))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-15).dp, y = (-20).dp)
                    .size(80.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = action.icon),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(action.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(action.subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp)
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
    val currentlyPlayingUrl by viewModel.currentlyPlayingStory.collectAsState()
    val playbackPosition by viewModel.playbackPosition.collectAsState()
    val totalDuration by viewModel.totalDuration.collectAsState()

    val isThisStoryPlaying = isPlaying && currentlyPlayingUrl == story.audiourl

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
                        Slider(
                            value = if (totalDuration > 0 && isThisStoryPlaying) playbackPosition.toFloat() else 0f,
                            onValueChange = { newPosition -> viewModel.seekTo(newPosition.toLong()) },
                            valueRange = 0f..(if (totalDuration > 0) totalDuration.toFloat() else 1f),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(formatDuration(if (isThisStoryPlaying) playbackPosition else 0L), style = MaterialTheme.typography.labelSmall)
                            Text(formatDuration(if (isThisStoryPlaying) totalDuration else 0L), style = MaterialTheme.typography.labelSmall)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        IconButton(
                            onClick = { viewModel.playStory(story) },
                            modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                painter = if (isThisStoryPlaying) painterResource(id = R.drawable.ic_pause) else painterResource(id = R.drawable.ic_play),
                                contentDescription = if (isThisStoryPlaying) "Pause" else "Play",
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

// ✅ NEW: Welcome Card for Chatbot Dialog (reused from original chatbot screen)
@Composable
fun AssistantWelcomeCard(
    quickQuestions: List<String>,
    onQuestionClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Cultural Assistant", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Namaste! 🙏 I can help you with:")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Quick Questions", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                quickQuestions.forEach { question ->
                    QuickQuestionItems(questionText = question, onClick = onQuestionClick)
                }
            }
        }
    }
}

@Composable
fun QuickQuestionItems(questionText: String, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(questionText) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AddCircle,
                contentDescription = "Ask",
                tint = Color(0xFF8E2DE2),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(questionText, color = Color.DarkGray)
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