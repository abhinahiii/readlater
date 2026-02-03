package com.readlater.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.readlater.data.AuthState
import com.readlater.data.SavedEvent
import com.readlater.ui.components.ArchiveConfirmationDialog
import com.readlater.ui.components.ArchivedEventCard
import com.readlater.ui.components.CompletedEventCard
import com.readlater.ui.components.DeleteConfirmationDialog
import com.readlater.ui.components.EventCard
import com.readlater.ui.components.MetroButton
import kotlinx.coroutines.launch
import java.util.Calendar

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
                isSyncing = isSyncing,
                onArchiveEvent = onArchiveEvent,
                onRescheduleEvent = onRescheduleEvent,
                onMarkDoneEvent = onMarkDoneEvent,
                onUndoCompleteEvent = onUndoCompleteEvent,
                onScheduleAgainEvent = onScheduleAgainEvent,
                onRestoreEvent = onRestoreEvent,
                onDeleteForeverEvent = onDeleteForeverEvent,
                onDisconnectClick = onDisconnectClick,
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
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "readlater",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "loading...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "readlater",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "never forget what you want to read",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        MetroButton(
            text = "connect google calendar",
            onClick = onConnectClick
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .padding(20.dp)
        ) {
            Text(
                text = "share any article or video to readlater, and we'll add it to your calendar so you remember to check it out.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4
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
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "readlater",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .padding(16.dp)
        ) {
            Text(
                text = "something went wrong. ${message.lowercase()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        MetroButton(
            text = "try again",
            onClick = onConnectClick
        )
    }
}

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "good morning"
        hour < 17 -> "good afternoon"
        else -> "good evening"
    }
}

private fun getSummaryText(upcomingEvents: List<SavedEvent>): String {
    val now = System.currentTimeMillis()
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val tomorrow = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }

    val todayEvents = upcomingEvents.count { event ->
        event.scheduledDateTime >= today.timeInMillis && event.scheduledDateTime < tomorrow.timeInMillis
    }
    val overdueEvents = upcomingEvents.count { it.scheduledDateTime < now }

    return when {
        overdueEvents > 0 && todayEvents > 0 -> "you have $todayEvents scheduled today, $overdueEvents overdue"
        overdueEvents > 0 -> "you have $overdueEvents overdue item${if (overdueEvents > 1) "s" else ""}"
        todayEvents > 0 -> "you have $todayEvents scheduled for today"
        upcomingEvents.isNotEmpty() -> "you have ${upcomingEvents.size} item${if (upcomingEvents.size > 1) "s" else ""} coming up"
        else -> "nothing scheduled yet"
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AuthenticatedScreen(
    userName: String,
    upcomingEvents: List<SavedEvent>,
    completedEvents: List<SavedEvent>,
    archivedEvents: List<SavedEvent>,
    isSyncing: Boolean,
    onArchiveEvent: (SavedEvent) -> Unit,
    onRescheduleEvent: (SavedEvent) -> Unit,
    onMarkDoneEvent: (SavedEvent) -> Unit,
    onUndoCompleteEvent: (SavedEvent) -> Unit,
    onScheduleAgainEvent: (SavedEvent) -> Unit,
    onRestoreEvent: (SavedEvent) -> Unit,
    onDeleteForeverEvent: (SavedEvent) -> Unit,
    onDisconnectClick: () -> Unit,
    onUrlClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    
    var showArchiveConfirmation by remember { mutableStateOf(false) }
    var eventToArchive by remember { mutableStateOf<SavedEvent?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<SavedEvent?>(null) }
    var showDisconnectConfirmation by remember { mutableStateOf(false) }

    val sortedUpcoming = remember(upcomingEvents) {
        val now = System.currentTimeMillis()
        upcomingEvents.sortedWith(compareBy({ it.scheduledDateTime >= now }, { it.scheduledDateTime }))
    }

    val greeting = remember { getGreeting() }
    val summaryText = remember(upcomingEvents) { getSummaryText(upcomingEvents) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp, bottom = 8.dp)
        ) {
            // Greeting row with sign out aligned to the right
            val greetingText = if (userName.isNotEmpty()) {
                "$greeting $userName,"
            } else {
                "$greeting,"
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = greetingText,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSyncing) {
                        Text(
                            text = "syncing",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "sign out",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable { showDisconnectConfirmation = true }
                            .padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = summaryText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp)
        ) {
            TabItem(
                text = "upcoming",
                count = upcomingEvents.size,
                selected = pagerState.currentPage == 0,
                onClick = { scope.launch { pagerState.animateScrollToPage(0) } }
            )
            Spacer(modifier = Modifier.width(20.dp))
            TabItem(
                text = "done",
                count = completedEvents.size,
                selected = pagerState.currentPage == 1,
                onClick = { scope.launch { pagerState.animateScrollToPage(1) } }
            )
            Spacer(modifier = Modifier.width(20.dp))
            TabItem(
                text = "archived",
                count = archivedEvents.size,
                selected = pagerState.currentPage == 2,
                onClick = { scope.launch { pagerState.animateScrollToPage(2) } }
            )
        }

        // Swipeable Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                when (page) {
                    0 -> {
                        if (sortedUpcoming.isEmpty()) {
                            EmptyState(
                                title = "nothing here yet",
                                description = "share an article or video to readlater to get started"
                            )
                        } else {
                            sortedUpcoming.forEach { event ->
                                EventCard(
                                    event = event,
                                    onArchiveClick = {
                                        eventToArchive = event
                                        showArchiveConfirmation = true
                                    },
                                    onRescheduleClick = { onRescheduleEvent(event) },
                                    onDoneClick = { onMarkDoneEvent(event) },
                                    onUrlClick = { onUrlClick(event.url) }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                    1 -> {
                        if (completedEvents.isEmpty()) {
                            EmptyState(
                                title = "nothing completed yet",
                                description = "items you mark as done will show up here"
                            )
                        } else {
                            completedEvents.forEach { event ->
                                CompletedEventCard(
                                    event = event,
                                    onUndoClick = { onUndoCompleteEvent(event) },
                                    onScheduleAgainClick = { onScheduleAgainEvent(event) },
                                    onUrlClick = { onUrlClick(event.url) }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                    2 -> {
                        if (archivedEvents.isEmpty()) {
                            EmptyState(
                                title = "archive is empty",
                                description = "archived items can be restored or permanently deleted"
                            )
                        } else {
                            archivedEvents.forEach { event ->
                                ArchivedEventCard(
                                    event = event,
                                    onRestoreClick = { onRestoreEvent(event) },
                                    onDeleteForeverClick = {
                                        eventToDelete = event
                                        showDeleteConfirmation = true
                                    },
                                    onUrlClick = { onUrlClick(event.url) }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Footer - fun stat
        val totalMinutesCompleted = completedEvents.sumOf { it.durationMinutes }
        val totalMinutesScheduled = upcomingEvents.sumOf { it.durationMinutes }
        
        val funStat = when {
            totalMinutesCompleted >= 60 -> {
                val hours = totalMinutesCompleted / 60
                val mins = totalMinutesCompleted % 60
                if (mins > 0) "you've cleared ${hours}h ${mins}m of content" 
                else "you've cleared ${hours}h of content"
            }
            totalMinutesCompleted > 0 -> "you've cleared ${totalMinutesCompleted}m of content"
            totalMinutesScheduled >= 60 -> {
                val hours = totalMinutesScheduled / 60
                "${hours}h+ of content waiting for you"
            }
            totalMinutesScheduled > 0 -> "${totalMinutesScheduled}m of content waiting for you"
            else -> null
        }
        
        if (funStat != null) {
            Text(
                text = funStat,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                textAlign = TextAlign.Center
            )
        }
    }

    // Archive confirmation
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

    // Delete confirmation
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

    // Disconnect confirmation
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
private fun DisconnectConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .clickable { /* consume click */ }
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "sign out?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "you'll need to connect your google calendar again to use readlater.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "cancel",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "sign out",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .clickable { onConfirm() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TabItem(
    text: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (selected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.onBackground)
            )
        }
    }
}

@Composable
private fun EmptyState(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

