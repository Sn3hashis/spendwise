package me.sm.spendwise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NotificationSetting(
    val title: String,
    val description: String,
    var enabled: Boolean
)

@Composable
fun NotificationScreen(
    onBackPress: () -> Unit,
//    onNotificationSettingChanged: (String, Boolean) -> Unit
) {
    var notificationSettings by remember {
        mutableStateOf(
            listOf(
                NotificationSetting(
                    "Expense Alert",
                    "Get notification about you're expense",
                    true
                ),
                NotificationSetting(
                    "Budget",
                    "Get notification when you're budget exceeding the limit",
                    true
                ),
                NotificationSetting(
                    "Tips & Articles",
                    "Small & useful pieces of pratical financial advice",
                    false
                )
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(
                    onClick = onBackPress,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Notification",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Divider()

            // Notification Settings
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                notificationSettings.forEach { setting ->
                    NotificationSettingItem(
                        setting = setting,
                        onToggle = { isEnabled ->
                            notificationSettings = notificationSettings.map {
                                if (it.title == setting.title) it.copy(enabled = isEnabled)
                                else it
                            }
//                            onNotificationSettingChanged(setting.title, isEnabled)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingItem(
    setting: NotificationSetting,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = setting.title,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = setting.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = setting.enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}