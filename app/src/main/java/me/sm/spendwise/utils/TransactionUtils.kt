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
        // Add more categories as needed
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
