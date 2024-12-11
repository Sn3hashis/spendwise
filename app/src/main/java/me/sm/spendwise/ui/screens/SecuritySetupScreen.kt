package me.sm.spendwise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import me.sm.spendwise.data.SecurityPreference
import me.sm.spendwise.data.SecurityMethod
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.sm.spendwise.utils.BiometricUtils
import me.sm.spendwise.ui.components.PinEntry

@Composable
fun SecuritySetupScreen(
    onSetupComplete: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    val context = LocalContext.current
    val securityPreference = remember { SecurityPreference(context) }
    val scope = rememberCoroutineScope()

    PinEntry(
        pin = pin,
        onPinChange = { newPin ->
            pin = newPin
            if (newPin.length == 4) {
                scope.launch {
                    securityPreference.savePin(newPin)
                    securityPreference.saveSecurityMethod(SecurityMethod.PIN)
                    onSetupComplete()
                }
            }
        },
        title = "Let's setup your PIN"
    )
}