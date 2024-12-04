package me.sm.spendwise.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sm.spendwise.R

@Composable
fun TransactionFilterScreen(
    onDismiss: () -> Unit,
    initialFilterType: String = "",
    initialSortType: String = "",
    onApplyFilters: (Int, String, String) -> Unit
) {
    var selectedFilterType by rememberSaveable { mutableStateOf(initialFilterType) }
    var selectedSortType by rememberSaveable { mutableStateOf(initialSortType) }
    val context = LocalContext.current

    val resetFilters = {
        selectedFilterType = ""
        selectedSortType = ""
    }

    val filterCount = listOf(selectedFilterType, selectedSortType)
        .count { it.isNotEmpty() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp, bottom = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Handle
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(2.dp)
                        )
                        .align(Alignment.CenterHorizontally)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Header with Reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Transaction",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(
                        onClick = resetFilters
                    ) {
                        Text(
                            text = "Reset",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Filter By Section
                Text(
                    text = "Filter By",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        text = "Income",
                        isSelected = selectedFilterType == "Income",
                        onClick = { selectedFilterType = "Income" }
                    )
                    FilterChip(
                        text = "Expense",
                        isSelected = selectedFilterType == "Expense",
                        onClick = { selectedFilterType = "Expense" }
                    )
                    FilterChip(
                        text = "Transfer",
                        isSelected = selectedFilterType == "Transfer",
                        onClick = { selectedFilterType = "Transfer" }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sort By Section
                Text(
                    text = "Sort By",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        text = "Highest",
                        isSelected = selectedSortType == "Highest",
                        onClick = { selectedSortType = "Highest" }
                    )
                    FilterChip(
                        text = "Lowest",
                        isSelected = selectedSortType == "Lowest",
                        onClick = { selectedSortType = "Lowest" }
                    )
                    FilterChip(
                        text = "Newest",
                        isSelected = selectedSortType == "Newest",
                        onClick = { selectedSortType = "Newest" }
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        text = "Oldest",
                        isSelected = selectedSortType == "Oldest",
                        onClick = { selectedSortType = "Oldest" }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Category Section
                Text(
                    text = "Category",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { /* Handle category selection */ }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Choose Category",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "0 Selected",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Apply Button at the bottom
            Button(
                onClick = { 
                    if (filterCount == 0) {
                        Toast.makeText(context, "No filter applied", Toast.LENGTH_SHORT).show()
                    }
                    onApplyFilters(
                        filterCount, 
                        selectedFilterType, 
                        selectedSortType
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Apply",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(36.dp)
            )
            .then(
                if (!isSelected) Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(36.dp)
                ) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.primary 
                else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
} 