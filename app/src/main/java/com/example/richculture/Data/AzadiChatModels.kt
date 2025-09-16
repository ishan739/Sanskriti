package com.example.richculture.Data


import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.richculture.R


data class Leader(
    val id: String, // A unique ID for navigation
    val name: String, // The full name to be sent to the API
    val title: String,
    val timeline: String,
    val description: String,
    val tags: List<String>,
    val imageResId: Int,
    val primaryColor: Color,
    val gradient: Brush
)

// The new, hand-picked list of 10 iconic leaders
val leaders = listOf(
    Leader("gandhi", "Mahatma Gandhi", "Father of the Nation", "1869-1948", "Leader of the Indian independence movement, known for his philosophy of nonviolent civil disobedience.", listOf("Non-violence", "Satyagraha"), R.drawable.ic_subhash, Color(0xFF4CAF50), Brush.linearGradient(listOf(Color(0xFF66BB6A), Color(0xFF4CAF50)))),
    Leader("nehru", "Jawaharlal Nehru", "First Prime Minister", "1889-1964", "A central figure in Indian politics before and after independence, serving as India's first Prime Minister.", listOf("Visionary", "Modern India"), R.drawable.ic_subhash, Color(0xFF2196F3), Brush.linearGradient(listOf(Color(0xFF64B5F6), Color(0xFF2196F3)))),
    Leader("patel", "Sardar Vallabhbhai Patel", "Iron Man of India", "1875-1950", "Played a leading role in the country's struggle for independence and guided its integration into a united, independent nation.", listOf("Integration", "Leadership"), R.drawable.ic_subhash, Color(0xFF795548), Brush.linearGradient(listOf(Color(0xFF8D6E63), Color(0xFF795548)))),
    Leader("bose", "Subhas Chandra Bose", "Netaji", "1897-1945", "A revolutionary nationalist whose defiant patriotism made him a hero in India, but whose attempts during WWII to rid India of British rule with the help of Nazi Germany and Imperial Japan left a troubled legacy.", listOf("Revolutionary", "INA"), R.drawable.ic_subhash, Color(0xFFF44336), Brush.linearGradient(listOf(Color(0xFFE57373), Color(0xFFF44336)))),
    Leader("bhagat", "Bhagat Singh", "Shaheed-e-Azam", "1907-1931", "A charismatic Indian revolutionary who participated in the mistaken murder of a junior British police officer in what was to be revenge for the death of an Indian nationalist.", listOf("Sacrifice", "Socialism"), R.drawable.ic_bhagat, Color(0xFFFF9800), Brush.linearGradient(listOf(Color(0xFFFFB74D), Color(0xFFFF9800)))),
    Leader("lakshmibai", "Rani Lakshmibai", "Queen of Jhansi", "1828-1858", "One of the leading figures of the Indian Rebellion of 1857 and became a symbol of resistance to the British Raj for Indian nationalists.", listOf("Warrior", "Courage"), R.drawable.ic_subhash, Color(0xFFE91E63), Brush.linearGradient(listOf(Color(0xFFF06292), Color(0xFFE91E63)))),
    Leader("ambedkar", "Dr. B. R. Ambedkar", "Architect of the Constitution", "1891-1956", "An Indian jurist, economist, politician and social reformer who inspired the Dalit Buddhist movement and campaigned against social discrimination.", listOf("Equality", "Law"), R.drawable.ic_subhash, Color(0xFF3F51B5), Brush.linearGradient(listOf(Color(0xFF7986CB), Color(0xFF3F51B5)))),
    Leader("vivekananda", "Swami Vivekananda", "Spiritual Leader", "1863-1902", "A key figure in the introduction of the Indian philosophies of Vedanta and Yoga to the Western world.", listOf("Spirituality", "Philosophy"), R.drawable.ic_viveka, Color(0xFF673AB7), Brush.linearGradient(listOf(Color(0xFF9575CD), Color(0xFF673AB7)))),
    Leader("indira", "Indira Gandhi", "Iron Lady of India", "1917-1984", "The first and, to date, only female Prime Minister of India, she was a central figure of the Indian National Congress.", listOf("Politics", "Strength"), R.drawable.ic_subhash, Color(0xFF009688), Brush.linearGradient(listOf(Color(0xFF4DB6AC), Color(0xFF009688)))),
    Leader("kalam", "A.P.J. Abdul Kalam", "The Missile Man", "1931-2015", "An aerospace scientist who served as the 11th President of India from 2002 to 2007. He was known for his pivotal role in India's civilian space program and military missile development.", listOf("Science", "Inspiration"), R.drawable.ic_kalam, Color(0xFF03A9F4), Brush.linearGradient(listOf(Color(0xFF4FC3F7), Color(0xFF03A9F4))))
)