package com.example.richculture.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.*
import com.example.richculture.retro.RetrofitInstance
import com.example.richculture.utility.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import kotlin.math.max

sealed interface UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
    object Loading : UiState<Nothing>
}

class BazaarViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _productsUiState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val productsUiState: StateFlow<UiState<List<Product>>> = _productsUiState.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _cartUiState = MutableStateFlow<UiState<CartResponse?>>(UiState.Loading)
    val cartUiState: StateFlow<UiState<CartResponse?>> = _cartUiState.asStateFlow()

    private val _addToCartSuccessMessage = MutableStateFlow<String?>(null)
    val addToCartSuccessMessage: StateFlow<String?> = _addToCartSuccessMessage.asStateFlow()

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()


    init {
        fetchProducts(null)
        getCart()
    }

    fun fetchProducts(category: String?) {
        viewModelScope.launch {
            _productsUiState.value = UiState.Loading
            try {
                val response = if (category.isNullOrBlank()) {
                    RetrofitInstance.bazaarApi.getAllItems()
                } else {
                    RetrofitInstance.bazaarApi.getItemsByCategory(category)
                }

                if (response.isSuccessful) {
                    _productsUiState.value = UiState.Success(response.body() ?: emptyList())
                } else {
                    _productsUiState.value = UiState.Error("Error: ${response.message()}")
                }
            } catch (e: IOException) {
                _productsUiState.value = UiState.Error("Network error. Please check your connection.")
            } catch (e: HttpException) {
                _productsUiState.value = UiState.Error("An unexpected error occurred.")
            }
        }
    }

    fun getProductById(productId: String) {
        viewModelScope.launch {
            val product = (_productsUiState.value as? UiState.Success)?.data?.find { it._id == productId }
            _selectedProduct.value = product
        }
    }

    fun getCart() {
        sessionManager.getCurrentUserToken()?.let { token ->
            viewModelScope.launch {
                _cartUiState.value = UiState.Loading
                try {
                    val response = RetrofitInstance.bazaarApi.getCart("Bearer $token")
                    if (response.isSuccessful) {
                        val cart = response.body()
                        _cartUiState.value = UiState.Success(cart)
                        _cartItemCount.value = cart?.items?.sumOf { it.quantity } ?: 0
                    } else {
                        _cartUiState.value = UiState.Success(null)
                        _cartItemCount.value = 0
                    }
                } catch (e: Exception) {
                    _cartUiState.value = UiState.Error("Could not load cart.")
                    _cartItemCount.value = 0
                }
            }
        } ?: run {
            _cartUiState.value = UiState.Success(null)
            _cartItemCount.value = 0
        }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        sessionManager.getCurrentUserToken()?.let { token ->
            _cartItemCount.update { it + quantity }
            _addToCartSuccessMessage.value = "${product.name} added to cart!"

            viewModelScope.launch {
                try {
                    val request = AddToCartRequest(product._id, quantity)
                    val response = RetrofitInstance.bazaarApi.addToCart("Bearer $token", request)
                    if (response.isSuccessful) {
                        val cart = response.body()
                        _cartUiState.value = UiState.Success(cart)
                        _cartItemCount.value = cart?.items?.sumOf { it.quantity } ?: 0
                    } else {
                        _cartItemCount.update { max(0, it - quantity) }
                    }
                } catch (e: Exception) {
                    _cartItemCount.update { max(0, it - quantity) }
                }
            }
        }
    }

    fun removeCartItem(itemId: String) {
        val currentState = _cartUiState.value
        if (currentState is UiState.Success && currentState.data != null) {
            val originalCart = currentState.data

            // Optimistic UI update
            val updatedItems = originalCart.items.filterNot { it.item._id == itemId }
            val updatedCart = originalCart.copy(
                items = updatedItems,
                totalAmount = updatedItems.sumOf { it.priceAtPurchase * it.quantity }
            )
            _cartUiState.value = UiState.Success(updatedCart)
            _cartItemCount.value = updatedCart.items.sumOf { it.quantity }

            sessionManager.getCurrentUserToken()?.let { token ->
                viewModelScope.launch {
                    try {
                        val response = RetrofitInstance.bazaarApi.removeCartItem("Bearer $token", itemId)
                        if (response.isSuccessful) {
                            // Sync with the server's response for consistency
                            val finalCart = response.body()
                            _cartUiState.value = UiState.Success(finalCart)
                            _cartItemCount.value = finalCart?.items?.sumOf { it.quantity } ?: 0
                        } else {
                            // If it fails, roll back to the original state
                            _cartUiState.value = UiState.Success(originalCart)
                            _cartItemCount.value = originalCart.items.sumOf { it.quantity }
                        }
                    } catch (e: Exception) {
                        _cartUiState.value = UiState.Success(originalCart)
                        _cartItemCount.value = originalCart.items.sumOf { it.quantity }
                    }
                }
            }
        }
    }

    fun updateCartItemQuantity(itemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeCartItem(itemId)
            return
        }

        val currentState = _cartUiState.value
        if (currentState is UiState.Success && currentState.data != null) {
            val originalCart = currentState.data

            // Optimistic UI update
            val updatedItems = originalCart.items.map {
                if (it.item._id == itemId) it.copy(quantity = newQuantity) else it
            }
            val updatedCart = originalCart.copy(
                items = updatedItems,
                totalAmount = updatedItems.sumOf { it.priceAtPurchase * it.quantity }
            )
            _cartUiState.value = UiState.Success(updatedCart)
            _cartItemCount.value = updatedCart.items.sumOf { it.quantity }

            sessionManager.getCurrentUserToken()?.let { token ->
                viewModelScope.launch {
                    try {
                        val request = UpdateQuantityRequest(newQuantity)
                        val response = RetrofitInstance.bazaarApi.updateCartItemQuantity("Bearer $token", itemId, request)
                        if (response.isSuccessful) {
                            // Sync with the server's response for consistency
                            val finalCart = response.body()
                            _cartUiState.value = UiState.Success(finalCart)
                            _cartItemCount.value = finalCart?.items?.sumOf { it.quantity } ?: 0
                        } else {
                            _cartUiState.value = UiState.Success(originalCart)
                            _cartItemCount.value = originalCart.items.sumOf { it.quantity }
                        }
                    } catch (e: Exception) {
                        _cartUiState.value = UiState.Success(originalCart)
                        _cartItemCount.value = originalCart.items.sumOf { it.quantity }
                    }
                }
            }
        }
    }

    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }

    fun clearAddToCartSuccessMessage() {
        _addToCartSuccessMessage.value = null
    }
}

