package com.example.richculture.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.richculture.Data.freedomFighters
import com.example.richculture.ViewModels.UniversalChatViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatInterfaceScreen(
    navController: NavController,
    leaderId: String,
    viewModel: UniversalChatViewModel = viewModel()
) {
    // Find the leader's data using the ID passed through navigation
    val leader = remember { freedomFighters.find { it.id == leaderId } } ?: return

    val chatResponse by viewModel.chatResponse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val conversationHistory = remember { mutableStateListOf<ChatMessage>() }

    // This effect runs when a new chatResponse arrives from the ViewModel
    LaunchedEffect(chatResponse) {
        chatResponse?.let { responseMessage ->
            conversationHistory.add(ChatMessage(responseMessage, isUser = false))
            viewModel.clearResponse() // Clear the response so it's not added again
            // Auto-scroll to the new message
            coroutineScope.launch {
                listState.animateScrollToItem(conversationHistory.size - 1)
            }
        }
    }

    // This effect runs once when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.resetChatState()
        conversationHistory.clear()
        // Add a welcoming message from the leader
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
                .imePadding() // Automatically handles keyboard overlap
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
                        // You can add a typing indicator here if you want
                    }
                }
            }
            MessageInputField(
                onSendClick = { text ->
                    if (text.isNotBlank()) {
                        conversationHistory.add(ChatMessage(text, isUser = true))
                        // Use the leader's ID as the target for the API call
                        viewModel.sendMessage(text, target = leader.id)
                        coroutineScope.launch {
                            listState.animateScrollToItem(conversationHistory.size)
                        }
                    }
                },
                accentColor = leader.primaryColor
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

@Composable
fun ChatBubble(message: String, isUser: Boolean, userColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) userColor else Color.White
            )
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun MessageInputField(onSendClick: (String) -> Unit, accentColor: Color) {
    var text by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask a question...") },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = accentColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            IconButton(
                onClick = {
                    onSendClick(text)
                    text = ""
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(accentColor, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

