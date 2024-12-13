package me.sm.spendwise.utils

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricUtils {
    private const val TAG = "BiometricUtils"

    fun showBiometricPrompt(
        activity: Activity,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onError: (Int, CharSequence) -> Unit = { _, _ -> },
        onAuthenticationFailed: () -> Unit = { },
        authenticators: Int = BiometricManager.Authenticators.BIOMETRIC_STRONG
    ) {
        try {
            if (activity !is FragmentActivity) {
                val error = "This feature requires FragmentActivity"
                Log.e(TAG, error)
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
                return
            }

            val biometricManager = BiometricManager.from(activity)
            
            when (biometricManager.canAuthenticate(authenticators)) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    val executor = ContextCompat.getMainExecutor(activity)
                    val biometricPrompt = BiometricPrompt(activity as FragmentActivity, executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                super.onAuthenticationSucceeded(result)
                                Log.d(TAG, "Authentication succeeded")
                                onSuccess()
                            }

                            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                super.onAuthenticationError(errorCode, errString)
                                Log.e(TAG, "Authentication error: $errorCode - $errString")
                                onError(errorCode, errString)
                            }

                            override fun onAuthenticationFailed() {
                                super.onAuthenticationFailed()
                                Log.w(TAG, "Authentication failed")
                                onAuthenticationFailed()
                                Toast.makeText(activity, "Authentication failed", Toast.LENGTH_SHORT).show()
                            }
                        })

                    val promptBuilder = BiometricPrompt.PromptInfo.Builder()
                        .setTitle(title)
                        .setSubtitle(subtitle)
                        .setAllowedAuthenticators(authenticators)
                        .setNegativeButtonText("Use PIN")

                    try {
                        biometricPrompt.authenticate(promptBuilder.build())
                    } catch (e: Exception) {
                        Log.e(TAG, "Error during biometric prompt: ${e.message}", e)
                        Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    val error = "Fingerprint hardware not available"
                    Log.e(TAG, error)
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    val error = "Biometric hardware unavailable"
                    Log.e(TAG, error)
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    Log.i(TAG, "No biometric credentials enrolled, launching enrollment")
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            authenticators)
                    }
                    activity.startActivity(enrollIntent)
                }
                else -> {
                    val error = "Biometric authentication not available"
                    Log.e(TAG, error)
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in showBiometricPrompt: ${e.message}", e)
            Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Add constants for face authentication
    object BiometricAuthenticators {
        const val FACE = BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
    }
}