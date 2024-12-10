package me.sm.spendwise.utils

import androidx.compose.ui.graphics.Color
import me.sm.spendwise.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentTime(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
}

fun getCurrentDate(): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
}

fun getCategoryIcon(category: String): Int {
    return when (category.lowercase()) {
        "food" -> R.drawable.ic_food
        "shopping" -> R.drawable.ic_shopping
        "transport" -> R.drawable.ic_transport
        "salary" -> R.drawable.ic_income_new
        "transfer" -> R.drawable.ic_transfer_new
        "Health" -> R.drawable.ic_health
        "Entertainment" -> R.drawable.ic_entertainment
        "Miscellaneous" -> R.drawable.ic_others
        "Financial" -> R.drawable.ic_finance
        "Education" -> R.drawable.ic_education
        "Family" -> R.drawable.ic_family
        "Travel" -> R.drawable.ic_travel
        "Business" -> R.drawable.ic_business
        "Investment" -> R.drawable.ic_investment
       "other_income" -> R.drawable.ic_other_income
       "Bonuses" -> R.drawable.ic_bonus
       "Loans" -> R.drawable.ic_loan

        // Add all your category icons here
        else -> R.drawable.ic_misc
    }
}
fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "food" -> Color(0xFFFF6B6B)
        "shopping" -> Color(0xFF4ECDC4)
        "transport" -> Color(0xFFFFBE0B)
        // Add more categories as needed
        else -> Color(0xFF95A5A6)
    }
}
