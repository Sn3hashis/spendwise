package me.sm.spendwise.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sm.spendwise.data.HapticsPreference
import android.util.Log

@Composable
fun HapticsScreen(
    onBackPress: () -> Unit
) {
    val context = LocalContext.current
    val hapticsPreference = remember { HapticsPreference(context) }
    var isHapticsEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        Log.d("HapticsScreen", "Starting LaunchedEffect")
        isHapticsEnabled = hapticsPreference.isHapticsEnabled()
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Haptics",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            HorizontalDivider()

            // Haptics Options
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                HapticsOptionItem(
                    title = "On",
                    isSelected = isHapticsEnabled,
                    onClick = {
                        isHapticsEnabled = true
                        hapticsPreference.setHapticsEnabled(true)
                    }
                )

                HapticsOptionItem(
                    title = "Off",
                    isSelected = !isHapticsEnabled,
                    onClick = {
                        isHapticsEnabled = false
                        hapticsPreference.setHapticsEnabled(false)
                    }
                )
            }
        }
    }
}

@Composable
private fun HapticsOptionItem(
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
            Checkbox(
                checked = true,
                onCheckedChange = null, // Read-only checkbox
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
} 