package me.sm.spendwise.ui.screens

import android.R.attr.onClick
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sm.spendwise.navigation.NavigationState
import me.sm.spendwise.navigation.Screen
import androidx.compose.runtime.collectAsState
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import me.sm.spendwise.data.ThemePreference

@Composable
fun SettingsScreen(

    onBackPress: () -> Unit,
) {
    val themePreference = ThemePreference(LocalContext.current)
val currentTheme by themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.name)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
            .fillMaxWidth()
        // .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
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
                    text = "Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Divider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SettingsItem(
                    title = "Currency",
                    value = "USD", // You'll likely want to make this dynamic
                    onClick = { NavigationState.navigateTo(Screen.Currency) },
                )

                SettingsItem(
                    title = "Theme",
                    value = currentTheme.lowercase()
                        .capitalize(),
                            // You'll likely want to make this dynamic

                    onClick = { NavigationState.navigateTo(Screen.Theme) },
                )
                SettingsItem(
                    title = "Language",
                    value = "English", // You'll likely want to make this dynamic
                    onClick = { NavigationState.navigateTo(Screen.Language) },
                )
                SettingsItem(
                    title = "Notifications",
                    value = "Off", // You'll likely want to make this dynamic
                    onClick = { NavigationState.navigateTo(Screen.Notifications) },
                )
                SettingsItem(
                    title = "About",
                    value = "",
                    onClick = { NavigationState.navigateTo(Screen.About) },
                )
                SettingsItem(
                    title = "Security",
                    value = "",
                    onClick = { NavigationState.navigateTo(Screen.Security) },
                )


                // ... other settings items
            }
        }
    }
}
@Composable
fun SettingsItem(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF8B5CF6)
            )
        }
    }
}

