package me.sm.spendwise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch
import me.sm.spendwise.data.SecurityMethod
import me.sm.spendwise.data.SecurityPreference
import me.sm.spendwise.ui.components.PinEntry
import me.sm.spendwise.utils.BiometricUtils

@Composable
fun SecurityVerificationScreen(
    onVerificationSuccess: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    val context = LocalContext.current
    val securityPreference = remember { SecurityPreference(context) }
    val scope = rememberCoroutineScope()
    var savedPin by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        securityPreference.getPin().collect { storedPin ->
            savedPin = storedPin ?: ""
        }
    }

    PinEntry(
        pin = pin,
        onPinChange = { newPin ->
            pin = newPin
            if (newPin.length == 4 && newPin == savedPin) {
                onVerificationSuccess()
            }
        },
        title = "Enter your PIN"
    )
}