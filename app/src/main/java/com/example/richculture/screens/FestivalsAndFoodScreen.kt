package com.example.richculture.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.richculture.Data.Festival
import com.example.richculture.ViewModel.FestivalViewModel

// --- NEW LIGHT THEME COLORS ---
private val lightBackground = Color(0xFFF7F2FA)
private val magentaAccent = Color(0xFFE91E63)
private val darkText = Color(0xFF1C1B1F)
private val lightSurface = Color.White
private val subtleGrayText = Color.Gray

private val religions = listOf("All", "Hindu", "Islam", "Christian", "Sikh", "Buddhist", "Jain", "Zoroastrian", "Secular", "Tribal", "Hindu-Sindhi", "Hindu-Assamese", "Modern")
private val regions = listOf("All", "Pan-India", "North India", "South India", "Punjab", "Tamil Nadu", "West Bengal", "Maharashtra", "Kerala", "Rajasthan", "Gujarat", "Karnataka", "Odisha", "Andhra Pradesh", "Telangana", "Bihar", "Uttar Pradesh", "Assam", "Meghalaya", "Arunachal Pradesh", "Nagaland", "Manipur", "Mizoram", "Tripura", "Ladakh", "Himachal Pradesh", "Uttarakhand", "Madhya Pradesh", "Chhattisgarh", "Goa")

// --- CUSTOM CURVED SHAPE FOR THE HEADER ---
class BottomArcShapeFood(private val arcHeight: Dp) : Shape {
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

@Composable
fun FestivalsAndFoodScreen(
    navController: NavHostController,
    viewModel: FestivalViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf("Festivals") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = lightBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- CURVED HEADER SECTION ---
            CurvedHeadera(
                title = "Festivals & Food",
                subtitle = "Explore cultural celebrations",
                onBackClicked = { navController.popBackStack() }
            )

            // --- CONTROLS SECTION ---
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(24.dp))
                TabSelector(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- CONTENT AREA (FESTIVALS OR FOOD) ---
            when (selectedTab) {
                "Festivals" -> FestivalsContent(viewModel = viewModel)
                "Food" -> FoodContent()
            }
        }
    }
}

@Composable
fun CurvedHeadera(title: String, subtitle: String, onBackClicked: () -> Unit) {
    val arcHeight = 30.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp) // Adjust height as needed
            .clip(BottomArcShapeFood(arcHeight))
            .background(magentaAccent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = arcHeight + 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun FestivalsContent(viewModel: FestivalViewModel) {
    val festivals by viewModel.festivals
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    var searchQuery by remember { mutableStateOf("") }
    var selectedReligion by remember { mutableStateOf("Hindu") }
    var selectedRegion by remember { mutableStateOf("All") }
    var selectedFestival by remember { mutableStateOf<Festival?>(null) }

    val filteredFestivals = remember(searchQuery, festivals) {
        if (searchQuery.isBlank()) festivals else festivals.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SearchBar(value = searchQuery, onValueChange = { searchQuery = it })
        FilterChipRow("Religions", religions, selectedReligion) {
            selectedReligion = it; if (it != "All") selectedRegion = "All"
        }
        FilterChipRow("Regions", regions, selectedRegion) {
            selectedRegion = it; if (it != "All") selectedReligion = "All"
        }

        Button(
            onClick = {
                when {
                    selectedReligion != "All" -> viewModel.fetchByReligion(selectedReligion)
                    selectedRegion != "All" -> viewModel.fetchByRegion(selectedRegion)
                    else -> viewModel.fetchByRegion("Pan-India")
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(containerColor = magentaAccent)
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search Icon", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Find Festivals", fontWeight = FontWeight.Bold)
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = magentaAccent)
            } else if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            } else if (filteredFestivals.isEmpty()) {
                Text("Select a filter and search.", textAlign = TextAlign.Center, color = subtleGrayText)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
                ) {
                    items(filteredFestivals) { festival ->
                        FestivalCard(festival = festival, onClick = { selectedFestival = festival })
                    }
                }
            }
        }
    }

    AnimatedVisibility(visible = selectedFestival != null, enter = fadeIn(), exit = fadeOut()) {
        selectedFestival?.let { FestivalDetailDialog(festival = it, onDismiss = { selectedFestival = null }) }
    }
}

// --- LIGHT THEME STYLED COMPOSABLES ---

@Composable
private fun TabSelector(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        TabButton("Festivals", selectedTab == "Festivals", { onTabSelected("Festivals") }, Modifier.weight(1f))
        TabButton("Food", selectedTab == "Food", { onTabSelected("Food") }, Modifier.weight(1f))
    }
}

@Composable
private fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(60.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) magentaAccent else lightSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if(isSelected) 4.dp else 2.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text, color = if (isSelected) Color.White else darkText, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Filter displayed results...", color = subtleGrayText) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = subtleGrayText) },
        shape = MaterialTheme.shapes.large,
        colors = TextFieldDefaults.colors(
            focusedTextColor = darkText,
            unfocusedTextColor = darkText,
            focusedContainerColor = lightSurface,
            unfocusedContainerColor = lightSurface,
            unfocusedIndicatorColor = Color.LightGray, // replaces unfocusedBorderColor
            focusedIndicatorColor = magentaAccent      // replaces focusedBorderColor
        )
        ,
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChipRow(title: String, items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = darkText)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { item ->
                FilterChip(
                    selected = item == selectedItem,
                    onClick = { onItemSelected(item) },
                    label = { Text(item) },
                    shape = MaterialTheme.shapes.large,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = lightSurface,
                        labelColor = subtleGrayText,
                        selectedContainerColor = magentaAccent,
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = item == selectedItem,   // <-- pass your chip state here
                        borderColor = Color.LightGray,
                        selectedBorderColor = magentaAccent,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 2.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun FestivalCard(festival: Festival, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = lightSurface)
    ) {
        Box {
            AsyncImage(
                model = festival.image,
                contentDescription = festival.name,
                modifier = Modifier.fillMaxWidth().height(160.dp),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 150f
                        )
                    )
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
            ) {
                Text(festival.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(festival.month, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
            }
        }
    }
}

@Composable
fun FestivalDetailDialog(festival: Festival, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.75f),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = lightSurface),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box {
                        AsyncImage(
                            model = festival.image,
                            contentDescription = festival.name,
                            modifier = Modifier.fillMaxWidth().height(200.dp).clip(MaterialTheme.shapes.extraLarge),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    Column(
                        modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(festival.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = darkText)
                        Text(
                            buildAnnotatedString {
                                append("Celebrated in "); withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(festival.region) }; append(" during "); withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(festival.month) }; append(", this "); withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(festival.religion) }; append(" festival holds deep cultural significance.")
                            },
                            style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp, color = subtleGrayText
                        )
                        Divider(color = Color.LightGray)
                        DetailSection("Mythological Significance", festival.mythologicalSignificance)
                        DetailSection("Special Foods", festival.specialFoods)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = magentaAccent)) {
                            Text("Close", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = magentaAccent)
        Text(content, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp, color = subtleGrayText)
    }
}

@Composable
private fun FoodContent() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text("The Food section is coming soon!", style = MaterialTheme.typography.headlineSmall, color = subtleGrayText)
    }
}

