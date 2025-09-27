package com.example.richculture.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.richculture.Data.User
import com.example.richculture.R
import com.example.richculture.ViewModels.UserViewModel
import com.example.richculture.utility.SessionManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.compose.koinViewModel

// --- Data Models ---
data class ProgressInfo(
    val levelProgress: Float, val sitesVisited: Int, val festivalsAttended: Int, val craftsBought: Int
)
data class Badge(
    val icon: ImageVector, val title: String, val description: String, val progress: Int, val color: Color
)
data class RecentOrder(
    val imageResId: Int, val name: String, val price: String, val date: String, val status: String, val statusColor: Color
)

// Modern vibrant color palette
object ModernColors {
    val PrimaryBlue = Color(0xFF0EA5E9)
    val PrimaryPurple = Color(0xFF8B5CF6)
    val Coral = Color(0xFFFF6B6B)
    val Emerald = Color(0xFF10B981)
    val Yellow = Color(0xFFFBBF24)
    val Pink = Color(0xFFEC4899)
    val Orange = Color(0xFFF97316)
    val Indigo = Color(0xFF6366F1)
}

// Dummy data
val dummyProgress = ProgressInfo(0.75f, 24, 12, 38)
val dummyBadges = listOf(
    Badge(Icons.Default.Star, "Festival Master", "Attended 10+ festivals", 100, ModernColors.Yellow),
    Badge(Icons.Default.LocationOn, "Explorer", "Visited 20+ monuments", 85, ModernColors.Emerald),
    Badge(Icons.Default.AddCircle, "Collector", "Bought 30+ crafts", 90, ModernColors.Coral)
)
val dummyOrders = listOf(
    RecentOrder(R.drawable.ic_rajasthani_scarf, "Silk Rajasthani Scarf", "₹2,850", "1 day ago", "Delivered", ModernColors.Emerald),
    RecentOrder(R.drawable.ic_rajasthani_scarf, "Handcrafted Pottery", "₹1,200", "3 days ago", "Processing", ModernColors.Orange)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    userViewModel: UserViewModel = koinViewModel()
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showEditSheet by remember { mutableStateOf(false) }

    // Animated gradient background
    DynamicGradientBackground()

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            ModernTopBar(onBackClick = { navController.popBackStack() })
        },
        containerColor = Color.Transparent
    ) { padding ->
        if (currentUser == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ModernColors.PrimaryBlue)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp)
        ) {
            item {
                ModernUserCard(
                    user = currentUser!!,
                    onEditClick = { showEditSheet = true }
                )
            }
            item { ModernProgressCard(progress = dummyProgress) }
            item { ModernAchievementsCard(badges = dummyBadges) }
            item { ModernOrdersCard(orders = dummyOrders) }
            item { ModernSettingsCard() }
            item {
                ModernSignOutCard {
                    userViewModel.logout()
                    sessionManager.navigateToAuth(navController)
                }
            }
        }

        if (showEditSheet) {
            ModalBottomSheet(
                onDismissRequest = { showEditSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                dragHandle = {
                    Surface(
                        modifier = Modifier
                            .width(50.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        color = Color.Gray.copy(alpha = 0.3f)
                    ) {}
                }
            ) {
                ModernEditProfileSheet(
                    user = currentUser!!,
                    onSaveChanges = { uri, name, bio, gender ->
                        scope.launch {
                            val token = sessionManager.getCurrentUserToken()
                            if (token != null) {
                                val imagePart = uri?.let { uriToMultipartBody(context, it, "profileImage") }
                                val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                                val bioPart = bio.toRequestBody("text/plain".toMediaTypeOrNull())
                                val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())
                                userViewModel.updateUserProfile(token, imagePart, namePart, bioPart, genderPart)
                                Toast.makeText(context, "Profile updated successfully! ✨", Toast.LENGTH_SHORT).show()
                                sheetState.hide()
                            }
                        }.invokeOnCompletion {
                            showEditSheet = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DynamicGradientBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_gradient")

    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF667eea),
        targetValue = Color(0xFF764ba2),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val color2 by infiniteTransition.animateColor(
        initialValue = Color(0xFF764ba2),
        targetValue = Color(0xFFF093FB),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val color3 by infiniteTransition.animateColor(
        initialValue = Color(0xFFF093FB),
        targetValue = Color(0xFF667eea),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(color1, color2, color3),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    )
}

@Composable
fun ModernTopBar(onBackClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBackClick,
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier
                    .size(48.dp)
                    .shadow(8.dp, CircleShape)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "My Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 28.sp
            )
        }
    }
}

@Composable
fun ModernCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor
    ) {
        content()
    }
}

@Composable
fun ModernUserCard(user: User, onEditClick: () -> Unit) {
    ModernCard(backgroundColor = Color.White) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                // Profile image with rainbow border
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    ModernColors.Pink,
                                    ModernColors.Orange,
                                    ModernColors.Yellow,
                                    ModernColors.Emerald,
                                    ModernColors.PrimaryBlue,
                                    ModernColors.PrimaryPurple
                                )
                            ),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                ) {
                    AsyncImage(
                        model = user.profileImage,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White),
                        contentScale = ContentScale.Crop
                    )
                }

                // Edit button with gradient
                Surface(
                    onClick = onEditClick,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(8.dp, CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ModernColors.Pink, ModernColors.Orange)
                            ),
                            shape = CircleShape
                        )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.background(
                            Brush.linearGradient(
                                colors = listOf(ModernColors.Pink, ModernColors.Orange)
                            )
                        )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                user.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1F2937)
            )

            Text(
                user.bio ?: "Cultural Explorer 🎭",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Status badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = ModernColors.Emerald.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, ModernColors.Emerald.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(ModernColors.Emerald, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Active Explorer",
                        color = ModernColors.Emerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ModernProgressCard(progress: ProgressInfo) {
    ModernCard(backgroundColor = Color.White) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Level Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1F2937)
                )
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ModernColors.PrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Text(
                        "${(progress.levelProgress * 100).toInt()}%",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = ModernColors.PrimaryBlue,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated progress bar
            LinearProgressIndicator(
                progress = { progress.levelProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = ModernColors.PrimaryBlue,
                trackColor = Color(0xFFF3F4F6),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ModernStatCard(
                    count = progress.sitesVisited,
                    label = "Sites",
                    icon = Icons.Default.LocationOn,
                    color = ModernColors.Coral
                )
                ModernStatCard(
                    count = progress.festivalsAttended,
                    label = "Festivals",
                    icon = Icons.Default.AddCircle,
                    color = ModernColors.Yellow
                )
                ModernStatCard(
                    count = progress.craftsBought,
                    label = "Crafts",
                    icon = Icons.Default.AddCircle,
                    color = ModernColors.Emerald
                )
            }
        }
    }
}

@Composable
fun ModernStatCard(count: Int, label: String, icon: ImageVector, color: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        modifier = Modifier.size(width = 90.dp, height = 80.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                count.toString(),
                fontWeight = FontWeight.Black,
                color = color,
                fontSize = 18.sp
            )
            Text(
                label,
                color = color.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModernAchievementsCard(badges: List<Badge>) {
    ModernCard(backgroundColor = Color.White) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Achievements",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                badges.forEach { badge ->
                    ModernBadgeCard(badge, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ModernBadgeCard(badge: Badge, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(20.dp),
        color = badge.color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, badge.color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = badge.color,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        badge.icon,
                        contentDescription = badge.title,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                badge.title,
                fontWeight = FontWeight.Bold,
                color = badge.color,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                "${badge.progress}%",
                color = badge.color.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModernOrdersCard(orders: List<RecentOrder>) {
    ModernCard(backgroundColor = Color.White) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Orders",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1F2937)
                )
                TextButton(onClick = { /* TODO */ }) {
                    Text(
                        "View All",
                        color = ModernColors.PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            orders.forEach { order ->
                ModernOrderRow(order)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ModernOrderRow(order: RecentOrder) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF9FAFB),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(50.dp)
            ) {
                Image(
                    painter = painterResource(id = order.imageResId),
                    contentDescription = order.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    order.name,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    fontSize = 14.sp
                )
                Text(
                    order.price,
                    color = ModernColors.PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = order.statusColor.copy(alpha = 0.1f)
            ) {
                Text(
                    order.status,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = order.statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ModernSettingsCard() {
    ModernCard(backgroundColor = Color.White) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModernSettingRow(
                icon = Icons.Default.Notifications,
                title = "Push Notifications",
                subtitle = "Festival alerts & cultural updates",
                color = ModernColors.Orange
            )

            Spacer(modifier = Modifier.height(12.dp))

            ModernSettingRow(
                icon = Icons.Default.LocationOn,
                title = "Location Services",
                subtitle = "Find nearby cultural events",
                color = ModernColors.Emerald
            )
        }
    }
}

@Composable
fun ModernSettingRow(icon: ImageVector, title: String, subtitle: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF9FAFB)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    fontSize = 14.sp
                )
                Text(
                    subtitle,
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp
                )
            }

            var checked by remember { mutableStateOf(true) }
            Switch(
                checked = checked,
                onCheckedChange = { checked = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = color,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFD1D5DB)
                )
            )
        }
    }
}

@Composable
fun ModernSignOutCard(onSignOut: () -> Unit) {
    Surface(
        onClick = onSignOut,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(2.dp, Color(0xFFEF4444)),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 15.dp,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Sign Out",
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Sign Out",
                color = Color(0xFFEF4444),
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun ModernEditProfileSheet(
    user: User,
    onSaveChanges: (Uri?, String, String, String) -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var name by remember { mutableStateOf(user.name) }
    var bio by remember { mutableStateOf(user.bio ?: "") }
    var gender by remember { mutableStateOf(user.gender ?: "Other") }
    val isUpdating by koinViewModel<UserViewModel>().isUpdatingProfile.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> selectedImageUri = uri }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Edit Profile ✨",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1F2937)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Profile image with gradient border
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.clickable { galleryLauncher.launch("image/*") }
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(ModernColors.Pink, ModernColors.PrimaryBlue)
                        ),
                        shape = CircleShape
                    )
                    .padding(3.dp)
            ) {
                AsyncImage(
                    model = selectedImageUri ?: user.profileImage,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.White),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_profile)
                )
            }

            Surface(
                shape = CircleShape,
                color = ModernColors.PrimaryBlue,
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Input fields with modern styling
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ModernColors.PrimaryBlue,
                focusedLabelColor = ModernColors.PrimaryBlue,
                cursorColor = ModernColors.PrimaryBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ModernColors.Emerald,
                focusedLabelColor = ModernColors.Emerald,
                cursorColor = ModernColors.Emerald
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = gender,
            onValueChange = { gender = it },
            label = { Text("Gender") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ModernColors.Pink,
                focusedLabelColor = ModernColors.Pink,
                cursorColor = ModernColors.Pink
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Save button with gradient
        Button(
            onClick = { onSaveChanges(selectedImageUri, name, bio, gender) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            enabled = !isUpdating,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(ModernColors.PrimaryBlue, ModernColors.PrimaryPurple)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Save Changes",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun uriToMultipartBody(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileBytes = inputStream.readBytes()
        inputStream.close()
        val requestFile = fileBytes.toRequestBody(context.contentResolver.getType(uri)?.toMediaTypeOrNull())
        MultipartBody.Part.createFormData(partName, "image.jpg", requestFile)
    } catch (e: Exception) {
        null
    }
}