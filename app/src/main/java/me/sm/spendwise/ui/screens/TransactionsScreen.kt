package me.sm.spendwise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import me.sm.spendwise.R
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.SheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onShowFilter: (Boolean) -> Unit = {}
) {
    var showFilter by rememberSaveable { mutableStateOf(false) }
    var filterCount by rememberSaveable { mutableStateOf(0) }
    var selectedFilterType by rememberSaveable { mutableStateOf("") }
    var selectedSortType by rememberSaveable { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // Header with Month selector and Filter
            Header(
                filterCount = filterCount,
                onFilterClick = { showFilter = true }
            )
            
            // Financial Report Card
            FinancialReportCard()
            
            // Transactions List
            TransactionsList()
        }

        // Filter Sheet
        if (showFilter) {
            ModalBottomSheet(
                onDismissRequest = { showFilter = false },
                sheetState = sheetState,
                dragHandle = null,
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                windowInsets = WindowInsets(0),
                modifier = Modifier
                    .fillMaxHeight(0.70f)
                    .zIndex(1f)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    TransactionFilterScreen(
                        onDismiss = { showFilter = false },
                        initialFilterType = selectedFilterType,
                        initialSortType = selectedSortType,
                        onApplyFilters = { count, filterType, sortType -> 
                            filterCount = count
                            selectedFilterType = filterType
                            selectedSortType = sortType
                            showFilter = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    filterCount: Int,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Month Selector
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(12.dp, 8.dp)
        ) {
            Text(
                text = "Month",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select Month",
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Filter Icon with Badge
        Box {
            IconButton(onClick = onFilterClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "Filter",
                    modifier = Modifier.size(24.dp)
                )
            }
            if (filterCount > 0) {
                Badge(
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(text = filterCount.toString())
                }
            }
        }
    }
}

@Composable
private fun FinancialReportCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "See your financial report",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = "See Report",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun TransactionsList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Today Section
        Text(
            text = "Today",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        TransactionItem(
            icon = R.drawable.ic_shopping,
            title = "Shopping",
            subtitle = "Buy some grocery",
            amount = "-$120",
            time = "10:00 AM",
            backgroundColor = Color(0xFFFCEED4)
        )
        
        TransactionItem(
            icon = R.drawable.ic_subscription,
            title = "Subscription",
            subtitle = "Disney+ Annual..",
            amount = "-$80",
            time = "03:30 PM",
            backgroundColor = Color(0xFFEEE5FF)
        )
        
        TransactionItem(
            icon = R.drawable.ic_food,
            title = "Food",
            subtitle = "Buy a ramen",
            amount = "-$32",
            time = "07:30 PM",
            backgroundColor = Color(0xFFFFE2E4)
        )

        // Yesterday Section
        Text(
            text = "Yesterday",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
        )
        
        TransactionItem(
            icon = R.drawable.ic_income_new,
            title = "Salary",
            subtitle = "Salary for July",
            amount = "+$5000",
            time = "04:30 PM",
            backgroundColor = Color(0xFFCFFAEA),
            isIncome = true
        )
        
        TransactionItem(
            icon = R.drawable.ic_transport,
            title = "Transportation",
            subtitle = "Charging Tesla",
            amount = "-$18",
            time = "08:30 PM",
            backgroundColor = Color(0xFFD1EFFF)
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
    backgroundColor: Color,
    isIncome: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = backgroundColor,
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
                color = if (isIncome) Color(0xFF00A86B) else MaterialTheme.colorScheme.error
            )
            Text(
                text = time,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 