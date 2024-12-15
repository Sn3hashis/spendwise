package me.sm.spendwise.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.view.HapticFeedbackConstants
import android.widget.Toast
import me.sm.spendwise.R
import me.sm.spendwise.navigation.NavigationState
import androidx.compose.runtime.saveable.rememberSaveable
import android.net.Uri
import me.sm.spendwise.ui.Screen as NavScreen
import me.sm.spendwise.data.CurrencyState
import me.sm.spendwise.data.NotificationManager
import me.sm.spendwise.data.Transaction
import me.sm.spendwise.data.TransactionManager
import me.sm.spendwise.utils.*
@Composable
fun IncomeScreen(
    onBackPress: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<IncomeCategory?>(null) }
    var description by remember { mutableStateOf("") }
    var selectedWallet by remember { mutableStateOf("") }
    var isRepeatEnabled by remember { mutableStateOf(false) }
    var showCategorySelector by remember { mutableStateOf(false) }
    var showAttachmentOptions by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val view = LocalView.current
    val currency = "USD"  // Replace with actual user currency

    if (showCategorySelector) {
        IncomeCategoryScreen(
            onBackPress = { showCategorySelector = false },
            onCategorySelected = { category ->
                selectedCategory = category
                showCategorySelector = false
            },
            initialCategory = selectedCategory
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF00A86B))  // Green color for income
                .statusBarsPadding()
        ) {
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                IconButton(
                    onClick = onBackPress,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "Income",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Amount Section with same implementation as ExpenseScreen
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

            // Bottom Sheet
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
                    // Category Selector
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

                    // Description Field
                    OutlinedTextField(
                        value = description,
                        onValueChange = { newValue -> description = newValue },
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

                    // Wallet Selector
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
                            ) { /* Handle wallet selection */ }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Wallet",
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // Attachment Section
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
                                .clickable { showAttachmentOptions = true }
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

                    // Repeat Transaction Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Repeat",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Repeat transaction",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                        Switch(
                            checked = isRepeatEnabled,
                            onCheckedChange = { isRepeatEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Save Button
                    Button(
                        onClick = { 
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            
                            if (amount.isEmpty() || amount == "0") {
                                Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show()
                               
                            } else {
                                NotificationManager.addTransactionNotification(
    type = "Income",
    amount = "${CurrencyState.currentCurrency}$amount",
    category = selectedCategory?.name ?: "General"
)
// When saving expense/income
TransactionManager.addTransaction(
    Transaction(
        type = "Income",
        title = description,
        category = selectedCategory?.name ?: "General",
        amount = "${CurrencyState.currentCurrency}$amount",
        time = getCurrentTime(),
        date = getCurrentDate(),
        icon = selectedCategory?.icon ?: R.drawable.ic_misc,
        backgroundColor = getCategoryColor(selectedCategory?.name ?: "General"),
        isIncome = true
    )
)

                                Toast.makeText(
                                    context, 
                                    "$${amount} $currency income saved", 
                                    Toast.LENGTH_SHORT
                                ).show()
                                NavigationState.navigateTo(NavScreen.Home)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00A86B)
                        )
                    ) {
                        Text(
                            text = "Save",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (showAttachmentOptions) {
        AttachmentOptionsScreen(
            onDismiss = { showAttachmentOptions = false },
            onCameraClick = {
                // Handle camera
                showAttachmentOptions = false
            },
            onGalleryClick = {
                // Handle gallery
                showAttachmentOptions = false
            },
            onDocumentClick = {
                // Handle document
                showAttachmentOptions = false
            },
            onImagesSelected = { uris ->
                selectedImages = uris
                showAttachmentOptions = false
            }
        )
    }
} 