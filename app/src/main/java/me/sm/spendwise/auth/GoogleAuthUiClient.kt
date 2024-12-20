package me.sm.spendwise.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import me.sm.spendwise.R
class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth
    private val TAG = "GoogleAuthUiClient"

    suspend fun signIn(): IntentSender? {
        val result = try {
            Log.d(TAG, "Starting sign in process")
            oneTapClient.beginSignIn(
                BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(context.getString(R.string.web_client_id))
                            .build()
                    )
                    .setAutoSelectEnabled(false)
                    .build()
            ).await()
        } catch(e: Exception) {
            Log.e(TAG, "Error during beginSignIn", e)
            Toast.makeText(
                context,
                "Sign in error: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = try {
            Log.d(TAG, "Getting credentials from intent")
            oneTapClient.getSignInCredentialFromIntent(intent)
        } catch(e: Exception) {
            Log.e(TAG, "Error getting credentials", e)
            Toast.makeText(
                context,
                "Credential error: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            return SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
        
        val googleIdToken = credential.googleIdToken
        if(googleIdToken == null) {
            Log.e(TAG, "No ID token found")
            Toast.makeText(
                context,
                "No ID token found",
                Toast.LENGTH_LONG
            ).show()
            return SignInResult(
                data = null,
                errorMessage = "No ID token found"
            )
        }
        
        Log.d(TAG, "Got ID token, attempting Firebase auth")
        Toast.makeText(
            context,
            "Authenticating with Firebase...",
            Toast.LENGTH_SHORT
        ).show()
        
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            Log.d(TAG, "Firebase auth successful, user: ${user?.email}")
            Toast.makeText(
                context,
                "Sign in successful!",
                Toast.LENGTH_SHORT
            ).show()
            SignInResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName,
                        profilePictureUrl = photoUrl?.toString(),
                        email = email
                    )
                },
                errorMessage = null
            )
        } catch(e: Exception) {
            Log.e(TAG, "Firebase auth failed", e)
            Toast.makeText(
                context,
                "Firebase auth failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            if(e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
            profilePictureUrl = photoUrl?.toString(),
            email = email
        )
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}