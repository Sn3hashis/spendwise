package me.sm.spendwise.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object CurrencyState {
    var currentCurrency by mutableStateOf("USD")
}
