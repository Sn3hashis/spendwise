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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackPress: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
            Divider(thickness = 0.7.dp, color = Color.LightGray)
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
                    value = "Light", // You'll likely want to make this dynamic
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

