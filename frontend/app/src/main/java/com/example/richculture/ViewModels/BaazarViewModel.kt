package com.example.richculture.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.*
import com.example.richculture.retro.BazaarApi
import com.example.richculture.retro.RetrofitInstance.bazaarApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
data class BazaarUiState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val cartItemCount: Int = 0
)

data class UiMessage(
    val id: Long,
    val message: String,
    val isError: Boolean = false
)

class BazaarViewModel() : ViewModel() {

    // Auth token - In real app, get this from AuthManager/SharedPreferences
    private val authToken = "Bearer your_auth_token_here"

    // UI State flows
    private val _bazaarUiState = MutableStateFlow(BazaarUiState())
    val bazaarUiState = _bazaarUiState.asStateFlow()

    private val _cartUiState = MutableStateFlow(CartUiState())
    val cartUiState = _cartUiState.asStateFlow()

    // Messages for snackbars/toasts
    private val _messages = MutableStateFlow<List<UiMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    // Track pending operations for optimistic updates
    private val pendingCartOperations = mutableMapOf<String, Int>()

    init {
        loadProducts()
        loadCart()
    }

    // --- Product Operations ---

    fun loadProducts() {
        viewModelScope.launch {
            _bazaarUiState.update { it.copy(isLoading = true, error = null) }

            try {
                val response = bazaarApi.getAllItems()
                if (response.isSuccessful) {
                    val products = response.body() ?: emptyList()
                    val categories = listOf("All") + products.map { it.category }.distinct().sorted()

                    _bazaarUiState.update { currentState ->
                        currentState.copy(
                            products = products,
                            filteredProducts = products,
                            categories = categories,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _bazaarUiState.update {
                        it.copy(isLoading = false, error = "Failed to load products: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _bazaarUiState.update {
                    it.copy(isLoading = false, error = "Network error: ${e.message}")
                }
            }
        }
    }

    fun selectCategory(category: String) {
        _bazaarUiState.update { currentState ->
            val filtered = if (category == "All") {
                currentState.products
            } else {
                currentState.products.filter { it.category == category }
            }

            currentState.copy(
                selectedCategory = category,
                filteredProducts = filtered.filter { product ->
                    currentState.searchQuery.isEmpty() ||
                            product.name.contains(currentState.searchQuery, ignoreCase = true) ||
                            product.description.contains(currentState.searchQuery, ignoreCase = true)
                }
            )
        }
    }

    fun searchProducts(query: String) {
        _bazaarUiState.update { currentState ->
            val baseProducts = if (currentState.selectedCategory == "All") {
                currentState.products
            } else {
                currentState.products.filter { it.category == currentState.selectedCategory }
            }

            val filtered = if (query.isEmpty()) {
                baseProducts
            } else {
                baseProducts.filter { product ->
                    product.name.contains(query, ignoreCase = true) ||
                            product.description.contains(query, ignoreCase = true) ||
                            product.category.contains(query, ignoreCase = true) ||
                            product.materialUsed.contains(query, ignoreCase = true) ||
                            product.origin.contains(query, ignoreCase = true) ||
                            product.artistName?.contains(query, ignoreCase = true) == true
                }
            }

            currentState.copy(
                searchQuery = query,
                filteredProducts = filtered
            )
        }
    }

    // --- Cart Operations ---

    fun loadCart() {
        viewModelScope.launch {
            _cartUiState.update { it.copy(isLoading = true, error = null) }

            try {
                val response = bazaarApi.getCart(authToken)
                if (response.isSuccessful) {
                    val cartResponse = response.body()
                    if (cartResponse != null) {
                        _cartUiState.update {
                            it.copy(
                                cartItems = cartResponse.items,
                                totalAmount = cartResponse.totalAmount,
                                cartItemCount = cartResponse.items.sumOf { item -> item.quantity },
                                isLoading = false,
                                error = null
                            )
                        }
                    } else {
                        _cartUiState.update { it.copy(isLoading = false, cartItems = emptyList()) }
                    }
                } else {
                    _cartUiState.update {
                        it.copy(isLoading = false, error = "Failed to load cart")
                    }
                }
            } catch (e: Exception) {
                _cartUiState.update {
                    it.copy(isLoading = false, error = "Network error: ${e.message}")
                }
            }
        }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        val optimisticCartItem = CartItem(
            item = product,
            quantity = quantity,
            priceAtPurchase = product.price,
            _id = "temp_${product._id}_${System.currentTimeMillis()}"
        )

        val currentPending = pendingCartOperations[product._id] ?: 0
        pendingCartOperations[product._id] = currentPending + quantity

        _cartUiState.update { currentState ->
            val existingIndex = currentState.cartItems.indexOfFirst { it.item._id == product._id }
            val updatedItems = if (existingIndex != -1) {
                currentState.cartItems.toMutableList().apply {
                    this[existingIndex] = this[existingIndex].copy(quantity = this[existingIndex].quantity + quantity)
                }
            } else {
                currentState.cartItems + optimisticCartItem
            }

            currentState.copy(
                cartItems = updatedItems,
                totalAmount = updatedItems.sumOf { it.priceAtPurchase * it.quantity },
                cartItemCount = updatedItems.sumOf { it.quantity }
            )
        }

        showMessage("Adding ${product.name} to cart...")

        viewModelScope.launch {
            try {
                val request = AddToCartRequest(itemId = product._id, quantity = quantity)
                val response = bazaarApi.addToCart(authToken, request)

                if (response.isSuccessful) {
                    // Always reload cart after success to sync UI fully with backend state
                    loadCart()
                    showMessage("${product.name} added to cart!")
                } else {
                    loadCart()
                    showMessage("Failed to add ${product.name} to cart", isError = true)
                }
            } catch (e: Exception) {
                loadCart()
                showMessage("Network error: ${e.message}", isError = true)
            } finally {
                pendingCartOperations.remove(product._id)
            }
        }
    }



    fun updateCartItemQuantity(itemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeCartItem(itemId)
            return
        }

        // Optimistic update
        _cartUiState.update { currentState ->
            val updatedItems = currentState.cartItems.map { cartItem ->
                if (cartItem.item._id == itemId) {
                    cartItem.copy(quantity = newQuantity)
                } else {
                    cartItem
                }
            }

            currentState.copy(
                cartItems = updatedItems,
                totalAmount = updatedItems.sumOf { it.priceAtPurchase * it.quantity },
                cartItemCount = updatedItems.sumOf { it.quantity }
            )
        }

        // API call with debouncing to avoid too many requests
        viewModelScope.launch {
            delay(500) // Debounce for 500ms
            try {
                val request = UpdateQuantityRequest(quantity = newQuantity)
                val response = bazaarApi.updateCartItemQuantity(authToken, itemId, request)

                if (response.isSuccessful) {
                    val cartResponse = response.body()
                    if (cartResponse != null) {
                        _cartUiState.update {
                            it.copy(
                                cartItems = cartResponse.items,
                                totalAmount = cartResponse.totalAmount,
                                cartItemCount = cartResponse.items.sumOf { item -> item.quantity }
                            )
                        }
                    }
                } else {
                    // Revert on failure
                    loadCart()
                    showMessage("Failed to update quantity", isError = true)
                }
            } catch (e: Exception) {
                loadCart()
                showMessage("Network error: ${e.message}", isError = true)
            }
        }
    }

    fun removeCartItem(itemId: String) {
        val removingItem = _cartUiState.value.cartItems.find { it.item._id == itemId }

        // Optimistic update
        _cartUiState.update { currentState ->
            val updatedItems = currentState.cartItems.filter { it.item._id != itemId }
            currentState.copy(
                cartItems = updatedItems,
                totalAmount = updatedItems.sumOf { it.priceAtPurchase * it.quantity },
                cartItemCount = updatedItems.sumOf { it.quantity }
            )
        }

        if (removingItem != null) {
            showMessage("Removing ${removingItem.item.name} from cart...")
        }

        viewModelScope.launch {
            try {
                val response = bazaarApi.removeCartItem(authToken, itemId)

                if (response.isSuccessful) {
                    val cartResponse = response.body()
                    if (cartResponse != null) {
                        _cartUiState.update {
                            it.copy(
                                cartItems = cartResponse.items,
                                totalAmount = cartResponse.totalAmount,
                                cartItemCount = cartResponse.items.sumOf { item -> item.quantity }
                            )
                        }
                        showMessage("${removingItem?.item?.name ?: "Item"} removed from cart")
                    }
                } else {
                    // Revert on failure
                    loadCart()
                    showMessage("Failed to remove item", isError = true)
                }
            } catch (e: Exception) {
                loadCart()
                showMessage("Network error: ${e.message}", isError = true)
            }
        }
    }

    fun getCartItemQuantity(productId: String): Int {
        val cartQuantity = _cartUiState.value.cartItems
            .find { it.item._id == productId }?.quantity ?: 0
        val pendingQuantity = pendingCartOperations[productId] ?: 0
        return cartQuantity + pendingQuantity
    }

    fun isProductInCart(productId: String): Boolean {
        return _cartUiState.value.cartItems.any { it.item._id == productId } ||
                pendingCartOperations.containsKey(productId)
    }

    // --- Message Management ---

    private fun showMessage(message: String, isError: Boolean = false) {
        val uiMessage = UiMessage(
            id = System.currentTimeMillis(),
            message = message,
            isError = isError
        )
        _messages.update { it + uiMessage }
    }

    fun messageShown(messageId: Long) {
        _messages.update { messages ->
            messages.filterNot { it.id == messageId }
        }
    }

    // --- Utility Functions ---

    fun refreshData() {
        loadProducts()
        loadCart()
    }

    fun clearError() {
        _bazaarUiState.update { it.copy(error = null) }
        _cartUiState.update { it.copy(error = null) }
    }
}