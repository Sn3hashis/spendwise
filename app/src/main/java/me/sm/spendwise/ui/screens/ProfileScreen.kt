import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import me.sm.spendwise.R
import me.sm.spendwise.navigation.NavigationState
import androidx.compose.runtime.*
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import me.sm.spendwise.ui.AppState
import me.sm.spendwise.ui.Screen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import kotlinx.coroutines.launch
import android.util.Log
import android.widget.Toast

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val showLogoutDialog = remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                 .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader()
            Spacer(modifier = Modifier.height(40.dp))
            ProfileMenu(showLogoutDialog = showLogoutDialog)
        }
    }
    if (showLogoutDialog.value) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog.value = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        AppState.logout(context)
                    }
                    showLogoutDialog.value = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog.value = false }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun ProfileHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            AsyncImage(
                model = AppState.currentUser?.photoUrl ?: R.drawable.profile_placeholder,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = AppState.currentUser?.displayName ?: "User",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = AppState.currentUser?.email ?: "",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ProfileMenu(showLogoutDialog: MutableState<Boolean>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProfileMenuItem(
            icon = R.drawable.ic_wallet,
            title = "Account",
            backgroundColor = Color(0xFFF3F0FF)
        )
         ProfileMenuItem(
            icon = R.drawable.ic_lends,
            title = "You Owed",
            backgroundColor = Color(0xFFF3F0FF)
        )
        ProfileMenuItem(
            icon = R.drawable.ic_manage_payee,
            title = "Manage Payee",
            backgroundColor = Color(0xFFF3F0FF),
            onClick = { NavigationState.navigateTo(Screen.ManagePayee) }
        )

        ProfileMenuItem(
            icon = R.drawable.ic_settings,
            title = "Settings",
            backgroundColor = Color(0xFFF3F0FF),
            onClick = { NavigationState.navigateTo(Screen.Settings) }
        )
        ProfileMenuItem(
            icon = R.drawable.ic_upload,
            title = "Export Data",
            backgroundColor = Color(0xFFF3F0FF)
        )
        ProfileMenuItem(
            icon = R.drawable.ic_logout,
            title = "Logout",
            backgroundColor = Color(0xFFFFF0F0),
            onClick = { showLogoutDialog.value = true }
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: Int,
    title: String,
    backgroundColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            //  .padding(vertical = 4.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0x1A000000),
                spotColor = Color(0x1A000000)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(backgroundColor, CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.Center),
                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

