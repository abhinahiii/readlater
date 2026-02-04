package com.readlater.ui.screens

import android.content.Context
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.readlater.data.AuthState
import com.readlater.data.SavedEvent
import com.readlater.ui.components.ArchiveConfirmationDialog
import com.readlater.ui.components.DeleteConfirmationDialog
import com.readlater.ui.components.MetroButton
import com.readlater.ui.components.MetroDateTimePicker
import com.readlater.ui.theme.DarkThemeColors
import com.readlater.ui.theme.EditorialSpacing
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    authState: AuthState,
    userName: String,
    upcomingEvents: List<SavedEvent>,
    completedEvents: List<SavedEvent>,
    archivedEvents: List<SavedEvent>,
    summaryMessage: String,
    isSyncing: Boolean,
    useDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onArchiveEvent: (SavedEvent) -> Unit,
    onRescheduleEvent: (SavedEvent) -> Unit,
    onMarkDoneEvent: (SavedEvent) -> Unit,
    onUndoCompleteEvent: (SavedEvent) -> Unit,
    onScheduleAgainEvent: (SavedEvent) -> Unit,
    onRestoreEvent: (SavedEvent) -> Unit,
    onDeleteForeverEvent: (SavedEvent) -> Unit,
    onManualAddEvent: suspend (String, String, LocalDate, LocalTime, Int) -> Result<Unit>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    when (authState) {
        is AuthState.Loading -> LoadingScreen(modifier)
        is AuthState.NotAuthenticated -> NotAuthenticatedScreen(onConnectClick, modifier)
        is AuthState.Authenticated -> {
            AuthenticatedScreen(
                userName = userName,
                upcomingEvents = upcomingEvents,
                completedEvents = completedEvents,
                archivedEvents = archivedEvents,
                summaryMessage = summaryMessage,
                isSyncing = isSyncing,
                onArchiveEvent = onArchiveEvent,
                onRescheduleEvent = onRescheduleEvent,
                onMarkDoneEvent = onMarkDoneEvent,
                onUndoCompleteEvent = onUndoCompleteEvent,
                onScheduleAgainEvent = onScheduleAgainEvent,
                onRestoreEvent = onRestoreEvent,
                onDeleteForeverEvent = onDeleteForeverEvent,
                onDisconnectClick = onDisconnectClick,
                onManualAddEvent = onManualAddEvent,
                onUrlClick = { url ->
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                },
                modifier = modifier
            )
        }
        is AuthState.Error -> ErrorScreen(authState.message, onConnectClick, modifier)
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkThemeColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "readlater",
                style = MaterialTheme.typography.displayLarge,
                color = DarkThemeColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(EditorialSpacing.m))
            Text(
                text = "loading...",
                style = MaterialTheme.typography.labelMedium,
                color = DarkThemeColors.TextSecondary
            )
        }
    }
}

@Composable
private fun NotAuthenticatedScreen(
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkThemeColors.Background)
            .padding(EditorialSpacing.m)
    ) {
        Text(
            text = "readlater",
            style = MaterialTheme.typography.titleLarge,
            color = DarkThemeColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(EditorialSpacing.xl))

        Text(
            text = "Save what you want to read.",
            style = MaterialTheme.typography.displayLarge,
            color = DarkThemeColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(EditorialSpacing.s))

        Text(
            text = "We'll schedule it to your calendar so you never miss it.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkThemeColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(EditorialSpacing.xl))

        MetroButton(
            text = "connect google calendar",
            onClick = onConnectClick
        )

        Spacer(modifier = Modifier.height(EditorialSpacing.l))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkThemeColors.Border)
                .padding(EditorialSpacing.m)
        ) {
            Text(
                text = "share any article or video to readlater, and we'll add it to your calendar so you remember to check it out.",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkThemeColors.TextSecondary,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3f
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkThemeColors.Background)
            .padding(EditorialSpacing.m)
    ) {
        Text(
            text = "readlater",
            style = MaterialTheme.typography.titleLarge,
            color = DarkThemeColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(EditorialSpacing.xl))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkThemeColors.Border)
                .padding(EditorialSpacing.m)
        ) {
            Text(
                text = "something went wrong. ${message.lowercase()}",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkThemeColors.TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(EditorialSpacing.l))

        MetroButton(
            text = "try again",
            onClick = onConnectClick
        )
    }
}

private data class DayBounds(val start: Long, val end: Long)

private fun getDayBounds(offsetDays: Int): DayBounds {
    val day = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, offsetDays)
    }
    val next = (day.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
    return DayBounds(day.timeInMillis, next.timeInMillis)
}

private fun formatTimeShort(millis: Long): String {
    val dateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
    return dateTime.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
}

private fun formatDurationCompact(minutes: Int): String {
    return when {
        minutes < 60 -> "${minutes}m"
        minutes % 60 == 0 -> "${minutes / 60}h"
        else -> "${minutes / 60}h ${minutes % 60}m"
    }
}

private fun formatSource(url: String): String {
    val host = runCatching { Uri.parse(url).host }.getOrNull().orEmpty()
    val cleaned = host.removePrefix("www.")
    return if (cleaned.isBlank()) "link" else cleaned
}

private fun eventLocalDate(event: SavedEvent): LocalDate {
    return Instant.ofEpochMilli(event.scheduledDateTime)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

private fun getWeekendDates(reference: LocalDate): Set<LocalDate> {
    val saturday = reference.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
    return setOf(saturday, saturday.plusDays(1))
}

enum class QuickScheduleOption {
    IN_10_MIN, IN_1_HOUR, TONIGHT, TOMORROW, WEEKEND
}

private fun calculateQuickScheduleTime(option: QuickScheduleOption): LocalDateTime {
    val now = LocalDateTime.now()
    return when (option) {
        QuickScheduleOption.IN_10_MIN -> now.plusMinutes(10)
        QuickScheduleOption.IN_1_HOUR -> now.plusHours(1)
        QuickScheduleOption.TONIGHT -> {
            val tonight = now.toLocalDate().atTime(20, 0)
            if (now.isAfter(tonight)) tonight.plusDays(1) else tonight
        }
        QuickScheduleOption.TOMORROW -> now.toLocalDate().plusDays(1).atTime(9, 0)
        QuickScheduleOption.WEEKEND -> {
            val saturday = now.toLocalDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
            saturday.atTime(10, 0)
        }
    }
}

private enum class QueueItemStatus {
    OVERDUE, NEXT_UP, PENDING, TOMORROW
}

private fun getItemStatus(event: SavedEvent, now: Long, nextUpEventId: String?, todayBounds: DayBounds, tomorrowBounds: DayBounds): QueueItemStatus {
    return when {
        event.scheduledDateTime < now -> QueueItemStatus.OVERDUE
        event.googleEventId == nextUpEventId -> QueueItemStatus.NEXT_UP
        event.scheduledDateTime in tomorrowBounds.start until tomorrowBounds.end -> QueueItemStatus.TOMORROW
        else -> QueueItemStatus.PENDING
    }
}

@Composable
private fun AuthenticatedScreen(
    userName: String,
    upcomingEvents: List<SavedEvent>,
    completedEvents: List<SavedEvent>,
    archivedEvents: List<SavedEvent>,
    summaryMessage: String,
    isSyncing: Boolean,
    onArchiveEvent: (SavedEvent) -> Unit,
    onRescheduleEvent: (SavedEvent) -> Unit,
    onMarkDoneEvent: (SavedEvent) -> Unit,
    onUndoCompleteEvent: (SavedEvent) -> Unit,
    onScheduleAgainEvent: (SavedEvent) -> Unit,
    onRestoreEvent: (SavedEvent) -> Unit,
    onDeleteForeverEvent: (SavedEvent) -> Unit,
    onDisconnectClick: () -> Unit,
    onManualAddEvent: suspend (String, String, LocalDate, LocalTime, Int) -> Result<Unit>,
    onUrlClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showArchiveConfirmation by remember { mutableStateOf(false) }
    var eventToArchive by remember { mutableStateOf<SavedEvent?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<SavedEvent?>(null) }
    var showDisconnectConfirmation by remember { mutableStateOf(false) }
    var showManualAdd by remember { mutableStateOf(false) }
    var isManualSaving by remember { mutableStateOf(false) }
    var showArchivedView by remember { mutableStateOf(false) }

    var quickScheduleUrl by remember { mutableStateOf("") }
    var isQuickSaving by remember { mutableStateOf(false) }

    val now = System.currentTimeMillis()
    val todayBounds = getDayBounds(0)
    val tomorrowBounds = getDayBounds(1)

    val sortedUpcoming = remember(upcomingEvents) {
        upcomingEvents.sortedBy { it.scheduledDateTime }
    }

    val todayCount = remember(upcomingEvents) {
        upcomingEvents.count { it.scheduledDateTime in todayBounds.start until todayBounds.end }
    }

    val nextUp = sortedUpcoming.firstOrNull { it.scheduledDateTime >= now }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkThemeColors.Background)
    ) {
        DarkHeaderBar(
            isSyncing = isSyncing,
            onArchiveClick = { showArchivedView = !showArchivedView },
            onSignOutClick = { showDisconnectConfirmation = true }
        )

        if (showArchivedView) {
            ArchivedView(
                archivedEvents = archivedEvents,
                onRestoreEvent = onRestoreEvent,
                onDeleteForeverEvent = { event ->
                    eventToDelete = event
                    showDeleteConfirmation = true
                },
                onUrlClick = onUrlClick,
                onBackClick = { showArchivedView = false },
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            ) {
                item {
                    HeroSection(
                        todayCount = todayCount,
                        nextUpTime = nextUp?.let { formatTimeShort(it.scheduledDateTime) }
                    )
                }

                item {
                    QuickActionsSection(
                        urlValue = quickScheduleUrl,
                        onUrlChange = { quickScheduleUrl = it },
                        isLoading = isQuickSaving,
                        onQuickSchedule = { option ->
                            if (quickScheduleUrl.isNotBlank()) {
                                scope.launch {
                                    isQuickSaving = true
                                    val dateTime = calculateQuickScheduleTime(option)
                                    val result = onManualAddEvent(
                                        quickScheduleUrl.trim(),
                                        "",
                                        dateTime.toLocalDate(),
                                        dateTime.toLocalTime(),
                                        30
                                    )
                                    result.onSuccess {
                                        Toast.makeText(context, "scheduled", Toast.LENGTH_SHORT).show()
                                        quickScheduleUrl = ""
                                    }.onFailure { error ->
                                        Toast.makeText(
                                            context,
                                            "failed: ${error.message ?: "unknown error"}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    isQuickSaving = false
                                }
                            }
                        },
                        onCustomSchedule = { showManualAdd = true }
                    )
                }

                item {
                    QueueSectionHeader(count = sortedUpcoming.size)
                }

                if (sortedUpcoming.isEmpty()) {
                    item {
                        EmptyQueueMessage()
                    }
                } else {
                    items(sortedUpcoming, key = { it.googleEventId }) { event ->
                        val status = getItemStatus(event, now, nextUp?.googleEventId, todayBounds, tomorrowBounds)
                        QueueListItem(
                            event = event,
                            status = status,
                            onUrlClick = { onUrlClick(event.url) },
                            onDoneClick = { onMarkDoneEvent(event) },
                            onRescheduleClick = { onRescheduleEvent(event) },
                            onArchiveClick = {
                                eventToArchive = event
                                showArchiveConfirmation = true
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        DarkBottomNavigation(
            showingArchive = showArchivedView,
            onDashboardClick = { showArchivedView = false },
            onArchiveClick = { showArchivedView = true },
            onSignOutClick = { showDisconnectConfirmation = true }
        )
    }

    if (showManualAdd) {
        ManualAddDialog(
            isSaving = isManualSaving,
            onDismiss = { if (!isManualSaving) showManualAdd = false },
            onSave = { url, title, date, time, duration ->
                scope.launch {
                    isManualSaving = true
                    val result = onManualAddEvent(url, title, date, time, duration)
                    result.onSuccess {
                        Toast.makeText(context, "event created", Toast.LENGTH_SHORT).show()
                        showManualAdd = false
                    }.onFailure { error ->
                        Toast.makeText(
                            context,
                            "failed: ${error.message ?: "unknown error"}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    isManualSaving = false
                }
            }
        )
    }

    if (showArchiveConfirmation && eventToArchive != null) {
        ArchiveConfirmationDialog(
            eventTitle = eventToArchive!!.title,
            onDismiss = {
                showArchiveConfirmation = false
                eventToArchive = null
            },
            onConfirm = {
                onArchiveEvent(eventToArchive!!)
                showArchiveConfirmation = false
                eventToArchive = null
            }
        )
    }

    if (showDeleteConfirmation && eventToDelete != null) {
        DeleteConfirmationDialog(
            eventTitle = eventToDelete!!.title,
            onDismiss = {
                showDeleteConfirmation = false
                eventToDelete = null
            },
            onConfirm = {
                onDeleteForeverEvent(eventToDelete!!)
                showDeleteConfirmation = false
                eventToDelete = null
            }
        )
    }

    if (showDisconnectConfirmation) {
        DisconnectConfirmationDialog(
            onDismiss = { showDisconnectConfirmation = false },
            onConfirm = {
                showDisconnectConfirmation = false
                onDisconnectClick()
            }
        )
    }
}

@Composable
private fun DarkHeaderBar(
    isSyncing: Boolean,
    onArchiveClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "readlater",
                style = MaterialTheme.typography.titleLarge,
                color = DarkThemeColors.TextPrimary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(DarkThemeColors.SyncGreen, CircleShape)
                )
                Text(
                    text = if (isSyncing) "Syncing..." else "Sync Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkThemeColors.SyncGreen
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkThemeColors.Border)
        )
    }
}

@Composable
private fun HeroSection(
    todayCount: Int,
    nextUpTime: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
    ) {
        WireframeCurves(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.TopEnd)
        )

        Column {
            Text(
                text = "$todayCount item${if (todayCount == 1) "" else "s"}\nscheduled for today.",
                style = MaterialTheme.typography.displayLarge,
                color = DarkThemeColors.TextPrimary
            )

            if (nextUpTime != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Next up: $nextUpTime",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkThemeColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun WireframeCurves(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val strokeWidth = 1.dp.toPx()
        val curveColor = DarkThemeColors.Border

        val path1 = Path().apply {
            moveTo(width * 0.3f, 0f)
            quadraticBezierTo(
                width * 0.5f, height * 0.4f,
                width, height * 0.3f
            )
        }

        val path2 = Path().apply {
            moveTo(width * 0.5f, 0f)
            quadraticBezierTo(
                width * 0.7f, height * 0.5f,
                width, height * 0.6f
            )
        }

        val path3 = Path().apply {
            moveTo(width * 0.7f, 0f)
            quadraticBezierTo(
                width * 0.85f, height * 0.6f,
                width, height * 0.9f
            )
        }

        drawPath(path1, curveColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        drawPath(path2, curveColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        drawPath(path3, curveColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
private fun QuickActionsSection(
    urlValue: String,
    onUrlChange: (String) -> Unit,
    isLoading: Boolean,
    onQuickSchedule: (QuickScheduleOption) -> Unit,
    onCustomSchedule: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkThemeColors.Border)
                .padding(16.dp)
        ) {
            BasicTextField(
                value = urlValue,
                onValueChange = onUrlChange,
                enabled = !isLoading,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = DarkThemeColors.TextPrimary),
                cursorBrush = SolidColor(DarkThemeColors.TextPrimary),
                decorationBox = { innerTextField ->
                    if (urlValue.isBlank()) {
                        Text(
                            text = "Paste URL to schedule...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkThemeColors.TextSecondary
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Quick Schedule",
            style = MaterialTheme.typography.labelSmall,
            color = DarkThemeColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PillButton(text = "In 10m", enabled = urlValue.isNotBlank() && !isLoading) {
                onQuickSchedule(QuickScheduleOption.IN_10_MIN)
            }
            PillButton(text = "In 1h", enabled = urlValue.isNotBlank() && !isLoading) {
                onQuickSchedule(QuickScheduleOption.IN_1_HOUR)
            }
            PillButton(text = "Tonight", enabled = urlValue.isNotBlank() && !isLoading) {
                onQuickSchedule(QuickScheduleOption.TONIGHT)
            }
            PillButton(text = "Tomorrow", enabled = urlValue.isNotBlank() && !isLoading) {
                onQuickSchedule(QuickScheduleOption.TOMORROW)
            }
            PillButton(text = "Weekend", enabled = urlValue.isNotBlank() && !isLoading) {
                onQuickSchedule(QuickScheduleOption.WEEKEND)
            }
            PillButton(text = "Custom...", enabled = !isLoading) {
                onCustomSchedule()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkThemeColors.Border)
        )
    }
}

@Composable
private fun PillButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val borderColor = if (enabled) DarkThemeColors.Border else DarkThemeColors.Border.copy(alpha = 0.5f)
    val textColor = if (enabled) DarkThemeColors.TextPrimary else DarkThemeColors.TextSecondary

    Box(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}

@Composable
private fun QueueSectionHeader(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Queue",
            style = MaterialTheme.typography.titleMedium,
            color = DarkThemeColors.TextPrimary
        )
        Text(
            text = "$count items",
            style = MaterialTheme.typography.labelSmall,
            color = DarkThemeColors.TextSecondary
        )
    }
}

@Composable
private fun EmptyQueueMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No items scheduled",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkThemeColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Paste a URL above to get started",
            style = MaterialTheme.typography.labelSmall,
            color = DarkThemeColors.TextSecondary
        )
    }
}

@Composable
private fun QueueListItem(
    event: SavedEvent,
    status: QueueItemStatus,
    onUrlClick: () -> Unit,
    onDoneClick: () -> Unit,
    onRescheduleClick: () -> Unit,
    onArchiveClick: () -> Unit
) {
    val isHighlighted = status == QueueItemStatus.NEXT_UP
    val backgroundColor = if (isHighlighted) DarkThemeColors.HighlightRow else Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onUrlClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title.ifBlank { "Untitled" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkThemeColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusLabel(status = status)
                    Text(
                        text = "· ${formatDurationCompact(event.durationMinutes)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkThemeColors.TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "done",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkThemeColors.TextSecondary,
                        modifier = Modifier.clickable { onDoneClick() }
                    )
                    Text(
                        text = "reschedule",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkThemeColors.TextSecondary,
                        modifier = Modifier.clickable { onRescheduleClick() }
                    )
                    Text(
                        text = "archive",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkThemeColors.TextSecondary,
                        modifier = Modifier.clickable { onArchiveClick() }
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = formatTimeShort(event.scheduledDateTime),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkThemeColors.TextPrimary
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkThemeColors.Border)
        )
    }
}

@Composable
private fun StatusLabel(status: QueueItemStatus) {
    val (text, color) = when (status) {
        QueueItemStatus.OVERDUE -> "Overdue" to DarkThemeColors.OverdueRed
        QueueItemStatus.NEXT_UP -> "Next Up" to DarkThemeColors.SyncGreen
        QueueItemStatus.PENDING -> "Pending" to DarkThemeColors.TextSecondary
        QueueItemStatus.TOMORROW -> "Tomorrow" to DarkThemeColors.TextSecondary
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color
    )
}

@Composable
private fun ArchivedView(
    archivedEvents: List<SavedEvent>,
    onRestoreEvent: (SavedEvent) -> Unit,
    onDeleteForeverEvent: (SavedEvent) -> Unit,
    onUrlClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Archive",
                style = MaterialTheme.typography.titleMedium,
                color = DarkThemeColors.TextPrimary
            )
            Text(
                text = "${archivedEvents.size} items",
                style = MaterialTheme.typography.labelSmall,
                color = DarkThemeColors.TextSecondary
            )
        }

        if (archivedEvents.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Archive is empty",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkThemeColors.TextSecondary
                )
            }
        } else {
            LazyColumn {
                items(archivedEvents, key = { it.googleEventId }) { event ->
                    ArchivedListItem(
                        event = event,
                        onUrlClick = { onUrlClick(event.url) },
                        onRestoreClick = { onRestoreEvent(event) },
                        onDeleteClick = { onDeleteForeverEvent(event) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ArchivedListItem(
    event: SavedEvent,
    onUrlClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUrlClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title.ifBlank { "Untitled" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkThemeColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "restore",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkThemeColors.TextSecondary,
                        modifier = Modifier.clickable { onRestoreClick() }
                    )
                    Text(
                        text = "delete",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkThemeColors.OverdueRed,
                        modifier = Modifier.clickable { onDeleteClick() }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkThemeColors.Border)
        )
    }
}

@Composable
private fun DarkBottomNavigation(
    showingArchive: Boolean,
    onDashboardClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkThemeColors.Border)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkThemeColors.Background)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.labelMedium,
                color = if (!showingArchive) DarkThemeColors.TextPrimary else DarkThemeColors.TextSecondary,
                modifier = Modifier.clickable { onDashboardClick() }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Archive,
                    contentDescription = "Archive",
                    tint = if (showingArchive) DarkThemeColors.TextPrimary else DarkThemeColors.TextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onArchiveClick() }
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = "Sign out",
                    tint = DarkThemeColors.TextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onSignOutClick() }
                )
            }
        }
    }
}

@Composable
private fun ManualAddDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, LocalDate, LocalTime, Int) -> Unit
) {
    val initialDate = remember { LocalDate.now() }
    val initialTime = remember {
        LocalTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0)
    }

    var url by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var selectedTime by remember { mutableStateOf(initialTime) }
    var selectedDuration by remember { mutableStateOf(30) }

    fun isTimeInPast(): Boolean {
        val now = LocalTime.now()
        return selectedDate == LocalDate.now() && selectedTime.isBefore(now)
    }

    val durations = listOf(15, 30, 45, 60, 90)

    Dialog(onDismissRequest = { if (!isSaving) onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkThemeColors.DialogBackground)
                .border(1.dp, DarkThemeColors.Border)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Add Link",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkThemeColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Schedule a new read",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkThemeColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                DarkTextField(
                    label = "Link",
                    value = url,
                    placeholder = "https://",
                    onValueChange = { url = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                DarkTextField(
                    label = "Title",
                    value = title,
                    placeholder = "Add a title",
                    onValueChange = { title = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                MetroDateTimePicker(
                    selectedDate = selectedDate,
                    selectedTime = selectedTime,
                    onDateTimeSelected = { date, time ->
                        selectedDate = date
                        selectedTime = time
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Duration",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkThemeColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    durations.forEach { minutes ->
                        DurationChip(
                            text = "${minutes} min",
                            selected = selectedDuration == minutes,
                            onClick = { selectedDuration = minutes }
                        )
                    }
                }

                if (isTimeInPast()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Selected time is in the past",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkThemeColors.OverdueRed
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        MetroButton(
                            text = "cancel",
                            onClick = onDismiss,
                            filled = false,
                            enabled = !isSaving
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MetroButton(
                            text = if (isSaving) "saving..." else "save",
                            onClick = {
                                onSave(url.trim(), title.trim(), selectedDate, selectedTime, selectedDuration)
                            },
                            enabled = !isSaving && url.isNotBlank() && title.isNotBlank() && !isTimeInPast()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DarkTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = DarkThemeColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkThemeColors.Border)
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = DarkThemeColors.TextPrimary),
                cursorBrush = SolidColor(DarkThemeColors.TextPrimary),
                decorationBox = { innerTextField ->
                    if (value.isBlank()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkThemeColors.TextSecondary
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DurationChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) DarkThemeColors.TextPrimary else Color.Transparent
    val content = if (selected) DarkThemeColors.Background else DarkThemeColors.TextPrimary

    Box(
        modifier = Modifier
            .border(1.dp, DarkThemeColors.Border, RoundedCornerShape(20.dp))
            .background(bg, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = content
        )
    }
}

@Composable
private fun DisconnectConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkThemeColors.DialogBackground)
                .border(1.dp, DarkThemeColors.Border)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Sign out?",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkThemeColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You'll need to connect your Google Calendar again to use readlater.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkThemeColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "cancel",
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkThemeColors.TextSecondary,
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "sign out",
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkThemeColors.OverdueRed,
                        modifier = Modifier
                            .clickable { onConfirm() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
