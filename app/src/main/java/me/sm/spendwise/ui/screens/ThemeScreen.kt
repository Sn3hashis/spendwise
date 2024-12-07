package me.sm.spendwise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

@Composable
fun ThemeScreen(
    onBackPress: () -> Unit,
    onThemeSelected: (ThemeMode) -> Unit
) {
    var selectedTheme by remember { mutableStateOf(ThemeMode.LIGHT) }

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
                    text = "Theme",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Divider()

            // Theme Options
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                ThemeOption(
                    title = "Light",
                    isSelected = selectedTheme == ThemeMode.LIGHT,
                    onClick = {
                        selectedTheme = ThemeMode.LIGHT
                        onThemeSelected(ThemeMode.LIGHT)
                    }
                )
                ThemeOption(
                    title = "Dark",
                    isSelected = selectedTheme == ThemeMode.DARK,
                    onClick = {
                        selectedTheme = ThemeMode.DARK
                        onThemeSelected(ThemeMode.DARK)
                    }
                )
                ThemeOption(
                    title = "Use device theme",
                    isSelected = selectedTheme == ThemeMode.SYSTEM,
                    onClick = {
                        selectedTheme = ThemeMode.SYSTEM
                        onThemeSelected(ThemeMode.SYSTEM)
                    }
                )
            }
        }
    }
}

@Composable
private fun ThemeOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}