package com.example.richculture.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.TripRequest
import com.example.richculture.Data.TripResponse
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class TravelPlannerUiState {
    object Empty : TravelPlannerUiState()
    object Loading : TravelPlannerUiState()
    data class Success(val tripResponse: TripResponse) : TravelPlannerUiState()
    data class Error(val message: String) : TravelPlannerUiState()
}

class TravelPlannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<TravelPlannerUiState>(TravelPlannerUiState.Empty)
    val uiState: StateFlow<TravelPlannerUiState> = _uiState

    fun fetchTripPlan(message: String, role: String = "user", conversationId: String = "conv_1") {
        viewModelScope.launch {
            _uiState.value = TravelPlannerUiState.Loading
            try {
                val request = TripRequest(message, role, conversationId)
                val response = RetrofitInstance.tripApi.getPlaces(request)
                _uiState.value = TravelPlannerUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = TravelPlannerUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}