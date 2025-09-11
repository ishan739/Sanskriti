package com.example.richculture.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.richculture.R

// Data class for recent scan items
data class RecentScan(
    val imageResId: Int,
    val title: String,
    val timestamp: String,
    val viewIconResId: Int
)

@Composable
fun ArScanScreen() {
    val recentScans = listOf(
        RecentScan(
            R.drawable.ic_tajmahal,
            "Taj Mahal",
            "Scanned 2 hours ago",
            R.drawable.ic_eye
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F7FC)) // Refreshed background color
            .verticalScroll(rememberScrollState())
    ) {
        // --- Refreshed Top Header Section ---
        ArScanHeader()

        // --- Main Content with Increased Padding ---
        Column(modifier = Modifier.padding(20.dp)) { // Increased padding
            // --- Refreshed How to Use Section ---
            HowToUseCard()
            Spacer(modifier = Modifier.height(28.dp))

            // --- Refreshed Scan Method Section ---
            Text(
                "Choose Scan Method",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold, // Bolder font
                color = Color.Black.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            ScanMethodCard(
                iconResId = R.drawable.ic_camera,
                title = "Camera Scan",
                subtitle = "Use camera to scan monuments in real-time",
                cardColor = Color(0xFFF6F2FF), // New light purple card color
                iconGradient = Brush.linearGradient(listOf(Color(0xFFC084FC), Color(0xFF9333EA))) // New icon gradient
            ) {}
            Spacer(modifier = Modifier.height(16.dp))
            ScanMethodCard(
                iconResId = R.drawable.ic_upload,
                title = "Upload Image",
                subtitle = "Select image from your gallery to identify",
                cardColor = Color(0xFFF0FAF6), // Matching light green card color
                iconGradient = Brush.linearGradient(listOf(Color(0xFF6EE7B7), Color(0xFF10B981))) // Matching icon gradient
            ) {}
            Spacer(modifier = Modifier.height(28.dp))

            // --- Recent Scans Section ---
            Text(
                "Recent Scans",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            recentScans.forEach { scan ->
                RecentScanCard(scan = scan)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ArScanHeader() {
    // **CHANGED**: New, more vibrant gradient
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF4C68D7), Color(0xFF8A54C8))
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "AR Scan Mode",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Point camera at monuments or upload images to discover their stories",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun HowToUseCard() {
    // **CHANGED**: Refreshed to a clean white card with more elevation
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "How to Use AR Scan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            InstructionStep(number = "1", text = "Choose camera scan or upload image option")
            Spacer(modifier = Modifier.height(12.dp))
            InstructionStep(number = "2", text = "Point at monument or select image from gallery")
            Spacer(modifier = Modifier.height(12.dp))
            InstructionStep(number = "3", text = "Get instant cultural information and stories")
        }
    }
}

@Composable
fun InstructionStep(number: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color(0xFFE6F0FF), CircleShape), // Refreshed light blue
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color(0xFF4C68D7), // Refreshed dark blue
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
    }
}

@Composable
fun ScanMethodCard(
    iconResId: Int,
    title: String,
    subtitle: String,
    cardColor: Color,
    iconGradient: Brush,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(iconGradient, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = title,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RecentScanCard(scan: RecentScan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = scan.imageResId),
                contentDescription = scan.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.title,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = scan.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Image(
                painter = painterResource(id = scan.viewIconResId),
                contentDescription = "View Scan",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* Handle view action */ }
            )
        }
    }
}

