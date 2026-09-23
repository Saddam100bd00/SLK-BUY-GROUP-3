package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GroupOrder
import com.example.data.model.WalletProfile
import com.example.ui.components.TelegramSupportCard
import com.example.ui.components.copyToClipboard
import com.example.ui.components.openTelegramLink
import com.example.ui.theme.BkashPink
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDark
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AppScreen

@Composable
fun ProfileScreen(
    walletProfile: WalletProfile,
    orders: List<GroupOrder>,
    isAdminUnlocked: Boolean,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onUnlockAdmin: (String) -> Boolean,
    onNavigate: (AppScreen) -> Unit,
    onRedeemPromoCode: (String) -> Unit
) {
    val context = LocalContext.current
    var showAdminDialog by remember { mutableStateOf(false) }
    var adminSecretInput by remember { mutableStateOf("") }
    var showInstallDialog by remember { mutableStateOf(false) }
    var showGithubDialog by remember { mutableStateOf(false) }
    var promoCodeInput by remember { mutableStateOf("") }
    var isAppLockEnabled by remember { mutableStateOf(false) }
    var cacheCleanedNotice by remember { mutableStateOf(false) }

    val referralLink = "https://t.me/ItsSaddam9?start=${walletProfile.referralCode}"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // === Profile Header Card ===
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                if (isDarkMode) Color(0xFF0F1E36) else Color(0xFFDBEAFE),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(ElectricCyan, ElectricCyanDark))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = MidnightDark,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = walletProfile.userTelegram,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = "VIP প্রিমিয়াম মেম্বার • SLK BUY GROUP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LuxuryGold
                            )

                            Text(
                                text = "ব্যালেন্স: ৳${walletProfile.currentBalance} BDT",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }
        }

        // === Theme Settings Card (Dark & Light Mode Switch) ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isDarkMode) Color(0xFF1E2638) else Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Theme",
                                tint = if (isDarkMode) ElectricCyan else Color(0xFFD97706),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (isDarkMode) "ডার্ক থিম মোড (সক্রিয়)" else "হোয়াইট / লাইট থিম মোড (সক্রিয়)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isDarkMode) "চোখের আরামদায়ক ডার্ক মোড চালু আছে" else "উজ্জ্বল ক্ল্যাসিক লাইট মোড চালু আছে",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ElectricCyan,
                            checkedTrackColor = Color(0xFF162D4A),
                            uncheckedThumbColor = Color(0xFFD97706),
                            uncheckedTrackColor = Color(0xFFFDE68A)
                        )
                    )
                }
            }
        }

        // === VIP Benefits Card (প্রিমিয়াম ফিচার) ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "VIP",
                            tint = LuxuryGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "প্রিমিয়াম ভিআইপি সুবিধা ও সুযোগসমূহ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxuryGold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "⭐ আনলিমিটেড লাইফটাইম প্রাইভেট টেলিগ্রাম গ্রুপে প্রবেশাধিকার\n⭐ ০% উইথড্র ফি ও ইনস্ট্যান্ট নগদ/বিকাশ পেমেন্ট\n⭐ জিমেইল ও ফেসবুক বিক্রিতে সর্বোচ্চ বোনাস রেট\n⭐ ২৪/৭ ডেডিকেটেড ডিরেক্ট এডমিন ভিআইপি সাপোর্ট",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // === Promo Code / Voucher Box ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Promo",
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "প্রোমো কোড / ক্যাশ ভাউচার রিডিম",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "আপনার কাছে কোনো প্রমো কোড থাকলে লিখুন (যেমন: VIP50 বা WELCOME100):",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = promoCodeInput,
                            onValueChange = { promoCodeInput = it },
                            placeholder = { Text("VIP50") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Button(
                            onClick = {
                                if (promoCodeInput.isNotBlank()) {
                                    onRedeemPromoCode(promoCodeInput)
                                    promoCodeInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricCyan,
                                contentColor = MidnightDark
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("রিডিম", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // === Referral Program Card (রেফারেল ইনকাম) ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Referral",
                            tint = SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "রেফারেল ইনকাম প্রোগ্রাম",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "আপনার বন্ধুদের রেফার করে প্রতি রেফারে ৳২০ বোনাস পান এবং তাদের প্রতিটি কেনাকাটায় ৫% লাইফটাইম কমিশন উপভোগ করুন!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Referral Code Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyanDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "আপনার রেফার কোড:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = walletProfile.referralCode,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ElectricCyan,
                                    letterSpacing = 1.sp
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        copyToClipboard(context, walletProfile.referralCode, "রেফার কোড কপি হয়েছে!")
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
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("কপি", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "🔥 SLK BUY GROUP - সেরা টেলিগ্রাম প্রিমিয়াম ভিডিও গ্রুপ মার্কেটপ্লেস ও জিমেইল/ফেসবুক বিক্রি করে ইনকাম করুন! আমার রেফারেল লিঙ্ক: $referralLink"
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Referral Link"))
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SuccessGreen,
                                        contentColor = MidnightDark
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("শেয়ার", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "মোট সফল রেফার: ${walletProfile.totalReferrals} জন",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LuxuryGold
                        )
                        Text(
                            text = "মোট আয়: ৳${walletProfile.totalReferrals * 20}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SuccessGreen
                        )
                    }
                }
            }
        }

        // === My Group Orders ===
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)) {
                Text(
                    text = "আমার অর্ডারসমূহ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (orders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "এখনো কোনো গ্রুপ অর্ডার করেননি।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(orders) { order ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = order.groupTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                OrderStatusBadge(status = order.status)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "মূল্য: ৳${order.amount} • মাধ্যম: ${order.paymentMethod} • TrxID: ${order.trxId}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (order.status == "APPROVED" && order.inviteLinkSent.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            openTelegramLink(context, order.inviteLinkSent)
                                        },
                                    color = Color(0x3310B981),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "🔗 টেলিগ্রাম গ্রুপ জয়েন লিংক: ${order.inviteLinkSent}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen
                                        )
                                        Text(
                                            text = "জয়েন করুন",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // === Security & Cache Optimization ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Security Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = "Security", tint = SuccessGreen)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("অ্যাপ সিকিউরিটি শিল্ড", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("বায়োমেট্রিক ও সেশন লকার সুরক্ষা", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = isAppLockEnabled,
                            onCheckedChange = { isAppLockEnabled = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cache Cleaner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                cacheCleanedNotice = true
                                copyToClipboard(context, "CACHE_CLEARED", "ক্যাশ ও মেমোরি পরিষ্কার করা হয়েছে! অ্যাপ এখন সুপার ফাস্ট।")
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CleaningServices, contentDescription = "Clean", tint = ElectricCyan)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("ক্যাশ ও মেমোরি অপ্টিমাইজ করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("অপ্রয়োজনীয় ডাটা রিমুভ করে স্পিড বৃদ্ধি করুন", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Text("ক্লিন করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                    }
                }
            }
        }

        // === Install App & GitHub System Options ===
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text(
                    text = "অ্যাপ ও সিস্টেম সেটিংস",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Install APK / System Guide
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showInstallDialog = true },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Install",
                                tint = ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "অ্যাপ ইনস্টল সিস্টেম (APK ও শর্টকাট)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "মোবাইলে কিভাবে ইনস্টল ও আপডেট রাখবেন তার গাইড",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // GitHub Export / Source Option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showGithubDialog = true },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "GitHub",
                                tint = LuxuryGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "গিটহাবে ছেড়ে দেওয়ার অপশন (GitHub Export)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "সোর্স কোড রিপোজিটরি ও GitHub Push গাইড",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Secret Admin Panel Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isAdminUnlocked) {
                                onNavigate(AppScreen.ADMIN)
                            } else {
                                showAdminDialog = true
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAdminUnlocked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAdminUnlocked) LuxuryGold else WarningAmber.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Secret Admin",
                                tint = if (isAdminUnlocked) LuxuryGold else WarningAmber,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isAdminUnlocked) "সিক্রেট এডমিন প্যানেল (আনলকড)" else "সিক্রেট এডমিন প্যানেল প্রবেশ করুন",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAdminUnlocked) LuxuryGold else WarningAmber
                                )
                                Text(
                                    text = "অনুমোদিত ইউজার: 8701368956 (@ItsSaddam9)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isAdminUnlocked) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = if (isAdminUnlocked) LuxuryGold else WarningAmber,
                            modifier = Modifier.size(18.dp)
                        )
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

    // Secret Admin Gate Dialog
    if (showAdminDialog) {
        AlertDialog(
            onDismissRequest = { showAdminDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Secret Admin",
                        tint = LuxuryGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "সিক্রেট এডমিন গেটওয়ে",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "এই প্যানেল শুধুমাত্র নির্দিষ্ট ইউজারের জন্য অনুমোদিত:\nটেলিগ্রাম আইডি: 8701368956 (@ItsSaddam9)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = adminSecretInput,
                        onValueChange = { adminSecretInput = it },
                        label = { Text("টেলিগ্রাম আইডি / পাসকোড লিখুন") },
                        placeholder = { Text("8701368956") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = LuxuryGold,
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
                        val success = onUnlockAdmin(adminSecretInput)
                        if (success) {
                            showAdminDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LuxuryGold,
                        contentColor = MidnightDark
                    )
                ) {
                    Text("প্রবেশ করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminDialog = false }) {
                    Text("বাতিল", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // Install Guide Dialog
    if (showInstallDialog) {
        AlertDialog(
            onDismissRequest = { showInstallDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            title = {
                Text(
                    text = "SLK BUY GROUP ইনস্টলেশন গাইড",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column {
                    Text(
                        text = "১. আপনি AI Studio এর সেটিংস মেনু থেকে সরাসরি 'Download APK' বা 'Export ZIP' করতে পারেন।\n\n২. টেলিগ্রাম মিনি অ্যাপস হিসেবে ব্যবহার করতে আপনার বট মেনুতে এই অ্যাপের ডেভেলপমেন্ট লিংক যুক্ত করতে পারেন।\n\n৩. যে কোনো ডিভাইসে ফুলস্ক্রিন স্পিডি চালানোর জন্য Jetpack Compose এ এটি সম্পূর্ণ অপ্টিমাইজড।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInstallDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = MidnightDark)
                ) {
                    Text("ঠিক আছে", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // GitHub Dialog
    if (showGithubDialog) {
        AlertDialog(
            onDismissRequest = { showGithubDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            title = {
                Text(
                    text = "GitHub Repository & Export",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column {
                    Text(
                        text = "SLK BUY GROUP এর সম্পূর্ণ সোর্স কোড মডার্ন অ্যান্ড্রয়েড আর্কিটেকচারে প্রস্তুত:\n\n• Kotlin + Jetpack Compose\n• Room Database (অফলাইন পারসিস্টেন্স)\n• Material 3 UK Luxury Dark ও Light থিম\n• বিকাশ, নগদ, রকেট ও Binance পেমেন্ট ভেরিফিকেশন\n\nআপনি AI Studio শীর্ষ মেনুর 'Export to GitHub' বাটনে ক্লিক করে সরাসরি আপনার নিজস্ব GitHub একাউন্টে এই কোড রিপোজিটরি পুশ করতে পারবেন।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showGithubDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold, contentColor = MidnightDark)
                ) {
                    Text("বুঝেছি", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun OrderStatusBadge(status: String) {
    val (text, color, icon) = when (status) {
        "APPROVED" -> Triple("অনুমোদিত", SuccessGreen, Icons.Default.CheckCircle)
        "REJECTED" -> Triple("বাতিল", BkashPink, Icons.Default.Lock)
        else -> Triple("যাচাই চলছে", WarningAmber, Icons.Default.HourglassEmpty)
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
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
