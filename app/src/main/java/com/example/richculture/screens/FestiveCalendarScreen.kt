package com.example.richculture.screens

import android.content.Intent
import android.os.Build
import android.provider.CalendarContract
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.richculture.R
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

// Modern Color Palette for Festival Calendar
object FestivalColors {
    val DeepPurple = Color(0xFF6366F1)
    val VibrantPink = Color(0xFFEC4899)
    val ElectricBlue = Color(0xFF06B6D4)
    val SunsetOrange = Color(0xFFF59E0B)
    val EmeraldGreen = Color(0xFF10B981)
    val CoralRed = Color(0xFFEF4444)
    val RoyalPurple = Color(0xFF8B5CF6)
    val GoldenYellow = Color(0xFFFBBF24)
}

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FestiveCalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val upcomingUiState by viewModel.upcomingHolidaysState.collectAsState()

    var selectedYear by remember { mutableStateOf(LocalDate.now().year) }
    var selectedMonth by remember { mutableStateOf(LocalDate.now().month) }

    // Fetch data when year/month changes
    LaunchedEffect(selectedYear, selectedMonth) {
        viewModel.fetchMonthHolidays(selectedYear, selectedMonth.value)
    }

    LaunchedEffect(Unit) {
        viewModel.fetchUpcomingHolidays()
    }

    // Animated gradient background
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedFestivalBackground()

        Scaffold(
            topBar = {
                ModernFestivalTopBar(
                    navController = navController,
                    selectedMonth = selectedMonth,
                    selectedYear = selectedYear
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    ModernDateSelector(
                        selectedYear = selectedYear,
                        selectedMonth = selectedMonth,
                        onYearChange = { selectedYear = it },
                        onMonthChange = { selectedMonth = it }
                    )
                }

                item {
                    AnimatedContent(
                        targetState = uiState,
                        transitionSpec = {
                            slideInVertically { it } + fadeIn() with
                                    slideOutVertically { -it } + fadeOut()
                        },
                        label = "CalendarContentAnimation"
                    ) { state ->
                        when (state) {
                            is CalendarUiState.Loading -> {
                                ModernLoadingState()
                            }
                            is CalendarUiState.Success -> {
                                if (state.holidays.isEmpty()) {
                                    ModernEmptyState()
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                                        ModernCalendarGrid(
                                            year = selectedYear,
                                            month = selectedMonth,
                                            holidays = state.holidays
                                        )
                                        ModernHolidayList(
                                            title = "This Month's Festivals",
                                            holidays = state.holidays,
                                            accentColor = FestivalColors.VibrantPink
                                        )
                                    }
                                }
                            }
                            is CalendarUiState.Error -> {
                                ModernErrorState(message = state.message)
                            }
                            is CalendarUiState.Empty -> { /* Initially empty */ }
                        }
                    }
                }

                // Upcoming Holidays Section
                item {
                    val upcomingState = upcomingUiState
                    if (upcomingState is CalendarUiState.Success && upcomingState.holidays.isNotEmpty()) {
                        ModernHolidayList(
                            title = "Upcoming Celebrations",
                            holidays = upcomingState.holidays,
                            accentColor = FestivalColors.ElectricBlue
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedFestivalBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "festival_bg")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A1B4B),
                        Color(0xFF2D1B69),
                        Color(0xFF0F0C29)
                    )
                )
            )
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Rotating gradient orbs
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    FestivalColors.VibrantPink.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                center = Offset(centerX + 200 * kotlin.math.cos(Math.toRadians(rotation.toDouble())).toFloat(),
                    centerY + 200 * kotlin.math.sin(Math.toRadians(rotation.toDouble())).toFloat()),
                radius = 300f * scale
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    FestivalColors.ElectricBlue.copy(alpha = 0.25f),
                    Color.Transparent
                ),
                center = Offset(centerX - 150 * kotlin.math.cos(Math.toRadians(rotation.toDouble() + 120)).toFloat(),
                    centerY - 150 * kotlin.math.sin(Math.toRadians(rotation.toDouble() + 120)).toFloat()),
                radius = 250f * scale
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    FestivalColors.SunsetOrange.copy(alpha = 0.2f),
                    Color.Transparent
                ),
                center = Offset(centerX + 100 * kotlin.math.cos(Math.toRadians(rotation.toDouble() + 240)).toFloat(),
                    centerY + 100 * kotlin.math.sin(Math.toRadians(rotation.toDouble() + 240)).toFloat()),
                radius = 200f * scale
            )
        )
    }
}

@Composable
fun ModernFestivalTopBar(
    navController: NavController,
    selectedMonth: Month,
    selectedYear: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            FestivalColors.DeepPurple.copy(alpha = 0.9f),
                            FestivalColors.VibrantPink.copy(alpha = 0.9f)
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = { navController.popBackStack() },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(8.dp, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        "Festival Calendar",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    )
                    Text(
                        "${selectedMonth.name.lowercase().replaceFirstChar { it.uppercase() }} $selectedYear",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ModernDateSelector(
    selectedYear: Int,
    selectedMonth: Month,
    onYearChange: (Int) -> Unit,
    onMonthChange: (Month) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(15.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Year selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    onClick = { onYearChange(selectedYear - 1) },
                    shape = CircleShape,
                    color = FestivalColors.DeepPurple.copy(alpha = 0.1f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous Year",
                            tint = FestivalColors.DeepPurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    text = selectedYear.toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1F2937)
                )

                Surface(
                    onClick = { onYearChange(selectedYear + 1) },
                    shape = CircleShape,
                    color = FestivalColors.DeepPurple.copy(alpha = 0.1f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next Year",
                            tint = FestivalColors.DeepPurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Month selector
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(Month.values()) { month ->
                    val isSelected = month == selectedMonth
                    Surface(
                        onClick = { onMonthChange(month) },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) FestivalColors.VibrantPink else Color.Transparent,
                        border = if (!isSelected) BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)) else null,
                        modifier = Modifier
                            .shadow(if (isSelected) 8.dp else 0.dp, RoundedCornerShape(20.dp))
                            .animateContentSize()
                    ) {
                        Text(
                            text = month.name.take(3),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            color = if (isSelected) Color.White else Color.Gray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModernCalendarGrid(year: Int, month: Month, holidays: List<Holiday>) {
    val holidayDates = remember(holidays) {
        holidays.mapNotNull { holiday ->
            try {
                val date = LocalDate.parse(holiday.date.iso, DateTimeFormatter.ISO_LOCAL_DATE)
                date.dayOfMonth to holiday
            } catch (e: Exception) {
                null
            }
        }.toMap()
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(15.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Calendar View",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Days of week header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { dayName ->
                    Text(
                        text = dayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calendar grid
            val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
            val firstDayOfMonth = YearMonth.of(year, month).atDay(1).dayOfWeek.value % 7
            val calendarDays = (1..daysInMonth).toList()
            val totalCells = (daysInMonth + firstDayOfMonth + 6) / 7 * 7
            val paddedDays = List(firstDayOfMonth) { null } + calendarDays +
                    List(totalCells - daysInMonth - firstDayOfMonth) { null }

            paddedDays.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    week.forEach { day ->
                        val holiday = day?.let { holidayDates[it] }
                        val isToday = day != null && LocalDate.now().let {
                            it.year == year && it.month == month && it.dayOfMonth == day
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when {
                                        holiday != null -> FestivalColors.VibrantPink.copy(alpha = 0.15f)
                                        isToday -> FestivalColors.ElectricBlue.copy(alpha = 0.2f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable(enabled = holiday != null) {
                                    // Handle click on festival day
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (day != null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = day.toString(),
                                        color = when {
                                            holiday != null -> FestivalColors.VibrantPink
                                            isToday -> FestivalColors.ElectricBlue
                                            else -> Color.Black
                                        },
                                        fontWeight = when {
                                            holiday != null || isToday -> FontWeight.Bold
                                            else -> FontWeight.Normal
                                        },
                                        fontSize = 14.sp
                                    )
                                    if (holiday != null) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .background(
                                                    FestivalColors.VibrantPink,
                                                    CircleShape
                                                )
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModernHolidayList(title: String, holidays: List<Holiday>, accentColor: Color) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(15.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1F2937)
                )
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        holidays.size.toString(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            holidays.forEach { holiday ->
                ModernHolidayCard(holiday = holiday, accentColor = accentColor)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ModernHolidayCard(holiday: Holiday, accentColor: Color) {
    val context = LocalContext.current
    val parsedDate = remember {
        try {
            LocalDate.parse(holiday.date.iso, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            null
        }
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.1f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
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
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (parsedDate != null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = accentColor.copy(alpha = 0.1f),
                    modifier = Modifier.size(60.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            parsedDate.dayOfMonth.toString(),
                            color = accentColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                        Text(
                            parsedDate.month.name.take(3).uppercase(),
                            color = accentColor.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    holiday.name,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    fontSize = 16.sp
                )
                Text(
                    holiday.type?.toString() ?: "Cultural Festival",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            Surface(
                shape = CircleShape,
                color = accentColor,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add to Calendar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernLoadingState() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(15.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = FestivalColors.VibrantPink,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
            Text(
                "Loading festivals...",
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModernEmptyState() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(15.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = FestivalColors.SunsetOrange.copy(alpha = 0.1f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.event),
                        contentDescription = "",
                        tint = FestivalColors.SunsetOrange,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Text(
                "No Festivals Found",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Text(
                "There are no recorded festivals for the selected month. Try selecting another month to explore more celebrations.",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ModernErrorState(message: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(15.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = FestivalColors.CoralRed.copy(alpha = 0.1f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "",
                        tint = FestivalColors.CoralRed,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Text(
                "Oops! Something went wrong",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FestivalColors.CoralRed
            )

            Text(
                text = message,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )

            Button(
                onClick = { /* Retry logic can be added here */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FestivalColors.CoralRed
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Try Again",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}