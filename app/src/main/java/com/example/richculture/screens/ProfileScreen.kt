package com.example.richculture.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.richculture.Data.User
import com.example.richculture.R
import com.example.richculture.ViewModels.UserViewModel
import com.example.richculture.navigate.Screen
import com.example.richculture.utility.SessionManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.compose.koinViewModel

// --- Data Models for UI (keeping dummy data for sections without APIs yet) ---
data class ProgressInfo(
    val levelProgress: Float, val sitesVisited: Int, val festivalsAttended: Int, val craftsBought: Int
)
data class Badge(
    val icon: ImageVector, val title: String, val description: String, val progress: Int, val color: Color
)
data class SavedItem(
    val imageResId: Int, val title: String, val category: String, val categoryColor: Color
)
data class RecentOrder(
    val imageResId: Int, val name: String, val price: String, val date: String, val status: String, val statusColor: Color
)

val dummyProgress = ProgressInfo(0.68f, 15, 8, 23)
val dummyBadges = listOf(
    Badge(Icons.Default.Star, "Festival Enthusiast", "Attended 5 festivals", 100, Color(0xFFE0F2F1)),
    Badge(Icons.Default.Place, "Monument Explorer", "Visited 10 heritage sites", 100, Color(0xFFE3F2FD))
)
val dummySavedItems = listOf(
    SavedItem(R.drawable.ic_tajmahal, "Taj Mahal AR Tour", "Monument", Color(0xFFF44336))
)
val dummyOrders = listOf(
    RecentOrder(R.drawable.ic_rajasthani_scarf, "Handwoven Banarasi Saree", "₹12,500", "2 days ago", "Delivered", Color(0xFF4CAF50))
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

    // Show a loading screen or message if the user data isn't available yet
    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color(0xFFFFFDE7), Color(0xFFFFF8E1))))
    ) {
        Scaffold(
            topBar = { ProfileTopAppBar(navController) },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding(), start = 10.dp, end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item { UserInfoCard(user = currentUser!!, onEditClick = { showEditSheet = true }) }
                item { ProgressTrackerCard(progress = dummyProgress) }
                item { AchievementsSection(badges = dummyBadges) }
                item { SavedItemsSection(items = dummySavedItems) }
                item { RecentOrdersSection(orders = dummyOrders) }
                item { SettingsSection() }
                item {
                    AccountActionsSection(
                        onSignOut = {
                            userViewModel.logout()
                            sessionManager.navigateToAuth(navController)
                        }
                    )
                }
            }
        }

        // --- EDIT PROFILE BOTTOM SHEET ---
        if (showEditSheet) {
            ModalBottomSheet(
                onDismissRequest = { showEditSheet = false },
                sheetState = sheetState
            ) {
                EditProfileSheetContent(
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
                                Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                                sheetState.hide()
                            }
                        }.invokeOnCompletion {
                            // ✅ CRITICAL FIX: The correct way to check for cancellation
                            if (it !is CancellationException) {
                                showEditSheet = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun UserInfoCard(user: User, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                AsyncImage(
                    model = user.profileImage,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(80.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_profile) // Fallback
                )
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(6.dp)
                        .clickable(onClick = onEditClick)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(user.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(user.bio ?: "No bio yet.", color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoRow(icon = Icons.Default.LocationOn, text = "Mumbai, India") // Placeholder
                InfoRow(icon = Icons.Default.Star, text = "Joined ${user.createdAt.take(10)}")
            }
        }
    }
}


@Composable
fun EditProfileSheetContent(user: User, onSaveChanges: (Uri?, String, String, String) -> Unit) {
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
            .padding(16.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Edit Your Profile", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.clickable { galleryLauncher.launch("image/*") }) {
            AsyncImage(
                model = selectedImageUri ?: user.profileImage,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(100.dp).clip(CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_profile)
            )
            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.align(Alignment.Center), tint = Color.White)
        }
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onSaveChanges(selectedImageUri, name, bio, gender) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isUpdating
        ) {
            if (isUpdating) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Save Changes")
            }
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopAppBar(navController: NavHostController) {
    TopAppBar(
        title = {
            Column {
                Text("My Profile", fontWeight = FontWeight.Bold , color = Color.Black)
                Text("Cultural journey dashboard", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back" , tint = Color.Black)
            }
        },
        actions = {
            IconButton(onClick = {}) { Icon(Icons.Default.Settings, contentDescription = "Settings" , tint = Color.Black) }
            IconButton(onClick = {}) { Icon(Icons.Default.Share, contentDescription = "Share" , tint = Color.Black) }
        },
        windowInsets = WindowInsets(0),
        modifier = Modifier.height(56.dp),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun AccountActionsSection(onSignOut: () -> Unit) {
    Section(title = "Account Actions") {
        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out", tint = Color.Red)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProgressTrackerCard(progress: ProgressInfo) {
    Section(title = "Progress Tracker") {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Level Progress", fontWeight = FontWeight.Bold)
                    Text("${(progress.levelProgress * 100).toInt()}% to Heritage Guru", color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress.levelProgress },
                    color = Color(0xFFFF5722),
                    trackColor = Color.Black.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    ProgressStat(count = progress.sitesVisited, label = "Sites Visited", color = Color(0xFFD32F2F))
                    ProgressStat(count = progress.festivalsAttended, label = "Festivals", color = Color(0xFF1976D2))
                    ProgressStat(count = progress.craftsBought, label = "Crafts Bought", color = Color(0xFF388E3C))
                }
            }
        }
    }
}

@Composable
fun AchievementsSection(badges: List<Badge>) {
    Section(title = "Achievements & Badges") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            badges.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowItems.forEach { badge ->
                        BadgeCard(badge = badge, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun SavedItemsSection(items: List<SavedItem>) {
    Section(title = "Saved Items", action = { TextChip("${items.size} items") }) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { item ->
                SavedItemRow(item)
            }
        }
    }
}

@Composable
fun RecentOrdersSection(orders: List<RecentOrder>) {
    Section(title = "Recent Orders", action = {
        TextButton(onClick = { /*TODO*/ }) {
            Text("View All")
        }
    }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f))
        ) {
            Column {
                orders.forEachIndexed { index, order ->
                    OrderRow(order)
                    if (index < orders.lastIndex) {
                        Divider(color = Color.Black.copy(alpha = 0.05f))
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection() {
    Section(title = "Settings") {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Language", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    LanguageChip("English", true)
                    LanguageChip("हिन्दी", false)
                    LanguageChip("தமிழ்", false)
                }
                Divider(color = Color.Black.copy(alpha = 0.05f))
                SettingRow(icon = Icons.Default.Notifications, title = "Notifications", subtitle = "Festival alerts & updates")
                Divider(color = Color.Black.copy(alpha = 0.05f))
                SettingRow(icon = Icons.Default.Lock, title = "Privacy & Security", subtitle = "Manage your data preferences", showToggle = false)
            }
        }
    }
}

@Composable
fun Section(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (action != null) {
                action()
            }
        }
        content()
    }
}

@Composable
fun BadgeCard(badge: Badge, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = badge.color.copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Icon(badge.icon, contentDescription = badge.title, modifier = Modifier.size(28.dp))
                if (badge.progress == 100) {
                    TextChip("Earned", Color(0xFF388E3C))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(badge.title, fontWeight = FontWeight.Bold)
            Text(badge.description, fontSize = 12.sp, color = Color.Gray)
            if (badge.progress < 100) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { badge.progress / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    trackColor = Color.Black.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun SavedItemRow(item: SavedItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.title,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold)
                TextChip(item.category, item.categoryColor, small = true)
            }
            Icon(Icons.Default.Favorite, contentDescription = "Favorited", tint = Color.Red)
        }
    }
}

@Composable
fun OrderRow(order: RecentOrder) {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = order.imageResId),
            contentDescription = order.name,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(order.name, fontWeight = FontWeight.Bold)
            Text(order.price, color = Color.Gray, fontSize = 12.sp)
            Text(order.date, color = Color.Gray, fontSize = 12.sp)
        }
        TextChip(order.status, order.statusColor)
    }
}

@Composable
fun SettingRow(icon: ImageVector, title: String, subtitle: String, showToggle: Boolean = true) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        if (showToggle) {
            var checked by remember { mutableStateOf(true) }
            Switch(checked = checked, onCheckedChange = { checked = it })
        } else {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Navigate", modifier = Modifier.size(16.dp))
        }
    }
}


@Composable
fun ProgressStat(count: Int, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count.toString(), color = color, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun TextChip(text: String, color: Color = Color.Gray, small: Boolean = false) {
    Text(
        text = text,
        color = color,
        fontSize = if (small) 10.sp else 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(50))
            .padding(horizontal = if (small) 8.dp else 12.dp, vertical = if (small) 4.dp else 6.dp)
    )
}

@Composable
fun LanguageChip(lang: String, isSelected: Boolean) {
    Text(
        text = lang,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                RoundedCornerShape(8.dp)
            )
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    )
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}

