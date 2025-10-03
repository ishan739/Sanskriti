package com.example.richculture.ViewModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.Music
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicViewModel : ViewModel() {

    private val _musicList = MutableStateFlow<List<Music>>(emptyList())
    val musicList: StateFlow<List<Music>> = _musicList

    private val _music = MutableStateFlow<Music?>(null)
    val music: StateFlow<Music?> = _music

    fun fetchAllMusic() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.musicApi.getAllMusic()
                _musicList.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchMusicById(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.musicApi.getMusicById(id)
                _music.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
