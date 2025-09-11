package com.example.richculture.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.richculture.R

// Data class for recent scan items
data class RecentScan(
    val imageResId: Int,
    val title: String,
    val timestamp: String,
    val viewIconResId: Int
)

@Composable
fun ArScanScreen(navController: NavController) {
    val recentScans = listOf(
        RecentScan(
            R.drawable.ic_tajmahal,
            "Taj Mahal",
            "Scanned 2 hours ago",
            R.drawable.ic_eye
        )
    )

    // Use a Box to layer the floating back button over the scrollable content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F7FC))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 20.dp),
            // The header provides its own top spacing, so no top padding is needed here.
        ) {
            // The large, shaped header is the first item and scrolls with the list.
            item {
                ArScanHeader()
            }

            // The rest of the content is in another item for clean padding.
            item {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HowToUseCard()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Choose Scan Method",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                    ScanMethodCard(
                        iconResId = R.drawable.ic_camera,
                        title = "Camera Scan",
                        subtitle = "Use camera to scan monuments in real-time",
                        cardColor = Color(0xFFF6F2FF),
                        iconGradient = Brush.linearGradient(listOf(Color(0xFFC084FC), Color(0xFF9333EA)))
                    ) {}
                    ScanMethodCard(
                        iconResId = R.drawable.ic_upload,
                        title = "Upload Image",
                        subtitle = "Select image from your gallery to identify",
                        cardColor = Color(0xFFF0FAF6),
                        iconGradient = Brush.linearGradient(listOf(Color(0xFF6EE7B7), Color(0xFF10B981)))
                    ) {}
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Recent Scans",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                    recentScans.forEach { scan ->
                        RecentScanCard(scan = scan)
                    }
                }
            }
        }

        // Floating Back Button that stays on top
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.2f), CircleShape) // Background for visibility
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}

@Composable
fun ArScanHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(BottomArcShapeArScan(arcHeight = 32.dp)) // Applying the custom shape
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF4C68D7), Color(0xFF8A54C8))
                )
            )
    ) {
        Column(
            // Top padding is increased to ensure content appears below the floating back button
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 72.dp, bottom = 32.dp)
        ) {
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

// Custom Shape for the Top App Bar
class BottomArcShapeArScan(private val arcHeight: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val arcHeightPx = with(density) { arcHeight.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - arcHeightPx)
            quadraticBezierTo(
                size.width / 2, size.height,
                0f, size.height - arcHeightPx
            )
            close()
        }
        return Outline.Generic(path)
    }
}

