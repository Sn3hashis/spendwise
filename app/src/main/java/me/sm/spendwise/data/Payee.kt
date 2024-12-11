package me.sm.spendwise.data

data class Payee(
    val id: Int,
    val name: String,
    val mobile: String,
    val email: String,
    val profilePic: String? = null
)