package com.example.richculture.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.Event
import com.example.richculture.Data.EventRequest
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events = _events.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun fetchEvents(city: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Add a small delay to show the loading animation
                delay(500)

                val response = RetrofitInstance.eventsApi.getEvents(EventRequest(city))
                _events.value = response

                // Clear any previous errors on success
                _error.value = null

            } catch (e: Exception) {
                // Handle different types of errors
                _error.value = when {
                    e.message?.contains("timeout", ignoreCase = true) == true ->
                        "Request timed out. Please check your internet connection and try again."
                    e.message?.contains("network", ignoreCase = true) == true ->
                        "Network error. Please check your internet connection."
                    e.message?.contains("404", ignoreCase = true) == true ->
                        "No events found for this location."
                    e.message?.contains("500", ignoreCase = true) == true ->
                        "Server error. Please try again later."
                    else ->
                        "Failed to load events. Please try again."
                }

                // Clear events on error
                _events.value = emptyList()

            } finally {
                _loading.value = false
            }
        }
    }

    fun clearEvents() {
        _events.value = emptyList()
        _error.value = null
        _loading.value = false
    }

    fun retryFetchEvents(city: String) {
        if (city.isNotBlank()) {
            fetchEvents(city)
        }
    }
}
