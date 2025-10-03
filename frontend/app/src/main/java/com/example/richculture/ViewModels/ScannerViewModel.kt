package com.example.richculture.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.richculture.Data.Scanner
import com.example.richculture.retro.RetrofitInstance
import com.example.richculture.utility.ScanHistoryManager
import com.example.richculture.utility.StoredScan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response

// ✅ UPDATED: The ViewModel now requires the ScanHistoryManager as a dependency.
class ScannerViewModel(
    // ✅ CRITICAL FIX: Removed 'private' to make this accessible to the UI.
    val scanHistoryManager: ScanHistoryManager
) : ViewModel() {

    private val _monumentInfo = MutableLiveData<Scanner?>()
    val monumentInfo: LiveData<Scanner?> = _monumentInfo

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _scanHistory = MutableStateFlow<List<StoredScan>>(emptyList())
    val scanHistory: StateFlow<List<StoredScan>> = _scanHistory.asStateFlow()

    init {
        loadScanHistory()
    }

    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.scannerApi.uploadImage(image)
                if (response.isSuccessful && response.body() != null) {
                    val scanResult = response.body()!!
                    _monumentInfo.postValue(scanResult)
                    addScanToHistory(scanResult)
                } else {
                    _error.postValue("Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.message}")
            }
        }
    }

    fun clearScanResult() {
        _monumentInfo.value = null
    }

    private fun addScanToHistory(scanResult: Scanner) {
        scanHistoryManager.addScanToHistory(scanResult)
        loadScanHistory()
    }

    private fun loadScanHistory() {
        _scanHistory.value = scanHistoryManager.getScanHistory()
    }
}

