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
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (Int, CharSequence) -> Unit
    ) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Verify Identity")
            .setSubtitle("Use your fingerprint to verify your identity")
            .setNegativeButtonText("Use PIN")
            .build()

        val biometricPrompt = BiometricPrompt(activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errorCode, errString)
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

    // Add constants for face authentication
    object BiometricAuthenticators {
        const val FACE = BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
    }
}