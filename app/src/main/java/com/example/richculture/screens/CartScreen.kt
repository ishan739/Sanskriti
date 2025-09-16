package com.example.richculture.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.richculture.Data.CartItem
import com.example.richculture.ViewModels.BazaarViewModel
import com.example.richculture.ViewModels.UiState
import com.example.richculture.navigate.Screen
import org.koin.androidx.compose.koinViewModel

import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.outlined.AddCircle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: BazaarViewModel = koinViewModel()
) {
    val cartState by viewModel.cartUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            when (val state = cartState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is UiState.Error -> Text(state.message, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                is UiState.Success -> {
                    val cart = state.data
                    if (cart == null || cart.items.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,

                            ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Your cart is empty", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Looks like you haven't added anything yet.", style = MaterialTheme.typography.bodyMedium)

                        }
                    } else {
                        Column(Modifier.fillMaxSize()) {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(cart.items) { cartItem ->
                                    CartItemRow(
                                        item = cartItem,
                                        onQuantityChange = { newQuantity ->
                                            viewModel.updateCartItemQuantity(cartItem.item._id, newQuantity)
                                        },
                                        onRemove = { viewModel.removeCartItem(cartItem.item._id) }
                                    )
                                }
                            }
                            // --- Total Amount & Checkout Section ---
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shadowElevation = 8.dp
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Total Amount:", style = MaterialTheme.typography.titleLarge)
                                        Text(
                                            "₹${"%.2f".format(cart.totalAmount)}",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { /* TODO: Handle checkout navigation */ },
                                        modifier = Modifier.fillMaxWidth().height(50.dp)
                                    ) {
                                        Text("PROCEED TO CHECKOUT", style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.item.imageUrl,
                contentDescription = item.item.name,
                modifier = Modifier
                    .size(90.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(item.item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Price: ₹${"%.2f".format(item.priceAtPurchase)}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onQuantityChange(item.quantity - 1) },
                        enabled = item.quantity > 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Outlined.AddCircle, contentDescription = "Decrease quantity")
                    }
                    Text(
                        "${item.quantity}",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(40.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { onQuantityChange(item.quantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Outlined.AddCircle, contentDescription = "Increase quantity")
                    }
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove item", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
