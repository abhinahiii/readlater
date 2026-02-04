package com.readlater.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.readlater.ui.theme.DarkThemeColors
import java.util.Locale

@Composable
fun ArchiveConfirmationDialog(
    eventTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkThemeColors.DialogBackground)
                .border(1.dp, DarkThemeColors.Border)
                .padding(24.dp)
        ) {
            Text(
                text = "archive this event?",
                style = MaterialTheme.typography.titleLarge,
                color = DarkThemeColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "\"${eventTitle.lowercase(Locale.ROOT)}\"",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkThemeColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "it will be moved to archive. you can restore it anytime.",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkThemeColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))

                MetroButton(
                    text = "cancel",
                    onClick = onDismiss,
                    filled = false,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                MetroButton(
                    text = "archive",
                    onClick = onConfirm,
                    filled = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    eventTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkThemeColors.DialogBackground)
                .border(1.dp, DarkThemeColors.Border)
                .padding(24.dp)
        ) {
            Text(
                text = "delete forever?",
                style = MaterialTheme.typography.titleLarge,
                color = DarkThemeColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "\"${eventTitle.lowercase(Locale.ROOT)}\"",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkThemeColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "it will be permanently deleted. this action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkThemeColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))

                MetroButton(
                    text = "cancel",
                    onClick = onDismiss,
                    filled = false,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    color = DarkThemeColors.OverdueRed
                ) {
                    Text(
                        text = "DELETE",
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkThemeColors.Background,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}
