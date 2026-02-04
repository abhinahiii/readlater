package com.readlater.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.readlater.ui.theme.DarkThemeColors
import com.readlater.ui.theme.EditorialSpacing
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RescheduleDialog(
    title: String,
    initialDate: LocalDate,
    initialTime: LocalTime,
    initialDuration: Int,
    onDismiss: () -> Unit,
    onConfirm: (LocalDateTime, Int) -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var selectedTime by remember { mutableStateOf(initialTime) }
    var selectedDuration by remember { mutableIntStateOf(initialDuration) }

    val durationOptions = listOf(
        15 to "15 min",
        30 to "30 min",
        45 to "45 min",
        60 to "1 hr",
        90 to "1.5 hr",
        120 to "2 hr"
    )

    fun formatDuration(minutes: Int): String {
        if (minutes < 60) return "$minutes min"
        val hours = minutes / 60
        val mins = minutes % 60
        return if (mins == 0) {
            if (hours == 1) "1 hr" else "$hours hr"
        } else {
            "$hours hr $mins min"
        }
    }

    fun isTimeInPast(): Boolean {
        val calendar = Calendar.getInstance()
        val deviceDate = LocalDate.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        val deviceTime = LocalTime.of(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )
        return selectedDate == deviceDate && selectedTime.isBefore(deviceTime)
    }

    val timeInPast = isTimeInPast()
    val isCustomDuration = durationOptions.none { it.first == selectedDuration }
    var showDurationPicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkThemeColors.DialogBackground)
                .border(1.dp, DarkThemeColors.Border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(EditorialSpacing.m)
            ) {
                Text(
                    text = title.lowercase(Locale.ROOT),
                    style = MaterialTheme.typography.displaySmall,
                    color = DarkThemeColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(EditorialSpacing.m))

                MetroDateTimePicker(
                    selectedDate = selectedDate,
                    selectedTime = selectedTime,
                    onDateTimeSelected = { date, time ->
                        selectedDate = date
                        selectedTime = time
                    }
                )

                Spacer(modifier = Modifier.height(EditorialSpacing.m))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "duration",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkThemeColors.TextSecondary,
                        modifier = Modifier.padding(bottom = EditorialSpacing.s)
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        durationOptions.forEach { (minutes, label) ->
                            val isSelected = selectedDuration == minutes
                            Box(
                                modifier = Modifier
                                    .border(1.dp, DarkThemeColors.Border)
                                    .background(if (isSelected) DarkThemeColors.TextPrimary else Color.Transparent)
                                    .clickable { selectedDuration = minutes }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) DarkThemeColors.Background else DarkThemeColors.TextPrimary
                                )
                            }
                        }

                        val customLabel = if (isCustomDuration) formatDuration(selectedDuration) else "custom"
                        Box(
                            modifier = Modifier
                                .border(1.dp, DarkThemeColors.Border)
                                .background(if (isCustomDuration) DarkThemeColors.TextPrimary else Color.Transparent)
                                .clickable { showDurationPicker = true }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = customLabel.lowercase(Locale.ROOT),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isCustomDuration) DarkThemeColors.Background else DarkThemeColors.TextPrimary
                                )
                                if (isCustomDuration) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = "edit duration",
                                        tint = DarkThemeColors.Background,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(EditorialSpacing.m))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        MetroButton(
                            text = "cancel",
                            onClick = onDismiss,
                            filled = false
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MetroButton(
                            text = "save",
                            onClick = {
                                onConfirm(
                                    LocalDateTime.of(selectedDate, selectedTime),
                                    selectedDuration
                                )
                            },
                            enabled = !timeInPast
                        )
                    }
                }
            }
        }
    }

    if (showDurationPicker) {
        var tempDuration by remember { mutableIntStateOf(selectedDuration) }
        Dialog(onDismissRequest = { showDurationPicker = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkThemeColors.DialogBackground)
                    .border(1.dp, DarkThemeColors.Border)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "custom duration",
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkThemeColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .border(1.dp, DarkThemeColors.Border)
                                .clickable {
                                    tempDuration = (tempDuration - 15).coerceAtLeast(15)
                                }
                                .padding(horizontal = 18.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "-",
                                style = MaterialTheme.typography.titleLarge,
                                color = DarkThemeColors.TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .border(1.dp, DarkThemeColors.Border)
                                .padding(horizontal = 18.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = formatDuration(tempDuration),
                                style = MaterialTheme.typography.bodyLarge,
                                color = DarkThemeColors.TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .border(1.dp, DarkThemeColors.Border)
                                .clickable {
                                    tempDuration = (tempDuration + 15).coerceAtMost(240)
                                }
                                .padding(horizontal = 18.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "+",
                                style = MaterialTheme.typography.titleLarge,
                                color = DarkThemeColors.TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "adjust in 15-minute steps.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkThemeColors.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            MetroButton(
                                text = "cancel",
                                onClick = { showDurationPicker = false },
                                filled = false
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            MetroButton(
                                text = "done",
                                onClick = {
                                    selectedDuration = tempDuration
                                    showDurationPicker = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
