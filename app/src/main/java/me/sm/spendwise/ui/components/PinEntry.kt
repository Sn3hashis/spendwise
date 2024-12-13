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

        // Number pad
        Column(
            modifier = Modifier.padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Numbers 1-9
            (1..9).chunked(3).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    row.forEach { number ->
                        TextButton(
                            onClick = {
                                if (pin.length < 4) {
                                    onPinChange(pin + number)
                                }
                            },
                            modifier = Modifier.size(72.dp),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            Text(
                                text = number.toString(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Last row with 0 and backspace
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                TextButton(
                    onClick = {
                        if (pin.length < 4) {
                            onPinChange(pin + "0")
                        }
                    },
                    modifier = Modifier.size(72.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text(
                        text = "0",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = {
                        if (pin.isNotEmpty()) {
                            onPinChange(pin.dropLast(1))
                        }
                    },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_backspace),
                        contentDescription = "Backspace",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}