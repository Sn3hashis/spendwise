package me.sm.spendwise.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit,
    onEmailSent: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authManager = remember { FirebaseAuthManager(context) }

    val isEmailValid = remember(email) {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 48.dp)
            .padding(horizontal = 24.dp)
    ) {
        // Header with back button and title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Text(
                text = "Forgot Password",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title and description
        Text(
            text = "Don't worry.",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Enter your email and we'll\nsend you a link to reset your\npassword.",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { newEmail -> 
                email = newEmail
                errorMessage = when {
                    newEmail.isEmpty() -> "Email is required"
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches() -> "Invalid email format"
                    else -> null
                }
            },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            isError = errorMessage != null
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Continue button
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = null  // Clear any previous errors
                    try {
                        // Try to send reset email directly
                        // Firebase will throw an error if email doesn't exist
                        authManager.sendPasswordResetEmail(email)
                        onEmailSent(email)  // Only called if email exists and reset link is sent
                    } catch (e: Exception) {
                        errorMessage = when {
                            e.message?.contains("user-not-found") == true -> "No account exists with this email"
                            e.message?.contains("invalid-email") == true -> "Invalid email format"
                            else -> e.message ?: "An error occurred"
                        }
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = email.isNotBlank() && isEmailValid && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
} 