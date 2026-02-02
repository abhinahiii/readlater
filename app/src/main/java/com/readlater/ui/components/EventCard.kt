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
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun EventCard(
    event: SavedEvent,
    isUpcoming: Boolean,
    onCancelClick: () -> Unit,
    onRescheduleClick: () -> Unit,
    onScheduleAgainClick: () -> Unit,
    onUrlClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateTime = Instant.ofEpochMilli(event.scheduledDateTime)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

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
                    text = dateTime.format(dateFormatter).lowercase(),
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

            // Status indicator for deleted events
            if (event.status == EventStatus.DELETED_FROM_CALENDAR) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "deleted from calendar",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (isUpcoming) {
                    // Upcoming events: Cancel + Reschedule
                    SmallMetroButton(
                        text = "cancel",
                        onClick = onCancelClick,
                        filled = false
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SmallMetroButton(
                        text = "reschedule",
                        onClick = onRescheduleClick,
                        filled = true
                    )
                } else {
                    // Past events: Schedule Again
                    SmallMetroButton(
                        text = "schedule again",
                        onClick = onScheduleAgainClick,
                        filled = true
                    )
                }
            }
        }
    }
}

@Composable
private fun SmallMetroButton(
    text: String,
    onClick: () -> Unit,
    filled: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RectangleShape,
        color = if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (filled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
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
        Text(
            text = title.lowercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (events.isEmpty()) {
            Text(
                text = if (isUpcoming) "no upcoming events" else "no past events",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            events.forEach { event ->
                EventCard(
                    event = event,
                    isUpcoming = isUpcoming,
                    onCancelClick = { onCancelClick(event) },
                    onRescheduleClick = { onRescheduleClick(event) },
                    onScheduleAgainClick = { onScheduleAgainClick(event) },
                    onUrlClick = { onUrlClick(event.url) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
