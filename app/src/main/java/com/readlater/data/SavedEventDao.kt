package com.readlater.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedEventDao {

    @Query("SELECT * FROM saved_events WHERE status != :excludeStatus ORDER BY scheduledDateTime ASC")
    fun getAllEventsExcluding(excludeStatus: EventStatus = EventStatus.CANCELLED): Flow<List<SavedEvent>>

    @Query("SELECT * FROM saved_events WHERE scheduledDateTime > :currentTime AND status = :status ORDER BY scheduledDateTime ASC")
    fun getUpcomingEvents(currentTime: Long, status: EventStatus = EventStatus.UPCOMING): Flow<List<SavedEvent>>

    @Query("SELECT * FROM saved_events WHERE scheduledDateTime <= :currentTime OR status = :status ORDER BY scheduledDateTime DESC")
    fun getPastEvents(currentTime: Long, status: EventStatus = EventStatus.PAST): Flow<List<SavedEvent>>

    @Query("SELECT * FROM saved_events WHERE googleEventId = :eventId")
    suspend fun getEventById(eventId: String): SavedEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: SavedEvent)

    @Update
    suspend fun updateEvent(event: SavedEvent)

    @Query("UPDATE saved_events SET status = :status WHERE googleEventId = :eventId")
    suspend fun updateEventStatus(eventId: String, status: EventStatus)

    @Query("UPDATE saved_events SET scheduledDateTime = :newDateTime, status = :status WHERE googleEventId = :eventId")
    suspend fun updateEventDateTime(eventId: String, newDateTime: Long, status: EventStatus = EventStatus.UPCOMING)

    @Query("DELETE FROM saved_events WHERE googleEventId = :eventId")
    suspend fun deleteEvent(eventId: String)

    @Query("SELECT * FROM saved_events")
    suspend fun getAllEventsOnce(): List<SavedEvent>
}
