package com.example.richculture.ViewModels

import com.example.richculture.Data.Holiday


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.richculture.retro.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CalendarUiState {
    object Loading : CalendarUiState()
    data class Success(val holidays: List<Holiday>) : CalendarUiState()
    data class Error(val message: String) : CalendarUiState()
    object Empty : CalendarUiState()
}

class CalendarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState.Empty)
    val uiState: StateFlow<CalendarUiState> = _uiState

    fun fetchYearHolidays(year: Int) {
        viewModelScope.launch {
            _uiState.value = CalendarUiState.Loading
            try {
                val data = RetrofitInstance.calendarApi.getHolidaysByYear(year)
                _uiState.value = CalendarUiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = CalendarUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchMonthHolidays(year: Int, month: Int) {
        viewModelScope.launch {
            _uiState.value = CalendarUiState.Loading
            try {
                val data = RetrofitInstance.calendarApi.getHolidaysByMonth(year, month)
                _uiState.value = CalendarUiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = CalendarUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchUpcomingHolidays() {
        viewModelScope.launch {
            _uiState.value = CalendarUiState.Loading
            try {
                val data = RetrofitInstance.calendarApi.getUpcomingHolidays()
                _uiState.value = CalendarUiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = CalendarUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
