package com.example.richculture.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.richculture.Data.Scanner
import com.example.richculture.R
import com.example.richculture.ViewModels.ScannerViewModel
import com.example.richculture.navigate.Screen
import com.example.richculture.utility.ScanHistoryManager
import com.example.richculture.utility.StoredScan
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun ArScanScreen(
    navController: NavController,
    viewModel: ScannerViewModel = koinViewModel()
) {
    val scanHistoryManager = koinViewModel<ScannerViewModel>().scanHistoryManager
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val scanResult by viewModel.monumentInfo.observeAsState()
    val error by viewModel.error.observeAsState()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    val recentScans by viewModel.scanHistory.collectAsState()

    val newImageUriResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>("image_uri")

    LaunchedEffect(newImageUriResult) {
        if (newImageUriResult != null) {
            imageUri = Uri.parse(newImageUriResult)
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("image_uri")
        }
    }

    LaunchedEffect(Unit) { viewModel.clearScanResult() }
    LaunchedEffect(error) {
        error?.let {
            isLoading = false
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F7FC))) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp)) {
            item { ArScanHeader() }
            item {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    HowToUseCard()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Choose Scan Method", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    ScanMethodCard(
                        iconResId = R.drawable.ic_camera, title = "Camera Scan", subtitle = "Use your camera in real-time",
                        cardColor = Color(0xFFF6F2FF), iconGradient = Brush.linearGradient(listOf(Color(0xFFC084FC), Color(0xFF9333EA)))
                    ) { navController.navigate(Screen.Camera.route) }
                    ScanMethodCard(
                        iconResId = R.drawable.ic_upload, title = "Upload Image", subtitle = "Select from your gallery",
                        cardColor = Color(0xFFF0FAF6), iconGradient = Brush.linearGradient(listOf(Color(0xFF6EE7B7), Color(0xFF10B981)))
                    ) { galleryLauncher.launch("image/*") }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (recentScans.isNotEmpty()) {
                        Text("Recent Scans", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                        recentScans.forEach { scan -> RecentScanCard(scan = scan) }
                    }
                }
            }
        }
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().background(Color.Black.copy(alpha = 0.2f), CircleShape)
        ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White) }

        AnimatedVisibility(
            visible = imageUri != null && scanResult == null,
            enter = slideInVertically { it } + fadeIn(), exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            SelectedImagePreview(
                imageUri = imageUri, isLoading = isLoading, onClear = { imageUri = null },
                onSubmit = {
                    isLoading = true
                    val imagePart = uriToMultipartBody(context, imageUri!!, "image")
                    if (imagePart != null) viewModel.uploadImage(imagePart)
                    else {
                        Toast.makeText(context, "Failed to process image.", Toast.LENGTH_SHORT).show()
                        isLoading = false
                    }
                }
            )
        }
    }

    if (scanResult != null) {
        // ✅ CRITICAL FIX 2: Stop the loading indicator when a result is received.
        LaunchedEffect(scanResult) {
            isLoading = false
        }
        ScannerResultDialog(
            result = scanResult!!, userImageUri = imageUri,
            onDismiss = {
                // ✅ CRITICAL FIX 1: Clear both the ViewModel result and the local image URI.
                viewModel.clearScanResult()
                imageUri = null
            },
            onReadMore = { url ->
                val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                navController.navigate(Screen.WebView.route + "/$encodedUrl")
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerResultDialog(
    result: Scanner,
    userImageUri: Uri?,
    onDismiss: () -> Unit,
    onReadMore: (String) -> Unit
) {
    val serifFontFamily = FontFamily(Font(R.font.playfair_display_regular))

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = userImageUri, contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(Brush.verticalGradient(colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Black.copy(alpha = 0.9f))))
                    }
                }
            )
            Scaffold(
                containerColor = Color.Transparent,
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = { onReadMore(result.mobileWikiUrl) },
                        containerColor = Color.White.copy(alpha = 0.9f),
                        contentColor = Color.Black,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        Image(painter = painterResource(id =R.drawable.ic_wiki),
                            modifier = Modifier.size(30.dp),
                            contentDescription = "Read More")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Read More on Wikipedia", fontWeight = FontWeight.Bold)
                    }
                },
                floatingActionButtonPosition = FabPosition.Center,
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = { IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White) } },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        windowInsets = WindowInsets(0)
                    )
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp) // Padding for FAB
                ) {
                    item {
                        AsyncImage(
                            model = result.originalImage, contentDescription = result.name,
                            modifier = Modifier.fillMaxWidth().aspectRatio(1.2f).clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = result.name, color = Color.White, fontSize = 40.sp, fontFamily = serifFontFamily, fontWeight = FontWeight.Bold)
                        Text(text = result.tagline, color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        Spacer(modifier = Modifier.height(20.dp))
                        Divider(color = Color.White.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = result.description, color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp, lineHeight = 24.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedImagePreview(
    imageUri: Uri?,
    isLoading: Boolean,
    onClear: () -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding(), // ✅ This adds padding for the system gesture bar
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                AsyncImage(
                    model = imageUri, contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(onClick = onClear, modifier = Modifier.padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                    Icon(Icons.Default.Close, contentDescription = "Clear Image", tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Brush.linearGradient(colors = listOf(Color(0xFF4C68D7), Color(0xFF8A54C8)))),
                    contentAlignment = Alignment.Center
                ) {
                    if(isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Submit for Analysis", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ArScanHeader() {
    Box(
        modifier = Modifier.fillMaxWidth().clip(BottomArcShapeArScan(arcHeight = 22.dp))
            .background(Brush.linearGradient(colors = listOf(Color(0xFF4C68D7), Color(0xFF8A54C8))))
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 42.dp, bottom = 32.dp)) {
            Text("AR Scan Mode", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Point camera at monuments or upload images to discover their stories", color = Color.White.copy(alpha = 0.9f), fontSize = 15.sp)
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
            Text("How to Use AR Scan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
            modifier = Modifier.size(24.dp).background(Color(0xFFE6F0FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, color = Color(0xFF4C68D7), fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(64.dp).background(iconGradient, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(id = iconResId), contentDescription = title, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun RecentScanCard(scan: StoredScan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = scan.imageUrl, contentDescription = scan.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = scan.name, fontWeight = FontWeight.Bold)
                Text(text = scan.timestamp, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_eye),
                contentDescription = "View Scan",
                modifier = Modifier.size(24.dp).clickable { /* TODO: Re-show the result dialog? */ }
            )
        }
    }
}

class BottomArcShapeArScan(private val arcHeight: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - with(density) { arcHeight.toPx() })
            quadraticBezierTo(
                size.width / 2,
                size.height + with(density) { arcHeight.toPx() },
                0f,
                size.height - with(density) { arcHeight.toPx() }
            )
            close()
        }
        return Outline.Generic(path)
    }
}

private fun uriToMultipartBody(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileBytes = inputStream.readBytes()
        inputStream.close()
        val requestFile = fileBytes.toRequestBody(context.contentResolver.getType(uri)?.toMediaTypeOrNull())
        MultipartBody.Part.createFormData(partName, "upload.jpg", requestFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

