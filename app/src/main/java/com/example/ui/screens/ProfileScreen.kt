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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GroupOrder
import com.example.data.model.ReferralEntry
import com.example.data.model.WalletProfile
import com.example.ui.components.TelegramSupportCard
import com.example.ui.components.copyToClipboard
import com.example.ui.components.openTelegramLink
import com.example.ui.theme.BkashPink
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDark
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MidnightBorder
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightCardHover
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AppScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    walletProfile: WalletProfile,
    orders: List<GroupOrder>,
    referralHistory: List<ReferralEntry> = emptyList(),
    referralLink: String,
    isAdminUnlocked: Boolean,
    onUnlockAdmin: (String) -> Boolean,
    onSimulateReferral: (name: String, tg: String) -> Unit = { _, _ -> },
    onNavigate: (AppScreen) -> Unit
) {
    val context = LocalContext.current
    var showAdminDialog by remember { mutableStateOf(false) }
    var adminSecretInput by remember { mutableStateOf("") }
    var showInstallDialog by remember { mutableStateOf(false) }
    var showGithubDialog by remember { mutableStateOf(false) }
    var showSimulateReferralDialog by remember { mutableStateOf(false) }
    var testRefName by remember { mutableStateOf("") }
    var testRefTg by remember { mutableStateOf("") }

    val totalReferralBonusEarned = referralHistory.sumOf { it.bonusAmount } + referralHistory.sumOf { it.commissionEarned }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDark),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        // === Profile Header Card ===
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0F1E36), MidnightDark)
                        )
                    )
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, MidnightBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MidnightCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
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
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = walletProfile.userTelegram,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
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
                                color = LuxuryGold
                            )

                            Text(
                                text = "ব্যালেন্স: ৳${walletProfile.currentBalance} BDT",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
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
                colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Referral",
                                tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "রেফারেল ইনকাম ও বেনিফিট",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Surface(
                            color = SuccessGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "প্রতি রেফারে ৳২০ বোনাস",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "আপনার বন্ধুদের রেফার লিংকের মাধ্যমে জয়েন করান। তারা জয়েন করলেই আপনার ওয়ালেটে ৳২০ ইনস্ট্যান্ট বোনাস জমা হবে এবং তাদের প্রতিটি গ্রুপ কেনাকাটায় লাইফটাইম ৫% কমিশন পাবেন।",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dynamic Referral Link Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MidnightCard,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyanDark)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "আপনার অটো রেফারেল টেলিগ্রাম লিংক:", fontSize = 11.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
                                Surface(
                                    color = LuxuryGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "কোড: ${walletProfile.referralCode}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LuxuryGold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = referralLink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MidnightSurface, RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        copyToClipboard(context, referralLink, "রেফারেল লিংক কপি হয়েছে!")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ElectricCyan,
                                        contentColor = MidnightDark
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("লিংক কপি", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "🔥 SLK BUY GROUP - সেরা টেলিগ্রাম প্রিমিয়াম ভিডিও গ্রুপ মার্কেটপ্লেস ও জিমেইল/ফেসবুক বিক্রি করে ইনকাম করুন! আমার রেফারেল লিংক দিয়ে প্রবেশ করুন: $referralLink"
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Referral Link"))
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TelegramBlue,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("শেয়ার করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Summary Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "মোট রেফারেল মেম্বার", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "${referralHistory.size} জন",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = LuxuryGold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "মোট রেফারেল আয়", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "৳$totalReferralBonusEarned BDT",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }
        }

        // === Referral History (রেফারেল হিস্টরি বিস্তারিত) ===
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.History, contentDescription = "History", tint = LuxuryGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "রেফারেল হিস্টরি (Referral History)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    OutlinedButton(
                        onClick = { showSimulateReferralDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Test", modifier = Modifier.size(12.dp), tint = ElectricCyan)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("নতুন মেম্বার টেস্ট", fontSize = 10.sp, color = ElectricCyan)
                    }
                }
                Text(
                    text = "আপনার রেফারেল লিংক থেকে যারা অ্যাপে প্রবেশ ও জয়েন করেছে তাদের তালিকা:",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        if (referralHistory.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightCard),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "এখনো কোনো রেফারেল মেম্বার যুক্ত হয়নি। আপনার রেফারেল লিংক বন্ধুদের সাথে শেয়ার করুন!",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        } else {
            items(referralHistory) { ref ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(ElectricCyan.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ref.referredUserName.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricCyan,
                                        fontSize = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = ref.referredUserName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "টেলিগ্রাম: ${ref.referredUserTelegram}",
                                        fontSize = 11.sp,
                                        color = ElectricCyan
                                    )
                                    Text(
                                        text = "জয়েন: ${formatTimestamp(ref.joinedTimestamp)}",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    color = SuccessGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "+৳${ref.bonusAmount} বোনাস",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = ref.status,
                                    fontSize = 9.sp,
                                    color = LuxuryGold
                                )
                            }
                        }
                    }
                }
            }
        }

        // === My Group Orders ===
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)) {
                Text(
                    text = "আমার গ্রুপ অর্ডারসমূহ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
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
                        color = TextSecondary
                    )
                }
            }
        } else {
            items(orders) { order ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MidnightCard),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
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
                                    color = TextPrimary
                                )
                                OrderStatusBadge(status = order.status)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "মূল্য: ৳${order.amount} • মাধ্যম: ${order.paymentMethod} • TrxID: ${order.trxId}",
                                fontSize = 11.sp,
                                color = TextSecondary
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

        // === Install App & GitHub System Options ===
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text(
                    text = "অ্যাপ ও সিস্টেম সেটিংস",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Install APK / System Guide
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showInstallDialog = true },
                    colors = CardDefaults.cardColors(containerColor = MidnightCard),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
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
                                    color = TextPrimary
                                )
                                Text(
                                    text = "মোবাইলে কিভাবে ইনস্টল ও আপডেট রাখবেন তার গাইড",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go",
                            tint = TextSecondary,
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
                    colors = CardDefaults.cardColors(containerColor = MidnightCard),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
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
                                    color = TextPrimary
                                )
                                Text(
                                    text = "সোর্স কোড রিপোজিটরি ও GitHub Push গাইড",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go",
                            tint = TextSecondary,
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
                        containerColor = if (isAdminUnlocked) Color(0xFF1E2638) else Color(0xFF1C1814)
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
                                    text = if (isAdminUnlocked) "সিক্রেট এডমিন প্যানেল (আনলকড)" else "সিক্রেট এডমিন ও ম্যানেজার প্যানেল",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAdminUnlocked) LuxuryGold else WarningAmber
                                )
                                Text(
                                    text = "আসল অনার (8701368956) ও ম্যানেজার লগইন",
                                    fontSize = 11.sp,
                                    color = TextSecondary
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
            containerColor = MidnightCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Secret Admin",
                        tint = LuxuryGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "সিক্রেট এডমিন ও ম্যানেজার গেটওয়ে",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "👑 আসল অনার: 8701368956 (@ItsSaddam9)\n🛡️ অথবা এডমিনের দেওয়া আপনার ম্যানেজার আইডি/পাসকোড লিখুন:",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = adminSecretInput,
                        onValueChange = { adminSecretInput = it },
                        label = { Text("আইডি / পাসকোড লিখুন") },
                        placeholder = { Text("8701368956 অথবা ম্যানেজার আইডি") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MidnightSurface,
                            unfocusedContainerColor = MidnightSurface,
                            focusedBorderColor = LuxuryGold,
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
                    Text("বাতিল", color = TextSecondary)
                }
            }
        )
    }

    // Simulate Referral Dialog (For testing real-time referral additions)
    if (showSimulateReferralDialog) {
        AlertDialog(
            onDismissRequest = { showSimulateReferralDialog = false },
            containerColor = MidnightCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = "Simulate", tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("নতুন রেফারেল জয়েন সিমুলেশন", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "আপনার রেফারেল লিংক থেকে কোনো মেম্বার টেলিগ্রামে জয়েন করলে কেমন দেখাবে তা টেস্ট করুন:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = testRefName,
                        onValueChange = { testRefName = it },
                        label = { Text("মেম্বারের নাম") },
                        placeholder = { Text("e.g. আকাশ আহমেদ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = testRefTg,
                        onValueChange = { testRefTg = it },
                        label = { Text("মেম্বারের টেলিগ্রাম") },
                        placeholder = { Text("@akash_vip") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSimulateReferral(testRefName, testRefTg)
                        showSimulateReferralDialog = false
                        testRefName = ""
                        testRefTg = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = MidnightDark)
                ) {
                    Text("মেম্বার জয়েন করান (+৳২০)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSimulateReferralDialog = false }) { Text("বাতিল", color = TextSecondary) }
            }
        )
    }

    // Install Guide Dialog
    if (showInstallDialog) {
        AlertDialog(
            onDismissRequest = { showInstallDialog = false },
            containerColor = MidnightCard,
            title = {
                Text(
                    text = "SLK BUY GROUP ইনস্টলেশন গাইড",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "১. আপনি AI Studio এর সেটিংস মেনু থেকে সরাসরি 'Download APK' বা 'Export ZIP' করতে পারেন।\n\n২. টেলিগ্রাম মিনি অ্যাপস হিসেবে ব্যবহার করতে আপনার বট মেনুতে এই অ্যাপের ডেভেলপমেন্ট লিংক যুক্ত করতে পারেন।\n\n৩. যে কোনো ডিভাইসে ফুলস্ক্রিন স্পিডি চালানোর জন্য Jetpack Compose এ এটি সম্পূর্ণ অপ্টিমাইজড।",
                        fontSize = 12.sp,
                        color = TextPrimary,
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
            containerColor = MidnightCard,
            title = {
                Text(
                    text = "GitHub Repository & Export",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "SLK BUY GROUP এর সম্পূর্ণ সোর্স কোড মডার্ন অ্যান্ড্রয়েড আর্কিটেকচারে প্রস্তুত:\n\n• Kotlin + Jetpack Compose\n• Room Database (অফলাইন পারসিস্টেন্স)\n• Material 3 UK Luxury Dark থিমিং\n• বিকাশ, নগদ ও রকেট পেমেন্ট ভেরিফিকেশন\n\nআপনি AI Studio শীর্ষ মেনুর 'Export to GitHub' বাটনে ক্লিক করে সরাসরি আপনার নিজস্ব GitHub একাউন্টে এই কোড রিপোজিটরি পুশ করতে পারবেন।",
                        fontSize = 12.sp,
                        color = TextPrimary,
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

private fun formatTimestamp(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "সম্প্রতি"
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
