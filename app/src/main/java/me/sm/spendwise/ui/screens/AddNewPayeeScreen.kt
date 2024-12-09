package me.sm.spendwise.ui.screens

import android.content.pm.PackageManager

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sm.spendwise.navigation.NavigationState
import me.sm.spendwise.navigation.Screen
import android.Manifest
import android.annotation.SuppressLint
import android.provider.ContactsContract
import android.widget.Toast
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import me.sm.spendwise.navigation.Screen as NavScreen
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import me.sm.spendwise.ui.screens.AttachmentOptionsScreen
import me.sm.spendwise.R
import me.sm.spendwise.data.NotificationManager
import me.sm.spendwise.navigation.NavigationState.payeeToEdit

@SuppressLint("Range")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewPayeeScreen(onPayeeAdded: (Payee) -> Unit, payeeToEdit: Payee? = null,  ) {
    var name by remember(payeeToEdit) { mutableStateOf(payeeToEdit?.name ?: "") }
    var phone by remember(payeeToEdit) { mutableStateOf(payeeToEdit?.mobile ?: "") }
    var email by remember(payeeToEdit) { mutableStateOf(payeeToEdit?.email ?: "") }
    var profilePicUri by remember(payeeToEdit) { mutableStateOf(payeeToEdit?.profilePic) }
    var isNameError by remember { mutableStateOf(false) }
    var isPhoneError by remember { mutableStateOf(false) }
    var showAttachmentOptions by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val contactPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        uri?.let { contactUri ->
            try {
                // Query for contact details
                val projection = arrayOf(
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                )

                context.contentResolver.query(
                    contactUri,
                    projection,
                    null,
                    null,
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val contactId =
                            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        name =
                            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                                ?: ""

                        // Get phone number
                        context.contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                            arrayOf(contactId),
                            null
                        )?.use { phoneCursor ->
                            if (phoneCursor.moveToFirst()) {
                                phone = phoneCursor.getString(
                                    phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                )
                                    ?.replace("[^0-9]".toRegex(), "") ?: ""
                            }
                        }

                        // Get email
                        context.contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            arrayOf(ContactsContract.CommonDataKinds.Email.DATA),
                            "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                            arrayOf(contactId),
                            null
                        )?.use { emailCursor ->
                            if (emailCursor.moveToFirst()) {
                                email = emailCursor.getString(
                                    emailCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA)
                                ) ?: ""
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle any errors
                Toast.makeText(
                    context,
                    "Error loading contact: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            contactPicker.launch(null)
        }
    }

    // Show AttachmentOptionsScreen when needed
    if (showAttachmentOptions) {
        AttachmentOptionsScreen(
            onDismiss = { showAttachmentOptions = false },
            onCameraClick = { /* Handled internally */ },
            onGalleryClick = { /* TODO */ },
            onDocumentClick = { /* Not needed */ },
            onImagesSelected = { uris ->
                if (uris.isNotEmpty()) {
                    profilePicUri = uris[0].toString()
                }
                showAttachmentOptions = false
            }
        )
    }
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                               text = if (payeeToEdit != null) "Update ${payeeToEdit!!.name}" else "Add New Payee",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { NavigationState.navigateBack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    modifier = Modifier.shadow(4.dp)
                )
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                if (profilePicUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = profilePicUri),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile_placeholder),
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        isNameError = it.isEmpty()
                    },
                    label = { Text("Name*") },
                    isError = isNameError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        isPhoneError = it.isEmpty()
                    },
                    label = { Text("Phone Number*") },
                    isError = isPhoneError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            try {
                                when {
                                    context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED -> {
                                        contactPicker.launch(null)
                                    }

                                    else -> {
                                        permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error accessing contacts: ${e.localizedMessage}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContactPhone, "Contacts")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select from Contacts")
                    }

                    OutlinedButton(
                        onClick = {
                            showAttachmentOptions = true
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, "Upload")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload Picture")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (name.isNotEmpty() && phone.isNotEmpty()) {
                            val newPayee = Payee(
                                id = System.currentTimeMillis().toInt(),
                                name = name,
                                mobile = phone.replace("\\s".toRegex(), ""),
                                email = email,
                                profilePic = profilePicUri,
                            )
                            onPayeeAdded(newPayee)
                            
                            // Add Toast
                            Toast.makeText(context, "$name added successfully", Toast.LENGTH_SHORT).show()
                            
                            // Add Notification
                            NotificationManager.addTransactionNotification(
                                type = "New Payee Added",
                                amount = "",
                                category = "$name Added in the payee list"
                            )
                            
                            NavigationState.navigateTo(NavScreen.ManagePayee)
                        } else {
                            isNameError = name.isEmpty()
                            isPhoneError = phone.isEmpty()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Save Payee")
                }
            }

            if (showAttachmentOptions) {
                AttachmentOptionsScreen(
                    onDismiss = { showAttachmentOptions = false },
                    onCameraClick = { },
                    onGalleryClick = { },
                    onDocumentClick = { },
                    onImagesSelected = { uris ->
                        if (uris.isNotEmpty()) {
                            profilePicUri = uris[0].toString()
                        }
                        showAttachmentOptions = false
                    }
                )
            }
        }
    }
}
