package me.sm.spendwise.ui.screens

import android.app.AlertDialog
import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.widget.EditText
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.sm.spendwise.R
import me.sm.spendwise.navigation.NavigationState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.VisualTransformation
import android.widget.Toast
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalView
import android.net.Uri
import me.sm.spendwise.ui.Screen
import me.sm.spendwise.data.CurrencyState
import me.sm.spendwise.data.NotificationManager
import me.sm.spendwise.data.Transaction
import me.sm.spendwise.data.TransactionManager
import me.sm.spendwise.data.ExpenseCategory
import me.sm.spendwise.data.ExpenseCategories

import me.sm.spendwise.utils.*
import me.sm.spendwise.data.HapticsPreference

@Composable
fun ExpenseScreen(
    onBackPress: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var description by remember { mutableStateOf("") }
    var isRepeatEnabled by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var showCategorySelector by remember { mutableStateOf(false) }
    var showAttachmentOptions by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val context = LocalContext.current
    val view = LocalView.current
    val hapticsPreference = remember { HapticsPreference(context) }

    if (showCategorySelector) {
        ExpenseCategoryScreen(
            onBackPress = { 
                hapticsPreference.performHapticFeedback(view)
                showCategorySelector = false 
            },
            onCategorySelected = { category ->
                hapticsPreference.performHapticFeedback(view)
                selectedCategory = category
                showCategorySelector = false
            },
            initialCategory = selectedCategory
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFF4842))
                .statusBarsPadding()
        ) {
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                IconButton(
                    onClick = { 
                        hapticsPreference.performHapticFeedback(view)
                        onBackPress() 
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "Expense",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Amount Section with haptic feedback
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "How much?",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 20.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = CurrencyState.currentCurrency,
                        color = Color.White,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box {
                        if (amount.isEmpty()) {
                            Text(
                                text = "0",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 72.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    focusRequester.requestFocus()
                                }
                            )
                        }
                        BasicTextField(
                            value = amount,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                                    hapticsPreference.performHapticFeedback(view)
                                    amount = newValue
                                }
                            },
                            textStyle = LocalTextStyle.current.copy(
                                color = Color.White,
                                fontSize = 72.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .width(IntrinsicSize.Min)
                                .focusRequester(focusRequester)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Sheet with haptic feedback
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .padding(top = 32.dp, bottom = 24.dp)
                ) {
                    // Category Selector with haptic feedback
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { 
                                hapticsPreference.performHapticFeedback(view)
                                showCategorySelector = true 
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = selectedCategory?.name ?: "Category",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description Field with haptic feedback
                    OutlinedTextField(
                        value = description,
                        onValueChange = { newValue -> 
                            hapticsPreference.performHapticFeedback(view)
                            description = newValue 
                        },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        singleLine = true,
                        visualTransformation = VisualTransformation.None
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Wallet Selector with haptic feedback
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { 
                                hapticsPreference.performHapticFeedback(view)
                                /* Handle wallet selection */ 
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Wallet",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 18.sp
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Attachment Section with haptic feedback
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { 
                                    hapticsPreference.performHapticFeedback(view)
                                    showAttachmentOptions = true 
                                }
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_attachment),
                                contentDescription = "Add attachment",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add attachment",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Repeat Transaction Switch with haptic feedback
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Repeat",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Repeat transaction",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 16.sp
                            )
                        }
                        Switch(
                            checked = isRepeatEnabled,
                            onCheckedChange = { 
                                hapticsPreference.performHapticFeedback(view)
                                isRepeatEnabled = it 
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    
                    Spacer(modifier = Modifier.height(16.dp))  // Added extra space before button

                    // Save Button with haptic feedback
                    Button(
                        onClick = { 
                            hapticsPreference.performHapticFeedback(view)
                            
                            if (amount.isEmpty() || amount == "0") {
                                Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show()
                            } else {
                                NotificationManager.addTransactionNotification(
                                    type = "Expense",
                                    amount = "${CurrencyState.currentCurrency}$amount",
                                    category = selectedCategory?.name ?: "General"
                                )
                                TransactionManager.addTransaction(
                                    Transaction(
                                        type = "Expense",
                                        title = description,
                                        category = selectedCategory?.name ?: "General",
                                        amount = "${CurrencyState.currentCurrency}$amount",
                                        time = getCurrentTime(),
                                        date = getCurrentDate(),
                                        icon = selectedCategory?.icon ?: R.drawable.ic_misc,
                                        backgroundColor = getCategoryColor(selectedCategory?.name ?: "General"),
                                        isIncome = false
                                    )
                                )
                                Toast.makeText(
                                    context, 
                                    "${CurrencyState.currentCurrency} ${amount} expense saved", 
                                    Toast.LENGTH_SHORT
                                ).show()
                                NavigationState.navigateTo(Screen.Home)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Save",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))  // Added extra space after button
                }
            }
        }
    }

    if (showAttachmentOptions) {
        AttachmentOptionsScreen(
            onDismiss = { 
                hapticsPreference.performHapticFeedback(view)
                showAttachmentOptions = false 
            },
            onCameraClick = {
                hapticsPreference.performHapticFeedback(view)
                showAttachmentOptions = false
            },
            onGalleryClick = {
                hapticsPreference.performHapticFeedback(view)
                showAttachmentOptions = false
            },
            onDocumentClick = {
                hapticsPreference.performHapticFeedback(view)
                showAttachmentOptions = false
            },
            onImagesSelected = { uris ->
                hapticsPreference.performHapticFeedback(view)
                selectedImages = uris
                showAttachmentOptions = false
            }
        )
    }
}
// Add number input dialog function
private fun showNumberInputDialog(context: Context, onAmountEntered: (String) -> Unit) {
    val builder = AlertDialog.Builder(context)
    val input = EditText(context)
    input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    input.filters = arrayOf(InputFilter.LengthFilter(10))
    
    builder.setView(input)
        .setTitle("Enter Amount")
        .setPositiveButton("OK") { _, _ ->
            val amount = input.text.toString()
            if (amount.isNotEmpty()) {
                onAmountEntered(amount)
            }
        }
        .setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        .show()
} 