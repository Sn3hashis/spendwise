package me.sm.spendwise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sm.spendwise.R

data class ExpenseCategory(
    val id: Int,
    val name: String,
    val description: String,
    val icon: Int
)

@Composable
fun ExpenseCategoryScreen(
    onBackPress: () -> Unit,
    onCategorySelected: (ExpenseCategory) -> Unit,
    initialCategory: ExpenseCategory? = null
) {
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    // Define categories list
    val categories = listOf(
        // 1. Household & Utilities
        ExpenseCategory(
            1,
            "Household & Utilities",
            "Rent, Bills, Internet, and other utilities",
            R.drawable.ic_home
        ),
        
        // 2. Groceries & Food
        ExpenseCategory(
            2,
            "Groceries & Food",
            "Groceries, Dining out, and Food delivery",
            R.drawable.ic_food
        ),
        
        // 3. Transportation
        ExpenseCategory(
            3,
            "Transportation",
            "Fuel, Public transport, and Vehicle expenses",
            R.drawable.ic_transport
        ),
        
        // 4. Personal & Health
        ExpenseCategory(
            4,
            "Personal & Health",
            "Healthcare, Personal care, and Fitness",
            R.drawable.ic_health
        ),
        
        // 5. Entertainment & Leisure
        ExpenseCategory(
            5,
            "Entertainment",
            "Movies, Subscriptions, and Hobbies",
            R.drawable.ic_entertainment
        ),
        
        // 6. Financial Expenses
        ExpenseCategory(
            6,
            "Financial",
            "Loans, Investments, and Insurance",
            R.drawable.ic_finance
        ),
        
        // 7. Education
        ExpenseCategory(
            7,
            "Education",
            "Tuition, Courses, and Educational supplies",
            R.drawable.ic_education
        ),
        
        // 8. Family & Social
        ExpenseCategory(
            8,
            "Family & Social",
            "Gifts, Donations, and Pet care",
            R.drawable.ic_family
        ),
        
        // 9. Travel
        ExpenseCategory(
            9,
            "Travel",
            "Tickets, Accommodation, and Sightseeing",
            R.drawable.ic_travel
        ),
        
        // 10. Miscellaneous
        ExpenseCategory(
            10,
            "Miscellaneous",
            "Repairs, Maintenance, and Other expenses",
            R.drawable.ic_others
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onBackPress,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Select Category",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Categories List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(categories) { category ->
                    CategoryItem(
                        category = category,
                        isSelected = category == selectedCategory,
                        onSelect = { selectedCategory = category }
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )
                }
            }

            // Select Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        selectedCategory?.let { category ->
                            onCategorySelected(category)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(32.dp),
                    enabled = selectedCategory != null
                ) {
                    Text(
                        text = "Select",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: ExpenseCategory,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon
        Icon(
            painter = painterResource(id = category.icon),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        // Category Details
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = category.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // Checkbox
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onSelect() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary
            )
        )
    }
} 