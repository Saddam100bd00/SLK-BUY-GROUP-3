package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
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
import com.example.ui.components.AppLogoBadge
import com.example.ui.components.PaymentBrandLogo
import com.example.ui.components.TelegramSupportCard
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDark
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MidnightBorder
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightCardHover
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.RocketPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun WalletScreen(
    walletProfile: WalletProfile,
    withdrawals: List<Withdrawal>,
    adminConfigs: Map<String, String> = emptyMap(),
    onWithdraw: (method: String, number: String, amount: Int) -> Unit,
    onRecharge: (amount: Int) -> Unit
) {
    var showRechargeDialog by remember { mutableStateOf(false) }
    var selectedMethod by remember { mutableStateOf("bKash") }
    var accountNumber by remember { mutableStateOf("") }
    var withdrawAmountText by remember { mutableStateOf("") }

    val appLogoUrl = adminConfigs["app_logo_url"]
    val bkashLogoUrl = adminConfigs["payment_logo_bkash"] ?: adminConfigs["bkash_logo_url"]
    val nagadLogoUrl = adminConfigs["payment_logo_nagad"] ?: adminConfigs["nagad_logo_url"]
    val rocketLogoUrl = adminConfigs["payment_logo_rocket"] ?: adminConfigs["rocket_logo_url"]
    val binanceLogoUrl = adminConfigs["payment_logo_binance"] ?: adminConfigs["binance_logo_url"]

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "আমার ওয়ালেট ও লেনদেন",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "বিকাশ • নগদ • রকেট • Binance নিরাপদ গেটওয়ে",
                            fontSize = 12.sp,
                            color = ElectricCyan
                        )
                    }

                    AppLogoBadge(
                        customLogoUrl = appLogoUrl,
                        size = 38.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // UK Luxury Glass Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0x5500E5FF), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MidnightCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF0F2642), Color(0xFF0D172A))
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
                                        color = TextSecondary
                                    )
                                }

                                Surface(
                                    color = Color(0x3300E5FF),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Instant Payout",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricCyan
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "৳${walletProfile.currentBalance}",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Stats breakdown
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("মোট আয়", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        "৳${walletProfile.totalEarned}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen
                                    )
                                }

                                Column {
                                    Text("মোট উইথড্র", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        "৳${walletProfile.totalWithdrawn}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LuxuryGold
                                    )
                                }

                                Column {
                                    Text("রেফারেল বোনাস", fontSize = 11.sp, color = TextSecondary)
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
                colors = CardDefaults.cardColors(containerColor = MidnightCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
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
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "পেমেন্ট মাধ্যম নির্বাচন করুন:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        data class WithdrawalMethodOption(
                            val id: String,
                            val name: String,
                            val color: Color,
                            val logoUrl: String?
                        )
                        val methods = listOf(
                            WithdrawalMethodOption("bKash", "বিকাশ", BkashPink, bkashLogoUrl),
                            WithdrawalMethodOption("Nagad", "নগদ", NagadOrange, nagadLogoUrl),
                            WithdrawalMethodOption("Rocket", "রকেট", RocketPurple, rocketLogoUrl),
                            WithdrawalMethodOption("Binance", "Binance", BinanceYellow, binanceLogoUrl)
                        )
                        for (opt in methods) {
                            val isSelected = selectedMethod == opt.id
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedMethod = opt.id },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) opt.color.copy(alpha = 0.22f) else MidnightSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) opt.color else MidnightBorder
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    PaymentBrandLogo(
                                        method = opt.id,
                                        customLogoUrl = opt.logoUrl,
                                        size = 28.dp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = opt.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(if (selectedMethod == "Binance") "আপনার Binance Pay ID / UID" else "$selectedMethod পার্সোনাল নাম্বার") },
                        placeholder = { Text(if (selectedMethod == "Binance") "e.g. 8701368956" else "01XXXXXXXXX") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MidnightSurface,
                            unfocusedContainerColor = MidnightSurface,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = MidnightBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
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
                            focusedContainerColor = MidnightSurface,
                            unfocusedContainerColor = MidnightSurface,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = MidnightBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
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
                    color = TextPrimary
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
                        color = TextSecondary
                    )
                }
            }
        } else {
            items(withdrawals) { item ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MidnightCard),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                PaymentBrandLogo(
                                    method = item.paymentMethod,
                                    customLogoUrl = when (item.paymentMethod.lowercase()) {
                                        "nagad", "নগদ" -> nagadLogoUrl
                                        "rocket", "রকেট" -> rocketLogoUrl
                                        "binance", "বাইনান্স", "বাইন্যান্স" -> binanceLogoUrl
                                        else -> bkashLogoUrl
                                    },
                                    size = 36.dp
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = "৳${item.amount} (${item.paymentMethod})",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "অ্যাকাউন্ট: ${item.accountNumber}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    if (item.adminTrxId.isNotBlank()) {
                                        Text(
                                            text = "TrxID: ${item.adminTrxId}",
                                            fontSize = 10.sp,
                                            color = SuccessGreen
                                        )
                                    }
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
            containerColor = MidnightCard,
            title = {
                Text(
                    text = "ওয়ালেট ব্যালেন্স রিচার্জ",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "টেস্ট বা দ্রুত ব্যালেন্স এড করতে অ্যামাউন্ট নির্বাচন করুন:",
                        fontSize = 12.sp,
                        color = TextSecondary
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
                                color = if (rechargeAmountText == amt.toString()) ElectricCyan else MidnightSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (rechargeAmountText == amt.toString()) ElectricCyan else MidnightBorder
                                )
                            ) {
                                Text(
                                    text = "৳$amt",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (rechargeAmountText == amt.toString()) MidnightDark else TextPrimary,
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
                            focusedContainerColor = MidnightSurface,
                            unfocusedContainerColor = MidnightSurface,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = MidnightBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
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
                    Text("বাতিল", color = TextSecondary)
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
