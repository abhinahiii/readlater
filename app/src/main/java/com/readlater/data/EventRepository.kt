package com.readlater.data

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneId

class EventRepository(
    context: Context,
    private val calendarRepository: CalendarRepository
) {
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.savedEventDao()

    fun getUpcomingEvents(): Flow<List<SavedEvent>> {
        val currentTime = System.currentTimeMillis()
        return dao.getAllEventsExcluding(EventStatus.CANCELLED).map { events ->
            events.filter { 
                it.scheduledDateTime > currentTime && 
                it.status != EventStatus.DELETED_FROM_CALENDAR 
            }
        }
    }

    fun getPastEvents(): Flow<List<SavedEvent>> {
        val currentTime = System.currentTimeMillis()
        return dao.getAllEventsExcluding(EventStatus.CANCELLED).map { events ->
            events.filter { 
                it.scheduledDateTime <= currentTime || 
                it.status == EventStatus.PAST ||
                it.status == EventStatus.DELETED_FROM_CALENDAR
            }.sortedByDescending { it.scheduledDateTime }
        }
    }

    suspend fun saveEvent(
        googleEventId: String,
        title: String,
        url: String,
        scheduledDateTime: LocalDateTime,
        durationMinutes: Int
    ) {
        val epochMillis = scheduledDateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val event = SavedEvent(
            googleEventId = googleEventId,
            title = title,
            url = url,
            scheduledDateTime = epochMillis,
            durationMinutes = durationMinutes,
            createdAt = System.currentTimeMillis(),
            status = EventStatus.UPCOMING
        )
        dao.insertEvent(event)
    }

    suspend fun cancelEvent(
        account: GoogleSignInAccount,
        eventId: String
    ): Result<Unit> {
        // Delete from Google Calendar
        val result = calendarRepository.deleteEvent(account, eventId)
        
        return result.map {
            // Mark as cancelled locally
            dao.updateEventStatus(eventId, EventStatus.CANCELLED)
        }
    }

    suspend fun rescheduleEvent(
        account: GoogleSignInAccount,
        eventId: String,
        newDateTime: LocalDateTime,
        durationMinutes: Int
    ): Result<Unit> {
        // Update in Google Calendar
        val result = calendarRepository.updateEvent(account, eventId, newDateTime, durationMinutes)
        
        return result.map {
            // Update locally
            val epochMillis = newDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            dao.updateEventDateTime(eventId, epochMillis, EventStatus.UPCOMING)
        }
    }

    suspend fun scheduleAgain(
        account: GoogleSignInAccount,
        originalEvent: SavedEvent,
        newDateTime: LocalDateTime,
        durationMinutes: Int
    ): Result<String> {
        // Create a new event in Google Calendar
        val result = calendarRepository.createEvent(
            account = account,
            title = originalEvent.title,
            description = originalEvent.url,
            startDateTime = newDateTime,
            durationMinutes = durationMinutes
        )
        
        return result.map { newEventId ->
            // Save the new event locally
            saveEvent(
                googleEventId = newEventId,
                title = originalEvent.title,
                url = originalEvent.url,
                scheduledDateTime = newDateTime,
                durationMinutes = durationMinutes
            )
            
            // Mark the old event as past (if not already)
            dao.updateEventStatus(originalEvent.googleEventId, EventStatus.PAST)
            
            newEventId
        }
    }

    suspend fun syncWithCalendar(account: GoogleSignInAccount) {
        val allEvents = dao.getAllEventsOnce()
        val currentTime = System.currentTimeMillis()

        for (event in allEvents) {
            // Skip already cancelled or deleted events
            if (event.status == EventStatus.CANCELLED || event.status == EventStatus.DELETED_FROM_CALENDAR) {
                continue
            }

            // Check if event still exists in Google Calendar
            val result = calendarRepository.getEvent(account, event.googleEventId)
            result.onSuccess { calendarEvent ->
                if (calendarEvent == null) {
                    // Event was deleted from Google Calendar
                    dao.updateEventStatus(event.googleEventId, EventStatus.DELETED_FROM_CALENDAR)
                } else {
                    // Update status based on time
                    val newStatus = if (event.scheduledDateTime <= currentTime) {
                        EventStatus.PAST
                    } else {
                        EventStatus.UPCOMING
                    }
                    if (event.status != newStatus) {
                        dao.updateEventStatus(event.googleEventId, newStatus)
                    }
                }
            }
        }
    }

    suspend fun getEventById(eventId: String): SavedEvent? {
        return dao.getEventById(eventId)
    }
}
