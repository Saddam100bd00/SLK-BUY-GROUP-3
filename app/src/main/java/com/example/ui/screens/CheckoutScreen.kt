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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.components.TelegramSupportCard
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.BkashPink
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
import com.example.ui.theme.TelegramBlue
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

    val bkashNumber = adminConfigs["bkash_number"] ?: "01789-567890"
    val nagadNumber = adminConfigs["nagad_number"] ?: "01812-345678"
    val rocketNumber = adminConfigs["rocket_number"] ?: "01934-567891"

    val currentPaymentNumber = when (selectedPaymentMethod) {
        "Nagad" -> nagadNumber
        "Rocket" -> rocketNumber
        else -> bkashNumber
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDark),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        // === Top Header ===
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "পেমেন্ট ও অর্ডার কনফার্মেশন",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "বিকাশ • নগদ • রকেট নিরাপদ পেমেন্ট",
                        fontSize = 11.sp,
                        color = ElectricCyan
                    )
                }
            }
        }

        // === Group Screenshot & Order Details Summary ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .border(1.dp, MidnightBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MidnightCard),
                shape = RoundedCornerShape(16.dp)
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
                                    color = TextPrimary,
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
                        color = TextPrimary
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
                            color = TextSecondary
                        )
                        Text(
                            text = "৳${group.price} BDT",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = LuxuryGold
                        )
                    }
                }
            }
        }

        // === Payment Method Selector (bKash, Nagad, Rocket) ===
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "পেমেন্ট মেথড সিলেক্ট করুন:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PaymentMethodChip(
                        name = "বিকাশ",
                        code = "bKash",
                        color = BkashPink,
                        isSelected = selectedPaymentMethod == "bKash",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "bKash" }
                    )

                    PaymentMethodChip(
                        name = "নগদ",
                        code = "Nagad",
                        color = NagadOrange,
                        isSelected = selectedPaymentMethod == "Nagad",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "Nagad" }
                    )

                    PaymentMethodChip(
                        name = "রকেট",
                        code = "Rocket",
                        color = RocketPurple,
                        isSelected = selectedPaymentMethod == "Rocket",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "Rocket" }
                    )
                }
            }
        }

        // === Step-by-Step Payment Instructions ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "পেমেন্ট নির্দেশনা (Step by Step):",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = LuxuryGold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Admin Number Display with Copy
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MidnightCard,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyanDark)
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
                                    text = "$selectedPaymentMethod পার্সোনাল নাম্বার:",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = currentPaymentNumber,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ElectricCyan,
                                    letterSpacing = 1.sp
                                )
                            }

                            Button(
                                onClick = {
                                    copyToClipboard(
                                        context,
                                        currentPaymentNumber.replace("-", "").replace(" ", ""),
                                        "$selectedPaymentMethod নাম্বার কপি হয়েছে!"
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricCyan,
                                    contentColor = MidnightDark
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("কপি", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 1
                    Text(
                        text = "১. আপনার $selectedPaymentMethod অ্যাপ ওপেন করে 'Send Money' (সেন্ড মানি) অপশন চাপুন।",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Step 2
                    Text(
                        text = "২. উপরের নাম্বারে নির্ধারিত মূল্য (৳${group.price}) টাকা সেন্ড মানি করুন।",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Step 3
                    Text(
                        text = "৩. পেমেন্ট সফল হওয়ার পর প্রাপ্ত Transaction ID (TrxID) টি কপি করে নিচের বক্সে দিন।",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // === Order Submission Form ===
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
                    Text(
                        text = "আপনার পেমেন্ট তথ্য দিন:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Telegram ID input
                    OutlinedTextField(
                        value = userTelegram,
                        onValueChange = { userTelegram = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("আপনার টেলিগ্রাম ইউজারনেম (e.g. @YourName)") },
                        placeholder = { Text("@user_id") },
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

                    // Sender Number
                    OutlinedTextField(
                        value = senderNumber,
                        onValueChange = { senderNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("যে নাম্বার থেকে টাকা পাঠিয়েছেন") },
                        placeholder = { Text("01XXXXXXXXX") },
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

                    // TrxID
                    OutlinedTextField(
                        value = trxId,
                        onValueChange = { trxId = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Transaction ID (TrxID)") },
                        placeholder = { Text("e.g. BKL890XP12") },
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

        // === Direct Contact Support Button (User Requirement: শেষে একটি কন্টাক্ট সাপোর্ট বাটন থাকবে যাতে সরাসরি কথা বলা যায়) ===
        item {
            Box(modifier = Modifier.padding(16.dp)) {
                TelegramSupportCard(
                    telegramUrl = adminConfigs["admin_telegram"] ?: "https://t.me/ItsSaddam9"
                )
            }
        }
    }
}

@Composable
fun PaymentMethodChip(
    name: String,
    code: String,
    color: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) color.copy(alpha = 0.25f) else MidnightCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) color else MidnightBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else TextSecondary
            )
        }
    }
}
