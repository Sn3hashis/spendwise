// PayeeRepository.kt
package me.sm.spendwise.data


class PayeeRepository {
    private val payees = mutableListOf<Payee>()

    fun addPayee(payee: Payee) {
        payees.add(payee)
    }

    fun getPayees(): List<Payee> {
        return payees
    }
}

data class Payee(
    val id: Int,
    val name: String,
    val mobile: String,
    val email: String,
    val profilePic: String? = null // Changed type from String? to Int?
)
