package com.example.richculture.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.Story
import com.example.richculture.retro.RetrofitInstance
import com.example.richculture.utility.AudioPlayerManager
import com.example.richculture.utility.PrefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Helper data class for the processed categories
data class StoryCategoryInfo(
    val name: String,
    val storyCount: Int,
    val firstThumbnailUrl: String
)

class StoryViewModel(application: Application) : AndroidViewModel(application) {

    private val prefManager = PrefManager(application)

    // --- State for Stories Screen ---
    private val _allStories = MutableStateFlow<List<Story>>(emptyList())
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()
    private val _displayedStories = MutableStateFlow<List<Story>>(emptyList())
    val displayedStories = _displayedStories.asStateFlow()
    private val _categories = MutableStateFlow<List<StoryCategoryInfo>>(emptyList())
    val categories = _categories.asStateFlow()
    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    // --- State for Home Screen's Story of the Day ---
    private val _storyOfTheDay = MutableStateFlow<Story?>(null)
    val storyOfTheDay = _storyOfTheDay.asStateFlow()
    val isStoryOfTheDayLoading = MutableStateFlow(true)

    // --- ✅ DELEGATED AUDIO STATE ---
    // The ViewModel now gets its state directly from the singleton AudioPlayerManager.
    val currentlyPlayingStory = AudioPlayerManager.currentPlayingUrl
    val isPlaying = AudioPlayerManager.isPlaying
    val playbackPosition = AudioPlayerManager.playbackPosition
    val totalDuration = AudioPlayerManager.totalDuration

    init {
        fetchAllStories()
        loadStoryOfTheDay()
    }

    fun loadStoryOfTheDay() { /* ... no changes ... */ }
    private fun fetchAllStories() { /* ... no changes ... */ }
    private fun processCategories(stories: List<Story>) { /* ... no changes ... */ }
    fun selectCategory(categoryName: String?) { /* ... no changes ... */ }

    // --- ✅ SIMPLIFIED PLAYER CONTROLS ---

    fun playStory(story: Story) {
        val context = getApplication<Application>().applicationContext
        // The logic is now a simple call to the manager.
        AudioPlayerManager.playOrPause(context, story.audiourl)
    }

    fun seekTo(position: Long) {
        // Delegate seeking to the manager.
        AudioPlayerManager.seekTo(position)
    }

    // It's crucial to release the player when the ViewModel is cleared.
    override fun onCleared() {
        super.onCleared()
        AudioPlayerManager.releasePlayer()
    }
}
