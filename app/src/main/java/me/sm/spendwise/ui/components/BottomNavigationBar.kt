package me.sm.spendwise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import me.sm.spendwise.R
import me.sm.spendwise.navigation.NavigationState
import me.sm.spendwise.ui.Screen

@Composable
fun BottomNavigationBar() {
    var showPopupMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()  // This ensures the Box takes full screen space
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), // Glass effect
            shadowElevation = 5.dp // Remove shadow
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 1f)) // Additional blur effect
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavBarItemWithLabel(
                        icon = R.drawable.ic_home,
                        label = "Home",
                        isSelected = NavigationState.currentScreen == Screen.Home,
                        onClick = { NavigationState.navigateTo(Screen.Home) }
                    )
                    NavBarItemWithLabel(
                        icon = R.drawable.ic_transaction,
                        label = "Transactions",
                        isSelected = NavigationState.currentScreen == Screen.Transaction,
                        onClick = { NavigationState.navigateTo(Screen.Transaction) }
                    )
                    // Plus button without label
                    NavBarItem(
                        icon = R.drawable.ic_plus,
                        isSelected = false,
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = { showPopupMenu = true }
                    )
                    NavBarItemWithLabel(
                        icon = R.drawable.ic_budget,
                        label = "Budget",
                        isSelected = NavigationState.currentScreen == Screen.Budget,
                        onClick = { NavigationState.navigateTo(Screen.Budget) }
                    )
                    NavBarItemWithLabel(
                        icon = R.drawable.ic_profile,
                        label = "Profile",
                        isSelected = NavigationState.currentScreen == Screen.Profile,
                        onClick = { NavigationState.navigateTo(Screen.Profile) }
                    )
                }
            }
        }
    }
        if (showPopupMenu) {
            PopupMenu(onDismiss = { showPopupMenu = false })
        }
    }


@Composable
private fun NavBarItemWithLabel(
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp), // Reduce spacing between icon and label
        modifier = Modifier.width(72.dp) // Give fixed width to ensure consistent spacing
    ) {
        NavBarItem(
            icon = icon,
            isSelected = isSelected,
            onClick = onClick
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

@Composable
private fun NavBarItem(
    icon: Int,
    isSelected: Boolean,
    backgroundColor: Color = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else Color.Transparent,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(40.dp)  // Make all items same size
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        shape = CircleShape,
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)  // Consistent icon size
            )
        }
    }
}

@Composable
private fun PopupMenu(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() }
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp) // Position above navbar
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(28.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PopupMenuItem(
                    icon = R.drawable.ic_expense,
                    text = "Expense",
                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    onClick = {
                        NavigationState.navigateTo(Screen.Expense)
                        onDismiss()
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                PopupMenuItem(
                    icon = R.drawable.ic_income_new,
                    text = "Income", 
                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick = {
                        NavigationState.navigateTo(Screen.Income)
                        onDismiss()
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                PopupMenuItem(
                    icon = R.drawable.ic_transfer_new,
                    text = "Transfer",
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick = {
                        NavigationState.navigateTo(Screen.Transfer)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun PopupMenuItem(
    icon: Int,
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = backgroundColor
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}