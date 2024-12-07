package me.sm.spendwise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.sm.spendwise.data.CurrencyPreference
import me.sm.spendwise.data.CurrencyState

data class Currency(
    val name: String,
    val code: String
)

@Composable
fun CurrencyScreen(
    onBackPress: () -> Unit,
    currencyPreference: CurrencyPreference = CurrencyPreference(LocalContext.current)
) {
    val currencies = listOf(
       Currency("INR", "₹"),  // Indian Rupee
Currency("USD", "$"),  // US Dollar
Currency("IDR", "Rp"), // Indonesian Rupiah
Currency("JPY", "¥"),  // Japanese Yen
Currency("RUB", "₽"),  // Russian Ruble
Currency("EUR", "€"),  // Euro
Currency("KRW", "₩")   // South Korean Won

    )

    val scope = rememberCoroutineScope()
    var selectedCurrency by remember { mutableStateOf(CurrencyState.currentCurrency) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
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
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(
                    onClick = onBackPress,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Currency",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Divider()

            // Currency List
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(currencies) { currency ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCurrency = currency.code
                                scope.launch {
                                    currencyPreference.saveCurrency(currency.code)
                                    CurrencyState.currentCurrency = currency.code
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currency.name} (${currency.code})",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (selectedCurrency == currency.code) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}