import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import me.sm.spendwise.R
import me.sm.spendwise.navigation.NavigationState
import androidx.compose.runtime.*
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.MutableState
import me.sm.spendwise.navigation.Screen as NavScreen
@Composable
fun ProfileScreen() {
    val showLogoutDialog = remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ProfileHeader()
            Spacer(modifier = Modifier.height(48.dp))
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
                    showLogoutDialog.value = false
                    NavigationState.navigateToLogin()
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
                .background(Color(0xFF8B5CF6))
                .padding(3.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile_photo),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Username",
            color = Color.Gray,
            fontSize = 16.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Iriana Saliha",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { /* Handle edit click */ }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile"
                )
            }
        }
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
            icon = R.drawable.ic_settings,
            title = "Settings",
            backgroundColor = Color(0xFFF3F0FF),
            onClick = { NavigationState.navigateTo(NavScreen.Settings) }
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(backgroundColor, CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

