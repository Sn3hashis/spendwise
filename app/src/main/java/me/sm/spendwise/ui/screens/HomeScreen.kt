package me.sm.spendwise.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sm.spendwise.R
import me.sm.spendwise.ui.components.SpendFrequencyChart
import me.sm.spendwise.data.CurrencyState
import me.sm.spendwise.data.NotificationManager
import me.sm.spendwise.navigation.NavigationState
import me.sm.spendwise.navigation.Screen as NavScreen

@Composable
fun HomeScreen(onNavigateToExpenseDetail: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp)
    ) {
        // Top Bar
        TopBar()

        // Account Balance
        AccountBalance()

        // Income/Expense Summary
        FinancialSummary()

        // Spend Frequency
        SpendFrequencySection()

        // Time Period Selector
        TimePeriodSelector()

        // Recent Transactions
        RecentTransactions(onTransactionClick = onNavigateToExpenseDetail)

        // Bottom spacing for navigation bar
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image
        Image(
            painter = painterResource(id = R.drawable.profile_placeholder),
            contentDescription = "Profile",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )

        // Month Selector
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { /* Handle month selection */ }
        ) {
            Text(
                text = "October",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select Month",
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        // Notification Icon
        Box {
            IconButton(
                onClick = { NavigationState.navigateTo(NavScreen.NotificationView) },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notification),
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            val unreadCount = NotificationManager.notifications.collectAsState().value.count { !it.isRead }
            if (unreadCount > 0) {
                Badge(
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(text = unreadCount.toString())
                }
            }
        }
    }
}

@Composable
private fun AccountBalance() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Account Balance",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${CurrencyState.currentCurrency}9400",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun FinancialSummary() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FinancialCard(
            title = "Income",
            amount = "${CurrencyState.currentCurrency} 5000",
            icon = R.drawable.ic_income_new,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = Color(0xFF00A86B),
            modifier = Modifier.weight(1f)
        )
        FinancialCard(
            title = "Expenses",
            amount = "${CurrencyState.currentCurrency} 1200",
            icon = R.drawable.ic_expense,
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = Color(0xFFFD3C4A),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FinancialCard(
    title: String,
    amount: String,
    icon: Int,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(32.dp)
            )
            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = amount,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}



@Composable
private fun SpendFrequencySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Spend Frequency",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            SpendFrequencyChart()
        }
    }
}

@Composable
private fun TimePeriodSelector() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        listOf("Today", "Week", "Month", "Year").forEach { period ->
            TimePeriodChip(
                text = period,
                isSelected = period == "Today",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TimePeriodChip(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp),
            textAlign = TextAlign.Center,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun RecentTransactions(onTransactionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transaction",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { /* Handle see all */ }) {
                Text(
                    text = "See All",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transaction Items
        TransactionItem(
            icon = R.drawable.ic_shopping,
            title = "Shopping",
            subtitle = "Buy some grocery",
            amount = "${CurrencyState.currentCurrency} 120",
            time = "10:00 AM",
            onClick = { onTransactionClick("Shopping") }
        )
        TransactionItem(
            icon = R.drawable.ic_subscription,
            title = "Subscription",
            subtitle = "Disney+ Annual..",
            amount = "${CurrencyState.currentCurrency}-80",
            time = "03:30 PM",
            onClick = { onTransactionClick("Subscription") }
        )
        TransactionItem(
            icon = R.drawable.ic_food,
            title = "Food",
            subtitle = "Buy a ramen",
            amount = "-$32",
            time = "07:30 PM",
            onClick = { onTransactionClick("Food") }
        )
    }
}


@Composable
private fun TransactionItem(
    icon: Int,
    title: String,
    subtitle: String,
    amount: String,
    time: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = amount,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = time,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

