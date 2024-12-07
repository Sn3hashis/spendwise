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
import me.sm.spendwise.navigation.Screen as NavScreen

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
                .height(85.dp)
                .align(Alignment.BottomCenter),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), // Glass effect
            shadowElevation = 0.dp // Remove shadow
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)) // Additional blur effect
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
                        isSelected = NavigationState.currentScreen == NavScreen.Home,
                        onClick = { NavigationState.navigateTo(NavScreen.Home) }
                    )
                    NavBarItemWithLabel(
                        icon = R.drawable.ic_transaction,
                        label = "Transactions",
                        isSelected = NavigationState.currentScreen == NavScreen.Transaction,
                        onClick = { NavigationState.navigateTo(NavScreen.Transaction) }
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
                        isSelected = NavigationState.currentScreen == NavScreen.Budget,
                        onClick = { NavigationState.navigateTo(NavScreen.Budget) }
                    )
                    NavBarItemWithLabel(
                        icon = R.drawable.ic_profile,
                        label = "Profile",
                        isSelected = NavigationState.currentScreen == NavScreen.Profile,
                        onClick = { NavigationState.navigateTo(NavScreen.Profile) }
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
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(28.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                PopupMenuItem(
                    icon = R.drawable.ic_expense,
                    text = "Expense",
                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    onClick = {
                        NavigationState.navigateTo(NavScreen.Expense)
                        onDismiss()
                    }
                )
                PopupMenuItem(
                    icon = R.drawable.ic_income_new,
                    text = "Income",
                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick = {
                        NavigationState.navigateTo(NavScreen.Income)
                        onDismiss()
                    }
                )
                PopupMenuItem(
                    icon = R.drawable.ic_transfer_new,
                    text = "Transfer",
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick = {
                        NavigationState.navigateTo(NavScreen.Transfer)
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(if (text == "Transfer") 56.dp else 48.dp), // Larger size for Transfer
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
                        modifier = Modifier.size(if (text == "Transfer") 32.dp else 24.dp) // Larger icon for Transfer
                    )
                }
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}