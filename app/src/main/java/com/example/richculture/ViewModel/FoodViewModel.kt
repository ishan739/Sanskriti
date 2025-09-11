package com.example.richculture.ViewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.Food
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.launch

class FoodViewModel : ViewModel() {

    private val _foodList = mutableStateOf<List<Food>>(emptyList())
    val foodList: State<List<Food>> = _foodList

    // --- ADDED for UI feedback ---
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        // Fetch all food items when the screen is first loaded
        fetchAllFoods()
    }

    fun fetchAllFoods() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _foodList.value = RetrofitInstance.foodApi.getAllFoods()
            } catch (e: Exception) {
                _error.value = "Failed to load food. Check connection."
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchFoodsByType(type: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _foodList.value = RetrofitInstance.foodApi.getFoodsByType(type)
            } catch (e: Exception) {
                _error.value = "Failed to load food by type."
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchFoodsByRegion(region: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _foodList.value = RetrofitInstance.foodApi.getFoodsByRegion(region)
            } catch (e: Exception) {
                _error.value = "Failed to load food by region."
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
