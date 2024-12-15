package me.sm.spendwise.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PinKeyboard(
    onKeyClick: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // First row (1-3)
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            for (number in 1..3) {
                PinKey(text = number.toString()) { onKeyClick(number.toString()) }
            }
        }

        // Second row (4-6)
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            for (number in 4..6) {
                PinKey(text = number.toString()) { onKeyClick(number.toString()) }
            }
        }

        // Third row (7-9)
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            for (number in 7..9) {
                PinKey(text = number.toString()) { onKeyClick(number.toString()) }
            }
        }

        // Fourth row (empty, 0, backspace)
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp)
     )

        {
            // Empty space under 7
            PinKey(text = "", modifier = Modifier.size(64.dp)) { }
            // 0 under 8
            PinKey(text = "0") { onKeyClick("0") }
            // Backspace under 9
            PinKey(
                icon = Icons.Default.Clear,
                contentDescription = "Backspace",
                onClick = onBackspace
            )
        }
    }
}

@Composable
private fun PinKey(
    text: String,
    modifier: Modifier = Modifier.size(64.dp),
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
private fun PinKey(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier.size(64.dp),
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp)
            )
        }
    }
} 