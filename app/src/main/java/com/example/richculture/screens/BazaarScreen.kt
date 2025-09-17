package com.example.richculture.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.richculture.Data.Product
import com.example.richculture.ViewModels.BazaarViewModel
import com.example.richculture.ViewModels.UiState
import com.example.richculture.navigate.Screen
// Assuming 'Screen' is a sealed class/enum in your navigation setup
// import com.example.richculture.navigate.Screen
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

// Add this enum to match your navigation setup.


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BazaarScreen(
    navController: NavController,
    viewModel: BazaarViewModel = koinViewModel()
) {
    val productsState by viewModel.productsUiState.collectAsState()
    val cartItemCount by viewModel.cartItemCount.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedCategory by remember { mutableStateOf("All") }

    // This state is used for the dialog
    if (selectedProduct != null) {
        ProductDetailDialog(
            product = selectedProduct!!,
            onDismiss = { selectedProduct = null },
            onAddToCart = {
                viewModel.addToCart(it)
                selectedProduct = null // Close dialog after adding
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFFFFBF3) // Soft, warm background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Custom Header
            item {
                BazaarHeader(
                    cartItemCount = cartItemCount,
                    onCartClick = { navController.navigate(Screen.Cart.route) }
                )
            }

            // Category Filter
            item {
                CategoryFilterRow(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category ->
                        selectedCategory = category
                        viewModel.fetchProducts(if (category == "All") null else category)
                    }
                )
            }

            // Products Section
            when (val state = productsState) {
                is UiState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFFF57C00))
                        }
                    }
                }
                is UiState.Error -> {
                    item {
                        Text(
                            text = state.message,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is UiState.Success -> {
                    items(state.data, key = { it._id }) { product ->
                        ProductCard(
                            product = product,
                            onProductClick = { selectedProduct = it },
                            onAddToCart = { viewModel.addToCart(it) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BazaarHeader(cartItemCount: Int, onCartClick: () -> Unit) {
    var animateCart by remember { mutableStateOf(false) }
    LaunchedEffect(cartItemCount) {
        if (cartItemCount > 0) {
            animateCart = true
            delay(300)
            animateCart = false
        }
    }
    val cartScale by animateFloatAsState(
        targetValue = if (animateCart) 1.2f else 1.0f,
        animationSpec = tween(150), label = ""
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Explore Our Heritage",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onCartClick) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(containerColor = Color(0xFFD32F2F)) { Text("$cartItemCount") }
                        }
                    }
                ) {
                    Icon(
                        Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart",
                        modifier = Modifier.scale(cartScale),
                        tint = Color(0xFFF57C00)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryFilterRow(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = mapOf<String, ImageVector>(
        "All" to Icons.Outlined.AddCircle,
        "Handicraft" to Icons.Outlined.AddCircle,
        "Painting" to Icons.Outlined.AddCircle,
        "Textile" to Icons.Outlined.AddCircle,
        "Jewelry" to Icons.Outlined.AddCircle,
        "Sculpture" to Icons.Outlined.AddCircle
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories.toList()) { (category, icon) ->
            CategoryChip(
                text = category,
                icon = icon,
                isSelected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFF57C00) else Color.White,
        animationSpec = tween(300), label = ""
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.DarkGray,
        animationSpec = tween(300), label = ""
    )

    Card(
        modifier = Modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = text, tint = contentColor, modifier = Modifier.size(18.dp))
            Text(text, color = contentColor, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = { onProductClick(product) }
    ) {
        Column {
            Box(modifier = Modifier.height(200.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Discount Badge
                product.oldPrice?.let { oldPrice ->
                    if (oldPrice > product.price) {
                        val discount = ((oldPrice - product.price) / oldPrice * 100).roundToInt()
                        Card(
                            modifier = Modifier.padding(8.dp).align(Alignment.TopStart),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F))
                        ) {
                            Text(
                                "$discount% OFF",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                // Wishlist Icon
                IconButton(
                    onClick = { /* TODO: Wishlist logic */ },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        Icons.Outlined.FavoriteBorder,
                        contentDescription = "Add to Wishlist",
                        tint = Color.White,
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape).padding(6.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                        Text(
                            text = "${product.rating ?: 4.5} (${product.reviewCount ?: 0})",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = "Origin", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text(product.origin, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                product.artistName?.let {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier.size(24.dp).background(Color(0xFFF57C00), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(it.first().uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Text("by $it", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(
                                color = Color.Black,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold)
                            ) {
                                append("₹${"%.2f".format(product.price)}")
                            }
                            product.oldPrice?.let {
                                append(" ")
                                withStyle(style = SpanStyle(
                                    color = Color.Gray,
                                    textDecoration = TextDecoration.LineThrough)
                                ) {
                                    append("₹${"%.2f".format(it)}")
                                }
                            }
                        }
                    )

                    Button(
                        onClick = { onAddToCart(product) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFBF3)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, Color(0xFFF57C00))
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFFF57C00))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add", color = Color(0xFFF57C00), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// Keeping the dialog from your original file as it's part of the functionality
@Composable
fun ProductDetailDialog(
    product: Product,
    onDismiss: () -> Unit,
    onAddToCart: (Product) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = MaterialTheme.shapes.large) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                )
                Column(Modifier.padding(16.dp)) {
                    Text(product.name, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text(product.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Material: ${product.materialUsed}", style = MaterialTheme.typography.bodySmall)
                    Text("Origin: ${product.origin}", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹${"%.2f".format(product.price)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(onClick = { onAddToCart(product) }) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Add to Cart")
                        }
                    }
                }
            }
        }
    }
}

