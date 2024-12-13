package me.sm.spendwise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import me.sm.spendwise.data.SecurityPreference
import me.sm.spendwise.data.SecurityMethod
import kotlinx.coroutines.launch
import me.sm.spendwise.ui.components.PinEntry
import android.util.Log
import kotlinx.coroutines.flow.first

@Composable
fun SecuritySetupScreen(
    onSetupComplete: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var stage by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val securityPreference = remember { SecurityPreference(context) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        Log.d("SecuritySetupScreen", "Starting setup")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (stage == 0) "Set PIN" else "Confirm PIN",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        PinEntry(
            pin = if (stage == 0) pin else confirmPin,
            onPinChange = { newPin ->
                if (stage == 0) {
                    pin = newPin
                    if (newPin.length == 4) {
                        stage = 1
                    }
                } else {
                    confirmPin = newPin
                    if (newPin.length == 4) {
                        if (newPin == pin) {
                            scope.launch {
                                try {
                                    Log.d("SecuritySetupScreen", "Saving PIN: $pin")
                                    // Save the PIN first
                                    securityPreference.savePin(pin)
                                    // Add PIN to enrolled methods
                                    securityPreference.addEnrolledMethod(SecurityMethod.PIN)
                                    // Set PIN as current security method
                                    securityPreference.saveSecurityMethod(SecurityMethod.PIN)
                                    
                                    // Verify the PIN was saved using first()
                                    val storedPin = securityPreference.getPin().first() ?: ""
                                    Log.d("SecuritySetupScreen", "Verifying saved PIN: $storedPin")
                                    
                                    if (storedPin == pin) {
                                        Log.d("SecuritySetupScreen", "PIN verified successfully")
                                        Toast.makeText(
                                            context,
                                            "PIN setup successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onSetupComplete()
                                    } else {
                                        Log.e("SecuritySetupScreen", "PIN verification failed")
                                        Toast.makeText(
                                            context,
                                            "Error setting up PIN. Please try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // Reset the state
                                        pin = ""
                                        confirmPin = ""
                                        stage = 0
                                    }
                                } catch (e: Exception) {
                                    Log.e("SecuritySetupScreen", "Error saving PIN", e)
                                    Toast.makeText(
                                        context,
                                        "Error setting up PIN. Please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Reset the state
                                    pin = ""
                                    confirmPin = ""
                                    stage = 0
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "PINs don't match. Try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                            pin = ""
                            confirmPin = ""
                            stage = 0
                        }
                    }
                }
            },
            title = if (stage == 0) "Enter new PIN" else "Confirm PIN",
            modifier = Modifier.fillMaxWidth()
        )
    }
}