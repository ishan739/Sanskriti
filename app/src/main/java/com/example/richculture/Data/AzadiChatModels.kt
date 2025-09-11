package com.example.richculture.Data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.richculture.R

data class FreedomFighter(
    val id: String, // Matches the key in your ViewModel's apiMap
    val name: String,
    val title: String,
    val timeline: String,
    val description: String,
    val tags: List<String>,
    val imageResId: Int,
    val primaryColor: Color,
    val gradient: Brush
)

// In a real app, this would come from an API, but for now, we define it here.
val freedomFighters = listOf(
    FreedomFighter(
        id = "bose",
        name = "Subhas Chandra Bose",
        title = "Netaji",
        timeline = "1897-1945",
        description = "A revolutionary leader who formed the Indian National Army.",
        tags = listOf("Revolutionary", "INA", "Leadership"),
        imageResId = R.drawable.ic_subhash, // Replace with your actual drawable
        primaryColor = Color(0xFFF44336),
        gradient = Brush.linearGradient(listOf(Color(0xFFE57373), Color(0xFFF44336)))
    ),
    FreedomFighter(
        id = "gandhi",
        name = "Mahatma Gandhi",
        title = "Father of the Nation",
        timeline = "1869-1948",
        description = "Be the change you wish to see in the world. Chat with me to learn about non-violence, truth, and the path to freedom.",
        tags = listOf("Non-violence", "Satyagraha", "Truth"),
        imageResId = R.drawable.ic_mahatama, // Replace with your actual drawable
        primaryColor = Color(0xFF4CAF50),
        gradient = Brush.linearGradient(listOf(Color(0xFF66BB6A), Color(0xFF4CAF50)))
    ),

    FreedomFighter(
        id = "bhagat",
        name = "Bhagat Singh",
        title = "Shaheed-e-Azam",
        timeline = "1907-1931",
        description = "A charismatic revolutionary who inspired a generation to fight for freedom.",
        tags = listOf("Revolutionary", "Socialism", "Youth Icon"),
        imageResId = R.drawable.ic_bhagat, // Replace with your actual drawable
        primaryColor = Color(0xFFFF9800),
        gradient = Brush.linearGradient(listOf(Color(0xFFFFB74D), Color(0xFFFF9800)))
    ),
    FreedomFighter(
        id = "vivekananda",
        name = "Swami Vivekananda",
        title = "Spiritual Leader",
        timeline = "1863-1902",
        description = "Arise, awake, and stop not till the goal is reached. Discuss philosophy and spirituality with me.",
        tags = listOf("Spirituality", "Philosophy", "Vedanta"),
        imageResId = R.drawable.ic_viveka, // Replace with your actual drawable
        primaryColor = Color(0xFF9C27B0),
        gradient = Brush.linearGradient(listOf(Color(0xFFBA68C8), Color(0xFF9C27B0)))
    ),
    FreedomFighter(
        id = "kalam",
        name = "A.P.J. Abdul Kalam",
        title = "The Missile Man",
        timeline = "1931-2015",
        description = "Dream is not that which you see while sleeping, it is something that does not let you sleep. Let's talk about science and dreams.",
        tags = listOf("Science", "Inspiration", "President"),
        imageResId = R.drawable.ic_kalam, // Replace with your actual drawable
        primaryColor = Color(0xFF2196F3),
        gradient = Brush.linearGradient(listOf(Color(0xFF64B5F6), Color(0xFF2196F3)))
    )
)
