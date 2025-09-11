package com.example.richculture.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.richculture.Data.FreedomFighter
import com.example.richculture.Data.freedomFighters
import com.example.richculture.R

@Composable
fun AzadiChatScreen(navController: NavController) {
    val featuredFighter = freedomFighters.first() // Let's feature Gandhi by default

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .verticalScroll(rememberScrollState())
    ) {
        HeaderSection(navController)
        FeaturedTodayCard(fighter = featuredFighter, navController = navController)
        ChooseYourLeaderSection(navController)
    }
}

@Composable
fun HeaderSection(navController: NavController) {
    val headerBrush = Brush.verticalGradient(
        listOf(Color(0xFF4DD0E1), Color(0xFF00ACC1))
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(headerBrush, shape = RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.background(Color.White.copy(0.2f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Azadi Chat", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(0.2f), CircleShape)) {
                        Icon(painter = painterResource(R.drawable.ic_fire), contentDescription = "Trending" , tint = Color.Yellow)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.3f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Text(
                    "💫 Connect with India's greatest freedom fighters and learn from their wisdom, courage, and sacrifice.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun FeaturedTodayCard(fighter: FreedomFighter, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Featured Today", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = fighter.imageResId),
                    contentDescription = fighter.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(fighter.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("${fighter.title} • ${fighter.timeline}", color = Color.Gray, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                fighter.tags.forEach { tag ->
                    Chip(text = tag)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(fighter.description, color = Color.DarkGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate("chat_interface/${fighter.id}")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = fighter.primaryColor)
            ) {
                Text("Start Conversation", color = Color.White)
            }
        }
    }
}

@Composable
fun ChooseYourLeaderSection(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Choose Your Leader", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(500.dp), // Adjust height as needed
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(freedomFighters) { fighter ->
                LeaderCard(fighter = fighter) {
                    navController.navigate("chat_interface/${fighter.id}")
                }
            }
        }
    }
}

@Composable
fun LeaderCard(fighter: FreedomFighter, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp) // Increased height so the image fits nicely
            ) {
                Image(
                    painter = painterResource(id = fighter.imageResId),
                    contentDescription = fighter.name,
                    modifier = Modifier
                        .fillMaxSize() // Image will now fill the Box
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)), // Rounded corners same as card top
                    contentScale = ContentScale.Crop // Crop to keep aspect ratio
                )
            }
            Column(Modifier.padding(12.dp)) {
                Text(
                    fighter.name,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    fighter.title,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Composable
fun Chip(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = Color(0xFF388E3C), fontSize = 12.sp)
    }
}
