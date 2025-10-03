package com.example.richculture.ViewModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.Data.Art
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArtViewModel : ViewModel() {

    private val _artList = MutableStateFlow<List<Art>>(emptyList())
    val artList: StateFlow<List<Art>> = _artList

    private val _art = MutableStateFlow<Art?>(null)
    val art: StateFlow<Art?> = _art

    fun fetchAllArts() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.artApi.getAllArts()
                _artList.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchArtById(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.artApi.getArtById(id)
                _art.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
