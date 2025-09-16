package com.example.richculture.Data

// --- Data Models for the Bazaar ---

data class Product(
    val _id: String,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val imageUrl: String,
    val category: String,
    val materialUsed: String,
    val origin: String,
    val isTraditional: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int,
    // These fields might not exist in every API response, so they are nullable
    val oldPrice: Double? = null,
    val rating: Double? = null,
    val reviewCount: Int? = null,
    val artistName: String? = null,
    val tags: List<String>? = null
)

data class CartItem(
    val item: Product,
    val quantity: Int,
    val priceAtPurchase: Double,
    val _id: String
)

data class CartResponse(
    val _id: String,
    val user: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)

// --- Request Models ---
data class AddToCartRequest(
    val itemId: String,
    val quantity: Int
)

data class UpdateQuantityRequest(
    val quantity: Int
)

// --- Order Model ---
data class Order(
    val _id: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val orderDate: String,
    val status: String
)

