package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import com.example.ui.viewmodel.SurveyViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(
    viewModel: SurveyViewModel,
    user: UserEntity?,
    transactions: List<TransactionEntity>,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf("UPI") } // UPI or Paytm
    var amountInput by remember { mutableStateOf("") }
    var upiIdInput by remember { mutableStateOf(user?.upiId ?: "") }
    var paytmPhoneInput by remember { mutableStateOf(user?.paytmPhone ?: "") }
    
    var infoMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successText by remember { mutableStateOf("") }

    // Synchronize default values when user loads
    LaunchedEffect(user) {
        if (upiIdInput.isEmpty() && user != null) {
            upiIdInput = user.upiId
        }
        if (paytmPhoneInput.isEmpty() && user != null) {
            paytmPhoneInput = user.paytmPhone
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SleekBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Balance Header card in Royal Blue style
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = SleekPrimaryContainer
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL REDEEMABLE CASH",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = SleekOnPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹${String.format("%.2f", user?.balance ?: 0.0)}",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                            color = SleekOnPrimaryContainer
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SleekPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        // Dynamic coin rotating or cashout exchange icon
                        Icon(
                            imageVector = Icons.Default.CurrencyExchange,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Cashout Form Section - Bordered Sleek design
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SleekSurface),
                border = BorderStroke(1.dp, SleekOutline),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Withdraw Money to Bank",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SleekOnBackground
                    )

                    // Tab selector matching navigation/chips palette
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SleekSurfaceVariant)
                            .padding(4.dp)
                    ) {
                        // UPI Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedMethod == "UPI") SleekPrimary else Color.Transparent)
                                .clickable { selectedMethod = "UPI" }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "UPI (GPay/PhonePe)",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMethod == "UPI") Color.White else SleekOnSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                        
                        // Paytm Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedMethod == "Paytm") SleekPrimary else Color.Transparent)
                                .clickable { selectedMethod = "Paytm" }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Paytm Wallet",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMethod == "Paytm") Color.White else SleekOnSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // UPI Input Details
                    if (selectedMethod == "UPI") {
                        OutlinedTextField(
                            value = upiIdInput,
                            onValueChange = { upiIdInput = it },
                            label = { Text("Enter UPI ID") },
                            placeholder = { Text("example@upi") },
                            leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = SleekPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("upi_id_field")
                        )
                    } else {
                        OutlinedTextField(
                            value = paytmPhoneInput,
                            onValueChange = { paytmPhoneInput = it },
                            label = { Text("Paytm Mobile Number") },
                            placeholder = { Text("10-digit mobile number") },
                            leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = SleekPrimary) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("paytm_phone_field")
                        )
                    }

                    // Amount Input
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Amount to Withdraw (₹)") },
                        placeholder = { Text("Min. ₹100") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = SleekPrimary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("withdraw_amount_field")
                    )

                    // Quick-preset tags
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("100", "200", "500").forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SleekSurfaceVariant)
                                    .clickable { amountInput = preset }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "₹$preset",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SleekPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Validation alerts
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    // Request Button
                    Button(
                        onClick = {
                            errorMessage = null
                            val amt = amountInput.toDoubleOrNull()
                            if (amt == null) {
                                errorMessage = "Please enter a valid numeric amount."
                                return@Button
                            }
                            viewModel.handleWithdrawal(
                                amount = amt,
                                method = selectedMethod,
                                upiId = upiIdInput,
                                paytmPhone = paytmPhoneInput,
                                onResult = { success, msg ->
                                    if (success) {
                                        successText = msg
                                        showSuccessDialog = true
                                        amountInput = "" // Clear amount on success
                                    } else {
                                        errorMessage = msg
                                    }
                                }
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_withdrawal_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SleekPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Process Cashout Request", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    
                    Text(
                        text = "🔒 Secure transaction verified under standard security guidelines. Transfers complete in less than 3 hours.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        textAlign = TextAlign.Center,
                        color = SleekOnSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Transaction List Header
        item {
            Text(
                text = "My Earnings History",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = SleekOnBackground
            )
        }

        // Transaction History rows
        if (transactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SleekOutline),
                    colors = CardDefaults.cardColors(containerColor = SleekSurface)
                ) {
                    Text(
                        text = "No prior transactions done yet.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = SleekOnSurfaceVariant
                    )
                }
            }
        } else {
            items(transactions) { tx ->
                TransactionRowItem(transaction = tx)
            }
        }
    }

    // Success confirmation dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = SuccessGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Request Pending Processing", fontWeight = FontWeight.Black)
                }
            },
            text = { Text(successText) },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun TransactionRowItem(transaction: TransactionEntity) {
    val isEarning = transaction.type == "EARNING"
    val dateString = remember(transaction.timestamp) {
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        formatter.format(Date(transaction.timestamp))
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SleekOutline),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isEarning) IconBgTech else SleekSurfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isEarning) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (isEarning) SleekPrimary else SleekOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = SleekOnBackground,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = SleekOnSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isEarning) "+ ₹${String.format("%.2f", transaction.amount)}" else "₹${String.format("%.2f", transaction.amount)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = if (isEarning) SleekPrimary else ErrorRed
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Status pill
                val color = when (transaction.status) {
                    "SUCCESS" -> IconBgTech to SleekPrimary
                    "PROCESSING" -> IconBgLifestyle to SleekSecondary
                    else -> IconBgFinance to ErrorRed
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.first)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = transaction.status,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = color.second
                    )
                }
            }
        }
    }
}
