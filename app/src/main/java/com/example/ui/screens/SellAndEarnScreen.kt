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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FacebookSubmission
import com.example.data.model.GmailSubmission
import com.example.ui.components.TelegramSupportCard
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricCyan
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
import com.example.ui.viewmodel.SellType

@Composable
fun SellAndEarnScreen(
    initialType: SellType,
    gmailSubmissions: List<GmailSubmission>,
    fbSubmissions: List<FacebookSubmission>,
    onSubmitGmail: (telegram: String, gmail: String, pass: String, recovery: String, type: String, reward: Int) -> Unit,
    onSubmitFacebook: (telegram: String, uid: String, login: String, pass: String, twoFactor: String, year: String, reward: Int) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (initialType == SellType.FACEBOOK) 1 else 0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDark),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        // === Page Header ===
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "একাউন্ট সেল করে আয় করুন",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "জিমেইল ও ফেসবুক আইডি বিক্রি করুন • এডমিন এপ্রুভ করলে অটো ওয়ালেট পেমেন্ট",
                    fontSize = 12.sp,
                    color = LuxuryGold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MidnightSurface,
                    contentColor = ElectricCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = ElectricCyan,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Mail,
                                    contentDescription = "Gmail",
                                    tint = if (selectedTab == 0) ElectricCyan else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "জিমেইল বিক্রি",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = "Facebook",
                                    tint = if (selectedTab == 1) TelegramBlue else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ফেসবুক আইডি বিক্রি",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    )
                }
            }
        }

        // === Content based on selected Tab ===
        if (selectedTab == 0) {
            // GMAIL SELLING CONTENT
            item {
                GmailSellFormSection(onSubmit = onSubmitGmail)
            }

            // Gmail Submissions History
            item {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)) {
                    Text(
                        text = "আমার জিমেইল সাবমিশন হিস্ট্রি",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            if (gmailSubmissions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "এখনো কোন জিমেইল সাবমিট করেননি।",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                items(gmailSubmissions) { sub ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                        GmailSubmissionCard(submission = sub)
                    }
                }
            }
        } else {
            // FACEBOOK SELLING CONTENT
            item {
                FacebookSellFormSection(onSubmit = onSubmitFacebook)
            }

            // Facebook Submissions History
            item {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)) {
                    Text(
                        text = "আমার ফেসবুক সাবমিশন হিস্ট্রি",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            if (fbSubmissions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "এখনো কোন ফেসবুক আইডি সাবমিট করেননি।",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                items(fbSubmissions) { sub ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                        FacebookSubmissionCard(submission = sub)
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
}

@Composable
fun GmailSellFormSection(
    onSubmit: (telegram: String, gmail: String, pass: String, recovery: String, type: String, reward: Int) -> Unit
) {
    var userTelegram by remember { mutableStateOf("") }
    var gmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var recoveryEmail by remember { mutableStateOf("") }
    var selectedTier by remember { mutableStateOf("Fresh (৳15)") }

    val rewardAmount = when (selectedTier) {
        "Old 6M+ (৳30)" -> 30
        "Old 1Yr+ (৳50)" -> 50
        else -> 15
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Detailed Rules Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightSurface),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Rules",
                        tint = LuxuryGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "জিমেইল বিক্রির নিয়মাবলী ও রেট",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = LuxuryGold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• ফ্রেশ জিমেইল: ৳১৫ প্রতি একাউন্ট\n• ৬ মাস পুরাতন জিমেইল: ৳৩০ প্রতি একাউন্ট\n• ১ বছর+ পুরাতন জিমেইল: ৳৫০ প্রতি একাউন্ট",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "⚠️ শর্ত: জিমেইলের ২-স্টেপ ভেরিফিকেশন (2FA) অবশ্যই বন্ধ থাকতে হবে। একাউন্টে কোনো ফোন নাম্বার অ্যাড থাকা যাবে না। এডমিন পাসওয়ার্ড চেক করে এপ্রুভ করলে সাথে সাথে ওয়ালেটে টাকা এড হবে।",
                    fontSize = 11.sp,
                    color = WarningAmber,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Submission Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "জিমেইল তথ্য সাবমিট করুন",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = userTelegram,
                    onValueChange = { userTelegram = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("আপনার টেলিগ্রাম ইউজারনেম (@username)") },
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
                    value = gmail,
                    onValueChange = { gmail = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("জিমেইল এড্রেস (example@gmail.com)") },
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
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("জিমেইল পাসওয়ার্ড") },
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
                    value = recoveryEmail,
                    onValueChange = { recoveryEmail = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("রিকভারি ইমেইল (যদি থাকে)") },
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

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "একাউন্টের বয়স / রেট নির্বাচন করুন:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tiers = listOf("Fresh (৳15)", "Old 6M+ (৳30)", "Old 1Yr+ (৳50)")
                    for (tier in tiers) {
                        val isSelected = selectedTier == tier
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTier = tier },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) ElectricCyan else MidnightSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ElectricCyan else MidnightBorder
                            )
                        ) {
                            Text(
                                text = tier,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MidnightDark else TextPrimary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSubmit(userTelegram, gmail, password, recoveryEmail, selectedTier, rewardAmount)
                        gmail = ""
                        password = ""
                        recoveryEmail = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricCyan,
                        contentColor = MidnightDark
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "জিমেইল সাবমিট করুন (পাবেন ৳$rewardAmount)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FacebookSellFormSection(
    onSubmit: (telegram: String, uid: String, login: String, pass: String, twoFactor: String, year: String, reward: Int) -> Unit
) {
    var userTelegram by remember { mutableStateOf("") }
    var uidOrLink by remember { mutableStateOf("") }
    var loginPhoneOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var twoFactorCode by remember { mutableStateOf("") }
    var selectedTier by remember { mutableStateOf("2022-2026 (৳40)") }

    val rewardAmount = when (selectedTier) {
        "2018-2022 (৳70)" -> 70
        "Old Friends (৳120)" -> 120
        else -> 40
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Detailed Rules Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightSurface),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "FB Rules",
                        tint = TelegramBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ফেসবুক আইডি বিক্রির নিয়মাবলী ও রেট",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TelegramBlue
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• ২০২২-২০২৬ নরমাল আইডি: ৳৪০\n• ২০১৮-২০২২ পুরাতন আইডি: ৳৭০\n• রিয়েল ফ্রেন্ডস যুক্ত পুরাতন ওল্ড আইডি: ৳১২০",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "⚠️ নির্দেশ: আইডি যেন কোনো সিকিউরিটি চেকে না পড়ে। টু-ফ্যাক্টর অন থাকলে ব্যাকআপ কোড বক্সে দিন। এডমিন আইডি লগইন চেক করার সাথে সাথে আপনার একাউন্টে টাকা যোগ হবে।",
                    fontSize = 11.sp,
                    color = WarningAmber,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Submission Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ফেসবুক আইডি সাবমিট করুন",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = userTelegram,
                    onValueChange = { userTelegram = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("আপনার টেলিগ্রাম ইউজারনেম (@username)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MidnightSurface,
                        unfocusedContainerColor = MidnightSurface,
                        focusedBorderColor = TelegramBlue,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = uidOrLink,
                    onValueChange = { uidOrLink = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("প্রোফাইল লিংক বা UID") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MidnightSurface,
                        unfocusedContainerColor = MidnightSurface,
                        focusedBorderColor = TelegramBlue,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = loginPhoneOrEmail,
                    onValueChange = { loginPhoneOrEmail = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("লগইন ফোন নাম্বার বা ইমেইল") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MidnightSurface,
                        unfocusedContainerColor = MidnightSurface,
                        focusedBorderColor = TelegramBlue,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("ফেসবুক পাসওয়ার্ড") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MidnightSurface,
                        unfocusedContainerColor = MidnightSurface,
                        focusedBorderColor = TelegramBlue,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = twoFactorCode,
                    onValueChange = { twoFactorCode = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("2FA সিকিউরিটি কোড / ব্যাকআপ কোড (যদি থাকে)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MidnightSurface,
                        unfocusedContainerColor = MidnightSurface,
                        focusedBorderColor = TelegramBlue,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "আইডির ক্যাটাগরি / বয়স:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tiers = listOf("2022-2026 (৳40)", "2018-2022 (৳70)", "Old Friends (৳120)")
                    for (tier in tiers) {
                        val isSelected = selectedTier == tier
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTier = tier },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) TelegramBlue else MidnightSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) TelegramBlue else MidnightBorder
                            )
                        ) {
                            Text(
                                text = tier,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextPrimary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSubmit(userTelegram, uidOrLink, loginPhoneOrEmail, password, twoFactorCode, selectedTier, rewardAmount)
                        uidOrLink = ""
                        loginPhoneOrEmail = ""
                        password = ""
                        twoFactorCode = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TelegramBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "ফেসবুক আইডি সাবমিট করুন (পাবেন ৳$rewardAmount)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun GmailSubmissionCard(submission: GmailSubmission) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = submission.gmail,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "রেট: ৳${submission.rewardAmount} • ${submission.accountType}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            SubmissionStatusPill(status = submission.status)
        }
    }
}

@Composable
fun FacebookSubmissionCard(submission: FacebookSubmission) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = submission.phoneOrEmail.ifBlank { submission.profileLinkOrUid },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "রেট: ৳${submission.rewardAmount} • ${submission.accountYear}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            SubmissionStatusPill(status = submission.status)
        }
    }
}

@Composable
fun SubmissionStatusPill(status: String) {
    val (text, bgColor, textColor, icon) = when (status) {
        "APPROVED" -> Quad("অনুমোদিত (পেইড)", Color(0x2210B981), SuccessGreen, Icons.Default.CheckCircle)
        "REJECTED" -> Quad("বাতিল", Color(0x22EF4444), DangerRed, Icons.Default.Close)
        else -> Quad("রিভিউ চলছে", Color(0x22F59E0B), WarningAmber, Icons.Default.HourglassEmpty)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
