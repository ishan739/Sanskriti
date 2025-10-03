package com.example.richculture.ViewModels

import com.example.richculture.Data.Dance


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DanceViewModel : ViewModel() {

    private val _danceList = MutableStateFlow<List<Dance>>(emptyList())
    val danceList: StateFlow<List<Dance>> = _danceList

    private val _dance = MutableStateFlow<Dance?>(null)
    val dance: StateFlow<Dance?> = _dance

    fun fetchAllDances() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.danceApi.getAllDances()
                _danceList.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchDanceById(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.danceApi.getDanceById(id)
                _dance.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
