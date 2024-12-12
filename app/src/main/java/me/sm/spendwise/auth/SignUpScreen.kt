package me.sm.spendwise.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import kotlinx.coroutines.launch
import me.sm.spendwise.R
import android.util.Patterns
import androidx.compose.ui.graphics.Color

@Composable
fun SignUpScreen(
    onSignUpSuccess: (String) -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isTermsAccepted by remember { mutableStateOf(false) }
    
    // Validation states
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    // Password strength
    val passwordStrength = calculatePasswordStrength(password)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authManager = remember { FirebaseAuthManager() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .statusBarsPadding()
    ) {
        // Top bar with back button and title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            
            Text(
                text = "Sign Up",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

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

            // Password strength indicator
            if (password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                PasswordStrengthIndicator(strength = passwordStrength)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Terms and conditions
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isTermsAccepted,
                onCheckedChange = { isTermsAccepted = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                )
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

        Spacer(modifier = Modifier.height(32.dp))

        // Sign Up button
        Button(
            onClick = {
                scope.launch {
                    try {
                        val result = authManager.signUpWithEmail(email, password, name)
                        if (result.data != null) {
                            onSignUpSuccess(email)
                        } else {
                            Toast.makeText(context, result.errorMessage ?: "Sign up failed", 
                                         Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, e.message ?: "Sign up failed", 
                                     Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            ),
            enabled = name.isNotBlank() && email.isNotBlank() && 
                     password.isNotBlank() && isTermsAccepted
        ) {
            Text(
                text = "Sign Up",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Or with divider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
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
            onClick = { /* Handle Google sign up */ },
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

        // Login prompt
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
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