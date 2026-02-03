package com.readlater.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.readlater.data.EventStatus
import com.readlater.data.SavedEvent
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun EventCard(
    event: SavedEvent,
    onArchiveClick: () -> Unit,
    onRescheduleClick: () -> Unit,
    onDoneClick: () -> Unit,
    onUrlClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateTime = Instant.ofEpochMilli(event.scheduledDateTime)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    val today = LocalDate.now()
    val eventDate = dateTime.toLocalDate()
    val isToday = eventDate == today
    val isOverdue = event.scheduledDateTime < System.currentTimeMillis()

    val dateFormatter = DateTimeFormatter.ofPattern("MMM d")
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    val dateText = when {
        isToday -> "today"
        eventDate == today.minusDays(1) -> "yesterday"
        eventDate == today.plusDays(1) -> "tomorrow"
        else -> dateTime.format(dateFormatter).lowercase()
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline)),
        shape = RectangleShape,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = event.title.ifBlank { "Untitled" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // URL (clickable)
            Text(
                text = event.url,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { onUrlClick() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Date and time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = " · ",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dateTime.format(timeFormatter).lowercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = " · ",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${event.durationMinutes}m",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Overdue indicator
            if (isOverdue && event.status == EventStatus.SCHEDULED) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "overdue",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Deleted from calendar indicator
            if (event.status == EventStatus.DELETED_FROM_CALENDAR) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "deleted from calendar",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons: archive, reschedule, done
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                SmallMetroButton(
                    text = "archive",
                    onClick = onArchiveClick,
                    filled = false
                )
                Spacer(modifier = Modifier.width(8.dp))
                SmallMetroButton(
                    text = "reschedule",
                    onClick = onRescheduleClick,
                    filled = false
                )
                Spacer(modifier = Modifier.width(8.dp))
                SmallMetroButton(
                    text = "done",
                    onClick = onDoneClick,
                    filled = true
                )
            }
        }
    }
}

@Composable
fun CompletedEventCard(
    event: SavedEvent,
    onUndoClick: () -> Unit,
    onScheduleAgainClick: () -> Unit,
    onUrlClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedAt = event.completedAt?.let {
        Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    val dateFormatter = DateTimeFormatter.ofPattern("MMM d")
    val completedText = completedAt?.let {
        val today = LocalDate.now()
        when (it) {
            today -> "completed today"
            today.minusDays(1) -> "completed yesterday"
            else -> "completed ${it.format(dateFormatter).lowercase()}"
        }
    } ?: "completed"

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline)),
        shape = RectangleShape,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = event.title.ifBlank { "Untitled" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // URL (clickable)
            Text(
                text = event.url,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { onUrlClick() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Completed date
            Text(
                text = "✓ $completedText",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons: undo, schedule again
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                SmallMetroButton(
                    text = "undo",
                    onClick = onUndoClick,
                    filled = false
                )
                Spacer(modifier = Modifier.width(8.dp))
                SmallMetroButton(
                    text = "schedule again",
                    onClick = onScheduleAgainClick,
                    filled = true
                )
            }
        }
    }
}

@Composable
fun ArchivedEventCard(
    event: SavedEvent,
    onRestoreClick: () -> Unit,
    onDeleteForeverClick: () -> Unit,
    onUrlClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val archivedAt = event.archivedAt?.let {
        Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    val dateFormatter = DateTimeFormatter.ofPattern("MMM d")
    val archivedText = archivedAt?.let {
        val today = LocalDate.now()
        when (it) {
            today -> "archived today"
            today.minusDays(1) -> "archived yesterday"
            else -> "archived ${it.format(dateFormatter).lowercase()}"
        }
    } ?: "archived"

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline)),
        shape = RectangleShape,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = event.title.ifBlank { "Untitled" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // URL (clickable)
            Text(
                text = event.url,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { onUrlClick() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Archived date
            Text(
                text = archivedText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons: restore, delete forever
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                SmallMetroButton(
                    text = "restore",
                    onClick = onRestoreClick,
                    filled = false
                )
                Spacer(modifier = Modifier.width(8.dp))
                SmallMetroButton(
                    text = "delete forever",
                    onClick = onDeleteForeverClick,
                    filled = false,
                    isDestructive = true
                )
            }
        }
    }
}

@Composable
private fun SmallMetroButton(
    text: String,
    onClick: () -> Unit,
    filled: Boolean,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false
) {
    val backgroundColor = when {
        filled -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when {
        isDestructive -> MaterialTheme.colorScheme.error
        filled -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RectangleShape,
        color = backgroundColor,
        border = BorderStroke(
            1.dp,
            if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun EventListSection(
    title: String,
    events: List<SavedEvent>,
    isUpcoming: Boolean,
    onCancelClick: (SavedEvent) -> Unit,
    onRescheduleClick: (SavedEvent) -> Unit,
    onScheduleAgainClick: (SavedEvent) -> Unit,
    onUrlClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (title.isNotEmpty()) {
            Text(
                text = title.lowercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (events.isEmpty()) {
            Text(
                text = if (isUpcoming) "no upcoming events" else "no completed events",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            events.forEach { event ->
                // This is the legacy component, kept for compatibility
                // New code should use EventCard, CompletedEventCard, or ArchivedEventCard directly
                EventCard(
                    event = event,
                    onArchiveClick = { onCancelClick(event) },
                    onRescheduleClick = { onRescheduleClick(event) },
                    onDoneClick = { onScheduleAgainClick(event) },
                    onUrlClick = { onUrlClick(event.url) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
