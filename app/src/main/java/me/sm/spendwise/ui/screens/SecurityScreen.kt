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

enum class SecurityMethod {
    PIN, FINGERPRINT, FACE_ID
}

@Composable
fun SecurityScreen(
    onBackPress: () -> Unit,
    onSecurityMethodSelected: (SecurityMethod) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(SecurityMethod.PIN) }

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
                    text = "Security",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Divider()

            // Security Options
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                SecurityOption(
                    title = "PIN",
                    isSelected = selectedMethod == SecurityMethod.PIN,
                    onClick = {
                        selectedMethod = SecurityMethod.PIN
                        onSecurityMethodSelected(SecurityMethod.PIN)
                    }
                )
                SecurityOption(
                    title = "Fingerprint",
                    isSelected = selectedMethod == SecurityMethod.FINGERPRINT,
                    onClick = {
                        selectedMethod = SecurityMethod.FINGERPRINT
                        onSecurityMethodSelected(SecurityMethod.FINGERPRINT)
                    }
                )
                SecurityOption(
                    title = "Face ID",
                    isSelected = selectedMethod == SecurityMethod.FACE_ID,
                    onClick = {
                        selectedMethod = SecurityMethod.FACE_ID
                        onSecurityMethodSelected(SecurityMethod.FACE_ID)
                    }
                )
            }
        }
    }
}

@Composable
private fun SecurityOption(
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