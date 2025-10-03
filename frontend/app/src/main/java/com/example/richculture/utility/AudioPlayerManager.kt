package com.example.richculture.utility

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// A singleton object to manage a single ExoPlayer instance for the entire app.
object AudioPlayerManager {

    private var exoPlayer: ExoPlayer? = null
    private var progressUpdateJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    // --- Public states for the UI to observe ---
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlayingUrl = MutableStateFlow<String?>(null)
    val currentPlayingUrl: StateFlow<String?> = _currentPlayingUrl.asStateFlow()

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> = _playbackPosition.asStateFlow()

    private val _totalDuration = MutableStateFlow(0L)
    val totalDuration: StateFlow<Long> = _totalDuration.asStateFlow()


    private fun initializePlayer(context: Context) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                        _isPlaying.value = isPlayingValue
                        if (isPlayingValue) startProgressUpdates() else stopProgressUpdates()
                    }
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            _totalDuration.value = exoPlayer?.duration ?: 0L
                        } else if (playbackState == Player.STATE_ENDED) {
                            releasePlayer()
                        }
                    }
                })
            }
        }
    }

    fun playOrPause(context: Context, audioUrl: String) {
        initializePlayer(context)
        if (_currentPlayingUrl.value != audioUrl) {
            _currentPlayingUrl.value = audioUrl
            val mediaItem = MediaItem.fromUri(audioUrl)
            exoPlayer?.apply {
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
        } else {
            if (exoPlayer?.isPlaying == true) exoPlayer?.pause() else exoPlayer?.play()
        }
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
        _playbackPosition.value = position
    }

    fun releasePlayer() {
        stopProgressUpdates()
        exoPlayer?.release()
        exoPlayer = null
        _isPlaying.value = false
        _currentPlayingUrl.value = null
        _playbackPosition.value = 0L
        _totalDuration.value = 0L
    }

    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = scope.launch {
            while (_isPlaying.value) {
                _playbackPosition.value = exoPlayer?.currentPosition ?: 0L
                delay(500)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
    }
}
