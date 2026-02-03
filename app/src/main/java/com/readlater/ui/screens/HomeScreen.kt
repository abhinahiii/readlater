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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.readlater.data.AuthState
import com.readlater.data.SavedEvent
import com.readlater.ui.components.ArchiveConfirmationDialog
import com.readlater.ui.components.ArchivedEventCard
import com.readlater.ui.components.CompletedEventCard
import com.readlater.ui.components.DeleteConfirmationDialog
import com.readlater.ui.components.EventCard
import com.readlater.ui.components.MetroButton
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    authState: AuthState,
    upcomingEvents: List<SavedEvent>,
    completedEvents: List<SavedEvent>,
    archivedEvents: List<SavedEvent>,
    summaryMessage: String,
    isSyncing: Boolean,
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
        is AuthState.Loading -> {
            LoadingScreen(modifier = modifier)
        }

        is AuthState.NotAuthenticated -> {
            NotAuthenticatedScreen(
                onConnectClick = onConnectClick,
                modifier = modifier
            )
        }

        is AuthState.Authenticated -> {
            AuthenticatedHomeScreen(
                account = authState.account,
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
                onUrlClick = { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
                modifier = modifier
            )
        }

        is AuthState.Error -> {
            ErrorScreen(
                message = authState.message,
                onConnectClick = onConnectClick,
                modifier = modifier
            )
        }
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "readlater",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "loading...",
                style = MaterialTheme.typography.labelLarge,
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "schedule time for content you discover",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(64.dp))

        MetroButton(
            text = "connect google calendar",
            onClick = onConnectClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .padding(16.dp)
        ) {
            Text(
                text = "after connecting, share any link to readlater to schedule reading time on your calendar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

        Spacer(modifier = Modifier.height(64.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .padding(16.dp)
        ) {
            Text(
                text = "error: ${message.lowercase()}",
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

@Composable
private fun AuthenticatedHomeScreen(
    account: GoogleSignInAccount,
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
    onUrlClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBottomTab by remember { mutableIntStateOf(0) }
    var showArchiveScreen by remember { mutableStateOf(false) }

    // Archive confirmation dialog state
    var showArchiveConfirmation by remember { mutableStateOf(false) }
    var eventToArchive by remember { mutableStateOf<SavedEvent?>(null) }

    // Delete confirmation dialog state
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<SavedEvent?>(null) }

    if (showArchiveScreen) {
        ArchiveScreen(
            archivedEvents = archivedEvents,
            onBackClick = { showArchiveScreen = false },
            onRestoreClick = onRestoreEvent,
            onDeleteForeverClick = { event ->
                eventToDelete = event
                showDeleteConfirmation = true
            },
            onUrlClick = onUrlClick
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                BottomNavigationBar(
                    selectedTab = selectedBottomTab,
                    onTabSelected = { selectedBottomTab = it }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Header
                HeaderSection(
                    account = account,
                    summaryMessage = summaryMessage,
                    isSyncing = isSyncing,
                    onArchiveClick = { showArchiveScreen = true }
                )

                // Main content based on selected bottom tab
                when (selectedBottomTab) {
                    0 -> EventsContent(
                        upcomingEvents = upcomingEvents,
                        completedEvents = completedEvents,
                        onArchiveEvent = { event ->
                            eventToArchive = event
                            showArchiveConfirmation = true
                        },
                        onRescheduleEvent = onRescheduleEvent,
                        onMarkDoneEvent = onMarkDoneEvent,
                        onUndoCompleteEvent = onUndoCompleteEvent,
                        onScheduleAgainEvent = onScheduleAgainEvent,
                        onUrlClick = onUrlClick
                    )
                    1 -> AnalyticsContent()
                }
            }
        }
    }

    // Archive confirmation dialog
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

    // Delete confirmation dialog
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
}

@Composable
private fun HeaderSection(
    account: GoogleSignInAccount,
    summaryMessage: String,
    isSyncing: Boolean,
    onArchiveClick: () -> Unit
) {
    val firstName = account.givenName ?: account.displayName?.split(" ")?.firstOrNull() ?: "there"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Left side: Greeting and summary
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "hey ${firstName.lowercase()}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = summaryMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Right side: Archive button and connection status
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "archive",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onArchiveClick() }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isSyncing) "syncing..." else "connected",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSyncing) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ArchiveScreen(
    archivedEvents: List<SavedEvent>,
    onBackClick: () -> Unit,
    onRestoreClick: (SavedEvent) -> Unit,
    onDeleteForeverClick: (SavedEvent) -> Unit,
    onUrlClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "archive",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            if (archivedEvents.isEmpty()) {
                Spacer(modifier = Modifier.height(48.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "no archived events",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "events you archive will appear here.\nyou can restore them anytime.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                archivedEvents.forEach { event ->
                    ArchivedEventCard(
                        event = event,
                        onRestoreClick = { onRestoreClick(event) },
                        onDeleteForeverClick = { onDeleteForeverClick(event) },
                        onUrlClick = { onUrlClick(event.url) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EventsContent(
    upcomingEvents: List<SavedEvent>,
    completedEvents: List<SavedEvent>,
    onArchiveEvent: (SavedEvent) -> Unit,
    onRescheduleEvent: (SavedEvent) -> Unit,
    onMarkDoneEvent: (SavedEvent) -> Unit,
    onUndoCompleteEvent: (SavedEvent) -> Unit,
    onScheduleAgainEvent: (SavedEvent) -> Unit,
    onUrlClick: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    // Sort upcoming events: overdue first, then by scheduled time
    val sortedUpcomingEvents = remember(upcomingEvents) {
        val currentTime = System.currentTimeMillis()
        upcomingEvents.sortedWith(compareBy(
            { it.scheduledDateTime >= currentTime }, // false (overdue) comes first
            { it.scheduledDateTime }
        ))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Section tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            SectionTab(
                text = "upcoming",
                count = upcomingEvents.size,
                selected = pagerState.currentPage == 0,
                onClick = { scope.launch { pagerState.animateScrollToPage(0) } }
            )
            Spacer(modifier = Modifier.width(24.dp))
            SectionTab(
                text = "completed",
                count = completedEvents.size,
                selected = pagerState.currentPage == 1,
                onClick = { scope.launch { pagerState.animateScrollToPage(1) } }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Swipeable pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                when (page) {
                    0 -> {
                        // Upcoming events
                        if (sortedUpcomingEvents.isEmpty()) {
                            Text(
                                text = "no upcoming events",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            HowToUseTip()
                        } else {
                            sortedUpcomingEvents.forEach { event ->
                                EventCard(
                                    event = event,
                                    onArchiveClick = { onArchiveEvent(event) },
                                    onRescheduleClick = { onRescheduleEvent(event) },
                                    onDoneClick = { onMarkDoneEvent(event) },
                                    onUrlClick = { onUrlClick(event.url) }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                    1 -> {
                        // Completed events
                        if (completedEvents.isEmpty()) {
                            Text(
                                text = "no completed events",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SectionTab(
    text: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "($count)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (selected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun HowToUseTip() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "how to use",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "1. find an article or video\n2. tap share\n3. select readlater\n4. pick a date and time\n5. event added to your calendar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5
            )
        }
    }
}

@Composable
private fun AnalyticsContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "analytics",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "coming soon",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Events"
                )
            },
            label = {
                Text(
                    text = "events",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.surface
            )
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Analytics"
                )
            },
            label = {
                Text(
                    text = "analytics",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}
