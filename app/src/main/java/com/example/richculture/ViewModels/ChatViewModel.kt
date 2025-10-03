package com.example.richculture.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.ChatRequest
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.UUID

class ChatbotViewModel : ViewModel() {

    private val _chatResponse = MutableStateFlow<String?>(null)
    val chatResponse: StateFlow<String?> = _chatResponse

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun sendMessage(
        message: String,
        conversationId: String? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val idToSend = conversationId ?: UUID.randomUUID().toString()
                val request = ChatRequest(
                    message = message,
                    conversation_id = idToSend
                )

                // Directly call the correct API
                val response = RetrofitInstance.chatApi.sendMessage(request)
                _chatResponse.value = response.response

            } catch (e: Exception) {
                _error.value = when (e) {
                    is HttpException -> "Network error: Code ${e.code()} - ${e.message()}"
                    else -> e.localizedMessage ?: "An unexpected error occurred"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearResponse() {
        _chatResponse.value = null
    }

    fun resetChatState() {
        _chatResponse.value = null
        _isLoading.value = false
        _error.value = null
    }
}
