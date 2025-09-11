package com.example.richculture.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.richculture.Data.Story
import com.example.richculture.retro.RetrofitInstance
import com.example.richculture.utility.PrefManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Helper data class for the processed categories
data class StoryCategoryInfo(
    val name: String,
    val storyCount: Int,
    val firstThumbnailUrl: String
)

// The ViewModel is changed to an AndroidViewModel to get the application context,
// which is needed for the PrefManager.
class StoryViewModel(application: Application) : AndroidViewModel(application) {

    // Instance of PrefManager to handle the 24-hour logic
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


    // --- Shared ExoPlayer State for the whole app ---
    private var exoPlayer: ExoPlayer? = null
    private val _currentlyPlayingStory = MutableStateFlow<Story?>(null)
    val currentlyPlayingStory = _currentlyPlayingStory.asStateFlow()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()
    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition = _playbackPosition.asStateFlow()
    private val _totalDuration = MutableStateFlow(0L)
    val totalDuration = _totalDuration.asStateFlow()
    private var progressUpdateJob: Job? = null

    init {
        // Fetch data for both screens when the ViewModel is created
        fetchAllStories()
        loadStoryOfTheDay()
    }

    // Logic for the Home Screen Highlight
    fun loadStoryOfTheDay() {
        viewModelScope.launch {
            isStoryOfTheDayLoading.value = true
            try {
                val savedStoryId = prefManager.getStoryOfTheDayId()
                if (savedStoryId != null) {
                    _storyOfTheDay.value = RetrofitInstance.storyApi.getStoryById(savedStoryId)
                } else {
                    // If no recent story is saved, fetch the full list to pick a random one
                    val all = RetrofitInstance.storyApi.getStories()
                    if (all.isNotEmpty()) {
                        val randomStory = all.random()
                        _storyOfTheDay.value = randomStory
                        prefManager.saveStoryOfTheDay(randomStory)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Handle error appropriately
            } finally {
                isStoryOfTheDayLoading.value = false
            }
        }
    }


    private fun fetchAllStories() {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val storyList = RetrofitInstance.storyApi.getStories()
                _allStories.value = storyList
                _displayedStories.value = storyList
                processCategories(storyList)
                error.value = null
            } catch (e: Exception) {
                error.value = "Failed to load stories. Please check your connection."
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

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

    fun selectCategory(categoryName: String?) {
        _selectedCategory.value = categoryName
        if (categoryName == null) {
            _displayedStories.value = _allStories.value
        } else {
            _displayedStories.value = _allStories.value.filter { it.category == categoryName }
        }
    }

    fun playStory(story: Story) {
        val context = getApplication<Application>().applicationContext
        if (_currentlyPlayingStory.value?.id == story.id) {
            if (_isPlaying.value) pause() else resume()
            return
        }
        releasePlayer()
        _currentlyPlayingStory.value = story
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(story.audiourl))
            prepare()
            playWhenReady = true
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) _totalDuration.value = exoPlayer?.duration ?: 0L
                    else if (playbackState == Player.STATE_ENDED) releasePlayer()
                }
                override fun onIsPlayingChanged(isPlayingChange: Boolean) {
                    _isPlaying.value = isPlayingChange
                    if (isPlayingChange) startProgressUpdates() else stopProgressUpdates()
                }
            })
        }
    }

    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            while (_isPlaying.value) {
                _playbackPosition.value = exoPlayer?.currentPosition ?: 0L
                delay(500)
            }
        }
    }

    private fun stopProgressUpdates() { progressUpdateJob?.cancel() }
    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
        _playbackPosition.value = position
    }
    fun pause() { exoPlayer?.pause() }
    private fun resume() { exoPlayer?.play() }

    private fun releasePlayer() {
        stopProgressUpdates()
        exoPlayer?.release()
        exoPlayer = null
        _currentlyPlayingStory.value = null
        _isPlaying.value = false
        _playbackPosition.value = 0L
        _totalDuration.value = 0L
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}

