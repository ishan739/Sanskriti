package com.example.richculture.utility

import android.content.Context
import android.content.SharedPreferences
import com.example.richculture.Data.Story

class PrefManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("SanskritiPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val STORY_ID_KEY = "story_of_the_day_id"
        private const val TIMESTAMP_KEY = "story_of_the_day_timestamp"
        private const val TWENTY_FOUR_HOURS_MS = 24 * 60 * 60 * 1000

        // ✅ NEW: Key for tracking if onboarding has been completed
        private const val ONBOARDING_COMPLETE_KEY = "onboarding_complete"
    }

    fun saveStoryOfTheDay(story: Story) {
        prefs.edit().apply {
            putInt(STORY_ID_KEY, story.id)
            putLong(TIMESTAMP_KEY, System.currentTimeMillis())
            apply()
        }
    }

    fun getStoryOfTheDayId(): Int? {
        val lastTimestamp = prefs.getLong(TIMESTAMP_KEY, 0L)
        val storyId = prefs.getInt(STORY_ID_KEY, -1)

        return if (storyId != -1 && (System.currentTimeMillis() - lastTimestamp) < TWENTY_FOUR_HOURS_MS) {
            storyId
        } else {
            null
        }
    }

    // ✅ NEW: Function to check if the user has seen the onboarding screens
    fun isOnboardingComplete(): Boolean {
        return prefs.getBoolean(ONBOARDING_COMPLETE_KEY, false)
    }

    // ✅ NEW: Function to set the onboarding as complete
    fun setOnboardingComplete() {
        prefs.edit().putBoolean(ONBOARDING_COMPLETE_KEY, true).apply()
    }
}