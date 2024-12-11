package me.sm.spendwise.ui.screens



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import me.sm.spendwise.data.SecurityPreference

@Composable
fun SecurityVerificationScreen(
    onVerificationSuccess: () -> Unit
) {
    val context = LocalContext.current
    val securityPreference = remember { SecurityPreference(context) }
    val scope = rememberCoroutineScope()
    var pin by remember { mutableStateOf("") }
    var securityMethod by remember { mutableStateOf(SecurityMethod.NONE) }

    LaunchedEffect(Unit) {
        securityPreference.securityMethodFlow.collect { method ->
            securityMethod = method
            if (method == SecurityMethod.FINGERPRINT) {
                showBiometricPrompt(
                    context as FragmentActivity,
                    onSuccess = onVerificationSuccess
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (securityMethod) {
            SecurityMethod.PIN -> {
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4) pin = it },
                    label = { Text("Enter PIN") },
                    singleLine = true
                )

                Button(
                    onClick = {
                        scope.launch {
                            securityPreference.getPin().collect { savedPin ->
                                if (pin == savedPin) {
                                    onVerificationSuccess()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Verify")
                }
            }
            SecurityMethod.FINGERPRINT -> {
                Text("Use fingerprint to verify")
            }
            SecurityMethod.NONE -> {
                Text("No security method set")
            }
        }
    }
}