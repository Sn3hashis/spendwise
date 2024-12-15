package me.sm.spendwise.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.sm.spendwise.R
import android.util.Patterns
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.draw.shadow
import me.sm.spendwise.ui.AppState
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.Identity

@Composable
fun SignUpScreen(
    onSignUpSuccess: (String) -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    // Add GoogleAuthUiClient
    val context = LocalContext.current
    val googleAuthUiClient = remember { GoogleAuthUiClient(
        context = context,
        oneTapClient = Identity.getSignInClient(context)
    ) }
    val scope = rememberCoroutineScope()
    val authManager = remember { FirebaseAuthManager(context) }
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isTermsAccepted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Validation states
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    // Password strength
    val passwordStrength = calculatePasswordStrength(password)

    // Google Sign In launcher
    val launcher = rememberLauncherForActivityResult(
        contract = StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                scope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    if (signInResult.data != null) {
                        AppState.currentUser = FirebaseAuth.getInstance().currentUser
                        onSignUpSuccess(signInResult.data.email ?: "")
                    } else {
                        Toast.makeText(
                            context,
                            signInResult.errorMessage ?: "Sign in failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    )

    // Add state for loading
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Add LaunchedEffect to handle errors
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            delay(3000)
            errorMessage = null
        }
    }

    // Move validateInputs inside the composable
    fun validateInputs(): Boolean {
        return when {
            name.isEmpty() -> {
                errorMessage = "Name is required"
                false
            }
            email.isEmpty() -> {
                errorMessage = "Email is required"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                errorMessage = "Invalid email format"
                false
            }
            password.isEmpty() -> {
                errorMessage = "Password is required"
                false
            }
            password.length < 8 -> {
                errorMessage = "Password must be at least 8 characters"
                false
            }
            !isTermsAccepted -> {
                errorMessage = "Please accept the terms and conditions"
                false
            }
            else -> true
        }
    }

    // Update the sign up button click handler
    val handleSignUp = {
        if (validateInputs()) {
            isLoading = true
            scope.launch {
                try {
                    authManager.signUpWithEmailAndPassword(
                        email = email,
                        password = password,
                        name = name,
                        onSuccess = {
                            isLoading = false
                            onSignUpSuccess(email)
                        },
                        onError = { error ->
                            isLoading = false
                            errorMessage = error
                        }
                    )
                } catch (e: Exception) {
                    isLoading = false
                    errorMessage = e.message ?: "An error occurred"
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top bar with back button and title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Sign Up",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Divider()

        // Content with padding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // Add divider with elevation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(1.dp)
                    )
                    .shadow(2.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))  // Reduced from 32.dp

            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Input fields with validation
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        emailError = validateEmail(it)
                    },
                    label = { Text("Email") },
                    isError = emailError != null,
                    supportingText = {
                        if (emailError != null) {
                            Text(
                                text = emailError!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            passwordError = validatePassword(it)
                        },
                        label = { Text("Password") },
                        isError = passwordError != null,
                        supportingText = {
                            if (passwordError != null) {
                                Text(
                                    text = passwordError!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None 
                                             else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility 
                                                else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        }
                    )

                    // Password strength indicator with reduced spacing
                    if (password.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))  // Reduced from 8.dp
                        PasswordStrengthIndicator(strength = passwordStrength)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))  // Reduced from 24.dp

                // Terms and conditions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isTermsAccepted,
                        onCheckedChange = { isTermsAccepted = it }
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("By signing up, you agree to the ")
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append("Terms of Service and Privacy Policy")
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))  // Reduced from 32.dp

                // Sign Up button
                Button(
                    onClick = handleSignUp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(32.dp),
                    enabled = !isLoading && isTermsAccepted && email.isNotEmpty() && password.isNotEmpty()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Sign Up",
                            fontSize = 16.sp
                        )
                    }
                }

                // Add error message display
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Or with divider
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f))
                    Text(
                        text = "Or with",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Divider(modifier = Modifier.weight(1f))
                }

                // Google Sign Up button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch
                                ).build()
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(32.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google icon",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Sign Up with Google",
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Login prompt at the bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Login",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(onClick = onLoginClick)
                )
            }
        }
    }
}

@Composable
private fun PasswordStrengthIndicator(strength: PasswordStrength) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = when {
                            index <= strength.ordinal -> strength.color
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        },
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
    
    Text(
        text = strength.label,
        color = strength.color,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp)
    )
}

enum class PasswordStrength(val label: String, val color: Color) {
    WEAK("Weak", Color.Red),
    FAIR("Fair", Color.Yellow),
    GOOD("Good", Color(0xFF4CAF50)),
    STRONG("Strong", Color(0xFF00C853))
}

private fun calculatePasswordStrength(password: String): PasswordStrength {
    if (password.length < 8) return PasswordStrength.WEAK
    
    var score = 0
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isLowerCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    
    return when (score) {
        0, 1 -> PasswordStrength.WEAK
        2 -> PasswordStrength.FAIR
        3 -> PasswordStrength.GOOD
        else -> PasswordStrength.STRONG
    }
}

private fun validateEmail(email: String): String? {
    return when {
        email.isEmpty() -> "Email is required"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
        else -> null
    }
}

private fun validatePassword(password: String): String? {
    return when {
        password.isEmpty() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters"
        !password.any { it.isUpperCase() } -> "Include at least one uppercase letter"
        !password.any { it.isLowerCase() } -> "Include at least one lowercase letter"
        !password.any { it.isDigit() } -> "Include at least one number"
        !password.any { !it.isLetterOrDigit() } -> "Include at least one special character"
        else -> null
    }
} 