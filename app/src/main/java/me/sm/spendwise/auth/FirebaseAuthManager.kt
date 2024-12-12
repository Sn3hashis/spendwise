package me.sm.spendwise.auth

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import me.sm.spendwise.ui.AppState
import javax.mail.*
import javax.mail.internet.*
import java.util.Properties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.sm.spendwise.data.PreferencesManager
import kotlinx.coroutines.withTimeout

class FirebaseAuthManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val OTP_VALIDITY_DURATION = 5 * 60 * 1000 // 5 minutes
    private val preferencesManager = PreferencesManager(context)
    private val sharedPreferences = context.getSharedPreferences("otp_prefs", Context.MODE_PRIVATE)

    private data class OtpData(
        val otp: String,
        val timestamp: Long
    )

    private fun saveOtpData(email: String, otpData: OtpData) {
        sharedPreferences.edit().apply {
            putString("${email}_otp", otpData.otp)
            putLong("${email}_timestamp", otpData.timestamp)
            apply()
        }
        Log.d("FirebaseAuthManager", "Saved OTP data to SharedPreferences: $otpData for email: $email")
    }

    private fun getOtpData(email: String): OtpData? {
        val otp = sharedPreferences.getString("${email}_otp", null)
        val timestamp = sharedPreferences.getLong("${email}_timestamp", -1)
        return if (otp != null && timestamp != -1L) {
            OtpData(otp, timestamp)
        } else {
            null
        }
    }

    private fun removeOtpData(email: String) {
        sharedPreferences.edit().apply {
            remove("${email}_otp")
            remove("${email}_timestamp")
            apply()
        }
        Log.d("FirebaseAuthManager", "Removed OTP data from SharedPreferences for email: $email")
    }

    // Email configuration
    private val emailProperties = Properties().apply {
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.port", "587")
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.ssl.trust", "smtp.gmail.com")
        put("mail.smtp.ssl.protocols", "TLSv1.2")
        
        // Add timeouts
        put("mail.smtp.connectiontimeout", "5000")  // 5 seconds connection timeout
        put("mail.smtp.timeout", "5000")            // 5 seconds socket read timeout
        put("mail.smtp.writetimeout", "5000")      // 5 seconds socket write timeout
    }

    private val emailUsername = "snehashismukherjeee@gmail.com"
    private val emailPassword = "gege fnxl ntec ixoc" // Make sure this is the correct App Password

    suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let {
                val otp = generateOTP()
                val lowercaseEmail = email.lowercase()
                Log.d("FirebaseAuthManager", "Generated OTP: $otp for email: $lowercaseEmail")
                
                val otpData = OtpData(otp, System.currentTimeMillis())
                saveOtpData(lowercaseEmail, otpData)
                Log.d("FirebaseAuthManager", "Stored OTP data: ${getOtpData(lowercaseEmail)}")

                if (sendOTP(email, otp)) {
                    Log.d("FirebaseAuthManager", "OTP sent successfully")
                    Log.d("FirebaseAuthManager", "Verifying OTP storage: ${getOtpData(lowercaseEmail)}")
                    onSuccess()
                } else {
                    Log.e("FirebaseAuthManager", "Failed to send OTP")
                    removeOtpData(lowercaseEmail)
                    onError("Failed to send verification code")
                }
            } ?: onError("Failed to create user")
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Sign up error", e)
            onError(e.message ?: "An error occurred during sign up")
        }
    }

    private suspend fun sendOTP(email: String, otp: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val session = Session.getInstance(emailProperties, object : Authenticator() {
                    override fun getPasswordAuthentication() =
                        PasswordAuthentication(emailUsername, emailPassword)
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(emailUsername))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
                    subject = "SpendWise Verification Code"
                    setText("""
                        Your SpendWise verification code is: $otp
                        
                        This code will expire in 5 minutes.
                        
                        If you didn't request this code, please ignore this email.
                    """.trimIndent())
                }

                withTimeout(10000) { // 10 second timeout for entire email operation
                    Transport.send(message)
                }
                
                Log.d("FirebaseAuthManager", "Email sent successfully with OTP: $otp")
                true
            } catch (e: Exception) {
                Log.e("FirebaseAuthManager", "Error sending OTP email", e)
                false
            }
        }
    }

    suspend fun resendVerificationEmail(): Boolean {
        return auth.currentUser?.email?.let { email ->
            val newOtp = generateOTP()
            val lowercaseEmail = email.lowercase()
            Log.d("FirebaseAuthManager", "Resending OTP: $newOtp for email: $lowercaseEmail")
            
            val otpData = OtpData(newOtp, System.currentTimeMillis())
            saveOtpData(lowercaseEmail, otpData)
            Log.d("FirebaseAuthManager", "Stored resent OTP data: ${getOtpData(lowercaseEmail)}")
            
            sendOTP(email, newOtp)
        } ?: false
    }

    suspend fun verifyOTP(otp: String): Boolean {
        val email = auth.currentUser?.email?.lowercase()
        if (email == null) {
            Log.e("FirebaseAuthManager", "No current user email found")
            return false
        }

        val otpData = getOtpData(email)
        Log.d("FirebaseAuthManager", "Verifying OTP: $otp for email: $email")
        Log.d("FirebaseAuthManager", "Stored OTP data: $otpData")

        if (otpData == null) {
            Log.e("FirebaseAuthManager", "No stored OTP data for email: $email")
            return false
        }

        val timeDiff = System.currentTimeMillis() - otpData.timestamp
        val isTimeValid = timeDiff <= OTP_VALIDITY_DURATION
        val isOtpMatch = otpData.otp == otp

        Log.d("FirebaseAuthManager", "Time difference: $timeDiff ms")
        Log.d("FirebaseAuthManager", "Is time valid: $isTimeValid")
        Log.d("FirebaseAuthManager", "OTP match: $isOtpMatch")

        val isValid = isOtpMatch && isTimeValid
        
        if (isValid) {
            preferencesManager.addVerifiedEmail(email)
            removeOtpData(email)
            Log.d("FirebaseAuthManager", "Email verified and stored: $email")
        } else {
            Log.e("FirebaseAuthManager", "OTP verification failed")
        }
        
        return isValid
    }

    private fun generateOTP(): String {
        return String.format("%06d", (100000..999999).random())
    }

    fun isEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
    }

    suspend fun checkEmailVerification(): Boolean {
        // Reload the user to get the latest status
        auth.currentUser?.reload()?.await()
        return auth.currentUser?.isEmailVerified ?: false
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): SignInResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                // Check verification status from persistent storage
                if (preferencesManager.isEmailVerified(email)) {
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
            } else {
                SignInResult(
                    data = null,
                    errorMessage = "Invalid credentials"
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

    suspend fun checkEmailVerified(): Boolean {
        return try {
            auth.currentUser?.reload()?.await()
            auth.currentUser?.isEmailVerified == true
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
} 