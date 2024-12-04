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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sm.spendwise.R

data class IncomeCategory(
    val id: Int,
    val name: String,
    val description: String,
    val icon: Int
)

@Composable
fun IncomeCategoryScreen(
    onBackPress: () -> Unit,
    onCategorySelected: (IncomeCategory) -> Unit,
    initialCategory: IncomeCategory? = null
) {
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    // Define income categories list
    val categories = listOf(
        // 1. Salary & Wages
        IncomeCategory(
            1,
            "Salary & Wages",
            "Full-time salary, Freelance, Consulting",
            R.drawable.ic_salary
        ),
        
        // 2. Business Income
        IncomeCategory(
            2,
            "Business Income",
            "Sales revenue, Partnerships",
            R.drawable.ic_business
        ),
        
        // 3. Investment Returns
        IncomeCategory(
            3,
            "Investment Returns",
            "Dividends, Interest, Capital gains",
            R.drawable.ic_investment
        ),
        
        // 4. Other Income
        IncomeCategory(
            4,
            "Other Income",
            "Gifts, Rental income, Refunds",
            R.drawable.ic_other_income
        ),
        
        // 5. Bonuses & Awards
        IncomeCategory(
            5,
            "Bonuses & Awards",
            "Annual bonus, Performance bonus, Prizes",
            R.drawable.ic_bonus
        ),
        
        // 6. Passive Income
        IncomeCategory(
            6,
            "Passive Income",
            "Royalties, Affiliate marketing, Ad revenue",
            R.drawable.ic_passive
        ),
        
        // 7. Loans/Borrowings
        IncomeCategory(
            7,
            "Loans/Borrowings",
            "Personal loans, Borrowed money",
            R.drawable.ic_loan
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
    category: IncomeCategory,
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