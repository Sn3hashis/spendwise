package me.sm.spendwise.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "security_prefs")

class SecurityPreference(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val dataStore = context.dataStore

    private val pinKey = stringPreferencesKey("pin")
    private val enrolledMethodsKey = stringSetPreferencesKey("enrolled_methods")
    private val currentMethodKey = stringPreferencesKey("current_method")

    private var cachedPin: String? = null
    private var cachedSecurityMethod: SecurityMethod? = null

    suspend fun savePin(pin: String) {
        Log.d("SecurityPreference", "Saving PIN")
        
        // First save locally
        dataStore.edit { preferences ->
            preferences[pinKey] = pin
        }
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
        // First check cache
        if (cachedPin != null) {
            Log.d("SecurityPreference", "Emitting cached PIN")
            emit(cachedPin)
            return@flow
        }

        // Then check local storage
        val localPin = dataStore.data.first()[pinKey]
        if (localPin != null) {
            Log.d("SecurityPreference", "Found PIN in local storage")
            cachedPin = localPin
            emit(localPin)
            return@flow
        }

        // Finally check Firebase
        val user = auth.currentUser ?: run {
            Log.d("SecurityPreference", "No user logged in")
            emit(null)
            return@flow
        }

        try {
            Log.d("SecurityPreference", "Checking Firebase for PIN")
            val snapshot = database.child("users")
                .child(user.uid)
                .child("pin")
                .get()
                .await()

            val firebasePin = snapshot.value as? String
            if (firebasePin != null) {
                Log.d("SecurityPreference", "Found PIN in Firebase")
                // Cache locally
                dataStore.edit { preferences ->
                    preferences[pinKey] = firebasePin
                }
                cachedPin = firebasePin
                
                // Set as enrolled method
                addEnrolledMethod(SecurityMethod.PIN)
                saveSecurityMethod(SecurityMethod.PIN)
                
                emit(firebasePin)
            } else {
                Log.d("SecurityPreference", "No PIN found in Firebase")
                emit(null)
            }
        } catch (e: Exception) {
            Log.e("SecurityPreference", "Error loading PIN from Firebase", e)
            emit(null)
        }
    }

    suspend fun hasPinSetup(): Boolean {
        // First check cache
        if (cachedPin != null) {
            Log.d("SecurityPreference", "PIN found in cache")
            return true
        }

        // Then check local storage
        val localPin = dataStore.data.first()[pinKey]
        if (localPin != null) {
            Log.d("SecurityPreference", "PIN found in local storage")
            cachedPin = localPin
            return true
        }

        // Finally check Firebase
        val user = auth.currentUser ?: return false
        return try {
            Log.d("SecurityPreference", "Checking PIN in Firebase for user: ${user.uid}")
            val snapshot = database.child("users")
                .child(user.uid)
                .child("pin")
                .get()
                .await()

            val firebasePin = snapshot.value as? String
            if (firebasePin != null) {
                Log.d("SecurityPreference", "Found PIN in Firebase: ${firebasePin.take(1)}***")
                // Cache locally
                dataStore.edit { preferences ->
                    preferences[pinKey] = firebasePin
                }
                cachedPin = firebasePin
                
                // Set as enrolled method
                addEnrolledMethod(SecurityMethod.PIN)
                saveSecurityMethod(SecurityMethod.PIN)
                
                true
            } else {
                Log.d("SecurityPreference", "No PIN found in Firebase for user: ${user.uid}")
                false
            }
        } catch (e: Exception) {
            Log.e("SecurityPreference", "Error checking PIN in Firebase: ${e.message}")
            false
        }
    }

    suspend fun clearPin() {
        cachedPin = null
        dataStore.edit { preferences ->
            preferences.remove(pinKey)
        }

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
        context.dataStore.edit { preferences ->
            val currentMethods = preferences[enrolledMethodsKey]?.toMutableSet() ?: mutableSetOf()
            currentMethods.add(method.name)
            preferences[enrolledMethodsKey] = currentMethods
        }
    }

    fun getEnrolledMethodsFlow(): Flow<Set<SecurityMethod>> {
        return context.dataStore.data.map { preferences ->
            preferences[enrolledMethodsKey]?.mapNotNull { methodName ->
                try {
                    SecurityMethod.valueOf(methodName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }?.toSet() ?: emptySet()
        }
    }

    suspend fun removeEnrolledMethod(method: SecurityMethod) {
        context.dataStore.edit { preferences ->
            val currentMethods = preferences[enrolledMethodsKey]?.toMutableSet() ?: mutableSetOf()
            currentMethods.remove(method.name)
            preferences[enrolledMethodsKey] = currentMethods
        }
    }

    fun getSecurityMethodFlow(): Flow<SecurityMethod?> {
        return context.dataStore.data.map { preferences ->
            if (cachedSecurityMethod != null) {
                cachedSecurityMethod
            } else {
                preferences[currentMethodKey]?.let { 
                    SecurityMethod.valueOf(it).also { method ->
                        cachedSecurityMethod = method
                    }
                }
            }
        }
    }

    suspend fun saveSecurityMethod(method: SecurityMethod) {
        cachedSecurityMethod = method
        context.dataStore.edit { preferences ->
            preferences[currentMethodKey] = method.name
        }

        // Sync with Firebase in background
        val user = auth.currentUser
        if (user != null) {
            try {
                database.child("users")
                    .child(user.uid)
                    .child("security_method")
                    .setValue(method.name)
                    .await()
            } catch (e: Exception) {
                Log.e("SecurityPreference", "Error syncing security method to Firebase", e)
            }
        }
    }

    suspend fun clearSecurityMethod() {
        cachedSecurityMethod = null
        context.dataStore.edit { preferences ->
            preferences.remove(currentMethodKey)
        }

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
        // Only clear local data, not Firebase data
        cachedPin = null
        cachedSecurityMethod = null
        
        // Clear local storage
        context.dataStore.edit { preferences ->
            preferences.clear()  // This clears all local preferences
        }
        
        // Don't clear Firebase data during logout
        // Remove this part:
        /*
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
        */
    }

    fun hasPinInLocalStorage(): Boolean {
        return cachedPin != null || runBlocking {
            dataStore.data.first()[pinKey] != null
        }
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
                dataStore.edit { preferences ->
                    preferences[pinKey] = firebasePin
                }
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
        // Clear only local data
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
}

enum class SecurityMethod {
    NONE, PIN, FINGERPRINT
}