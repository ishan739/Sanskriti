package com.example.richculture.utility

import android.content.Context
import android.content.SharedPreferences
import com.example.richculture.Data.Scanner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

// Data class specifically for storing scan history. It's simpler than the full Scanner response.
data class StoredScan(
    val name: String,
    val imageUrl: String,
    val timestamp: String
)

class ScanHistoryManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ScanHistoryPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val HISTORY_KEY = "scan_history"
        private const val MAX_HISTORY_SIZE = 3
    }

    // Adds a new successful scan to the history and trims the list.
    fun addScanToHistory(scanResult: Scanner) {
        val currentHistory = getScanHistory().toMutableList()
        val timestamp = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault()).format(Date())

        val newScan = StoredScan(
            name = scanResult.name,
            imageUrl = scanResult.originalImage,
            timestamp = "Scanned on $timestamp"
        )

        // Add the new scan to the top of the list
        currentHistory.add(0, newScan)

        // Trim the list to the max size
        val updatedHistory = if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.take(MAX_HISTORY_SIZE)
        } else {
            currentHistory
        }

        saveScanHistory(updatedHistory)
    }

    // Retrieves the list of recent scans from SharedPreferences.
    fun getScanHistory(): List<StoredScan> {
        val json = prefs.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<StoredScan>>() {}.type
        return gson.fromJson(json, type)
    }

    // Saves the list to SharedPreferences as a JSON string.
    private fun saveScanHistory(history: List<StoredScan>) {
        val json = gson.toJson(history)
        prefs.edit().putString(HISTORY_KEY, json).apply()
    }
}
