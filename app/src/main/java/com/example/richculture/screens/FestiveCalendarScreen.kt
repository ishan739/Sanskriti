package com.example.richculture.screens

import android.content.Intent
import android.os.Build
import android.provider.CalendarContract
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.richculture.Data.Holiday
import com.example.richculture.ViewModels.CalendarUiState
import com.example.richculture.ViewModels.CalendarViewModel
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import com.example.richculture.R

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FestiveCalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    // ✅ NEW: Collect state for upcoming holidays
    val upcomingUiState by viewModel.upcomingHolidaysState.collectAsState()

    var selectedYear by remember { mutableStateOf(LocalDate.now().year) }
    var selectedMonth by remember { mutableStateOf(LocalDate.now().month) }

    // Fetch monthly data whenever the year or month changes
    LaunchedEffect(selectedYear, selectedMonth) {
        viewModel.fetchMonthHolidays(selectedYear, selectedMonth.value)
    }

    // ✅ NEW: Fetch upcoming holidays once when the screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchUpcomingHolidays()
    }

    Scaffold(
        topBar = { CalendarTopAppBar(navController) },
        containerColor = Color.Transparent // 🔑 Transparent so gradient shows
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFEE5B0C), // deep purple
                            Color(0xFFF3DF17)  // bright blue
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 2000f)
                    )
                )
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    DateSelectionHeader(
                        selectedYear = selectedYear,
                        selectedMonth = selectedMonth,
                        onYearChange = { newYear -> selectedYear = newYear },
                        onMonthChange = { newMonth -> selectedMonth = newMonth }
                    )
                }

                item {
                    AnimatedContent(
                        targetState = uiState,
                        label = "CalendarContentAnimation"
                    ) { state ->
                        when (state) {
                            is CalendarUiState.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            is CalendarUiState.Success -> {
                                if (state.holidays.isEmpty()) {
                                    EmptyState()
                                } else {
                                    HolidayContent(
                                        year = selectedYear,
                                        month = selectedMonth,
                                        holidays = state.holidays
                                    )
                                }
                            }
                            is CalendarUiState.Error -> {
                                ErrorState(message = state.message)
                            }
                            is CalendarUiState.Empty -> { /* Initially empty */ }
                        }
                    }
                }

                // ✅ Upcoming Holidays Section
                item {
                    val upcomingState = upcomingUiState
                    if (upcomingState is CalendarUiState.Success && upcomingState.holidays.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        UpcomingHolidaysSection(holidays = upcomingState.holidays)
                    }
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HolidayContent(year: Int, month: Month, holidays: List<Holiday>) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CalendarView(year = year, month = month, holidays = holidays)
        HolidayList(holidays = holidays)
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
                        listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
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
                Text("Explore the celebrations of India", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DateSelectionHeader(
    selectedYear: Int,
    selectedMonth: Month,
    onYearChange: (Int) -> Unit,
    onMonthChange: (Month) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { onYearChange(selectedYear - 1) }) {
                Icon(painter = painterResource(id = R.drawable.ic_left), contentDescription = "Previous Year")
            }
            Text(
                text = selectedYear.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onYearChange(selectedYear + 1) }) {
                Icon(painter = painterResource(id = R.drawable.ic_right), contentDescription = "Next Year")
            }
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            items(Month.values()) { month ->
                val isSelected = month == selectedMonth
                Button(
                    onClick = { onMonthChange(month) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF42A5F5) else Color.White,
                        contentColor = if (isSelected) Color.White else Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(month.name.take(3).lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(year: Int, month: Month, holidays: List<Holiday>) {
    val holidayDates = remember(holidays) {
        holidays.mapNotNull {
            try {
                LocalDate.parse(it.date.iso, DateTimeFormatter.ISO_LOCAL_DATE).dayOfMonth
            } catch (e: Exception) {
                null
            }
        }.toSet()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
            val firstDayOfMonth = YearMonth.of(year, month).atDay(1).dayOfWeek.value % 7 // Sun=0
            val calendarDays = (1..daysInMonth).toList()

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                    Text(it, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Spacer(Modifier.height(8.dp))

            val totalCells = (daysInMonth + firstDayOfMonth + 6) / 7 * 7
            val paddedDays = List(firstDayOfMonth) { null } + calendarDays + List(totalCells - daysInMonth - firstDayOfMonth) { null }
            paddedDays.chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    week.forEach { day ->
                        val isHoliday = day != null && holidayDates.contains(day)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isHoliday) Color(0xFFEC407A).copy(alpha = 0.3f) else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            if (day != null) {
                                Text(
                                    text = day.toString(),
                                    color = if (isHoliday) Color(0xFFD81B60) else Color.Black,
                                    fontWeight = if (isHoliday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HolidayList(holidays: List<Holiday>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Festivals This Month", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        holidays.forEach { holiday ->
            HolidayCard(holiday = holiday)
        }
    }
}

// ✅ NEW: Composable for the upcoming holidays section
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpcomingHolidaysSection(holidays: List<Holiday>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Upcoming Festivals", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        holidays.forEach { holiday ->
            HolidayCard(holiday = holiday) // Reusing the same card for a consistent look
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HolidayCard(holiday: Holiday) {
    val context = LocalContext.current
    val parsedDate = remember {
        try {
            LocalDate.parse(holiday.date.iso, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            null
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (parsedDate != null) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEC407A).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            parsedDate.dayOfMonth.toString(),
                            color = Color(0xFFD81B60),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            parsedDate.month.name.take(3),
                            color = Color(0xFFD81B60),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(holiday.name, fontWeight = FontWeight.Bold)
                Text(holiday.type?.toString() ?: "Festival", fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(
                onClick = {
                    parsedDate?.let { date ->
                        val startMillis = date.atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli()

                        val intent = Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startMillis)
                            .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                            .putExtra(CalendarContract.Events.TITLE, holiday.name)
                            .putExtra(CalendarContract.Events.DESCRIPTION, "Celebration of ${holiday.name}")

                        context.startActivity(intent)
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.1f))
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_calendar_month), contentDescription = "Add to Calendar", tint = Color.Black)
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_event), contentDescription = "", tint = Color.LightGray, modifier = Modifier.size(64.dp))
        Text(
            "No Festivals Found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            "There are no recorded festivals for the selected month and year. Try selecting another.",
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

@Composable
fun ErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_event), contentDescription = "", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(64.dp))
        Text(
            "Something Went Wrong",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = message,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

