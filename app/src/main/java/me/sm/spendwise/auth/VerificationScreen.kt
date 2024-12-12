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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.alpha

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable

import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color

@Composable
fun OtpInput(
    otpText: String,
    onOtpTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(6) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .background(
                        color = if (index < otpText.length) 
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (index < otpText.length) 
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (index < otpText.length) otpText[index].toString() else "",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    // Hidden text field for keyboard input
    BasicTextField(
        value = otpText,
        onValueChange = { 
            if (it.length <= 6) onOtpTextChange(it.filter { char -> char.isDigit() })
        },
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0f),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}

@Composable
fun VerificationScreen(
    email: String,
    onBackClick: () -> Unit,
    onVerificationComplete: () -> Unit
) {
    var otpValue by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(300) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authManager = remember { FirebaseAuthManager() }

    // Send OTP when screen is first displayed
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            if (authManager.sendVerificationEmail(email)) {
                while (timeLeft > 0) {
                    delay(1000)
                    timeLeft--
                }
            } else {
                errorMessage = "Failed to send verification email"
            }
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .statusBarsPadding()
    ) {
        // Back button and title
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
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Text(
                text = "Verification",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Enter your\nVerification Code",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // OTP Input
        OtpInput(
            otpText = otpValue,
            onOtpTextChange = { otpValue = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Timer
        Text(
            text = String.format("%02d:%02d", timeLeft/60, timeLeft%60),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email info text
        Text(
            text = "We send verification code to your\nemail ${maskEmail(email)}. You can\ncheck your inbox.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Resend link
        Text(
            text = "I didn't received the code? Send again",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                scope.launch {
                    if (authManager.resendVerificationEmail()) {
                        timeLeft = 300
                        Toast.makeText(context, "New code sent", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Verify button
        Button(
            onClick = {
                scope.launch {
                    if (authManager.verifyOTP(otpValue)) {
                        onVerificationComplete()
                    } else {
                        Toast.makeText(context, "Invalid verification code", Toast.LENGTH_SHORT).show()
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
            enabled = otpValue.length == 6
        ) {
            Text(
                text = "Verify",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Add error message display
        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Add loading indicator
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

private fun maskEmail(email: String): String {
    val parts = email.split("@")
    if (parts.size != 2) return email
    val username = parts[0]
    val domain = parts[1]
    val maskedUsername = when {
        username.length <= 2 -> username
        username.length <= 4 -> username.take(2) + "*".repeat(username.length - 2)
        else -> username.take(2) + "*".repeat(3) + username.takeLast(2)
    }
    return "$maskedUsername@$domain"
} 