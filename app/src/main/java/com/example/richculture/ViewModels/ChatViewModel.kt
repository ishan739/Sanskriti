package com.example.richculture.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.ChatRequest
import com.example.richculture.retro.ChatApi
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.UUID

// ✅ The Data class is now here for clarity and updated to require a conversation_id

class UniversalChatViewModel : ViewModel() {

    private val _chatResponse = MutableStateFlow<String?>(null)
    val chatResponse: StateFlow<String?> = _chatResponse

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val apiMap: Map<String, ChatApi> = mapOf(
        "tour" to RetrofitInstance.chatApi,
        "bhagat" to RetrofitInstance.bhagatApi,
        "bose" to RetrofitInstance.boseApi,
        "kalam" to RetrofitInstance.kalamApi,
        "vivekananda" to RetrofitInstance.vivekanandaApi,
        "gandhi" to RetrofitInstance.gandhiApi
    )

    fun sendMessage(
        message: String,
        target: String = "tour",
        conversationId: String? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // ✅ THE FIX: We now guarantee a conversation ID is always present.
                // If one isn't provided, we generate a new unique one.
                val idToSend = conversationId ?: UUID.randomUUID().toString()
                val request = ChatRequest(message, idToSend)

                val api = apiMap[target] ?: RetrofitInstance.chatApi
                val response = api.sendMessage(request)

                _chatResponse.value = response.response

            } catch (e: Exception) {
                _error.value = when (e) {
                    is HttpException -> {
                        if (e.code() == 500) {
                            "There's a problem with the server right now. Please try again later."
                        } else {
                            // We now also log the specific error code for better debugging
                            "Network error: Code ${e.code()} - ${e.message()}"
                        }
                    }
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
