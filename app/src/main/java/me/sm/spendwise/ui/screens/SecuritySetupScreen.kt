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

@Composable
fun SecuritySetupScreen(
    onSetupComplete: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf<SecurityMethod?>(null) }
    var pin by remember { mutableStateOf("") }
    var showPinInput by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val securityPreference = remember { SecurityPreference(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Setup Security",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 32.dp)
        )

        Text(
            text = "Choose your preferred security method",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // PIN Option
        Button(
            onClick = {
                selectedMethod = SecurityMethod.PIN
                showPinInput = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Setup PIN")
        }

        // Fingerprint Option
        if (BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            == BiometricManager.BIOMETRIC_SUCCESS) {
            Button(
                onClick = {
                    selectedMethod = SecurityMethod.FINGERPRINT
                    showBiometricPrompt(
                        context as FragmentActivity,
                        onSuccess = {
                            securityPreference.saveSecurityMethod(SecurityMethod.FINGERPRINT)
                            onSetupComplete()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Setup Fingerprint")
            }
        }

        if (showPinInput) {
            OutlinedTextField(
                value = pin,
                onValueChange = {
                    if (it.length <= 4) pin = it
                },
                label = { Text("Enter 4-digit PIN") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            Button(
                onClick = {
                    if (pin.length == 4) {
                        securityPreference.savePin(pin)
                        securityPreference.saveSecurityMethod(SecurityMethod.PIN)
                        onSetupComplete()
                    } else {
                        Toast.makeText(context, "Please enter a 4-digit PIN", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = pin.length == 4,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm PIN")
            }
        }
    }
}

private fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Setup Fingerprint")
        .setSubtitle("Scan your fingerprint to setup")
        .setNegativeButtonText("Cancel")
        .build()

    biometricPrompt.authenticate(promptInfo)
}