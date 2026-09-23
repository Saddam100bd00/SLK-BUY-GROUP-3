package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TelegramGroup
import com.example.ui.components.AppLogoBadge
import com.example.ui.components.PaymentBrandLogo
import com.example.ui.components.TelegramSupportCard
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.BinanceDark
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.BkashPink
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDark
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MidnightBorder
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.RocketPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun CheckoutScreen(
    group: TelegramGroup,
    adminConfigs: Map<String, String>,
    onBack: () -> Unit,
    onSubmitOrder: (telegram: String, method: String, senderNumber: String, trxId: String) -> Unit
) {
    val context = LocalContext.current

    var selectedPaymentMethod by remember { mutableStateOf("bKash") }
    var senderNumber by remember { mutableStateOf("") }
    var trxId by remember { mutableStateOf("") }
    var userTelegram by remember { mutableStateOf("") }

    val appLogoUrl = adminConfigs["app_logo_url"]
    val bkashLogoUrl = adminConfigs["payment_logo_bkash"] ?: adminConfigs["bkash_logo_url"]
    val nagadLogoUrl = adminConfigs["payment_logo_nagad"] ?: adminConfigs["nagad_logo_url"]
    val rocketLogoUrl = adminConfigs["payment_logo_rocket"] ?: adminConfigs["rocket_logo_url"]
    val binanceLogoUrl = adminConfigs["payment_logo_binance"] ?: adminConfigs["binance_logo_url"]

    val bkashNumber = adminConfigs["bkash_number"] ?: "01789-567890"
    val nagadNumber = adminConfigs["nagad_number"] ?: "01812-345678"
    val rocketNumber = adminConfigs["rocket_number"] ?: "01934-567891"
    val binanceAddress = adminConfigs["binance_id"] ?: adminConfigs["binance_address"] ?: "8701368956 (Binance Pay ID)"

    val currentPaymentNumber = when (selectedPaymentMethod) {
        "Nagad" -> nagadNumber
        "Rocket" -> rocketNumber
        "Binance" -> binanceAddress
        else -> bkashNumber
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        // === Top Header with Official App Logo ===
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    AppLogoBadge(
                        customLogoUrl = appLogoUrl,
                        size = 38.dp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "পেমেন্ট ও অর্ডার কনফার্মেশন",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "বিকাশ • নগদ • রকেট • Binance নিরাপদ গেটওয়ে",
                            fontSize = 11.sp,
                            color = ElectricCyanDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // === Group Screenshot & Order Details Summary ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Screenshot preview banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF0F1E36), Color(0xFF0D2538))
                                )
                            )
                            .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(ElectricCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircleFilled,
                                    contentDescription = "Video",
                                    tint = MidnightDark,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = BkashPink,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = group.badge,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = group.samplePostTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Text(
                                    text = "সদস্য সংখ্যা: ${group.membersCount}",
                                    fontSize = 11.sp,
                                    color = ElectricCyan
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = group.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "পরিশোধযোগ্য মূল্য:",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "৳${group.price} BDT",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = LuxuryGold
                            )
                            if (selectedPaymentMethod == "Binance") {
                                val usdtAmount = String.format("%.2f", group.price / 122.0)
                                Text(
                                    text = " (~$$usdtAmount USDT)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BinanceYellow
                                )
                            }
                        }
                    }
                }
            }
        }

        // === Payment Method Selector (bKash, Nagad, Rocket, Binance) ===
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(
                    text = "পেমেন্ট মেথড সিলেক্ট করুন:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMethodSelectionCard(
                        name = "বিকাশ",
                        code = "bKash",
                        logoUrl = bkashLogoUrl,
                        brandColor = BkashPink,
                        isSelected = selectedPaymentMethod == "bKash",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "bKash" }
                    )

                    PaymentMethodSelectionCard(
                        name = "নগদ",
                        code = "Nagad",
                        logoUrl = nagadLogoUrl,
                        brandColor = NagadOrange,
                        isSelected = selectedPaymentMethod == "Nagad",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "Nagad" }
                    )

                    PaymentMethodSelectionCard(
                        name = "রকেট",
                        code = "Rocket",
                        logoUrl = rocketLogoUrl,
                        brandColor = RocketPurple,
                        isSelected = selectedPaymentMethod == "Rocket",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "Rocket" }
                    )

                    PaymentMethodSelectionCard(
                        name = "Binance",
                        code = "Binance",
                        logoUrl = binanceLogoUrl,
                        brandColor = BinanceYellow,
                        isSelected = selectedPaymentMethod == "Binance",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "Binance" }
                    )
                }
            }
        }

        // === Step-by-Step Payment Instructions with Logos ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PaymentBrandLogo(
                            method = selectedPaymentMethod,
                            customLogoUrl = when (selectedPaymentMethod) {
                                "Nagad" -> nagadLogoUrl
                                "Rocket" -> rocketLogoUrl
                                "Binance" -> binanceLogoUrl
                                else -> bkashLogoUrl
                            },
                            size = 32.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "$selectedPaymentMethod পেমেন্ট নির্দেশনা:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxuryGold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Admin Number / Pay ID Display with Copy Button
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ElectricCyanDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedPaymentMethod == "Binance") "Binance Pay ID / USDT Address:" else "$selectedPaymentMethod পার্সোনাল নাম্বার:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = currentPaymentNumber,
                                    fontSize = if (selectedPaymentMethod == "Binance") 15.sp else 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (selectedPaymentMethod == "Binance") BinanceYellow else ElectricCyanDark,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Button(
                                onClick = {
                                    val cleaned = if (selectedPaymentMethod == "Binance") {
                                        currentPaymentNumber.split(" ")[0].trim()
                                    } else {
                                        currentPaymentNumber.replace("-", "").replace(" ", "").trim()
                                    }
                                    copyToClipboard(
                                        context,
                                        cleaned,
                                        "$selectedPaymentMethod অ্যাড্রেস কপি হয়েছে!"
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricCyan,
                                    contentColor = MidnightDark
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("কপি", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (selectedPaymentMethod == "Binance") {
                        Text(
                            text = "১. আপনার Binance অ্যাপে গিয়ে Pay অথবা Send অপশন ওপেন করুন।\n" +
                                    "২. উপরের Binance Pay ID অথবা USDT তে ৳${group.price} সমপরিমাণ (~$$ {String.format(\"%.2f\", group.price / 122.0)} USDT) সেন্ড করুন।\n" +
                                    "৩. পেমেন্ট সম্পন্ন হলে Order ID / TxID এবং আপনার Telegram ইউজারনেম নিচে সাবমিট করুন।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    } else {
                        Text(
                            text = "১. আপনার $selectedPaymentMethod অ্যাপ অথবা ডায়াল কোড ব্যবহার করে 'Send Money' সিলেক্ট করুন।\n" +
                                    "২. উপরে দেওয়া নাম্বারে সঠিক মূল্য ৳${group.price} সেন্ড মানি করুন।\n" +
                                    "৩. সফলভাবে পেমেন্ট করার পর যে নাম্বার থেকে টাকা পাঠিয়েছেন এবং ফিরতি মেসেজের Transaction ID (TrxID) নিচে লিখে সাবমিট করুন।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // === Order Input Verification Form ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Safe",
                            tint = SuccessGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "পেমেন্ট তথ্য প্রদান করুন:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Telegram Username
                    OutlinedTextField(
                        value = userTelegram,
                        onValueChange = { userTelegram = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("আপনার টেলিগ্রাম ইউজারনেম (@username)") },
                        placeholder = { Text("e.g. @YourUsername") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sender Number / Binance UID
                    OutlinedTextField(
                        value = senderNumber,
                        onValueChange = { senderNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(if (selectedPaymentMethod == "Binance") "আপনার Binance Pay ID / UID" else "যে $selectedPaymentMethod নাম্বার থেকে টাকা পাঠিয়েছেন") },
                        placeholder = { Text(if (selectedPaymentMethod == "Binance") "e.g. 123456789" else "e.g. 017XXXXXXXX") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // TrxID / TxHash
                    OutlinedTextField(
                        value = trxId,
                        onValueChange = { trxId = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(if (selectedPaymentMethod == "Binance") "Binance Order ID / TxID" else "Transaction ID (TrxID)") },
                        placeholder = { Text(if (selectedPaymentMethod == "Binance") "e.g. 2389104812" else "e.g. BKL890XP12") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Order Button
                    Button(
                        onClick = {
                            onSubmitOrder(userTelegram, selectedPaymentMethod, senderNumber, trxId)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = MidnightDark
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Confirm",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "অর্ডার নিশ্চিত করুন (৳${group.price})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // === Direct Contact Support Button ===
        item {
            Box(modifier = Modifier.padding(16.dp)) {
                TelegramSupportCard(
                    telegramUrl = adminConfigs["admin_telegram"] ?: "https://t.me/ItsSaddam9"
                )
            }
        }
    }
}

/**
 * Payment method selection card featuring the brand logo + brand label
 */
@Composable
fun PaymentMethodSelectionCard(
    name: String,
    code: String,
    logoUrl: String?,
    brandColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) brandColor.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) brandColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PaymentBrandLogo(
                method = code,
                customLogoUrl = logoUrl,
                size = 32.dp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
