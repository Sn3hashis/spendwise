package me.sm.spendwise.data

import me.sm.spendwise.R

data class IncomeCategory(
    val id: Int,
    val name: String,
    val description: String,
    val icon: Int
)

object IncomeCategories {
    val categories = listOf(
        IncomeCategory(
            1,
            "Salary & Wages",
            "Full-time salary, Freelance, Consulting",
            R.drawable.ic_salary
        ),
        IncomeCategory(
            2,
            "Business Income",
            "Sales revenue, Partnerships",
            R.drawable.ic_business
        ),
        IncomeCategory(
            3,
            "Investment Returns",
            "Dividends, Interest, Capital gains",
            R.drawable.ic_investment
        ),
        IncomeCategory(
            4,
            "Other Income",
            "Gifts, Rental income, Refunds",
            R.drawable.ic_other_income
        ),
        IncomeCategory(
            5,
            "Bonuses & Awards",
            "Annual bonus, Performance bonus, Prizes",
            R.drawable.ic_bonus
        ),
        IncomeCategory(
            6,
            "Passive Income",
            "Royalties, Affiliate marketing, Ad revenue",
            R.drawable.ic_passive
        ),
        IncomeCategory(
            7,
            "Loans/Borrowings",
            "Personal loans, Borrowed money",
            R.drawable.ic_loan
        )
    )
} 