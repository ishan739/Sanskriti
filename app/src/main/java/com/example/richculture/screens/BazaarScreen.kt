package com.example.richculture.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.richculture.R
import com.example.richculture.navigate.Screen

// --- Data classes ---
data class Product(
    val id: Int,
    val name: String,
    val imageResId: Int,
    val price: Int,
    val oldPrice: Int,
    val rating: Float,
    val reviewCount: Int,
    val seller: String,
    val isBestseller: Boolean = false,
    val isNew: Boolean = false
)

data class BazaarCategory(
    val name: String,
    val count: Int
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BazaarScreen(navController: NavController) {
    // Demo Data
    val categories = listOf(
        BazaarCategory("All", 156),
        BazaarCategory("Handicrafts", 45),
        BazaarCategory("Textiles", 62),
        BazaarCategory("Jewelry", 25),
        BazaarCategory("Paintings", 24)
    )

    val products = listOf(
        Product(1, "Handwoven Pashmina Shawl", R.drawable.ic_arts, 4500, 6000, 4.8f, 124, "Kashmir Craft Co.", isBestseller = true),
        Product(2, "Traditional Kundan Necklace", R.drawable.ic_blue_pottery, 8500, 12000, 4.9f, 89, "Rajasthani Jewels", isNew = true),
        Product(3, "Madhubani Painting", R.drawable.ic_madhubani, 2500, 3000, 4.9f, 75, "Mithila Arts"),
        Product(4, "Rajasthani Block Print Scarf", R.drawable.ic_rajasthani_scarf, 1200, 1500, 4.7f, 150, "Jaipur Textiles"),
    )

    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var searchQuery by remember { mutableStateOf("") }
    val cart = remember { mutableStateListOf<Product>() }
    val wishlist = remember { mutableStateListOf<Product>() }

    val filteredProducts = products.filter {
        (selectedCategory.name == "All" || it.name.contains(selectedCategory.name, ignoreCase = true)) &&
                it.name.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF3E0), Color(0xFFFBE9E7))
                )
            )
    ) {
        Scaffold(
            topBar = { BazaarTopAppBar(navController, cart.size, wishlist.size) },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding()),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp), // ✅ Crucial padding for floating nav bar
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item { SearchAndFilterBar(searchQuery) { searchQuery = it } }
                item { CategoryFilter(categories, selectedCategory, onCategorySelected = { selectedCategory = it }) }
                item { FestivalSaleBanner() }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Products", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("${filteredProducts.size} items", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }
                items(filteredProducts.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { product ->
                            ProductCard(
                                product = product,
                                modifier = Modifier.weight(1f),
                                isInCart = product in cart,
                                isInWishlist = product in wishlist,
                                onAddToCart = {
                                    if (product !in cart) cart.add(product) else cart.remove(product)
                                },
                                onWishlistToggle = {
                                    if (product !in wishlist) wishlist.add(product) else wishlist.remove(product)
                                }
                            )
                        }
                        if (rowItems.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BazaarTopAppBar(navController: NavController, cartCount: Int, wishlistCount: Int) {
    Box {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF388E3C), Color(0xFF66BB6A))
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Sanskriti Bazaar 🛍️", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Authentic handicrafts & textiles", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
            BadgedBox(badge = { if (wishlistCount > 0) Badge { Text("$wishlistCount") } }) {
                IconButton(onClick = { /* Wishlist screen */ }) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist", tint = Color.White)
                }
            }
            BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                IconButton(onClick = { navController.navigate(Screen.Order.route) }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun SearchAndFilterBar(query: String, onQueryChange: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Handicrafts, textiles...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                shape = CircleShape,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
                    focusedContainerColor = Color.White
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = { /* filter logic */ },
                shape = CircleShape,
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_filter), contentDescription = "Filter")
            }
        }
    }
}

@Composable
fun CategoryFilter(categories: List<BazaarCategory>, selectedCategory: BazaarCategory, onCategorySelected: (BazaarCategory) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Button(
                onClick = { onCategorySelected(category) },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF10B981) else Color.White.copy(alpha = 0.7f),
                    contentColor = if (isSelected) Color.White else Color.DarkGray
                ),
                border = if (!isSelected) BorderStroke(1.dp, Color.White) else null,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text("${category.name} (${category.count})")
            }
        }
    }
}

@Composable
fun FestivalSaleBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(110.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(colors = listOf(Color(0xFFF87171), Color(0xFFFB923C))))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Festival Sale 🎉", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Up to 50% OFF", color = Color.White, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { /* Shop now */ },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Shop Now", color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold)
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_arts),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    isInCart: Boolean,
    isInWishlist: Boolean,
    onAddToCart: () -> Unit,
    onWishlistToggle: () -> Unit
) {
    val discountPercent = ((product.oldPrice - product.price).toDouble() / product.oldPrice * 100).toInt()

    Card(
        modifier = modifier.clickable { /* product detail */ },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = product.imageResId),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                when {
                    product.isBestseller -> TagChip("Bestseller", Color(0xFFF9A825), Modifier.align(Alignment.TopStart))
                    product.isNew -> TagChip("New", Color(0xFF10B981), Modifier.align(Alignment.TopStart))
                }
                IconButton(
                    onClick = onWishlistToggle,
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                ) {
                    Icon(
                        if (isInWishlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isInWishlist) Color(0xFFE91E63) else Color.Black,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.7f), CircleShape)
                            .padding(6.dp)
                            .size(20.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${product.rating} (${product.reviewCount})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
                Text(product.seller, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("₹${product.price}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    Text("₹${product.oldPrice}", textDecoration = TextDecoration.LineThrough, color = Color.Gray, fontSize = 12.sp)
                }
                Text("$discountPercent% off", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = if (isInCart) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    else ButtonDefaults.outlinedButtonColors(),
                    border = if (!isInCart) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                ) {
                    Text(if (isInCart) "In Cart" else "Add to Cart", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun TagChip(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .padding(8.dp)
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .background(color, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
    )
}
