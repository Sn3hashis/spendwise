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
import me.sm.spendwise.navigation.Screen
import android.util.Log

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
    var selectedMethod by remember { mutableStateOf<SecurityMethod>(SecurityMethod.NONE) }
    var enrolledMethods by remember { mutableStateOf<Set<SecurityMethod>>(emptySet()) }
    var methodToVerify by remember { mutableStateOf<SecurityMethod?>(null) }

    LaunchedEffect(Unit) {
        Log.d("SecurityScreen", "Starting LaunchedEffect")
        
        // Collect all flows in parallel
        launch {
            securityPreference.getEnrolledMethodsFlow().collect { methods ->
                Log.d("SecurityScreen", "Enrolled methods: $methods")
                enrolledMethods = methods
            }
        }
        
        launch {
            securityPreference.getSecurityMethodFlow().collect { method ->
                Log.d("SecurityScreen", "Current security method: $method")
                selectedMethod = method ?: SecurityMethod.NONE
            }
        }
        
        launch {
            securityPreference.getPin().collect { storedPin ->
                Log.d("SecurityScreen", "PIN loaded: ${storedPin != null}")
                savedPin = storedPin ?: ""
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
                Log.d("SecurityScreen", "Checking PIN enrollment. Enrolled methods: $enrolledMethods")
                if (SecurityMethod.PIN in enrolledMethods) {
                    Log.d("SecurityScreen", "PIN is enrolled, showing verification")
                    methodToVerify = SecurityMethod.PIN
                    showPinVerification = true
                } else {
                    Log.d("SecurityScreen", "PIN not enrolled, navigating to setup")
                    scope.launch {
                        NavigationState.navigateTo(Screen.SecuritySetup)
                    }
                }
            }
            SecurityMethod.FINGERPRINT -> {
                BiometricUtils.showBiometricPrompt(
                    activity = context as FragmentActivity,
                    title = "Setup Fingerprint",
                    subtitle = "Confirm fingerprint to enable fingerprint authentication",
                    onSuccess = {
                        scope.launch {
                            securityPreference.saveSecurityMethod(SecurityMethod.FINGERPRINT)
                            securityPreference.addEnrolledMethod(SecurityMethod.FINGERPRINT)
                            selectedMethod = SecurityMethod.FINGERPRINT
                            Toast.makeText(
                                context,
                                "Fingerprint authentication enabled",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
            SecurityMethod.NONE -> {
                scope.launch {
                    securityPreference.saveSecurityMethod(SecurityMethod.NONE)
                    selectedMethod = SecurityMethod.NONE
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
                onPinChange = { newPin ->
                    pin = newPin
                    if (newPin.length == 4) {
                        Log.d("SecurityScreen", "PIN entered, length: 4")
                        if (newPin == savedPin) {
                            Log.d("SecurityScreen", "PIN verified successfully")
                            scope.launch {
                                methodToVerify?.let { method ->
                                    Log.d("SecurityScreen", "Saving security method: $method")
                                    securityPreference.saveSecurityMethod(method)
                                    selectedMethod = method
                                    showPinVerification = false
                                    methodToVerify = null
                                    pin = ""
                                    Toast.makeText(
                                        context,
                                        "PIN selected as security method",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Log.d("SecurityScreen", "PIN verification failed")
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
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
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