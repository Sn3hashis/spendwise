package me.sm.spendwise.data

import me.sm.spendwise.R

data class ExpenseCategory(
    val id: Int,
    val name: String,
    val description: String,
    val icon: Int
)

object ExpenseCategories {
    val categories = listOf(
        ExpenseCategory(
            1,
            "Household & Utilities",
            "Rent, Bills, Internet, and other utilities",
            R.drawable.ic_home
        ),
        ExpenseCategory(
            2,
            "Groceries & Food",
            "Groceries, Dining out, and Food delivery",
            R.drawable.ic_food
        ),
        ExpenseCategory(
            3,
            "Transportation",
            "Fuel, Public transport, and Vehicle expenses",
            R.drawable.ic_transport
        ),
        ExpenseCategory(
            4,
            "Personal & Health",
            "Healthcare, Personal care, and Fitness",
            R.drawable.ic_health
        ),
        ExpenseCategory(
            5,
            "Entertainment",
            "Movies, Subscriptions, and Hobbies",
            R.drawable.ic_entertainment
        ),
        ExpenseCategory(
            6,
            "Financial",
            "Loans, Investments, and Insurance",
            R.drawable.ic_finance
        ),
        ExpenseCategory(
            7,
            "Education",
            "Tuition, Courses, and Educational supplies",
            R.drawable.ic_education
        ),
        ExpenseCategory(
            8,
            "Family & Social",
            "Gifts, Donations, and Pet care",
            R.drawable.ic_family
        ),
        ExpenseCategory(
            9,
            "Travel",
            "Tickets, Accommodation, and Sightseeing",
            R.drawable.ic_travel
        ),
        ExpenseCategory(
            10,
            "Miscellaneous",
            "Repairs, Maintenance, and Other expenses",
            R.drawable.ic_others
        )
    )
} 