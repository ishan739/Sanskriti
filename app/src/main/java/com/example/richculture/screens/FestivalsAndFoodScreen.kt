package com.example.richculture.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.richculture.Data.Food // Assuming Food data class is in this package
import com.example.richculture.ViewModels.FestivalViewModel
import com.example.richculture.ViewModels.FoodViewModel // Assuming FoodViewModel is in this package

// --- THEME COLORS ---
private val lightBackground = Color(0xFFF7F2FA)
private val magentaAccent = Color(0xFFE91E63)
private val darkText = Color(0xFF1C1B1F)
private val lightSurface = Color.White
private val subtleGrayText = Color.Gray

// --- FILTER OPTIONS ---
private val religions = listOf("All", "Hindu", "Islam", "Christian", "Sikh", "Buddhist", "Jain", "Zoroastrian", "Secular", "Tribal", "Hindu-Sindhi", "Hindu-Assamese", "Modern")
private val festivalRegions = listOf("All", "Pan-India", "North India", "South India", "Punjab", "Tamil Nadu", "West Bengal", "Maharashtra", "Kerala", "Rajasthan", "Gujarat", "Karnataka", "Odisha", "Andhra Pradesh", "Telangana", "Bihar", "Uttar Pradesh", "Assam", "Meghalaya", "Arunachal Pradesh", "Nagaland", "Manipur", "Mizoram", "Tripura", "Ladakh", "Himachal Pradesh", "Uttarakhand", "Madhya Pradesh", "Chhattisgarh", "Goa")
private val foodTypes = listOf("All", "Veg", "Non-Veg")
private val foodRegions = listOf("All", "North", "South", "East", "West")

// --- CUSTOM SHAPE FOR HEADER ---
class BottomArcShapeFood(private val arcHeight: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val arcHeightPx = with(density) { arcHeight.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f); lineTo(size.width, 0f); lineTo(size.width, size.height - arcHeightPx)
            quadraticBezierTo(size.width / 2, size.height, 0f, size.height - arcHeightPx); close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun FestivalsAndFoodScreen(
    navController: NavHostController,
    festivalViewModel: FestivalViewModel = viewModel(),
    foodViewModel: FoodViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf("Festivals") }
    var selectedFestival by remember { mutableStateOf<Festival?>(null) }
    var selectedFood by remember { mutableStateOf<Food?>(null) }

    // Initial data fetch (safe to keep)
    LaunchedEffect(Unit) {
        festivalViewModel.fetchByRegion("Pan-India")
        foodViewModel.fetchAllFoods()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lightBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            FestivalsHeader(
                title = "Festivals & Food",
                subtitle = "Explore cultural celebrations"
            )

            // Tabs
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                TabSelector(selectedTab, onTabSelected = { selectedTab = it })
            }

            // Tab Content
            when (selectedTab) {
                "Festivals" -> FestivalsTabContent(
                    viewModel = festivalViewModel,
                    onFestivalClick = { selectedFestival = it }
                )

                "Food" -> FoodTabContent(
                    viewModel = foodViewModel,
                    onFoodClick = { selectedFood = it }
                )
            }
        }

        // Back Button floating on top
        FloatingBackButton { navController.popBackStack() }

        // Festival Details
        AnimatedVisibility(visible = selectedFestival != null) {
            selectedFestival?.let {
                FestivalDetailDialog(it, onDismiss = { selectedFestival = null })
            }
        }

        // Food Details
        AnimatedVisibility(visible = selectedFood != null) {
            selectedFood?.let {
                FoodDetailDialog(it, onDismiss = { selectedFood = null })
            }
        }
    }
}

// --- FESTIVALS SECTION ---
@Composable
private fun FestivalsTabContent(
    viewModel: FestivalViewModel,
    onFestivalClick: (Festival) -> Unit
) {
    val festivals by viewModel.festivals
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    var searchQuery by remember { mutableStateOf("") }

    val filteredFestivals = remember(searchQuery, festivals) {
        festivals.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item { FestivalsControls(viewModel = viewModel) }

        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                SearchBar(value = searchQuery, onValueChange = { searchQuery = it })
            }
        }

        item { LoadingErrorState(isLoading, error, filteredFestivals.isEmpty()) }

        if (filteredFestivals.isNotEmpty() && !isLoading) {
            items(filteredFestivals.chunked(2)) { rowItems ->
                GridRow(rowItems) { festival ->
                    FestivalCard(festival, onClick = { onFestivalClick(festival) })
                }
            }
        }
    }
}

// --- FOOD SECTION ---
@Composable
private fun FoodTabContent(
    viewModel: FoodViewModel,
    onFoodClick: (Food) -> Unit
) {
    val foodList by viewModel.foodList
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    var searchQuery by remember { mutableStateOf("") }

    val filteredFood = remember(searchQuery, foodList) {
        foodList.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item { FoodControls(viewModel = viewModel) }

        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                SearchBar(value = searchQuery, onValueChange = { searchQuery = it })
            }
        }

        item { LoadingErrorState(isLoading, error, filteredFood.isEmpty()) }

        if (filteredFood.isNotEmpty() && !isLoading) {
            items(filteredFood.chunked(2)) { rowItems ->
                GridRow(rowItems) { food ->
                    FoodCard(food, onClick = { onFoodClick(food) })
                }
            }
        }
    }
}




@Composable
private fun FestivalsControls(viewModel: FestivalViewModel) {
    var selectedReligion by remember { mutableStateOf("Hindu") }
    var selectedRegion by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DropdownFilter("Religion", religions, selectedReligion, { selectedReligion = it }, Modifier.weight(1f))
            DropdownFilter("Region", festivalRegions, selectedRegion, { selectedRegion = it }, Modifier.weight(1f))
        }
        FindButton("Find Festivals") {
            when {
                selectedReligion != "All" -> viewModel.fetchByReligion(selectedReligion)
                selectedRegion != "All" -> viewModel.fetchByRegion(selectedRegion)
                else -> viewModel.fetchByRegion("Pan-India")
            }
        }
    }
}
@Composable
private fun FoodControls(viewModel: FoodViewModel) {
    var selectedType by remember { mutableStateOf("All") }
    var selectedRegion by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DropdownFilter("Category", foodTypes, selectedType, { selectedType = it }, Modifier.weight(1f))
            DropdownFilter("Region", foodRegions, selectedRegion, { selectedRegion = it }, Modifier.weight(1f))
        }
        FindButton("Find Food") {
            when {
                selectedType != "All" -> viewModel.fetchFoodsByType(selectedType)
                selectedRegion != "All" -> viewModel.fetchFoodsByRegion(selectedRegion)
                else -> viewModel.fetchAllFoods()
            }
        }
    }
}

// --- REUSABLE UI COMPONENTS ---
@Composable
fun FestivalsHeader(title: String, subtitle: String) {
    val arcHeight = 30.dp
    Box(modifier = Modifier.fillMaxWidth().clip(BottomArcShapeFood(arcHeight)).background(magentaAccent)) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 72.dp, bottom = arcHeight + 10.dp)) {
            Text(title, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 15.sp)
        }
    }
}

@Composable
fun FloatingBackButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.padding(16.dp).background(Color.Black.copy(alpha = 0.2f), CircleShape)
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
    }
}

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
        colors = CardDefaults.cardColors(containerColor = if (isSelected) magentaAccent else lightSurface),
        elevation = CardDefaults.cardElevation(if(isSelected) 4.dp else 2.dp),
        onClick = onClick
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text, color = if (isSelected) Color.White else darkText, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Filter displayed results...", color = subtleGrayText) },
        leadingIcon = { Icon(Icons.Default.Search, "Search", tint = subtleGrayText) },
        shape = MaterialTheme.shapes.large,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = lightSurface, unfocusedContainerColor = lightSurface,
            unfocusedIndicatorColor = Color.LightGray, focusedIndicatorColor = magentaAccent
        ), singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownFilter(label: String, options: List<String>, selected: String, onSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected, onValueChange = {}, readOnly = true,
            label = { Text(label , color = Color.Black) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = lightSurface, unfocusedContainerColor = lightSurface,
                unfocusedIndicatorColor = Color.LightGray, focusedIndicatorColor = magentaAccent
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false } , containerColor = Color.Gray) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option , color = Color.Black) }, onClick = {
                    onSelected(option); expanded = false
                })
            }
        }
    }
}

@Composable
private fun FindButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = MaterialTheme.shapes.large, colors = ButtonDefaults.buttonColors(containerColor = magentaAccent)
    ) {
        Icon(Icons.Default.Search, "Search", modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun <T> GridRow(rowItems: List<T>, itemContent: @Composable (T) -> Unit) {
    Row(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        rowItems.forEach { item -> Box(Modifier.weight(1f)) { itemContent(item) } }
        if (rowItems.size == 1) Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun LoadingErrorState(isLoading: Boolean, error: String?, isEmpty: Boolean) {
    Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
        if (isLoading) CircularProgressIndicator(color = magentaAccent)
        else if (error != null) Text(error, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        else if (isEmpty) Text("Select a filter and search.", textAlign = TextAlign.Center, color = subtleGrayText)
    }
}

@Composable
private fun FestivalCard(festival: Festival, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(lightSurface)
    ) {
        Box {
            AsyncImage(festival.image, festival.name, Modifier.fillMaxWidth().height(160.dp), contentScale = ContentScale.Crop)
            Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.8f)), startY = 150f)))
            Column(Modifier.align(Alignment.BottomStart).padding(12.dp)) {
                Text(festival.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(festival.month, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.9f))
            }
        }
    }
}

@Composable
private fun FoodCard(food: Food, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(lightSurface)
    ) {
        Box {
            AsyncImage(food.imageurl, food.name, Modifier.fillMaxWidth().height(160.dp), contentScale = ContentScale.Crop)
            Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.8f)), startY = 150f)))
            Column(Modifier.align(Alignment.BottomStart).padding(12.dp)) {
                Text(food.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(food.region, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.9f))
            }
        }
    }
}

@Composable
fun FestivalDetailDialog(festival: Festival, onDismiss: () -> Unit) {
    Dialog(onDismiss, DialogProperties(false)) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(0.5f)), Alignment.Center) {
            Card(Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.75f), shape = MaterialTheme.shapes.extraLarge, colors = CardDefaults.cardColors(lightSurface)) {
                Column(Modifier.fillMaxSize()) {
                    Box {
                        AsyncImage(festival.image, festival.name, Modifier.fillMaxWidth().height(200.dp).clip(MaterialTheme.shapes.extraLarge), contentScale = ContentScale.Crop)
                        IconButton({ onDismiss() }, Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(0.5f), CircleShape)) {
                            Icon(Icons.Default.Close, "Close", tint = Color.White)
                        }
                    }
                    Column(Modifier.padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(festival.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = darkText)
                        Text(
                            buildAnnotatedString {
                                append("Celebrated in "); withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(festival.region) }; append(" during "); withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(festival.month) }; append(", this "); withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(festival.religion) }; append(" festival holds deep cultural significance.")
                            }, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp, color = subtleGrayText
                        )
                        Divider(color = Color.LightGray)
                        DetailSection("Mythological Significance", festival.mythologicalSignificance)
                        DetailSection("Special Foods", festival.specialFoods)
                        Spacer(Modifier.height(8.dp))
                        Button({ onDismiss() }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(magentaAccent)) {
                            Text("Close", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodDetailDialog(food: Food, onDismiss: () -> Unit) {
    Dialog(onDismiss, DialogProperties(false)) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(0.5f)), Alignment.Center) {
            Card(Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.75f), shape = MaterialTheme.shapes.extraLarge, colors = CardDefaults.cardColors(lightSurface)) {
                Column(Modifier.fillMaxSize()) {
                    Box {
                        AsyncImage(food.imageurl, food.name, Modifier.fillMaxWidth().height(200.dp).clip(MaterialTheme.shapes.extraLarge), contentScale = ContentScale.Crop)
                        IconButton({ onDismiss() }, Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(0.5f), CircleShape)) {
                            Icon(Icons.Default.Close, "Close", tint = Color.White)
                        }
                    }
                    Column(Modifier.padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(food.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = darkText)
                        Text(
                            buildAnnotatedString {
                                append("A popular "); withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(food.type) }; append(" dish from "); withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(food.region) }; append(" India, known for its rich flavors.")
                            }, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp, color = subtleGrayText
                        )
                        Divider(color = Color.LightGray)
                        DetailSection("Main Ingredients", food.mainIngredients)
                        DetailSection("Description", food.description)
                        Spacer(Modifier.height(8.dp))
                        Button({ onDismiss() }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(magentaAccent)) {
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

