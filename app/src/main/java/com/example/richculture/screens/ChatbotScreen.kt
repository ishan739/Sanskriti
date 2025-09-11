package com.example.richculture.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.richculture.R

@Composable
fun ChatbotScreen(navController: NavController) {
    val quickQuestions = listOf(
        "Tell me about Taj Mahal", "What festivals are celebrated in December?",
        "Explain Bharatanatyam dance", "Traditional food of Rajasthan",
        "Heritage sites in Kerala", "Significance of Diwali"
    )

    // ✅ THE STRUCTURE IS CHANGED HERE
    // We now use a Column to manually position the content and the input field.
    Scaffold(
        topBar = { ChatbotHeader(navController) },
        containerColor = Color(0xFFF0F2F5)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply padding from the Scaffold
        ) {
            // The LazyColumn for messages now takes up the remaining space, pushing the input field down.
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // ✅ THIS IS THE KEY CHANGE
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    AssistantWelcomeCard(quickQuestions)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                // Your chat messages would go here
            }

            // The MessageInputField is now the last item in the Column, placing it at the bottom.
            MessageInputField()
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
fun AssistantWelcomeCard(quickQuestions: List<String>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = R.drawable.ic_chatbot_avatar), contentDescription = "Assistant Avatar", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cultural Assistant", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Image(painter = painterResource(id = R.drawable.ic_volume), contentDescription = "Text to Speech", modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Namaste! 🙏 I'm your cultural heritage assistant. I can help you with:")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Quick Questions", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                quickQuestions.forEach { question ->
                    QuickQuestionItem(questionText = question)
                }
            }
        }
    }
}

@Composable
fun QuickQuestionItem(questionText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F2F5))
            .clickable { /* Handle question click */ }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, contentDescription = "Ask", tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(questionText, color = Color.DarkGray)
    }
}

@Composable
fun MessageInputField() {
    var text by remember { mutableStateOf("") }
    var isMicEnabled by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 63.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask about monuments, festivals, ...", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                trailingIcon = {
                    IconButton(onClick = { isMicEnabled = !isMicEnabled }) {
                        Image(painter = painterResource(id = R.drawable.ic_mic), contentDescription = if (isMicEnabled) "Voice Input" else "Voice Muted")
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { /* Handle send message */ },
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Brush.linearGradient(colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))))
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

