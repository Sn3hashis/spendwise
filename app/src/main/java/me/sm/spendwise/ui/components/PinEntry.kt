package me.sm.spendwise.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import me.sm.spendwise.data.HapticsPreference
import androidx.compose.ui.platform.LocalContext

@Composable
fun PinEntry(
    pin: String,
    onPinChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val hapticsPreference = remember { HapticsPreference(context) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // PIN dots display
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 32.dp)
        ) {
            repeat(4) { index ->
                Surface(
                    modifier = Modifier.size(16.dp),
                    shape = MaterialTheme.shapes.small,
                    color = if (index < pin.length) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    }
                ) {}
            }
        }

        // Use PinKeyboard with haptic feedback
        PinKeyboard(
            onKeyClick = { digit ->
                if (pin.length < 4) {
                    hapticsPreference.performPinEntryHapticFeedback(view)
                    onPinChange(pin + digit)
                }
            },
            onBackspace = {
                if (pin.isNotEmpty()) {
                    hapticsPreference.performPinEntryHapticFeedback(view)
                    onPinChange(pin.dropLast(1))
                }
            },
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}