package com.example.richculture.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.Place
import com.example.richculture.Data.TravelPlannerRequest
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class TravelPlannerUiState {
    object Loading : TravelPlannerUiState()
    data class Success(val places: List<Place>) : TravelPlannerUiState()
    data class Error(val message: String) : TravelPlannerUiState()
    object Empty : TravelPlannerUiState()
}

class TravelPlannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<TravelPlannerUiState>(TravelPlannerUiState.Empty)
    val uiState: StateFlow<TravelPlannerUiState> = _uiState

    fun fetchPlaces(city: String, conversationId: String = "test_123") {
        viewModelScope.launch {
            _uiState.value = TravelPlannerUiState.Loading
            try {
                val request = TravelPlannerRequest(
                    message = city,
                    role = "user",
                    conversation_id = conversationId
                )

                val response = RetrofitInstance.travelPlannerApi.getPlaces(request)

                if (response.places.isNotEmpty()) {
                    _uiState.value = TravelPlannerUiState.Success(response.places)
                } else {
                    _uiState.value = TravelPlannerUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = TravelPlannerUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}