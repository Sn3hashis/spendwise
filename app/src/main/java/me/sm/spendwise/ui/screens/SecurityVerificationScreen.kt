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
import me.sm.spendwise.navigation.NavigationState
import me.sm.spendwise.navigation.Screen

@Composable
fun SecurityVerificationScreen(
    onVerificationSuccess: () -> Unit
) {
    var showPinVerification by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    val context = LocalContext.current
    val securityPreference = remember { SecurityPreference(context) }
    val scope = rememberCoroutineScope()
    var savedPin by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf<SecurityMethod>(SecurityMethod.NONE) }

    LaunchedEffect(Unit) {
        // Load the security method and PIN immediately
        securityPreference.getSecurityMethodFlow().collect { method ->
            selectedMethod = method ?: SecurityMethod.NONE
            if (method == SecurityMethod.PIN) {
                // If PIN is the method, load the saved PIN
                securityPreference.getPin().collect { storedPin ->
                    Log.d("SecurityVerificationScreen", "Loaded PIN: $storedPin")
                    savedPin = storedPin ?: ""
                }
            }
        }
    }

    if (showPinVerification || selectedMethod == SecurityMethod.PIN) {
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
                        Log.d("SecurityVerificationScreen", "Verifying PIN: $newPin with saved PIN: $savedPin")
                        if (newPin == savedPin) {
                            Log.d("SecurityVerificationScreen", "PIN verified successfully")
                            onVerificationSuccess()
                        } else {
                            Log.d("SecurityVerificationScreen", "PIN verification failed")
                            Toast.makeText(
                                context,
                                "Incorrect PIN",
                                Toast.LENGTH_SHORT
                            ).show()
                            pin = ""
                        }
                    }
                },
                title = "Enter your PIN",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    LaunchedEffect(selectedMethod) {
        if (selectedMethod == SecurityMethod.FINGERPRINT && !showPinVerification) {
            BiometricUtils.showBiometricPrompt(
                activity = context as FragmentActivity,
                title = "Verify Fingerprint",
                subtitle = "Use your fingerprint to unlock",
                onSuccess = {
                    onVerificationSuccess()
                },
                onError = { errorCode, _ ->
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED || 
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        // User clicked "Use PIN" or cancelled
                        showPinVerification = true
                    }
                }
            )
        }
    }
}