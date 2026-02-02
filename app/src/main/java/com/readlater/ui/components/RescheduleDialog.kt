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
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar

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

    val durations = listOf(
        15 to "15m",
        30 to "30m",
        45 to "45m",
        60 to "1h",
        90 to "1.5h",
        120 to "2h"
    )

    // Check if selected time is in the past
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

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(2.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                BrutalistDateTimePicker(
                    selectedDate = selectedDate,
                    selectedTime = selectedTime,
                    onDateTimeSelected = { date, time ->
                        selectedDate = date
                        selectedTime = time
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "DURATION",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        durations.forEach { (minutes, label) ->
                            val isSelected = selectedDuration == minutes
                            Box(
                                modifier = Modifier
                                    .border(2.dp, Color.Black)
                                    .background(if (isSelected) Color.Black else Color.White)
                                    .clickable { selectedDuration = minutes }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isSelected) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        BrutalistButton(
                            text = "Cancel",
                            onClick = onDismiss,
                            filled = false
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        BrutalistButton(
                            text = "Confirm",
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
}
