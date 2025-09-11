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

        // Return saved ID only if it's not default and less than 24 hours old
        return if (storyId != -1 && (System.currentTimeMillis() - lastTimestamp) < TWENTY_FOUR_HOURS_MS) {
            storyId
        } else {
            null // It's stale or doesn't exist, we need a new one
        }
    }
}

