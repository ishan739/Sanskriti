package com.example.richculture.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.richculture.Data.Monument
import com.example.richculture.R
import com.example.richculture.ViewModel.MonumentViewModel
import com.example.richculture.utility.allDistricts

// ... (glassmorphic modifier and other composables are unchanged)
fun Modifier.glassmorphic(
    backgroundColor: Color = Color.Black.copy(alpha = 0.35f)
) = composed {
    this
        .clip(RoundedCornerShape(16.dp))
        .background(backgroundColor)
        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
}


@Composable
fun HeritageExplorerScreen(
    navController: NavController,
    viewModel: MonumentViewModel = viewModel()
) {
    val monuments by viewModel.displayedMonuments.collectAsState()
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    var selectedMonument by remember { mutableStateOf<Monument?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CurvedHeader(
                title = "Heritage Explorer",
                subtitle = "Discover India's timeless marvels",
                onBackClicked = { navController.popBackStack() }
            )
        },
        containerColor = Color(0xFFF0F2F5)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeritageSearchField(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { district ->
                        viewModel.fetchByDistrict(district)
                    }
                )
            }

            if (isLoading) {
                item { Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            } else if (error != null) {
                item { Text(text = error!!, modifier = Modifier.fillMaxWidth().padding(16.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error) }
            } else {
                item {
                    Text(
                        text = if (searchQuery.isNotBlank()) "Search Results" else "Featured Monuments",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                items(monuments) { monument ->
                    MonumentCard(
                        monument = monument,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = { selectedMonument = monument }
                    )
                }
            }
        }
    }

    AnimatedVisibility(visible = selectedMonument != null, enter = fadeIn(), exit = fadeOut()) {
        selectedMonument?.let { monument ->
            MonumentDetailDialog(
                monument = monument,
                onDismiss = { selectedMonument = null }
            )
        }
    }
}

// ✅ MODIFIED: The search field logic and styling are updated here.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeritageSearchField(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val filteredDistricts = remember(searchQuery) {
        if (searchQuery.isEmpty()) emptyList() else allDistricts.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    ExposedDropdownMenuBox(
        expanded = expanded && filteredDistricts.isNotEmpty(),
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { newValue ->
                // ✅ 1st CHANGE: Only expand the menu when typing, not deleting.
                if (newValue.length > searchQuery.length) {
                    expanded = true
                } else if (newValue.length < searchQuery.length) {
                    expanded = false
                }
                onQueryChange(newValue)
            },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = false
                        onSearch(searchQuery)
                    },
                    modifier = Modifier.background(Brush.horizontalGradient(listOf(Color(0xFF64B5F6), Color(0xFF1976D2))), CircleShape)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                }
            },
            placeholder = { Text("Search monuments by district...") },
            shape = CircleShape,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded && filteredDistricts.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            filteredDistricts.take(5).forEach { district ->
                DropdownMenuItem(
                    // ✅ 2nd CHANGE: Set the text color to Black.
                    text = { Text(district, color = Color.Black) },
                    onClick = {
                        onQueryChange(district)
                        expanded = false
                        onSearch(district)
                    }
                )
            }
        }
    }
}


// --- THE REST OF THE FILE IS UNCHANGED ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MonumentDetailDialog(monument: Monument, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val images = listOf(monument.cover, monument.furl, monument.surl, monument.turl).filter { it.isNotBlank() }
    val pagerState = rememberPagerState(pageCount = { images.size })

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.7f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F5))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        ) { page ->
                            AsyncImage(
                                model = images[page],
                                contentDescription = "Image ${page + 1} of ${monument.name}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopStart).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                        IconButton(
                            onClick = {
                                val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(monument.name)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                context.startActivity(mapIntent)
                            },
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Open in Maps", tint = Color.White)
                        }
                        Row(
                            Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(images.size) { iteration ->
                                val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                                Box(modifier = Modifier.clip(CircleShape).background(color).size(8.dp))
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.padding(20.dp).weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(monument.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(monument.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, lineHeight = 22.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CurvedHeader(title: String, subtitle: String, onBackClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(BottomArcShapeHeritage(20.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF1976D2), Color(0xFF64B5F6))))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClicked, modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun MonumentCard(monument: Monument, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val textStyleWithShadow = TextStyle(shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), offset = Offset(1f, 1f), blurRadius = 2f))
    Card(
        modifier = modifier.fillMaxWidth().height(250.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = monument.cover,
                contentDescription = monument.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(id = R.drawable.ic_arts),
                error = painterResource(id = R.drawable.ic_camera)
            )
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f), Color.Black.copy(alpha = 0.9f)))))
            Box(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(12.dp).glassmorphic()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = monument.name, color = Color.White, style = textStyleWithShadow.copy(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp))
                    MonumentInfoRow(icon = Icons.Default.LocationOn, text = monument.location)
                    Text(
                        text = monument.description,
                        color = Color.White.copy(alpha = 0.9f),
                        style = textStyleWithShadow.copy(fontSize = 12.sp, lineHeight = 16.sp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun MonumentInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
    }
}

class BottomArcShapeHeritage(private val arcHeight: Dp) : Shape {
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

