package me.sm.spendwise.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import me.sm.spendwise.navigation.NavigationState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.ui.res.painterResource
import me.sm.spendwise.R
import me.sm.spendwise.ui.Screen

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import me.sm.spendwise.data.NotificationManager
import me.sm.spendwise.data.Payee


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayeeScreen(
    payees: List<Payee>,
    onBackPress: () -> Unit,
    onAddPayeeClick: () -> Unit,
    onEditPayeeClick: (Payee) -> Unit,
    onDeletePayeeClick: (Payee) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf<Payee?>(null) }
    val context = LocalContext.current

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
                                "All Payees",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackPress) {
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
        },

        floatingActionButton = {
            FloatingActionButton(onClick = onAddPayeeClick) {
                Icon(Icons.Default.Add, contentDescription = "Add new Payee")
            }
        },
    ) { innerPadding ->
        if (payees.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No Payee Available\nClick on add new button to add",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize()
            ) {
                items(payees) { payee ->
                    PayeeCard(
                        payee = payee,
                        onEditClick = { onEditPayeeClick(payee) },
                        onDeleteClick = { showDeleteDialog = payee },
                        onEmailClick = {  },
                        onWhatsAppClick = {  },
                        onSmsClick = {  },
                        onProfileClick = {  }
                    )
                }
            }
        }
    }

    showDeleteDialog?.let { payee ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Payee") },
            text = { Text("Are you sure you want to delete ${payee.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeletePayeeClick(payee)
                        Toast.makeText(context, "${payee.name} deleted successfully", Toast.LENGTH_SHORT).show()
                        NotificationManager.addTransactionNotification(
                            type = "Payee Deleted",
                            amount = "",
                            category = "${payee.name} deleted from payee list"
                        )
                        showDeleteDialog = null
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("No")
                }
            }
        )
    }
}@Composable
fun PayeeCard(
    payee: Payee,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEmailClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onSmsClick: () -> Unit,
    onProfileClick: () -> Unit = {} // Add new parameter for profile click
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onProfileClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = payee.profilePic ?: R.drawable.profile_placeholder
                ),
                contentDescription = "Profile picture of ${payee.name}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(payee.name, fontWeight = FontWeight.Bold)
            Text(payee.mobile)
            if (!payee.email.isNullOrEmpty()) {
                Text(
                    text = payee.email,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Divider(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            )

            // Communication buttons with colored icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = onEmailClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = "Email",
                        tint = Color.Unspecified  // This preserves icon's original colors
                    )
                }
                IconButton(onClick = onWhatsAppClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_whatsapp),
                        contentDescription = "WhatsApp",
                        tint = Color.Unspecified
                    )
                }
                IconButton(onClick = onSmsClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sms),
                        contentDescription = "SMS",
                        tint = Color.Unspecified
                    )
                }
            }

            Divider(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            )
            // Edit and Delete buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Edit Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(40.dp)  // Reduced size
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)  // Smaller icon
                        )
                    }
                    Text(
                        text = "Edit",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Delete Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(40.dp)  // Reduced size
                            .background(
                                color = Color.Red,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)  // Smaller icon
                        )
                    }
                    Text(
                        text = "Delete",
                        fontSize = 12.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
