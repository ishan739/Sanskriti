package com.example.richculture.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.Month
import java.time.YearMonth

// --- New Data Models for the Redesigned Screen ---

data class Festivala(
    val day: Int,
    val name: String,
    val description: String,
    val category: String,
    val color: Color
)

data class UpcomingEvent(
    val title: String,
    val date: String,
    val location: String,
    val type: String
)

// --- Comprehensive Dummy Data for Interaction ---

@RequiresApi(Build.VERSION_CODES.O)
val festivalDataByMonth = mapOf(
    Month.JANUARY to listOf(
        Festivala(14, "Makar Sankranti", "Harvest festival", "Major Festival", Color(0xFFFFF176)),
        Festivala(26, "Republic Day", "National holiday", "National", Color(0xFF81C784))
    ),
    Month.FEBRUARY to listOf(
        Festivala(12, "Vasant Panchami", "Festival of spring", "Religious", Color(0xFFFFF176)),
        Festivala(20, "Shivaratri", "Night of Shiva", "Major Festival", Color(0xFFF06292))
    ),
    Month.MARCH to listOf(
        Festivala(24, "Holi", "Festival of colors", "Major Festival", Color(0xFFF06292)),
        Festivala(25, "Holika Dahan", "Holi bonfire", "Religious", Color(0xFFFFB74D))
    ),
    Month.OCTOBER to listOf(
        Festivala(12, "Dussehra", "Victory of good over evil", "Major Festival", Color(0xFFFFB74D))
    ),
    Month.NOVEMBER to listOf(
        Festivala(1, "Diwali", "Festival of lights", "Major Festival", Color(0xFFFFF176)),
        Festivala(3, "Govardhan Puja", "Mountain lifting celebration", "Religious", Color(0xFF81C784)),
        Festivala(4, "Bhai Dooj", "Brother-sister bond", "Family", Color(0xFFCE93D8)),
        Festivala(15, "Karva Chauth", "Fast for husband's long life", "Regional", Color(0xFFF48FB1))
    )
)

val upcomingEvents = listOf(
    UpcomingEvent("Diwali Preparation Workshop", "Oct 18, 2024 • 6:00 PM", "Cultural Center", "Workshop"),
    UpcomingEvent("Rangoli Competition", "Oct 23, 2024 • 10:00 AM", "Community Hall", "Competition")
)


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FestiveCalendarScreen(navController: NavController) {
    var selectedMonth by remember { mutableStateOf(Month.FEBRUARY) }
    val festivalsForSelectedMonth = festivalDataByMonth[selectedMonth] ?: emptyList()

    Scaffold(
        topBar = { CalendarTopAppBar(navController) },
        containerColor = Color(0xFFFFF8F5) // Soft off-white background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(), // ✅ only top padding from scaffold
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // ✅ manual bottom padding (space above bottom nav)
        ){
            item {
                Sectionn(title = "Select Month") {
                    MonthSelector(selectedMonth = selectedMonth, onMonthSelected = { selectedMonth = it })
                }
            }
            item {
                CalendarView(
                    month = selectedMonth,
                    year = 2024,
                    festivals = festivalsForSelectedMonth
                )
            }
            item {
                Sectionn(title = "${selectedMonth.name.lowercase().capitalize()} Festivals") {
                    FestivalList(festivals = festivalsForSelectedMonth)
                }
            }
            item {
                Sectionn(title = "Upcoming Events") {
                    UpcomingEventsList(events = upcomingEvents)
                }
            }
        }
    }
}

@Composable
fun CalendarTopAppBar(navController: NavController) {
    Box {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFEC407A), Color(0xFFD81B60))
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Festival Calendar 🗓️", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Never miss a celebration", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun Sectionn(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        content()
    }
}

@Composable
fun MonthSelector(selectedMonth: Month, onMonthSelected: (Month) -> Unit) {
    val months = Month.values()
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(months) { month ->
            val isSelected = month == selectedMonth
            Button(
                onClick = { onMonthSelected(month) },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFFF06292) else Color.White,
                    contentColor = if (isSelected) Color.White else Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(month.name.lowercase().capitalize().take(3))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(month: Month, year: Int, festivals: List<Festivala>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${month.name.lowercase().capitalize()} $year highlights", fontWeight = FontWeight.Bold)
            Text("Festival highlights", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            // Calendar Grid
            val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
            val firstDayOfMonth = YearMonth.of(year, month).atDay(1).dayOfWeek.value % 7 // Sun=0
            val calendarDays = (1..daysInMonth).toList()
            val festivalDays = festivals.map { it.day to it.color }.toMap()

            // Day labels (Sun, Mon, etc.)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                    Text(it, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Dates
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val totalCells = (daysInMonth + firstDayOfMonth + 6) / 7 * 7
                val paddedDays = List(firstDayOfMonth) { null } + calendarDays + List(totalCells - daysInMonth - firstDayOfMonth) { null }
                paddedDays.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        week.forEach { day ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(festivalDays[day] ?: Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                if (day != null) {
                                    Text(
                                        text = day.toString(),
                                        color = if (festivalDays.containsKey(day)) Color.White else Color.Black,
                                        fontWeight = if (festivalDays.containsKey(day)) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FestivalList(festivals: List<Festivala>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        festivals.sortedBy { it.day }.forEach { festival ->
            FestivalCard(festival = festival)
        }
    }
}

@Composable
fun FestivalCard(festival: Festivala) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = festival.color.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(festival.color),
                contentAlignment = Alignment.Center
            ) {
                Text(festival.day.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(festival.name, fontWeight = FontWeight.Bold)
                Text(festival.description, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { /* Learn More */ },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.7f),
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text("Learn More", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Notifications, contentDescription = "Remind me", tint = Color.Gray)
                }
            }
            Text(
                text = festival.category,
                fontSize = 10.sp,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.5f), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun UpcomingEventsList(events: List<UpcomingEvent>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        events.forEach { event ->
            EventCard(event = event)
        }
    }
}

@Composable
fun EventCard(event: UpcomingEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8EAF6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFF3F51B5))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, fontWeight = FontWeight.Bold)
                Text(event.date, fontSize = 12.sp, color = Color.Gray)
                Text(event.location, fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text = event.type,
                fontSize = 10.sp,
                color = Color.DarkGray,
                modifier = Modifier
                    .background(Color.LightGray.copy(alpha = 0.5f), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}