package com.example.richculture.ViewModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.richculture.Data.AddCommentRequest
import com.example.richculture.Data.PostResponse
import com.example.richculture.retro.RetrofitInstance
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CommunityViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<PostResponse>>()
    val posts: LiveData<List<PostResponse>> get() = _posts

    private val _uploadResponse = MutableLiveData<PostResponse?>()
    val uploadResponse: LiveData<PostResponse?> get() = _uploadResponse

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error


    fun getPosts() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.communityApi.getPosts()
                if (response.isSuccessful) {
                    val sortedPosts = response.body()?.sortedByDescending { it.createdAt ?: "" }
                    _posts.postValue(sortedPosts ?: emptyList())
                } else {
                    _error.postValue("Failed to load posts: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.message}")
            }
        }
    }

    fun likePost(postId: String, token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.communityApi.likePost(postId, "Bearer $token")
                if (!response.isSuccessful) {
                    _error.postValue("Failed to like: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.message}")
            }
        }
    }

    // ✅ UPDATED: The function now builds the correct request body
    fun addComment(postId: String, text: String, token: String) {
        viewModelScope.launch {
            try {
                val request = AddCommentRequest(message = text)
                val response = RetrofitInstance.communityApi.addComment(postId, "Bearer $token", request)
                if (!response.isSuccessful) {
                    _error.postValue("Failed to comment: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.message}")
            }
        }
    }

    fun deleteComment(postId: String, commentId: String, token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.communityApi.deleteComment(postId, commentId, "Bearer $token")
                if (!response.isSuccessful) {
                    _error.postValue("Failed to delete comment: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.message}")
            }
        }
    }

    fun uploadPost(
        imagePart: MultipartBody.Part,
        caption: RequestBody,
        location: RequestBody,
        token: String
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.communityApi.uploadPost(imagePart, caption, location, "Bearer $token")
                if (response.isSuccessful) {
                    _uploadResponse.postValue(response.body())
                    getPosts()
                } else {
                    _error.postValue("Upload failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.message}")
            }
        }
    }
}

