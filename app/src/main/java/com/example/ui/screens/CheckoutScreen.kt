package com.example.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.TelegramGroup
import com.example.ui.components.TelegramSupportCard
import com.example.ui.components.copyToClipboard
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
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun PaymentBrandLogo(
    customUrl: String?,
    @DrawableRes defaultResId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    if (!customUrl.isNullOrBlank()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(customUrl)
                .crossfade(true)
                .error(defaultResId)
                .placeholder(defaultResId)
                .build(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else {
        Image(
            painter = painterResource(id = defaultResId),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun CheckoutScreen(
    group: TelegramGroup,
    adminConfigs: Map<String, String>,
    onBack: () -> Unit,
    onSubmitOrder: (telegram: String, method: String, senderNumber: String, trxId: String) -> Unit
) {
    val context = LocalContext.current

    // Payment methods: bKash, Nagad, Rocket, Binance
    var selectedPaymentMethod by remember { mutableStateOf("bKash") }
    var senderNumberOrBinanceId by remember { mutableStateOf("") }
    var trxId by remember { mutableStateOf("") }
    var userTelegram by remember { mutableStateOf("") }

    val appLogoUrl = adminConfigs["app_logo_url"]
    val bkashLogoUrl = adminConfigs["bkash_logo_url"]
    val nagadLogoUrl = adminConfigs["nagad_logo_url"]
    val rocketLogoUrl = adminConfigs["rocket_logo_url"]
    val binanceLogoUrl = adminConfigs["binance_logo_url"]

    val bkashNumber = adminConfigs["bkash_number"] ?: "01789-567890"
    val nagadNumber = adminConfigs["nagad_number"] ?: "01812-345678"
    val rocketNumber = adminConfigs["rocket_number"] ?: "01934-567891"
    val binanceId = adminConfigs["binance_id"] ?: "87013689"
    val binanceUsdt = adminConfigs["binance_usdt_address"] ?: "TYeK8Q3LqW4vH1m9PbzE9102XUsdtTrc20"

    val isBinance = selectedPaymentMethod == "Binance"

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
                tonalElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
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

                    // Official App Logo Displayed In Payment Options Header
                    PaymentBrandLogo(
                        customUrl = appLogoUrl,
                        defaultResId = R.drawable.ic_app_logo,
                        contentDescription = "SLK App Logo",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "পেমেন্ট ও অর্ডার কনফার্মেশন",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Store",
                                tint = ElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "বিকাশ • নগদ • রকেট • Binance নিরাপদ পেমেন্ট",
                            fontSize = 11.sp,
                            color = ElectricCyan
                        )
                    }
                }
            }
        }

        // === Group Screenshot & Order Details Summary ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Screenshot preview banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(95.dp)
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
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(ElectricCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircleFilled,
                                    contentDescription = "Video",
                                    tint = MidnightDark,
                                    modifier = Modifier.size(26.dp)
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
                                    text = "গ্রুপ সদস্য: ${group.membersCount} জন",
                                    fontSize = 11.sp,
                                    color = ElectricCyan
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "৳${group.price} BDT",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = LuxuryGold
                            )
                            if (isBinance) {
                                val approxUsdt = String.format("%.2f", group.price / 125.0)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(~$approxUsdt USDT)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF0B90B)
                                )
                            }
                        }
                    }
                }
            }
        }

        // === Payment Method Selector (bKash, Nagad, Rocket, Binance) ===
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "পেমেন্ট মেথড সিলেক্ট করুন:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "৪টি মেথড সক্রিয়",
                        fontSize = 11.sp,
                        color = ElectricCyan
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMethodLogoChip(
                        name = "বিকাশ",
                        customUrl = bkashLogoUrl,
                        defaultResId = R.drawable.ic_bkash,
                        brandColor = BkashPink,
                        isSelected = selectedPaymentMethod == "bKash",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "bKash" }
                    )

                    PaymentMethodLogoChip(
                        name = "নগদ",
                        customUrl = nagadLogoUrl,
                        defaultResId = R.drawable.ic_nagad,
                        brandColor = NagadOrange,
                        isSelected = selectedPaymentMethod == "Nagad",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "Nagad" }
                    )

                    PaymentMethodLogoChip(
                        name = "রকেট",
                        customUrl = rocketLogoUrl,
                        defaultResId = R.drawable.ic_rocket,
                        brandColor = RocketPurple,
                        isSelected = selectedPaymentMethod == "Rocket",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "Rocket" }
                    )

                    PaymentMethodLogoChip(
                        name = "Binance",
                        customUrl = binanceLogoUrl,
                        defaultResId = R.drawable.ic_binance,
                        brandColor = Color(0xFFF0B90B),
                        isSelected = selectedPaymentMethod == "Binance",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPaymentMethod = "Binance" }
                    )
                }
            }
        }

        // === Step-by-Step Payment Instructions & Account Numbers ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isBinance) "Binance Pay / USDT পেমেন্ট নির্দেশনা:" else "$selectedPaymentMethod পেমেন্ট নির্দেশনা (Step by Step):",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxuryGold
                        )

                        // Active Logo Badge
                        PaymentBrandLogo(
                            customUrl = when (selectedPaymentMethod) {
                                "Nagad" -> nagadLogoUrl
                                "Rocket" -> rocketLogoUrl
                                "Binance" -> binanceLogoUrl
                                else -> bkashLogoUrl
                            },
                            defaultResId = when (selectedPaymentMethod) {
                                "Nagad" -> R.drawable.ic_nagad
                                "Rocket" -> R.drawable.ic_rocket
                                "Binance" -> R.drawable.ic_binance
                                else -> R.drawable.ic_bkash
                            },
                            contentDescription = selectedPaymentMethod,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isBinance) {
                        // --- BINANCE SPECIFIC SECTION ---
                        // Binance Pay ID
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0B90B).copy(alpha = 0.6f))
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
                                        text = "Binance Pay ID:",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = binanceId,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFF0B90B),
                                        letterSpacing = 1.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        copyToClipboard(context, binanceId, "Binance Pay ID কপি হয়েছে!")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF0B90B),
                                        contentColor = Color(0xFF1E2329)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // Binance USDT Address (TRC20)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
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
                                        text = "USDT Address (TRC20 / BEP20):",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = binanceUsdt,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricCyan,
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        copyToClipboard(context, binanceUsdt, "USDT Address কপি হয়েছে!")
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
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("কপি", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "১. আপনার Binance অ্যাপ ওপেন করে Binance Pay অথবা Wallet Transfer এ যান।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )
                        Text(
                            text = "২. Pay ID ($binanceId) অথবা USDT Address এ নির্ধারিত পরিমাণের USDT পাঠান।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )
                        Text(
                            text = "৩. পেমেন্টের পর প্রাপ্ত Binance Order ID বা TxID নিচের বক্সে দিন।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )

                    } else {
                        // --- BKASH / NAGAD / ROCKET SECTION ---
                        val currentPaymentNumber = when (selectedPaymentMethod) {
                            "Nagad" -> nagadNumber
                            "Rocket" -> rocketNumber
                            else -> bkashNumber
                        }

                        val activeColor = when (selectedPaymentMethod) {
                            "Nagad" -> NagadOrange
                            "Rocket" -> RocketPurple
                            else -> BkashPink
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, activeColor.copy(alpha = 0.6f))
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
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = currentPaymentNumber,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = activeColor,
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
                                        containerColor = activeColor,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp)
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

                        Text(
                            text = "১. আপনার $selectedPaymentMethod অ্যাপ ওপেন করে 'Send Money' (সেন্ড মানি) চাপুন।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "২. উপরের নাম্বারে নির্ধারিত মূল্য (৳${group.price}) টাকা সেন্ড মানি করুন।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "৩. পেমেন্ট সফল হওয়ার পর Transaction ID (TrxID) টি নিচের বক্সে দিন।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // === Order Submission Form ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "আপনার পেমেন্ট তথ্য দিন:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
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
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sender Number or Binance ID
                    OutlinedTextField(
                        value = senderNumberOrBinanceId,
                        onValueChange = { senderNumberOrBinanceId = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(if (isBinance) "যে Binance Pay ID / Email থেকে পাঠিয়েছেন" else "যে নাম্বার থেকে টাকা পাঠিয়েছেন")
                        },
                        placeholder = {
                            Text(if (isBinance) "8701XXXX / user@binance.com" else "01XXXXXXXXX")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // TrxID / Binance Order ID
                    OutlinedTextField(
                        value = trxId,
                        onValueChange = { trxId = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(if (isBinance) "Binance Pay Order ID / TxID" else "Transaction ID (TrxID)")
                        },
                        placeholder = {
                            Text(if (isBinance) "e.g. 2198034567" else "e.g. BKL890XP12")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Order Button
                    Button(
                        onClick = {
                            onSubmitOrder(userTelegram, selectedPaymentMethod, senderNumberOrBinanceId, trxId)
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
 * Payment Method Chip displaying the real vector or custom URL logo.
 */
@Composable
fun PaymentMethodLogoChip(
    name: String,
    customUrl: String?,
    @DrawableRes defaultResId: Int,
    brandColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) brandColor.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) brandColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PaymentBrandLogo(
                customUrl = customUrl,
                defaultResId = defaultResId,
                contentDescription = name,
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) brandColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
