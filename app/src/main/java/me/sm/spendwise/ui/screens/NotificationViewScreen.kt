package me.sm.spendwise.ui.screens 

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sm.spendwise.data.NotificationManager

data class NotificationItem(
    val title: String,
    val subtitle: String,
    val time: String,
    val date: String,
    val isRead: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationViewScreen(
    onBackClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val notifications = NotificationManager.notifications.collectAsState()

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete All Notifications") },
            text = { Text("Are you sure you want to delete all notifications?") },
            confirmButton = {
                TextButton(onClick = {
                    NotificationManager.clearAll()
                    showDeleteConfirmation = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Notification") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options"
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mark all as read") },
                                onClick = {
                                    NotificationManager.markAllAsRead()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete all notifications") },
                                onClick = {
                                    showDeleteConfirmation = true
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (notifications.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notifications yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val (newNotifications, pastNotifications) = notifications.value.partition { !it.isRead }

                if (newNotifications.isNotEmpty()) {
                    item {
                        SectionHeader("New Notifications")
                    }
                    items(newNotifications) { notification ->
                        NotificationItem(notification = notification)
                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )
                    }
                }

                if (pastNotifications.isNotEmpty()) {
                    item {
                        SectionHeader("Past Notifications")
                    }
                    items(pastNotifications) { notification ->
                        NotificationItem(notification = notification)
                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Divider(
            modifier = Modifier.padding(horizontal = 24.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun NotificationItem(
    notification: NotificationItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = notification.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                color = if (notification.isRead) 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else 
                    MaterialTheme.colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.time,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Text(
                    text = notification.date,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = notification.subtitle,
            color = if (notification.isRead) 
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            else 
                MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}
