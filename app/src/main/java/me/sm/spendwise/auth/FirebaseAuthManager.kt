package me.sm.spendwise.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import me.sm.spendwise.ui.AppState

class FirebaseAuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val functions = Firebase.functions
    private var storedOTP: String? = null

    suspend fun signUpWithEmail(email: String, password: String, name: String): SignInResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            
            // Update profile with name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            result.user?.updateProfile(profileUpdates)?.await()
            
            // Generate and store OTP
            storedOTP = generateOTP()
            sendOTPEmail(email, storedOTP!!)
            
            SignInResult(
                data = result.user?.run {
                    UserData(
                        userId = uid,
                        username = name,
                        profilePictureUrl = photoUrl?.toString(),
                        email = email
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    private fun generateOTP(): String {
        return String.format("%06d", (0..999999).random())
    }

    private suspend fun sendOTPEmail(email: String, otp: String) {
        try {
            val data = hashMapOf(
                "email" to email,
                "otp" to otp
            )
            
            functions
                .getHttpsCallable("sendOTPEmail")
                .call(data)
                .await()
            
            Log.d("FirebaseAuthManager", "OTP email sent successfully to $email")
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Error sending OTP email", e)
            throw e
        }
    }

    suspend fun verifyOTP(otp: String): Boolean {
        return otp == storedOTP
    }

    suspend fun sendVerificationEmail(email: String): Boolean {
        storedOTP = generateOTP()
        return try {
            sendOTPEmail(email, storedOTP!!)
            true
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Failed to send verification email", e)
            false
        }
    }

    suspend fun resendVerificationEmail(): Boolean {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("FirebaseAuthManager", "No user logged in")
            return false
        }

        storedOTP = generateOTP()
        return try {
            sendOTPEmail(currentUser.email!!, storedOTP!!)
            true
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Failed to resend verification email", e)
            false
        }
    }

    suspend fun signInWithEmail(email: String, password: String): SignInResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user?.isEmailVerified == true) {
                AppState.currentUser = result.user
                SignInResult(
                    data = result.user?.run {
                        UserData(
                            userId = uid,
                            username = displayName,
                            profilePictureUrl = photoUrl?.toString(),
                            email = email
                        )
                    },
                    errorMessage = null
                )
            } else {
                SignInResult(
                    data = null,
                    errorMessage = "Please verify your email first"
                )
            }
        } catch (e: Exception) {
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified == true
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
} 