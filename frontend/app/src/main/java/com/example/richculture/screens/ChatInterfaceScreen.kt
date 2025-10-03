package com.example.richculture.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.richculture.Data.leaders
import com.example.richculture.ViewModels.AzadiChatViewModel
import com.example.richculture.screens.composables.ChatBubble
import com.example.richculture.screens.composables.ChatMessage
import com.example.richculture.screens.composables.MessageInputField
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatInterfaceScreen(
    navController: NavController,
    leaderId: String,
    viewModel: AzadiChatViewModel = koinViewModel()
) {
    val leader = remember { leaders.find { it.id == leaderId } } ?: return

    val chatResponse by viewModel.chatResponse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val conversationHistory = remember { mutableStateListOf<ChatMessage>() }
    // ✅ NEW: Hoisted state for the text input field
    var text by remember { mutableStateOf("") }

    LaunchedEffect(chatResponse) {
        chatResponse?.let { responseMessage ->
            conversationHistory.add(ChatMessage(responseMessage, isUser = false))
            viewModel.clearResponse()
            coroutineScope.launch {
                listState.animateScrollToItem(conversationHistory.size - 1)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.resetChatState()
        conversationHistory.clear()
        conversationHistory.add(ChatMessage("Greetings! How may I share my story with you today?", isUser = false))
    }

    Scaffold(
        topBar = {
            ChatInterfaceHeader(
                navController = navController,
                leaderName = leader.name,
                leaderImageRes = leader.imageResId
            )
        },
        containerColor = Color(0xFFF0F4F8)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(conversationHistory) { message ->
                    ChatBubble(
                        message = message.text,
                        isUser = message.isUser,
                        userColor = leader.primaryColor
                    )
                }
                if (isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
            // ✅ CRITICAL FIX: The call to MessageInputField is now correct
            MessageInputField(
                text = text,
                onTextChange = { newText -> text = newText },
                onSendClick = {
                    if (text.isNotBlank()) {
                        conversationHistory.add(ChatMessage(text, isUser = true))
                        viewModel.sendMessage(text, leaderName = leader.name)
                        text = "" // Clear the text after sending
                        coroutineScope.launch {
                            listState.animateScrollToItem(conversationHistory.size)
                        }
                    }
                },
                textFieldColors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = leader.primaryColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                buttonBrush = leader.gradient
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInterfaceHeader(
    navController: NavController,
    leaderName: String,
    leaderImageRes: Int
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = leaderImageRes),
                    contentDescription = leaderName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(leaderName, fontWeight = FontWeight.Bold)
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back" , tint = Color.Black)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black
        )
    )
}

