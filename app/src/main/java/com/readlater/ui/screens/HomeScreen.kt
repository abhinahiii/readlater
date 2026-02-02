package com.readlater.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.readlater.data.AuthState
import com.readlater.data.SavedEvent
import com.readlater.ui.components.EventListSection
import com.readlater.ui.components.MetroButton

@Composable
fun HomeScreen(
    authState: AuthState,
    upcomingEvents: List<SavedEvent>,
    pastEvents: List<SavedEvent>,
    isLoading: Boolean,
    isSyncing: Boolean,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onCancelEvent: (SavedEvent) -> Unit,
    onRescheduleEvent: (SavedEvent) -> Unit,
    onScheduleAgainEvent: (SavedEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    when (authState) {
        is AuthState.Loading -> {
            // Centered loading state
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

        is AuthState.NotAuthenticated -> {
            // Not authenticated - show setup/connect screen
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

        is AuthState.Authenticated -> {
            // Authenticated - show home screen with events
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header
                Text(
                    text = "readlater",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = authState.account.email ?: "connected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isSyncing) {
                        Text(
                            text = "syncing...",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Upcoming Events
                EventListSection(
                    title = "upcoming",
                    events = upcomingEvents,
                    isUpcoming = true,
                    onCancelClick = onCancelEvent,
                    onRescheduleClick = onRescheduleEvent,
                    onScheduleAgainClick = { /* not used for upcoming */ },
                    onUrlClick = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Past Events
                EventListSection(
                    title = "past",
                    events = pastEvents,
                    isUpcoming = false,
                    onCancelClick = { /* not used for past */ },
                    onRescheduleClick = { /* not used for past */ },
                    onScheduleAgainClick = onScheduleAgainEvent,
                    onUrlClick = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // How to use tip (collapsed)
                if (upcomingEvents.isEmpty() && pastEvents.isEmpty()) {
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
                                text = "1. find an article or video\n2. tap share\n3. select readlater\n4. pick a date and time\n5. event created on your calendar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Disconnect button at bottom
                MetroButton(
                    text = "disconnect",
                    onClick = onDisconnectClick,
                    filled = false
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        is AuthState.Error -> {
            // Error state
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
                        text = "error: ${authState.message.lowercase()}",
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
    }
}

// Keep SetupScreen as an alias for backward compatibility
@Composable
fun SetupScreen(
    authState: AuthState,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HomeScreen(
        authState = authState,
        upcomingEvents = emptyList(),
        pastEvents = emptyList(),
        isLoading = false,
        isSyncing = false,
        onConnectClick = onConnectClick,
        onDisconnectClick = onDisconnectClick,
        onCancelEvent = {},
        onRescheduleEvent = {},
        onScheduleAgainEvent = {},
        modifier = modifier
    )
}
