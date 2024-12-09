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
import me.sm.spendwise.navigation.Screen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.ui.res.painterResource
import me.sm.spendwise.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayeeScreen(
    payees: List<Payee>,
    onBackPress: () -> Unit,
    onAddPayeeClick: () -> Unit,
    onEditPayeeClick: (Payee) -> Unit,
    onDeletePayeeClick: (Payee) -> Unit
) {
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
                        onDeleteClick = { onDeletePayeeClick(payee) },
                        onEmailClick = {  },
                        onWhatsAppClick = {  },
                        onSmsClick = {  },
                        onProfileClick = {  }
                    )
                }
            }
        }
    }
}
@Composable
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

            // Updated Edit and Delete buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Edit",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color.Red,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White
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
