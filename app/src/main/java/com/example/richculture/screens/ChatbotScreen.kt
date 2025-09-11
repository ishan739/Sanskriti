package com.example.richculture.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.richculture.ViewModels.UniversalChatViewModel
import kotlinx.coroutines.launch

// Data class to represent a single chat message for better state management
data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun ChatbotScreen(
    navController: NavController,
    viewModel: UniversalChatViewModel = viewModel()
) {
    // State from ViewModel
    val chatResponse by viewModel.chatResponse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // UI State
    var text by remember { mutableStateOf("") }
    val conversationHistory = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // --- IMPROVEMENT: Handle new responses and add them to the conversation history ---
    LaunchedEffect(chatResponse) {
        chatResponse?.let { response ->
            conversationHistory.add(ChatMessage(response, isUser = false))
            viewModel.clearResponse() // Clear response to prevent re-adding on recompose
            // Auto-scroll to the new message
            coroutineScope.launch {
                listState.animateScrollToItem(conversationHistory.lastIndex)
            }
        }
    }

    // --- IMPROVEMENT: Reset chat state when the screen is first entered ---
    LaunchedEffect(Unit) {
        viewModel.resetChatState()
        conversationHistory.clear()
    }

    val quickQuestions = listOf(
        "Tell me about Taj Mahal",
        "What festivals are celebrated in December?",
        "Traditional food of Rajasthan"
    )

    Scaffold(
        topBar = { ChatbotHeader(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // --- UI POLISH: A subtle gradient background is more visually appealing ---
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFF4F6FC), Color(0xFFE9EBFA))
                    )
                )
                // --- FIX 1: This prevents the input field from overlapping the system navigation bar ---
                .navigationBarsPadding()
                // --- FIX 2: This makes the UI adjust when the keyboard opens/closes ---
                .imePadding()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // The welcome card now only shows if the conversation is empty
                if (conversationHistory.isEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        AssistantWelcomeCard(
                            quickQuestions = quickQuestions,
                            onQuestionClick = { question ->
                                text = question // Correctly places question in the text field
                            }
                        )
                    }
                }

                // --- IMPROVEMENT: Display the entire conversation history ---
                items(conversationHistory) { message ->
                    ChatBubble(isUser = message.isUser, message = message.text)
                }

                // Display a typing indicator when loading
                if (isLoading) {
                    item {
                        ChatBubble(isUser = false, message = "Typing...")
                    }
                }

                // Display any errors inline
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

            // Input Field
            MessageInputField(
                text = text,
                onTextChange = { text = it },
                onSendClick = {
                    if (text.isNotBlank()) {
                        // Immediately add user message to history for a responsive feel
                        conversationHistory.add(ChatMessage(text, isUser = true))
                        viewModel.sendMessage(text, "unique_conversation_id_123")
                        text = ""
                        // Auto-scroll when the user sends a message
                        coroutineScope.launch {
                            listState.animateScrollToItem(conversationHistory.lastIndex)
                        }
                    }
                }
            )
        }
    }
}

// --- NO CHANGES NEEDED FOR ChatbotHeader OR OTHER COMPOSABLES IN THIS FILE ---

@Composable
fun ChatbotHeader(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Brush.linearGradient(colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("GB English", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Language", tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("AI Cultural Assistant", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
            Text("Ask me anything about Indian heritage", color = Color.White.copy(alpha = 0.9f), modifier = Modifier.padding(start = 4.dp))
        }
    }
}

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
                    QuickQuestionItem(questionText = question, onClick = onQuestionClick)
                }
            }
        }
    }
}

@Composable
fun QuickQuestionItem(questionText: String, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick(questionText) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = "Ask",
            tint = Color(0xFF8E2DE2),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(questionText, color = Color.DarkGray)
    }
}


@Composable
fun MessageInputField(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    // --- UI POLISH: Enhanced Glassmorphic effect with a subtle border ---
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.5f) // Slightly less transparent
            ),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Ask about monuments, festivals...",
                            color = Color.Gray.copy(alpha = 0.9f)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color(0xFF8E2DE2),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onSendClick,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                            )
                        )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(isUser: Boolean, message: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 0.dp,
                bottomEnd = if (isUser) 0.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) Color(0xFF6A1B9A) else Color.White
            ),
            modifier = Modifier.widthIn(max = 280.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) Color.White else Color.Black
            )
        }
    }
}

