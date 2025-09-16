package com.example.richculture.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.richculture.Data.CartItem
import com.example.richculture.Data.Product
import com.example.richculture.ViewModels.BazaarViewModel
import com.example.richculture.ViewModels.UiState
import com.example.richculture.navigate.Screen
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BazaarScreen(
    navController: NavController,
    viewModel: BazaarViewModel = koinViewModel()
) {
    val productsState by viewModel.productsUiState.collectAsState()
    val cartItemCount by viewModel.cartItemCount.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>("All") }

    // Animation state for the cart icon
    var animateCart by remember { mutableStateOf(false) }
    LaunchedEffect(cartItemCount) {
        if (cartItemCount > 0) {
            animateCart = true
            delay(300) // Duration of the "bump" animation
            animateCart = false
        }
    }
    val cartScale by animateFloatAsState(
        targetValue = if (animateCart) 1.3f else 1.0f,
        animationSpec = tween(durationMillis = 150)
    )

    // Show product detail dialog when a product is selected
    if (selectedProduct != null) {
        ProductDetailDialog(
            product = selectedProduct!!,
            onDismiss = { selectedProduct = null },
            onAddToCart = {
                viewModel.addToCart(it)
                selectedProduct = null // Close dialog on add
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rich Culture Bazaar") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge { Text("$cartItemCount") }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                modifier = Modifier.scale(cartScale)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            CategoryFilter(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    viewModel.fetchProducts(if (category == "All") null else category)
                }
            )
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = productsState) {
                    is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is UiState.Error -> Text(state.message, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                    is UiState.Success -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(state.data) { product ->
                                ProductCard(
                                    product = product,
                                    onProductClick = { selectedProduct = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilter(
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("All", "Handicraft", "Painting", "Textile", "Jewelry", "Sculpture", "Other")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                leadingIcon = if (selectedCategory == category) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                } else {
                    null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: Product,
    onProductClick: (Product) -> Unit
) {
    Card(
        onClick = { onProductClick(product) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(product.imageUrl).crossfade(true).build(),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                // You can add more details here like rating, etc.
                product.artistName?.let {
                    Text(
                        text = "By $it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "₹${"%.2f".format(product.price)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun ProductDetailDialog(
    product: Product,
    onDismiss: () -> Unit,
    onAddToCart: (Product) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
                Column(Modifier.padding(16.dp)) {
                    Text(product.name, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text(product.description, style = MaterialTheme.typography.bodyMedium)
                    // Add more details from the product
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
