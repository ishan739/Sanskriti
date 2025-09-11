package com.example.richculture.ViewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.Festival
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.launch

class FestivalViewModel : ViewModel() {

    private val _festivals = mutableStateOf<List<Festival>>(emptyList())
    val festivals: State<List<Festival>> = _festivals

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun fetchByReligion(religion: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Calling the CORRECT function name from the API interface
                _festivals.value = RetrofitInstance.festivalApi.getFestivalByReligion(religion)
            } catch (e: Exception) {
                Log.e("FestivalViewModel", "Error fetching by religion: ${e.message}")
                _error.value = "Failed to load festivals. Please check your connection."
                _festivals.value = emptyList() // Clear previous results on error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchByRegion(region: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Calling the CORRECT function name from the API interface
                _festivals.value = RetrofitInstance.festivalApi.getFestivalByRegion(region)
            } catch (e: Exception) {
                Log.e("FestivalViewModel", "Error fetching by region: ${e.message}")
                _error.value = "Failed to load festivals. Please check your connection."
                _festivals.value = emptyList() // Clear previous results on error
            } finally {
                _isLoading.value = false
            }
        }
    }
}

