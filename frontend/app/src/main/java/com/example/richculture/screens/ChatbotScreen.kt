//package com.example.richculture.screens
//
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowDropDown
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.richculture.ViewModels.ChatbotViewModel
//import com.example.richculture.screens.composables.ChatBubble
//import com.example.richculture.screens.composables.ChatMessage
//import com.example.richculture.screens.composables.MessageInputField
//import kotlinx.coroutines.launch
//import org.koin.androidx.compose.koinViewModel
//
//@Composable
//fun ChatbotScreen(
//    navController: NavController,
//    viewModel: ChatbotViewModel = koinViewModel()
//) {
//    val chatResponse by viewModel.chatResponse.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val error by viewModel.error.collectAsState()
//
//    // ✅ State for the text input is now managed here (hoisted)
//    var text by remember { mutableStateOf("") }
//    val conversationHistory = remember { mutableStateListOf<ChatMessage>() }
//    val listState = rememberLazyListState()
//    val coroutineScope = rememberCoroutineScope()
//
//    LaunchedEffect(chatResponse) {
//        chatResponse?.let { response ->
//            conversationHistory.add(ChatMessage(response, isUser = false))
//            viewModel.clearResponse()
//            coroutineScope.launch {
//                listState.animateScrollToItem(conversationHistory.lastIndex)
//            }
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        viewModel.resetChatState()
//        conversationHistory.clear()
//    }
//
//    val quickQuestions = listOf(
//        "Tell me about Taj Mahal",
//        "What festivals are celebrated in December?",
//        "Traditional food of Rajasthan"
//    )
//
//    val userBubbleColor = Color(0xFF6A1B9A)
//    val buttonBrush = Brush.linearGradient(colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))
//
//    Scaffold(
//        topBar = { ChatbotHeader(navController) }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .background(Brush.verticalGradient(colors = listOf(Color(0xFFF4F6FC), Color(0xFFE9EBFA))))
//                .navigationBarsPadding()
//                .imePadding()
//        ) {
//            LazyColumn(
//                state = listState,
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(horizontal = 16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                if (conversationHistory.isEmpty()) {
//                    item {
//                        Spacer(modifier = Modifier.height(16.dp))
//                        AssistantWelcomeCard(
//                            quickQuestions = quickQuestions,
//                            onQuestionClick = { question ->
//                                // ✅ This now works correctly
//                                text = question
//                            }
//                        )
//                    }
//                }
//
//                items(conversationHistory) { message ->
//                    ChatBubble(
//                        isUser = message.isUser,
//                        message = message.text,
//                        userColor = userBubbleColor
//                    )
//                }
//
//                if (isLoading) {
//                    item {
//                        ChatBubble(isUser = false, message = "Typing...", userColor = userBubbleColor)
//                    }
//                }
//
//                error?.let { err ->
//                    item {
//                        Text(text = "Error: $err", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
//                    }
//                }
//            }
//
//            MessageInputField(
//                // ✅ Pass the hoisted state to the input field
//                text = text,
//                onTextChange = { newText -> text = newText },
//                onSendClick = {
//                    if (text.isNotBlank()) {
//                        conversationHistory.add(ChatMessage(text, isUser = true))
//                        viewModel.sendMessage(text, "unique_conversation_id_123")
//                        text = "" // Clear the text after sending
//                        coroutineScope.launch {
//                            listState.animateScrollToItem(conversationHistory.lastIndex)
//                        }
//                    }
//                },
//                buttonBrush = buttonBrush,
//                cardColors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
//                cardElevation = CardDefaults.cardElevation(0.dp),
//                cardBorder = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
//                textFieldColors = TextFieldDefaults.colors(
//                    focusedTextColor = Color.Black,
//                    unfocusedTextColor = Color.Black,
//                    cursorColor = Color(0xFF8E2DE2),
//                    focusedContainerColor = Color.Transparent,
//                    unfocusedContainerColor = Color.Transparent,
//                    disabledContainerColor = Color.Transparent,
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent
//                ),
//                placeholderText = "Ask about monuments, festivals..."
//            )
//        }
//    }
//}
//
//// --- ✅ ALL MISSING COMPOSABLES ARE NOW INCLUDED ---
//
//@Composable
//fun ChatbotHeader(navController: NavController) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
//            .background(Brush.linearGradient(colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))))
//            .padding(horizontal = 16.dp, vertical = 12.dp)
//            .statusBarsPadding()
//    ) {
//        Column {
//            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
//                IconButton(onClick = { navController.popBackStack() }) {
//                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
//                }
//                Spacer(modifier = Modifier.weight(1f))
//                Row(
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(12.dp))
//                        .background(Color.White.copy(alpha = 0.2f))
//                        .padding(horizontal = 12.dp, vertical = 6.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text("GB English", color = Color.White, fontWeight = FontWeight.SemiBold)
//                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Language", tint = Color.White)
//                }
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//            Text("AI Cultural Assistant", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
//            Text("Ask me anything about Indian heritage", color = Color.White.copy(alpha = 0.9f), modifier = Modifier.padding(start = 4.dp))
//        }
//    }
//}
//
//@Composable
//fun AssistantWelcomeCard(
//    quickQuestions: List<String>,
//    onQuestionClick: (String) -> Unit
//) {
//    Card(
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(2.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text("Cultural Assistant", fontWeight = FontWeight.Bold, fontSize = 18.sp)
//            Spacer(modifier = Modifier.height(8.dp))
//            Text("Namaste! 🙏 I can help you with:")
//            Spacer(modifier = Modifier.height(16.dp))
//            Text("Quick Questions", fontWeight = FontWeight.Bold)
//            Spacer(modifier = Modifier.height(8.dp))
//            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
//                quickQuestions.forEach { question ->
//                    QuickQuestionItem(questionText = question, onClick = onQuestionClick)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun QuickQuestionItem(questionText: String, onClick: (String) -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick(questionText) },
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))
//    ) {
//        Row(
//            modifier = Modifier.padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                Icons.Default.Search,
//                contentDescription = "Ask",
//                tint = Color(0xFF8E2DE2),
//                modifier = Modifier.size(18.dp)
//            )
//            Spacer(modifier = Modifier.width(12.dp))
//            Text(questionText, color = Color.DarkGray)
//        }
//    }
//}
//
