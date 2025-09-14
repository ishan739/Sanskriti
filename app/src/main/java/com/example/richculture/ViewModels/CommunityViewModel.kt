package com.example.richculture.ViewModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
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


    // ✅ Get posts (sorted by createdAt DESC so latest first)
    fun getPosts() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.communityApi.getPosts()
                if (response.isSuccessful) {
                    val sortedPosts = response.body()?.sortedByDescending { it.createdAt }
                    _posts.postValue(sortedPosts ?: emptyList())
                } else {
                    _error.postValue("Failed to load posts: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.message}")
            }
        }
    }

    // ✅ Like/Unlike Post
    fun likePost(postId: String, token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.communityApi.likePost(postId, "Bearer $token")
                if (response.isSuccessful) {
                    // Don't call getPosts() here to support optimistic UI.
                    // The UI will handle the immediate change.
                } else {
                    _error.postValue("Failed to like: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.message}")
            }
        }
    }

    // ✅ Add Comment
    fun addComment(postId: String, text: String, token: String) {
        viewModelScope.launch {
            try {
                val body = mapOf("text" to text)
                val response = RetrofitInstance.communityApi.addComment(postId, "Bearer $token", body)
                if (!response.isSuccessful) {
                    _error.postValue("Failed to comment: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.message}")
            }
        }
    }

    // ✅ Delete Comment
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

    // ✅ NEW: Upload Post
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
                    // Refresh the post list after a successful upload
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
