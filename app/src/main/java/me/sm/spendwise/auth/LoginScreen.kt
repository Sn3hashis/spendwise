package me.sm.spendwise.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import me.sm.spendwise.R
import android.app.Activity
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.common.api.ApiException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import me.sm.spendwise.ui.AppState
import me.sm.spendwise.data.SecurityPreference
import me.sm.spendwise.ui.Screen
import me.sm.spendwise.data.SecurityMethod
import kotlinx.coroutines.launch
import android.widget.Toast
import com.google.android.gms.auth.api.identity.Identity
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.compose.foundation.clickable
import android.util.Log
import androidx.compose.foundation.Image
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val securityPreference = remember { SecurityPreference(context) }
    val auth = remember { FirebaseAuth.getInstance() }
    val googleSignInClient = remember { getGoogleSignInClient(context) }

    val scope = rememberCoroutineScope()

    val googleAuthUiClient by remember {
        mutableStateOf(
            GoogleAuthUiClient(
                context = context,
                oneTapClient = Identity.getSignInClient(context)
            )
        )
    }

    val authManager = remember { FirebaseAuthManager(context) }

    val TAG = "LoginScreen"

    val launcher = rememberLauncherForActivityResult(
        contract = StartIntentSenderForResult(),
        onResult = { result ->
            Log.d(TAG, "Got activity result: ${result.resultCode}")
            Toast.makeText(
                context,
                "Processing sign-in result...",
                Toast.LENGTH_SHORT
            ).show()
            
            if(result.resultCode == Activity.RESULT_OK) {
                scope.launch {
                    try {
                        Log.d(TAG, "Processing sign-in result")
                        val signInResult = googleAuthUiClient.signInWithIntent(
                            intent = result.data ?: return@launch
                        )
                        Log.d(TAG, "Sign in result: $signInResult")
                        if (signInResult.data != null) {
                            AppState.currentUser = auth.currentUser
                            AppState.currentScreen = Screen.Main
                            Toast.makeText(
                                context,
                                "Sign in successful, redirecting...",
                                Toast.LENGTH_SHORT
                            ).show()
                            onLoginSuccess()
                        } else {
                            Log.e(TAG, "Sign in failed: ${signInResult.errorMessage}")
                            Toast.makeText(
                                context,
                                "Sign in failed: ${signInResult.errorMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error during sign in", e)
                        Toast.makeText(
                            context,
                            "Sign in error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Log.d(TAG, "Sign in cancelled or failed: ${result.resultCode}")
                Toast.makeText(
                    context,
                    "Sign in cancelled",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    scope.launch {
                        try {
                            val authResult = auth.signInWithCredential(credential).await()
                            val user = authResult.user
                            if (user != null) {
                                AppState.currentUser = user
                                AppState.currentScreen = Screen.Main
                                onLoginSuccess()
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Firebase auth failed", e)
                            Toast.makeText(
                                context,
                                "Authentication failed: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: ApiException) {
                    Log.e(TAG, "Google sign in failed", e)
                    Toast.makeText(
                        context,
                        "Google sign in failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome text with subtitle
        Column(
            modifier = Modifier.padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome Back",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Please sign in to continue",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
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
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            visualTransformation = if (isPasswordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible)
                            Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        // Add Forgot Password text
        Text(
            text = "Forgot Password?",
            color = Color(0xFFB4B4FF),
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
                .clickable { onForgotPasswordClick() }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Sign In button
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    try {
                        val result = authManager.signInWithEmailAndPassword(email, password)
                        if (result.data != null) {
                            onLoginSuccess()
                        } else {
                            errorMessage = result.errorMessage
                        }
                    } catch (e: Exception) {
                        errorMessage = e.message
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(32.dp),
            enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "Login",
                    fontSize = 16.sp
                )
            }
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

        // Google Sign In button
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
                    text = "Sign in with Google",
                    fontSize = 16.sp
                )
            }
        }

        // Sign up prompt at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account? ",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "Sign Up",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onSignUpClick)
            )
        }
    }
}

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google icon",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Continue with Google",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.web_client_id))
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}