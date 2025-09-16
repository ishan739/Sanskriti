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

    // --- Delegated Audio State ---
    val currentlyPlayingStory = AudioPlayerManager.currentPlayingUrl
    val isPlaying = AudioPlayerManager.isPlaying
    val playbackPosition = AudioPlayerManager.playbackPosition
    val totalDuration = AudioPlayerManager.totalDuration

    init {
        fetchAllStories()
        loadStoryOfTheDay()
    }

    // ✅ RE-IMPLEMENTED: Logic to fetch and handle the Story of the Day
    fun loadStoryOfTheDay() {
        viewModelScope.launch {
            isStoryOfTheDayLoading.value = true
            try {
                val savedStoryId = prefManager.getStoryOfTheDayId()
                if (savedStoryId != null) {
                    // If a recent story is saved, fetch it directly
                    _storyOfTheDay.value = RetrofitInstance.storyApi.getStoryById(savedStoryId)
                } else {
                    // Otherwise, fetch all stories to pick a new random one
                    val all = RetrofitInstance.storyApi.getStories()
                    if (all.isNotEmpty()) {
                        val randomStory = all.random()
                        _storyOfTheDay.value = randomStory
                        // Save the new story and timestamp
                        prefManager.saveStoryOfTheDay(randomStory)
                    }
                }
            } catch (e: Exception) {
                error.value = "Could not load highlight."
            } finally {
                isStoryOfTheDayLoading.value = false
            }
        }
    }

    // ✅ RE-IMPLEMENTED: Logic to fetch all stories from the API
    private fun fetchAllStories() {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val storyList = RetrofitInstance.storyApi.getStories()
                _allStories.value = storyList
                _displayedStories.value = storyList // Initially display all
                processCategories(storyList)
                error.value = null
            } catch (e: Exception) {
                error.value = "Failed to load stories. Please check your connection."
            } finally {
                isLoading.value = false
            }
        }
    }

    // ✅ RE-IMPLEMENTED: Logic to group stories by category
    private fun processCategories(stories: List<Story>) {
        _categories.value = stories.groupBy { it.category }
            .map { (categoryName, storiesInCategory) ->
                StoryCategoryInfo(
                    name = categoryName,
                    storyCount = storiesInCategory.size,
                    firstThumbnailUrl = storiesInCategory.firstOrNull()?.thumbnail ?: ""
                )
            }
    }

    // ✅ RE-IMPLEMENTED: Logic to filter the displayed stories
    fun selectCategory(categoryName: String?) {
        _selectedCategory.value = categoryName
        _displayedStories.value = if (categoryName == null) {
            _allStories.value // Show all stories
        } else {
            _allStories.value.filter { it.category == categoryName } // Show filtered stories
        }
    }

    fun playStory(story: Story) {
        val context = getApplication<Application>().applicationContext
        AudioPlayerManager.playOrPause(context, story.audiourl)
    }

    fun seekTo(position: Long) {
        AudioPlayerManager.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        AudioPlayerManager.releasePlayer()
    }
}

