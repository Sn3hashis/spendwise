package me.sm.spendwise.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "security_prefs")

class SecurityPreference(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val sharedPreferences = context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    companion object {
        private const val PIN_KEY = "pin_code"
        private const val HAS_PIN_KEY = "has_pin"
        private const val ENROLLED_METHODS_KEY = "enrolled_methods"
        private const val CURRENT_METHOD_KEY = "current_method"
    }

    private var cachedPin: String? = null
    private var cachedSecurityMethod: SecurityMethod? = null

    suspend fun savePin(pin: String) {
        Log.d("SecurityPreference", "Saving PIN")
        
        // First save locally
        sharedPreferences.edit()
            .putString(PIN_KEY, pin)
            .putBoolean(HAS_PIN_KEY, true)
            .apply()
        cachedPin = pin
        
        // Add PIN as enrolled method
        addEnrolledMethod(SecurityMethod.PIN)
        saveSecurityMethod(SecurityMethod.PIN)

        // Then save to Firebase
        val user = auth.currentUser
        if (user != null) {
            try {
                Log.d("SecurityPreference", "Saving PIN to Firebase for user: ${user.uid}")
                database.child("users")
                    .child(user.uid)
                    .child("pin")
                    .setValue(pin)
                    .await()
                Log.d("SecurityPreference", "PIN saved to Firebase successfully")
            } catch (e: Exception) {
                Log.e("SecurityPreference", "Error saving PIN to Firebase: ${e.message}")
            }
        }
    }

    suspend fun loadPin(): Flow<String?> = flow {
        // Try local first
        getLocalPin()?.let {
            emit(it)
            return@flow
        }

        // Quick Firebase check
        try {
            withTimeout(500) {
                val snapshot = database.child("users")
                    .child(auth.currentUser?.uid ?: "")
                    .child("pin")
                    .get()
                    .await()

                val firebasePin = snapshot.value as? String
                if (!firebasePin.isNullOrEmpty()) {
                    // Only cache the PIN locally, don't change security method
                    sharedPreferences.edit()
                        .putString(PIN_KEY, firebasePin)
                        .putBoolean(HAS_PIN_KEY, true)
                        .apply()
                    cachedPin = firebasePin
                    emit(firebasePin)
                } else {
                    emit(null)
                }
            }
        } catch (e: Exception) {
            Log.e("SecurityPreference", "Error loading PIN", e)
            emit(null)
        }
    }

    suspend fun clearPin() {
        cachedPin = null
        sharedPreferences.edit()
            .remove(PIN_KEY)
            .remove(HAS_PIN_KEY)
            .apply()

        val user = auth.currentUser
        if (user != null) {
            try {
                database.child("users")
                    .child(user.uid)
                    .child("pin")
                    .removeValue()
                    .await()
            } catch (e: Exception) {
                Log.e("SecurityPreference", "Error clearing PIN from Firebase", e)
            }
        }
    }

    suspend fun addEnrolledMethod(method: SecurityMethod) {
        val currentMethods = sharedPreferences.getStringSet(ENROLLED_METHODS_KEY, mutableSetOf()) ?: mutableSetOf()
        currentMethods.add(method.name)
        sharedPreferences.edit()
            .putStringSet(ENROLLED_METHODS_KEY, currentMethods)
            .apply()
    }

    fun getEnrolledMethodsFlow(): Flow<Set<SecurityMethod>> = flow {
        val methodNames = sharedPreferences.getStringSet(ENROLLED_METHODS_KEY, emptySet()) ?: emptySet()
        val methods = methodNames.mapNotNull { methodName ->
            try {
                SecurityMethod.valueOf(methodName)
            } catch (e: IllegalArgumentException) {
                null
            }
        }.toSet()
        emit(methods)
    }

    suspend fun removeEnrolledMethod(method: SecurityMethod) {
        val currentMethods = sharedPreferences.getStringSet(ENROLLED_METHODS_KEY, mutableSetOf()) ?: mutableSetOf()
        currentMethods.remove(method.name)
        sharedPreferences.edit()
            .putStringSet(ENROLLED_METHODS_KEY, currentMethods)
            .apply()
    }

    fun getSecurityMethodFlow(): Flow<SecurityMethod?> = flow {
        val methodName = sharedPreferences.getString(CURRENT_METHOD_KEY, null)
        val method = methodName?.let {
            try {
                SecurityMethod.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
        emit(method)
    }

    suspend fun saveSecurityMethod(method: SecurityMethod) {
        Log.d("SecurityPreference", "Saving security method: $method")
        
        // Save locally first
        cachedSecurityMethod = method
        sharedPreferences.edit()
            .putString(CURRENT_METHOD_KEY, method.name)
            .apply()

        // Add to enrolled methods if not already enrolled
        if (method != SecurityMethod.NONE) {
            addEnrolledMethod(method)
        }

        // Then save to Firebase
        val user = auth.currentUser
        if (user != null) {
            try {
                database.child("users")
                    .child(user.uid)
                    .child("security_method")
                    .setValue(method.name)
                    .await()
                Log.d("SecurityPreference", "Security method saved to Firebase: $method")
            } catch (e: Exception) {
                Log.e("SecurityPreference", "Error saving security method to Firebase", e)
            }
        }
    }

    suspend fun clearSecurityMethod() {
        cachedSecurityMethod = null
        sharedPreferences.edit()
            .remove(CURRENT_METHOD_KEY)
            .apply()

        val user = auth.currentUser
        if (user != null) {
            try {
                database.child("users")
                    .child(user.uid)
                    .child("security_method")
                    .removeValue()
                    .await()
            } catch (e: Exception) {
                Log.e("SecurityPreference", "Error clearing security method from Firebase", e)
            }
        }
    }

    suspend fun clearAll() {
        // Clear all local data
        cachedPin = null
        cachedSecurityMethod = null
        sharedPreferences.edit().clear().apply()
    }

    fun hasPinInLocalStorage(): Boolean {
        return cachedPin != null || sharedPreferences.getString(PIN_KEY, null) != null
    }

    suspend fun syncWithFirebase() {
        val user = auth.currentUser ?: return
        try {
            Log.d("SecurityPreference", "Syncing with Firebase for user: ${user.uid}")
            val snapshot = database.child("users")
                .child(user.uid)
                .child("pin")
                .get()
                .await()

            val firebasePin = snapshot.value as? String
            if (firebasePin != null) {
                Log.d("SecurityPreference", "Found PIN in Firebase: ${firebasePin.take(1)}***")
                // Save PIN locally
                sharedPreferences.edit()
                    .putString(PIN_KEY, firebasePin)
                    .putBoolean(HAS_PIN_KEY, true)
                    .apply()
                cachedPin = firebasePin
                
                // Set as enrolled method
                addEnrolledMethod(SecurityMethod.PIN)
                saveSecurityMethod(SecurityMethod.PIN)
            } else {
                Log.d("SecurityPreference", "No PIN found in Firebase for user: ${user.uid}")
            }
        } catch (e: Exception) {
            Log.e("SecurityPreference", "Error syncing with Firebase: ${e.message}")
        }
    }

    // When user logs in or signs up
    suspend fun initializeSession(pin: String) {
        cachedPin = pin
        savePin(pin)
    }

    // When user logs out
    suspend fun clearSession() {
        clearAll()
    }

    // Add this method for actually deleting account data
    suspend fun deleteAccountData() {
        val user = auth.currentUser ?: return
        try {
            // Delete all user data from Firebase
            database.child("users")
                .child(user.uid)
                .removeValue()
                .await()
            
            // Clear local data
            clearAll()
        } catch (e: Exception) {
            Log.e("SecurityPreference", "Error deleting account data: ${e.message}")
        }
    }

    suspend fun hasPinSetup(): Boolean {
        // First check cache
        if (cachedPin != null) {
            addEnrolledMethod(SecurityMethod.PIN)
            return true
        }

        // Then check local storage
        val localPin = sharedPreferences.getString(PIN_KEY, null)
        if (!localPin.isNullOrEmpty()) {
            Log.d("SecurityPreference", "Found PIN in local storage")
            cachedPin = localPin
            addEnrolledMethod(SecurityMethod.PIN)
            return true
        }

        // Finally check Firebase
        val user = auth.currentUser ?: return false
        return try {
            Log.d("SecurityPreference", "Checking Firebase for PIN")
            val snapshot = database.child("users")
                .child(user.uid)
                .child("pin")
                .get()
                .await()

            val firebasePin = snapshot.value as? String
            if (!firebasePin.isNullOrEmpty()) {
                Log.d("SecurityPreference", "Found PIN in Firebase: ${firebasePin.take(1)}***")
                // Cache locally
                sharedPreferences.edit()
                    .putString(PIN_KEY, firebasePin)
                    .putBoolean(HAS_PIN_KEY, true)
                    .apply()
                cachedPin = firebasePin
                addEnrolledMethod(SecurityMethod.PIN)
                true
            } else {
                Log.d("SecurityPreference", "No PIN found in Firebase")
                false
            }
        } catch (e: Exception) {
            Log.e("SecurityPreference", "Error checking PIN setup", e)
            false
        }
    }

    suspend fun verifyPin(enteredPin: String): Boolean {
        // First check cache
        if (cachedPin != null) {
            return cachedPin == enteredPin
        }

        // Then check local storage
        val localPin = sharedPreferences.getString(PIN_KEY, null)
        if (!localPin.isNullOrEmpty()) {
            cachedPin = localPin
            return localPin == enteredPin
        }

        // Finally check Firebase
        val user = auth.currentUser ?: return false
        return try {
            val snapshot = database.child("users")
                .child(user.uid)
                .child("pin")
                .get()
                .await()

            val firebasePin = snapshot.value as? String
            if (!firebasePin.isNullOrEmpty()) {
                // Cache locally
                sharedPreferences.edit()
                    .putString(PIN_KEY, firebasePin)
                    .putBoolean(HAS_PIN_KEY, true)
                    .apply()
                cachedPin = firebasePin
                firebasePin == enteredPin
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("SecurityPreference", "Error verifying PIN", e)
            false
        }
    }

    // Get PIN from local storage only
    fun getLocalPin(): String? {
        // First check memory cache
        cachedPin?.let { return it }
        
        // Then check SharedPreferences
        return sharedPreferences.getString(PIN_KEY, null)?.also {
            cachedPin = it // Cache for future use
        }
    }

    // Cache PIN locally
    fun cachePin(pin: String) {
        cachedPin = pin
        sharedPreferences.edit()
            .putString(PIN_KEY, pin)
            .putBoolean(HAS_PIN_KEY, true)
            .apply() // apply() is already asynchronous
    }

    // Get current security method synchronously
    fun getCurrentSecurityMethod(): SecurityMethod {
        // First check cache
        cachedSecurityMethod?.let { return it }

        // Then check local storage
        val methodName = sharedPreferences.getString(CURRENT_METHOD_KEY, null)
        return if (methodName != null) {
            try {
                SecurityMethod.valueOf(methodName).also {
                    cachedSecurityMethod = it
                }
            } catch (e: IllegalArgumentException) {
                SecurityMethod.PIN
            }
        } else {
            SecurityMethod.PIN
        }
    }

    // Load security method from Firebase if needed
    suspend fun loadSecurityMethod(): Flow<SecurityMethod> = flow {
        // First try local
        val localMethod = getCurrentSecurityMethod()
        emit(localMethod)

        // Then check Firebase
        try {
            withTimeout(500) {
                val snapshot = database.child("users")
                    .child(auth.currentUser?.uid ?: "")
                    .child("security_method")
                    .get()
                    .await()

                val methodName = snapshot.value as? String
                if (!methodName.isNullOrEmpty()) {
                    val method = SecurityMethod.valueOf(methodName)
                    cachedSecurityMethod = method
                    sharedPreferences.edit()
                        .putString(CURRENT_METHOD_KEY, method.name)
                        .apply()
                    emit(method)
                }
            }
        } catch (e: Exception) {
            Log.e("SecurityPreference", "Error loading security method", e)
        }
    }

    // Add this method to initialize security method on app start
    suspend fun initializeSecurityMethod() {
        try {
            // First get local method
            val localMethod = getCurrentSecurityMethod()
            Log.d("SecurityPreference", "Local security method: $localMethod")
            cachedSecurityMethod = localMethod

            // Then try to get from Firebase with longer timeout
            val user = auth.currentUser
            if (user != null) {
                try {
                    withTimeout(2000) { // Increased timeout to 2 seconds
                        val snapshot = database.child("users")
                            .child(user.uid)
                            .child("security_method")
                            .get()
                            .await()

                        val methodName = snapshot.value as? String
                        if (!methodName.isNullOrEmpty()) {
                            val firebaseMethod = SecurityMethod.valueOf(methodName)
                            if (firebaseMethod != localMethod) {
                                Log.d("SecurityPreference", "Firebase security method different from local, updating to: $firebaseMethod")
                                cachedSecurityMethod = firebaseMethod
                                sharedPreferences.edit()
                                    .putString(CURRENT_METHOD_KEY, firebaseMethod.name)
                                    .apply()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SecurityPreference", "Error loading security method from Firebase, keeping local method: $localMethod", e)
                }
            }
        } catch (e: Exception) {
            Log.e("SecurityPreference", "Error in initializeSecurityMethod", e)
            // Default to PIN on error
            cachedSecurityMethod = SecurityMethod.PIN
        }

        // Log final security method
        val finalMethod = getCurrentSecurityMethod()
        Log.d("SecurityPreference", "Final security method: $finalMethod")
    }
}

enum class SecurityMethod {
    NONE, PIN, FINGERPRINT
}