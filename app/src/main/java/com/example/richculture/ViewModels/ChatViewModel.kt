package com.example.richculture.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.ChatRequest
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ChatViewModel : ViewModel() {

    private val _chatResponse = MutableStateFlow<String?>(null)
    val chatResponse: StateFlow<String?> = _chatResponse

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun sendMessage(message: String, conversationId: String? = null) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val request = ChatRequest(
                    message = message,
                    conversation_id = conversationId
                )
                val response = RetrofitInstance.chatApi.sendMessage(request)

                _chatResponse.value = response.response

            } catch (e: Exception) {
                _error.value = when (e) {
                    is HttpException -> {
                        if (e.code() == 500) {
                            "There's a problem with the server right now. Please try again later."
                        } else {
                            "Network error: ${e.message()}"
                        }
                    }
                    else -> e.localizedMessage ?: "An unexpected error occurred"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * --- THIS IS THE MISSING FUNCTION ---
     * Clears the chat response to prevent it from being processed again on recomposition.
     */
    fun clearResponse() {
        _chatResponse.value = null
    }

    /**
     * --- THIS FUNCTION IS ALSO NEEDED ---
     * Resets the entire chat state (response, loading, error).
     * Call this when entering the chat screen to ensure a fresh start.
     */
    fun resetChatState() {
        _chatResponse.value = null
        _isLoading.value = false
        _error.value = null
    }
}

