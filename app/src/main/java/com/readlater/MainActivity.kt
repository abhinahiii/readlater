package com.readlater

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.readlater.data.AuthRepository
import com.readlater.data.AuthState
import com.readlater.data.CalendarRepository
import com.readlater.data.EventRepository
import com.readlater.data.SavedEvent
import com.readlater.ui.components.RescheduleDialog
import com.readlater.ui.screens.HomeScreen
import com.readlater.ui.theme.ReadLaterTheme
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class MainActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var calendarRepository: CalendarRepository
    private lateinit var eventRepository: EventRepository

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        authRepository.handleSignInResult(result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authRepository = AuthRepository(applicationContext)
        calendarRepository = CalendarRepository(applicationContext)
        eventRepository = EventRepository(applicationContext, calendarRepository)

        setContent {
            ReadLaterTheme {
                val authState by authRepository.authState.collectAsState()
                val scope = rememberCoroutineScope()

                // Event lists
                val upcomingEvents by eventRepository.getUpcomingEvents().collectAsState(initial = emptyList())
                val completedEvents by eventRepository.getCompletedEvents().collectAsState(initial = emptyList())
                val archivedEvents by eventRepository.getArchivedEvents().collectAsState(initial = emptyList())

                // Summary message
                var summaryMessage by remember { mutableStateOf("") }

                // Loading/syncing states
                var isSyncing by remember { mutableStateOf(false) }
                var isLoading by remember { mutableStateOf(false) }

                // Dialog state for reschedule/schedule again
                var showRescheduleDialog by remember { mutableStateOf(false) }
                var selectedEventForReschedule by remember { mutableStateOf<SavedEvent?>(null) }
                var isScheduleAgain by remember { mutableStateOf(false) }

                // Update summary message when events change
                LaunchedEffect(upcomingEvents, authState) {
                    if (authState is AuthState.Authenticated) {
                        summaryMessage = eventRepository.getSummaryMessage()
                    }
                }

                // Sync when authenticated
                LaunchedEffect(authState) {
                    if (authState is AuthState.Authenticated) {
                        val account = authRepository.getAccount()
                        if (account != null) {
                            isSyncing = true
                            try {
                                eventRepository.syncWithCalendar(account)
                                summaryMessage = eventRepository.getSummaryMessage()
                            } catch (e: Exception) {
                                // Silently fail sync
                            }
                            isSyncing = false
                        }
                    }
                }

                HomeScreen(
                    authState = authState,
                    upcomingEvents = upcomingEvents,
                    completedEvents = completedEvents,
                    archivedEvents = archivedEvents,
                    summaryMessage = summaryMessage,
                    isSyncing = isSyncing,
                    onConnectClick = {
                        signInLauncher.launch(authRepository.getSignInIntent())
                    },
                    onDisconnectClick = {
                        scope.launch {
                            authRepository.signOut()
                            Toast.makeText(
                                this@MainActivity,
                                "Disconnected",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onArchiveEvent = { event ->
                        scope.launch {
                            isLoading = true
                            val account = authRepository.getAccount()
                            if (account != null) {
                                val result = eventRepository.archiveEvent(account, event.googleEventId)
                                result.onSuccess {
                                    summaryMessage = eventRepository.getSummaryMessage()
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Event archived",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }.onFailure { error ->
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Failed: ${error.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            isLoading = false
                        }
                    },
                    onRescheduleEvent = { event ->
                        selectedEventForReschedule = event
                        isScheduleAgain = false
                        showRescheduleDialog = true
                    },
                    onMarkDoneEvent = { event ->
                        scope.launch {
                            isLoading = true
                            val result = eventRepository.markAsCompleted(event.googleEventId)
                            result.onSuccess {
                                summaryMessage = eventRepository.getSummaryMessage()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Marked as done",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.onFailure { error ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Failed: ${error.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            isLoading = false
                        }
                    },
                    onUndoCompleteEvent = { event ->
                        scope.launch {
                            isLoading = true
                            val result = eventRepository.undoComplete(event.googleEventId)
                            result.onSuccess {
                                summaryMessage = eventRepository.getSummaryMessage()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Moved back to upcoming",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.onFailure { error ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Failed: ${error.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            isLoading = false
                        }
                    },
                    onScheduleAgainEvent = { event ->
                        selectedEventForReschedule = event
                        isScheduleAgain = true
                        showRescheduleDialog = true
                    },
                    onRestoreEvent = { event ->
                        scope.launch {
                            isLoading = true
                            val account = authRepository.getAccount()
                            if (account != null) {
                                val result = eventRepository.restoreFromArchive(account, event)
                                result.onSuccess {
                                    summaryMessage = eventRepository.getSummaryMessage()
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Event restored",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }.onFailure { error ->
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Failed: ${error.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            isLoading = false
                        }
                    },
                    onDeleteForeverEvent = { event ->
                        scope.launch {
                            isLoading = true
                            val result = eventRepository.deleteEventPermanently(event.googleEventId)
                            result.onSuccess {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Event deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.onFailure { error ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Failed: ${error.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            isLoading = false
                        }
                    }
                )

                // Reschedule/Schedule Again Dialog
                if (showRescheduleDialog && selectedEventForReschedule != null) {
                    val event = selectedEventForReschedule!!
                    val eventDateTime = Instant.ofEpochMilli(event.scheduledDateTime)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()

                    // For schedule again, default to next hour from now
                    val defaultDate = if (isScheduleAgain) LocalDate.now() else eventDateTime.toLocalDate()
                    val defaultTime = if (isScheduleAgain) {
                        LocalTime.now().plusHours(1).withMinute(0).withSecond(0)
                    } else {
                        eventDateTime.toLocalTime()
                    }

                    RescheduleDialog(
                        title = if (isScheduleAgain) "Schedule Again" else "Reschedule",
                        initialDate = defaultDate,
                        initialTime = defaultTime,
                        initialDuration = event.durationMinutes,
                        onDismiss = {
                            showRescheduleDialog = false
                            selectedEventForReschedule = null
                        },
                        onConfirm = { newDateTime, newDuration ->
                            scope.launch {
                                isLoading = true
                                showRescheduleDialog = false
                                val account = authRepository.getAccount()
                                if (account != null) {
                                    if (isScheduleAgain) {
                                        val result = eventRepository.scheduleAgain(
                                            account = account,
                                            originalEvent = event,
                                            newDateTime = newDateTime,
                                            durationMinutes = newDuration
                                        )
                                        result.onSuccess {
                                            summaryMessage = eventRepository.getSummaryMessage()
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Event scheduled",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }.onFailure { error ->
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Failed: ${error.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } else {
                                        val result = eventRepository.rescheduleEvent(
                                            account = account,
                                            eventId = event.googleEventId,
                                            newDateTime = newDateTime,
                                            durationMinutes = newDuration
                                        )
                                        result.onSuccess {
                                            summaryMessage = eventRepository.getSummaryMessage()
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Event rescheduled",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }.onFailure { error ->
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Failed: ${error.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                                selectedEventForReschedule = null
                                isLoading = false
                            }
                        }
                    )
                }
            }
        }
    }
}
