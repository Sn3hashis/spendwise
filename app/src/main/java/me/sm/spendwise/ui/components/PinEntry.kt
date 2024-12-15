package me.sm.spendwise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sm.spendwise.R

@Composable
fun PinEntry(
    pin: String,
    onPinChange: (String) -> Unit,
    title: String,
    onResetPin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        // PIN dots
        Row(
            modifier = Modifier.padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = if (index < pin.length) 
                                MaterialTheme.colorScheme.primary
                            else 
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )
            }
        }

        // Reset PIN button
        TextButton(
            onClick = onResetPin,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Reset PIN")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Use PinKeyboard instead of custom implementation
        PinKeyboard(
            onKeyClick = { digit ->
                if (pin.length < 4) {
                    onPinChange(pin + digit)
                }
            },
            onBackspace = {
                if (pin.isNotEmpty()) {
                    onPinChange(pin.dropLast(1))
                }
            },
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}