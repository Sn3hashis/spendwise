package me.sm.spendwise.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sm.spendwise.R
import androidx.compose.foundation.isSystemInDarkTheme
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AttachmentOptionsScreen(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDocumentClick: () -> Unit,
    onImagesSelected: (List<Uri>) -> Unit
) {
    val context = LocalContext.current
    val isDarkMode = isSystemInDarkTheme()
    val backgroundColor = if (isDarkMode) {
        MaterialTheme.colorScheme.surface
    } else {
        Color.White
    }

    val overlayColor = if (isDarkMode) {
        Color.Black.copy(alpha = 0.7f)
    } else {
        Color.Black.copy(alpha = 0.5f)
    }

    // Permissions and Result Launchers
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onCameraClick() // Call the onCameraClick callback when permission is granted
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            if (uris != null && uris.size <= 3) {
                onImagesSelected(uris) // Handle image selection (limit 3 images)
            } else {
                Toast.makeText(context, "You can select a maximum of 3 images", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { isSuccess ->
            val currentImageUri = null
            if (isSuccess && currentImageUri != null) {
                onCameraClick() // Handle success (image captured)
            } else {
                // Handle failure (e.g. user cancelled or other error)
                Toast.makeText(context, "Camera capture failed", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // State to hold the URI of the captured image
    var currentImageUri by remember { mutableStateOf<Uri?>(null) }

    // Create the file URI for the image
    fun createImageFile(): Uri? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(null) // External storage directory
        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            )
            currentImageUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return currentImageUri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(overlayColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() }
    ) {
        // Bottom Sheet Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(backgroundColor)
        ) {
            // Indicator at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isDarkMode) Color.Gray else Color(0xFFE8E8E8))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Options Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttachmentOption(
                    icon = R.drawable.ic_camera,
                    label = "Camera",
                    onClick = {
                        // Create image file and launch camera intent
                        val uri = createImageFile()
                        uri?.let {
                            takePictureLauncher.launch(uri)
                        }
                    }
                )
                AttachmentOption(
                    icon = R.drawable.ic_gallery,
                    label = "Image",
                    onClick = {
                        galleryLauncher.launch("image/*")
                    }
                )
                AttachmentOption(
                    icon = R.drawable.ic_document,
                    label = "Doc",
                    onClick = onDocumentClick
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AttachmentOption(
    icon: Int,
    label: String,
    onClick: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val backgroundColor = if (isDarkMode) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        Color(0xFFF3F2F8)
    }

    val contentColor = if (isDarkMode) {
        MaterialTheme.colorScheme.primary
    } else {
        Color(0xFF7F3DFF)
    }

    Column(
        modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
