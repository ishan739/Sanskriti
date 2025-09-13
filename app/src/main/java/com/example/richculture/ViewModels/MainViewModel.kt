package com.example.richculture.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.navigate.Screen
import com.example.richculture.utility.PrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val prefManager: PrefManager) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            // Move the disk read operation to a background thread for performance
            val isOnboardingComplete = withContext(Dispatchers.IO) {
                prefManager.isOnboardingComplete()
            }

            // Set the appropriate start destination
            _startDestination.value = if (isOnboardingComplete) {
                Screen.Auth.route
            } else {
                Screen.Onboarding.route // New users start at our unified onboarding screen
            }
        }
    }
}

