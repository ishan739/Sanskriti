package com.example.richculture.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.TripRequest
import com.example.richculture.Data.TripResponse
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.concurrent.ConcurrentHashMap


sealed class TravelPlannerUiState {
    object Empty : TravelPlannerUiState()
    object Loading : TravelPlannerUiState()
    data class Success(val tripResponse: TripResponse) : TravelPlannerUiState()
    data class Error(val message: String) : TravelPlannerUiState()
}

class TravelPlannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<TravelPlannerUiState>(TravelPlannerUiState.Empty)
    val uiState: StateFlow<TravelPlannerUiState> = _uiState

    // --- FRONTEND FIX 1: Client-Side Cache ---
    // This map will store successful trip plans to avoid re-fetching the same data.
    // Using ConcurrentHashMap for thread safety, although not strictly necessary in this ViewModel.
    private val tripCache = ConcurrentHashMap<String, TripResponse>()

    private var lastRequest: TripRequest? = null

    fun fetchTripPlan(message: String, role: String = "user", conversationId: String = "conv_1") {
        // Normalize the cache key to handle different casing (e.g., "Delhi" vs "delhi")
        val cacheKey = message.trim().lowercase()

        // --- Check the cache before making a network call ---
        if (tripCache.containsKey(cacheKey)) {
            _uiState.value = TravelPlannerUiState.Loading
            // Use the cached data instead of hitting the network
            _uiState.value = TravelPlannerUiState.Success(tripCache[cacheKey]!!)
            return // Stop execution here
        }

        viewModelScope.launch {
            _uiState.value = TravelPlannerUiState.Loading
            try {
                val request = TripRequest(message, role, conversationId)
                lastRequest = request
                val response = RetrofitInstance.tripApi.getPlaces(request)

                // --- On success, save the response to the cache ---
                tripCache[cacheKey] = response

                _uiState.value = TravelPlannerUiState.Success(response)
            } catch (e: Exception) {
                // --- FRONTEND FIX 2: Smarter Error Handling ---
                val errorMessage = when (e) {
                    is HttpException -> {
                        // Try to read the specific error message from the server
                        val errorBody = e.response()?.errorBody()?.string()
                        if (errorBody != null && errorBody.contains("Request too large", ignoreCase = true)) {
                            // Provide a user-friendly message for the specific rate-limit issue
                            "This request is too complex for the AI. Please try a more specific location."
                        } else {
                            "A server error occurred (Code: ${e.code()}). Please try again."
                        }
                    }
                    is java.net.UnknownHostException -> "Network error. Please check your internet connection."
                    else -> e.message ?: "An unknown error occurred."
                }
                _uiState.value = TravelPlannerUiState.Error(errorMessage)
            }
        }
    }

    fun retryFetchTripPlan() {
        lastRequest?.let {
            // When retrying, bypass the cache to force a new network call
            val cacheKey = it.message.trim().lowercase()
            tripCache.remove(cacheKey)
            fetchTripPlan(it.message, it.role, it.conversation_id)
        }
    }
}

