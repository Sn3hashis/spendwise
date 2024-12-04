package me.sm.spendwise.ui.components


import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sm.spendwise.R
import androidx.compose.ui.platform.LocalContext
import me.sm.spendwise.MainActivity

import me.sm.spendwise.navigation.NavigationState
import me.sm.spendwise.ui.theme.blurredBackground
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    var showPopupMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Semi-transparent overlay when menu is expanded
        AnimatedVisibility(
            visible = showPopupMenu,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { showPopupMenu = false }
            )
        }

        // Expanded Menu Options
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-100).dp)
        ) {
            AnimatedVisibility(
                visible = showPopupMenu,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    MenuButton(
                        icon = R.drawable.ic_transfer_new,
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        onClick = { 
                            NavigationState.currentScreen = "Transfer"
                            showPopupMenu = false
                        }
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(40.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        MenuButton(
                            icon = R.drawable.ic_income_new,
                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = { 
                                NavigationState.currentScreen = "Income"
                                showPopupMenu = false
                            }
                        )
                        MenuButton(
                            icon = R.drawable.ic_expense,
                            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                            onClick = { 
                                NavigationState.currentScreen = "Expense"
                                showPopupMenu = false
                            }
                        )
                    }
                }
            }
        }

        // Main Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(80.dp)
                .blurredBackground(
                    color = if (isSystemInDarkTheme()) {
                        Color.Black.copy(alpha = 0.5f)
                    } else {
                        Color.White.copy(alpha = 0.5f)
                    },
                    borderRadius = 24.dp,
                    blurRadius = 20.dp
                )
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val items = listOf(
                    BottomNavItem("Home", R.drawable.ic_home, NavigationState.currentScreen == "Home"),
                    BottomNavItem("Transaction", R.drawable.ic_transaction, NavigationState.currentScreen == "Transaction"),
                    BottomNavItem("", 0, false),
                    BottomNavItem("Budget", R.drawable.ic_budget, NavigationState.currentScreen == "Budget"),
                    BottomNavItem("Profile", R.drawable.ic_profile, NavigationState.currentScreen == "Profile")
                )
                items.forEachIndexed { index, item ->
                    if (index == 2) {
                        Spacer(modifier = Modifier.width(56.dp))
                    } else {
                        NavItem(
                            item = item,
                            onClick = {
                                NavigationState.currentScreen = item.title
                            }
                        )
                    }
                }
            }
        }

        // FAB
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = -28.dp)
                .size(56.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            border = null
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { showPopupMenu = !showPopupMenu },
                contentAlignment = Alignment.Center
            ) {
                val rotation by animateFloatAsState(
                    targetValue = if (showPopupMenu) 45f else 0f,
                    animationSpec = tween(300)
                )
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (showPopupMenu) "Close Menu" else "Open Menu",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
@Composable
private fun NavItem(
    item: BottomNavItem,
    onClick: () -> Unit
) {
    val iconSize = if (item.title == "Transaction") 28.dp else 24.dp
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.title,
            modifier = Modifier.size(iconSize),
            tint = if (item.isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        if (item.title.isNotEmpty()) {
            Text(
                text = item.title,
                fontSize = 12.sp,
                color = if (item.isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
@Composable
private fun MenuButton(
    icon: Int,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(56.dp)
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
                modifier = Modifier.size(40.dp)
            )
        }
    }
}
data class BottomNavItem(
    val title: String,
    val icon: Int,
    val isSelected: Boolean
)