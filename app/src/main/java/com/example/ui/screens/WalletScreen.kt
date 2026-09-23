package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WalletProfile
import com.example.data.model.Withdrawal
import com.example.ui.components.TelegramSupportCard
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.RocketPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun WalletScreen(
    walletProfile: WalletProfile,
    withdrawals: List<Withdrawal>,
    onWithdraw: (method: String, number: String, amount: Int) -> Unit,
    onRecharge: (amount: Int) -> Unit
) {
    var showRechargeDialog by remember { mutableStateOf(false) }
    var selectedMethod by remember { mutableStateOf("bKash") }
    var accountNumber by remember { mutableStateOf("") }
    var withdrawAmountText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        // === Page Header & Balance Card ===
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "আমার ওয়ালেট ও লেনদেন",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "বিকাশ • নগদ • রকেট • Binance এ সরাসরি উইথড্র ও রিচার্জ করুন",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Luxury Balance Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = "Wallet",
                                        tint = LuxuryGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "বর্তমান ব্যালেন্স",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Instant Payout",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "৳${walletProfile.currentBalance}",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Stats breakdown
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("মোট আয়", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        "৳${walletProfile.totalEarned}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen
                                    )
                                }

                                Column {
                                    Text("মোট উইথড্র", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        "৳${walletProfile.totalWithdrawn}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LuxuryGold
                                    )
                                }

                                Column {
                                    Text("রেফারেল বোনাস", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        "৳${walletProfile.totalReferrals * 20}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricCyan
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Recharge Button
                            Button(
                                onClick = { showRechargeDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricCyan,
                                    contentColor = MidnightDark
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ব্যালেন্স রিচার্জ করুন (Add Balance)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // === Withdraw Form Section ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = "Withdraw",
                            tint = LuxuryGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "টাকা উইথড্র করুন",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "পেমেন্ট মাধ্যম নির্বাচন করুন:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val methods = listOf(
                            Triple("bKash", BkashPink, "বিকাশ"),
                            Triple("Nagad", NagadOrange, "নগদ"),
                            Triple("Rocket", RocketPurple, "রকেট"),
                            Triple("Binance", Color(0xFFF0B90B), "Binance")
                        )
                        for ((method, color, name) in methods) {
                            val isSelected = selectedMethod == method
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedMethod = method },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            ) {
                                Text(
                                    text = name,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(if (selectedMethod == "Binance") "Binance Pay ID / USDT Address" else "$selectedMethod পার্সোনাল নাম্বার") },
                        placeholder = { Text(if (selectedMethod == "Binance") "Pay ID বা TRC20 Address" else "01XXXXXXXXX") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("উইথড্র পরিমাণ (সর্বনিম্ন ৳৫০)") },
                        placeholder = { Text("৳100") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val amount = withdrawAmountText.toIntOrNull() ?: 0
                            onWithdraw(selectedMethod, accountNumber, amount)
                            accountNumber = ""
                            withdrawAmountText = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LuxuryGold,
                            contentColor = MidnightDark
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "উইথড্র রিকোয়েস্ট পাঠান",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // === Withdrawals History ===
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)) {
                Text(
                    text = "উইথড্র হিস্ট্রি",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (withdrawals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "এখনো কোনো উইথড্র করেননি।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(withdrawals) { item ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "৳${item.amount} (${item.paymentMethod})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "নাম্বার: ${item.accountNumber}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (item.adminTrxId.isNotBlank()) {
                                    Text(
                                        text = "TrxID: ${item.adminTrxId}",
                                        fontSize = 10.sp,
                                        color = SuccessGreen
                                    )
                                }
                            }

                            WithdrawalStatusBadge(status = item.status)
                        }
                    }
                }
            }
        }

        // Support Helpline
        item {
            Box(modifier = Modifier.padding(16.dp)) {
                TelegramSupportCard()
            }
        }
    }

    // Quick Add Balance Dialog
    if (showRechargeDialog) {
        var rechargeAmountText by remember { mutableStateOf("100") }

        AlertDialog(
            onDismissRequest = { showRechargeDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            title = {
                Text(
                    text = "ওয়ালেট ব্যালেন্স রিচার্জ",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column {
                    Text(
                        text = "টেস্ট বা দ্রুত ব্যালেন্স এড করতে অ্যামাউন্ট নির্বাচন করুন:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (amt in listOf(100, 300, 500)) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { rechargeAmountText = amt.toString() },
                                shape = RoundedCornerShape(8.dp),
                                color = if (rechargeAmountText == amt.toString()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (rechargeAmountText == amt.toString()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            ) {
                                Text(
                                    text = "৳$amt",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (rechargeAmountText == amt.toString()) MidnightDark else MaterialTheme.colorScheme.onSurface,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = rechargeAmountText,
                        onValueChange = { rechargeAmountText = it },
                        label = { Text("রিচার্জ পরিমাণ (৳)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = rechargeAmountText.toIntOrNull() ?: 100
                        onRecharge(amount)
                        showRechargeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricCyan,
                        contentColor = MidnightDark
                    )
                ) {
                    Text("এড করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRechargeDialog = false }) {
                    Text("বাতিল", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
fun WithdrawalStatusBadge(status: String) {
    val (text, color, icon) = when (status) {
        "APPROVED" -> Triple("পেইড", SuccessGreen, Icons.Default.CheckCircle)
        "REJECTED" -> Triple("বাতিল", DangerRed, Icons.Default.Close)
        else -> Triple("অপেক্ষমান", WarningAmber, Icons.Default.HourglassEmpty)
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
