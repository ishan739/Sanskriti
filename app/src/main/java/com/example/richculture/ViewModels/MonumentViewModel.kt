package com.example.richculture.ViewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.Monument
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MonumentViewModel : ViewModel() {

    // This is the list the UI will actually display.
    private val _displayedMonuments = MutableStateFlow<List<Monument>>(emptyList())
    val displayedMonuments = _displayedMonuments.asStateFlow()

    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    init {
        // ✅ Fetch a default, popular district on startup to show featured monuments.
        fetchByDistrict("Agra", isInitialLoad = true)
    }

    // ✅ MODIFIED: Added a flag to differentiate the initial load from a user search.
    fun fetchByDistrict(district: String, isInitialLoad: Boolean = false) {
        if (district.isBlank()) {
            return
        }

        isLoading.value = true
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.monumentApi.getMonumentsByDistrict(district)
                if (isInitialLoad) {
                    // On initial load, just show a random sample.
                    _displayedMonuments.value = result.shuffled().take(10)
                } else {
                    // On user search, show all results.
                    _displayedMonuments.value = result
                }
                error.value = null
            } catch (e: Exception) {
                error.value = "Could not find monuments in '$district'."
                _displayedMonuments.value = emptyList()
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }
}

