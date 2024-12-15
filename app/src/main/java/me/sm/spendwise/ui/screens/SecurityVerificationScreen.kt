package me.sm.spendwise.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch
import me.sm.spendwise.data.SecurityMethod
import me.sm.spendwise.data.SecurityPreference
import me.sm.spendwise.ui.components.PinEntry
import me.sm.spendwise.utils.BiometricUtils
import me.sm.spendwise.ui.AppState
import me.sm.spendwise.ui.Screen

@Composable
fun SecurityVerificationScreen(
    onVerificationSuccess: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    val context = LocalContext.current
    val securityPreference = remember { SecurityPreference(context) }
    val scope = rememberCoroutineScope()
    var savedPin by remember { mutableStateOf("") }
    var attempts by remember { mutableStateOf(0) }
    val maxAttempts = 3

    LaunchedEffect(Unit) {
        securityPreference.loadPin().collect { storedPin ->
            Log.d("SecurityVerification", "Loaded PIN: ${storedPin != null}")
            savedPin = storedPin ?: ""
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Enter PIN",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        PinEntry(
            pin = pin,
            onPinChange = { newPin ->
                pin = newPin
                if (newPin.length == 4) {
                    if (newPin == savedPin) {
                        onVerificationSuccess()
                    } else {
                        attempts++
                        if (attempts >= maxAttempts) {
                            scope.launch {
                                securityPreference.clearPin()
                                AppState.currentScreen = Screen.Login
                            }
                            Toast.makeText(
                                context,
                                "Too many incorrect attempts. Please login again.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Incorrect PIN. ${maxAttempts - attempts} attempts remaining",
                                Toast.LENGTH_SHORT
                            ).show()
                            pin = ""
                        }
                    }
                }
            },
            title = "Enter your PIN"
        )
    }
}