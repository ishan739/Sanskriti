package com.example.richculture.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.richculture.ViewModels.CommunityViewModel
import com.example.richculture.utility.TokenManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavController,
    communityViewModel: CommunityViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token = tokenManager.getToken()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var caption by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val uploadResult by communityViewModel.uploadResponse.observeAsState()
    val error by communityViewModel.error.observeAsState()

    // --- LAUNCHER TO PICK AN IMAGE FROM THE GALLERY ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
        }
    )

    // --- HANDLE SUCCESS AND ERROR STATES ---
    LaunchedEffect(uploadResult) {
        uploadResult?.let {
            Toast.makeText(context, "Post created successfully!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Your Memory") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // --- IMAGE SELECTION BOX ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Text("Tap to select an image", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TEXT FIELDS ---
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Write a caption...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Add a location (e.g., Taj Mahal, Agra)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // --- POST BUTTON ---
            Button(
                onClick = {
                    if (imageUri != null && caption.isNotBlank() && location.isNotBlank() && token != null) {
                        isLoading = true
                        val imagePart = uriToMultipartBody(context, imageUri!!, "image")
                        val captionPart = caption.toRequestBody("text/plain".toMediaTypeOrNull())
                        val locationPart = location.toRequestBody("text/plain".toMediaTypeOrNull())

                        if (imagePart != null) {
                            communityViewModel.uploadPost(imagePart, captionPart, locationPart, token)
                        } else {
                            Toast.makeText(context, "Could not process image.", Toast.LENGTH_SHORT).show()
                            isLoading = false
                        }
                    } else {
                        Toast.makeText(context, "Please fill all fields and select an image.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFFAB47BC), Color(0xFF7E57C2))),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Post Memory", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// --- HELPER FUNCTION TO CONVERT URI TO MULTIPARTBODY.PART ---
private fun uriToMultipartBody(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileBytes = inputStream.readBytes()
        inputStream.close()

        val requestFile = fileBytes.toRequestBody(
            context.contentResolver.getType(uri)?.toMediaTypeOrNull()
        )

        // Use a generic filename or try to get it from the URI
        val fileName = "upload.jpg"
        MultipartBody.Part.createFormData(partName, fileName, requestFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

