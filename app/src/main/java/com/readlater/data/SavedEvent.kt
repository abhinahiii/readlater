package com.readlater.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EventStatus {
    UPCOMING,
    PAST,
    CANCELLED,
    DELETED_FROM_CALENDAR
}

@Entity(tableName = "saved_events")
data class SavedEvent(
    @PrimaryKey val googleEventId: String,
    val title: String,
    val url: String,
    val scheduledDateTime: Long,  // epoch millis
    val durationMinutes: Int,
    val createdAt: Long,
    val status: EventStatus
)
