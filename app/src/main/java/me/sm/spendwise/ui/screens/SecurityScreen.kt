package me.sm.spendwise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import me.sm.spendwise.data.SecurityPreference
import me.sm.spendwise.utils.BiometricUtils
import kotlinx.coroutines.launch
import me.sm.spendwise.data.SecurityMethod
import androidx.biometric.BiometricManager
import me.sm.spendwise.ui.components.PinEntry
import me.sm.spendwise.navigation.NavigationState
import me.sm.spendwise.ui.Screen
import android.util.Log
import androidx.biometric.BiometricPrompt

@Composable
fun SecurityScreen(
    onBackPress: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var showPinVerification by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    val context = LocalContext.current
    val securityPreference = remember { SecurityPreference(context) }
    val scope = rememberCoroutineScope()
    var savedPin by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf(SecurityMethod.NONE) }
    var enrolledMethods by remember { mutableStateOf<Set<SecurityMethod>>(emptySet()) }
    var methodToVerify by remember { mutableStateOf<SecurityMethod?>(null) }
    var currentSecurityMethod by remember { mutableStateOf(SecurityMethod.PIN) }
    var hasPinSetup by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Log.d("SecurityScreen", "Starting LaunchedEffect")
        
        // Get current security method first and set it as selected
        val method = securityPreference.getCurrentSecurityMethod()
        currentSecurityMethod = method
        selectedMethod = method
        Log.d("SecurityScreen", "Initial security method: $method")
        
        // Then collect enrolled methods
        launch {
            securityPreference.getEnrolledMethodsFlow().collect { methods ->
                Log.d("SecurityScreen", "Enrolled methods: $methods")
                enrolledMethods = methods
            }
        }
        
        // Load PIN without changing security method
        launch {
            securityPreference.loadPin().collect { storedPin ->
                Log.d("SecurityScreen", "PIN loaded: ${storedPin != null}")
                savedPin = storedPin ?: ""
                if (storedPin != null) {
                    hasPinSetup = true
                }
            }
        }

        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    fun handleSecurityMethodSelection(method: SecurityMethod) {
        Log.d("SecurityScreen", "Method selected: $method")
        when (method) {
            SecurityMethod.PIN -> {
                if (hasPinSetup) {
                    Log.d("SecurityScreen", "PIN is set up, showing verification")
                    showPinVerification = true
                    methodToVerify = method
                } else {
                    Log.d("SecurityScreen", "PIN not enrolled, navigating to setup")
                    NavigationState.navigateTo(Screen.SecuritySetup)
                }
            }
            SecurityMethod.FINGERPRINT -> {
                BiometricUtils.showBiometricPrompt(
                    activity = context as FragmentActivity,
                    onSuccess = {
                        scope.launch {
                            securityPreference.saveSecurityMethod(SecurityMethod.FINGERPRINT)
                            selectedMethod = SecurityMethod.FINGERPRINT
                            currentSecurityMethod = SecurityMethod.FINGERPRINT
                            Toast.makeText(
                                context,
                                "Fingerprint authentication enabled",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onError = { errorCode, _ ->
                        if (errorCode == BiometricPrompt.ERROR_CANCELED) {
                            // User canceled, revert selection
                            selectedMethod = SecurityMethod.PIN
                            currentSecurityMethod = SecurityMethod.PIN
                        }
                        Toast.makeText(
                            context,
                            "Fingerprint setup failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
            SecurityMethod.NONE -> {
                scope.launch {
                    securityPreference.saveSecurityMethod(SecurityMethod.NONE)
                    selectedMethod = SecurityMethod.NONE
                    currentSecurityMethod = SecurityMethod.NONE
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("SecurityScreen", "SecurityScreen disposed")
        }
    }

    if (showPinVerification) {
        Log.d("SecurityScreen", "Showing PIN verification UI")
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Verify PIN",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PinEntry(
                pin = pin,
                onPinChange = { newPin -> pin = newPin },
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { 
                    Log.d("SecurityScreen", "PIN verification cancelled")
                    showPinVerification = false
                    methodToVerify = null
                    pin = ""
                }
            ) {
                Text("Cancel")
            }
        }
    } else {
        Log.d("SecurityScreen", "Showing main security UI")
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Top Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    IconButton(
                        onClick = onBackPress,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = "Security",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Divider()

                // Security Options - Show preferred method first
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Show selected method first if it's enrolled
                    if (selectedMethod != SecurityMethod.NONE && selectedMethod in enrolledMethods) {
                        SecurityOptionItem(
                            title = getMethodDisplayName(selectedMethod),
                            isSelected = true,
                            isEnrolled = true,
                            onClick = { handleSecurityMethodSelection(selectedMethod) }
                        )
                    }

                    // Show other enrolled methods
                    enrolledMethods
                        .filter { it != selectedMethod }
                        .forEach { method ->
                            SecurityOptionItem(
                                title = getMethodDisplayName(method),
                                isSelected = method == selectedMethod,
                                isEnrolled = true,
                                onClick = { handleSecurityMethodSelection(method) }
                            )
                        }

                    // Show remaining methods
                    SecurityMethod.values()
                        .filter { it != SecurityMethod.NONE && it !in enrolledMethods }
                        .forEach { method ->
                            SecurityOptionItem(
                                title = getMethodDisplayName(method),
                                isSelected = method == selectedMethod,
                                isEnrolled = false,
                                onClick = { handleSecurityMethodSelection(method) }
                            )
                        }
                }
            }
        }
    }
}

@Composable
private fun SecurityOptionItem(
    title: String,
    isSelected: Boolean,
    isEnrolled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (isEnrolled) {
                Text(
                    text = "Enrolled",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        if (isSelected) {
            Checkbox(
                checked = true,
                onCheckedChange = null, // Read-only checkbox
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

private fun getMethodDisplayName(method: SecurityMethod): String {
    return when (method) {
        SecurityMethod.PIN -> "PIN"
        SecurityMethod.FINGERPRINT -> "Fingerprint"
        SecurityMethod.NONE -> "None"
    }
}