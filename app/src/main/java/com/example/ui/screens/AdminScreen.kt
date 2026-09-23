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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FacebookSubmission
import com.example.data.model.GmailSubmission
import com.example.data.model.GroupOrder
import com.example.data.model.Withdrawal
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.RocketPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun AdminScreen(
    orders: List<GroupOrder>,
    gmailSubmissions: List<GmailSubmission>,
    fbSubmissions: List<FacebookSubmission>,
    withdrawals: List<Withdrawal>,
    adminConfigs: Map<String, String>,
    onLockAdmin: () -> Unit,
    onApproveOrder: (GroupOrder, String) -> Unit,
    onRejectOrder: (GroupOrder) -> Unit,
    onApproveGmail: (GmailSubmission) -> Unit,
    onRejectGmail: (GmailSubmission, String) -> Unit,
    onApproveFacebook: (FacebookSubmission) -> Unit,
    onRejectFacebook: (FacebookSubmission, String) -> Unit,
    onApproveWithdrawal: (Withdrawal, String) -> Unit,
    onRejectWithdrawal: (Withdrawal) -> Unit,
    onAddNewGroup: (title: String, category: String, desc: String, members: String, price: Int, origPrice: Int, link: String, badge: String) -> Unit,
    onUpdatePaymentNumber: (String, String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val pendingOrdersCount = orders.count { it.status == "PENDING" }
    val pendingGmailsCount = gmailSubmissions.count { it.status == "PENDING" }
    val pendingFbsCount = fbSubmissions.count { it.status == "PENDING" }
    val pendingWithdrawalsCount = withdrawals.count { it.status == "PENDING" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // === Top Admin Header ===
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(LuxuryGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = MidnightDark,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "SUPER ADMIN PANEL",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = LuxuryGold
                                )
                            }
                            Text(
                                text = "Admin ID: 8701368956 (t.me/ItsSaddam9)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    IconButton(
                        onClick = onLockAdmin,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = WarningAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // === Admin Stats Overview Ticker ===
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AdminStatPill(count = "$pendingOrdersCount", label = "গ্রুপ অর্ডার", color = ElectricCyan)
                    AdminStatPill(count = "$pendingGmailsCount", label = "জিমেইল", color = LuxuryGold)
                    AdminStatPill(count = "$pendingFbsCount", label = "ফেসবুক", color = TelegramBlue)
                    AdminStatPill(count = "$pendingWithdrawalsCount", label = "উইথড্র", color = SuccessGreen)
                }
            }
        }

        // === Admin Section Tabs ===
        item {
            val tabs = listOf(
                "অর্ডার ($pendingOrdersCount)",
                "জিমেইল ($pendingGmailsCount)",
                "ফেসবুক ($pendingFbsCount)",
                "উইথড্র ($pendingWithdrawalsCount)",
                "প্রাইস কন্ট্রোল",
                "পেমেন্ট ও লোগো",
                "নতুন গ্রুপ"
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs.indices.toList()) { index ->
                    val isSelected = selectedTab == index
                    Surface(
                        modifier = Modifier.clickable { selectedTab = index },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) LuxuryGold else MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) LuxuryGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = tabs[index],
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MidnightDark else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // === TAB CONTENT ===

        // 0. ORDERS TAB
        if (selectedTab == 0) {
            if (orders.isEmpty()) {
                item {
                    EmptyAdminNotice(text = "কোনো গ্রুপ অর্ডার নেই")
                }
            } else {
                items(orders) { order ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        AdminOrderCard(
                            order = order,
                            onApprove = { link -> onApproveOrder(order, link) },
                            onReject = { onRejectOrder(order) }
                        )
                    }
                }
            }
        }

        // 1. GMAIL SUBMISSIONS TAB
        else if (selectedTab == 1) {
            if (gmailSubmissions.isEmpty()) {
                item {
                    EmptyAdminNotice(text = "কোনো জিমেইল সাবমিশন নেই")
                }
            } else {
                items(gmailSubmissions) { sub ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        AdminGmailCard(
                            submission = sub,
                            onApprove = { onApproveGmail(sub) },
                            onReject = { reason -> onRejectGmail(sub, reason) }
                        )
                    }
                }
            }
        }

        // 2. FACEBOOK SUBMISSIONS TAB
        else if (selectedTab == 2) {
            if (fbSubmissions.isEmpty()) {
                item {
                    EmptyAdminNotice(text = "কোনো ফেসবুক আইডি সাবমিশন নেই")
                }
            } else {
                items(fbSubmissions) { sub ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        AdminFacebookCard(
                            submission = sub,
                            onApprove = { onApproveFacebook(sub) },
                            onReject = { reason -> onRejectFacebook(sub, reason) }
                        )
                    }
                }
            }
        }

        // 3. WITHDRAWALS TAB
        else if (selectedTab == 3) {
            if (withdrawals.isEmpty()) {
                item {
                    EmptyAdminNotice(text = "কোনো উইথড্র রিকোয়েস্ট নেই")
                }
            } else {
                items(withdrawals) { item ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        AdminWithdrawalCard(
                            withdrawal = item,
                            onApprove = { trx -> onApproveWithdrawal(item, trx) },
                            onReject = { onRejectWithdrawal(item) }
                        )
                    }
                }
            }
        }

        // 4. PRICE CONTROL TAB (Gmail & FB Rates)
        else if (selectedTab == 4) {
            item {
                AdminPriceControlSection(
                    configs = adminConfigs,
                    onUpdateConfig = onUpdatePaymentNumber
                )
            }
        }

        // 5. PAYMENT & LOGO SETTINGS TAB
        else if (selectedTab == 5) {
            item {
                AdminPaymentAndLogoSettingsSection(
                    configs = adminConfigs,
                    onUpdateConfig = onUpdatePaymentNumber
                )
            }
        }

        // 6. ADD NEW GROUP TAB
        else if (selectedTab == 6) {
            item {
                AdminAddNewGroupForm(onAddNewGroup = onAddNewGroup)
            }
        }
    }
}

@Composable
fun AdminStatPill(count: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun EmptyAdminNotice(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = TextSecondary, fontSize = 13.sp)
    }
}

@Composable
fun AdminPriceControlSection(
    configs: Map<String, String>,
    onUpdateConfig: (String, String) -> Unit
) {
    val context = LocalContext.current

    // Gmail Price States
    var freshGmail by remember { mutableStateOf(configs["gmail_price_fresh"] ?: "15") }
    var old6mGmail by remember { mutableStateOf(configs["gmail_price_old_6m"] ?: "30") }
    var old1yrGmail by remember { mutableStateOf(configs["gmail_price_old_1yr"] ?: "50") }

    // FB Price States
    var fbNew by remember { mutableStateOf(configs["fb_price_new"] ?: "40") }
    var fbOld by remember { mutableStateOf(configs["fb_price_old"] ?: "70") }
    var fbVeryOld by remember { mutableStateOf(configs["fb_price_very_old"] ?: "120") }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Gmail Pricing Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = "Price", tint = LuxuryGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "জিমেইল প্রাইজ পরিবর্তন (নতুন, পুরাতন, আরো পুরাতন)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = LuxuryGold
                    )
                }
                Text(
                    text = "ইউজাররা সেল করার সময় এখানে নির্ধারিত রেট দেখতে পাবে ও ওয়ালেটে এই পরিমাণ টাকা জমা হবে",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Fresh Gmail
                OutlinedTextField(
                    value = freshGmail,
                    onValueChange = { freshGmail = it },
                    label = { Text("নতুন ফ্রেশ জিমেইল রেট (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        onUpdateConfig("gmail_price_fresh", freshGmail)
                        copyToClipboard(context, freshGmail, "নতুন জিমেইল রেট ৳$freshGmail সেভ হয়েছে!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = MidnightDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ফ্রেশ জিমেইল রেট সেভ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 6M Old Gmail
                OutlinedTextField(
                    value = old6mGmail,
                    onValueChange = { old6mGmail = it },
                    label = { Text("পুরাতন ৬ মাস+ জিমেইল রেট (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        onUpdateConfig("gmail_price_old_6m", old6mGmail)
                        copyToClipboard(context, old6mGmail, "পুরাতন জিমেইল রেট ৳$old6mGmail সেভ হয়েছে!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold, contentColor = MidnightDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("পুরাতন জিমেইল রেট সেভ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 1Yr+ Old Gmail
                OutlinedTextField(
                    value = old1yrGmail,
                    onValueChange = { old1yrGmail = it },
                    label = { Text("আরো পুরাতন ১ বছর+ জিমেইল রেট (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        onUpdateConfig("gmail_price_old_1yr", old1yrGmail)
                        copyToClipboard(context, old1yrGmail, "১ বছর+ জিমেইল রেট ৳$old1yrGmail সেভ হয়েছে!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = MidnightDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("১ বছর+ পুরাতন রেট সেভ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Facebook Pricing Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TelegramBlue.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = "Price", tint = TelegramBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ফেসবুক আইডি প্রাইজ পরিবর্তন (নতুন, পুরাতন, আরো পুরাতন)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TelegramBlue
                    )
                }
                Text(
                    text = "ইউজাররা ফেসবুক আইডি সেল করার সময় এই রেট অনুযায়ী টাকা পাবে",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // FB New 2022-2026
                OutlinedTextField(
                    value = fbNew,
                    onValueChange = { fbNew = it },
                    label = { Text("নতুন আইডি রেট ২০২২-২০২৬ (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        onUpdateConfig("fb_price_new", fbNew)
                        copyToClipboard(context, fbNew, "নতুন ফেসবুক রেট ৳$fbNew সেভ হয়েছে!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TelegramBlue, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("নতুন ফেসবুক রেট সেভ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // FB Old 2018-2022
                OutlinedTextField(
                    value = fbOld,
                    onValueChange = { fbOld = it },
                    label = { Text("পুরাতন আইডি রেট ২০১৮-২০২২ (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        onUpdateConfig("fb_price_old", fbOld)
                        copyToClipboard(context, fbOld, "পুরাতন ফেসবুক রেট ৳$fbOld সেভ হয়েছে!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold, contentColor = MidnightDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("পুরাতন ফেসবুক রেট সেভ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // FB Very Old Friends
                OutlinedTextField(
                    value = fbVeryOld,
                    onValueChange = { fbVeryOld = it },
                    label = { Text("আরো পুরাতন রিয়েল ফ্রেন্ডস যুক্ত আইডি রেট (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        onUpdateConfig("fb_price_very_old", fbVeryOld)
                        copyToClipboard(context, fbVeryOld, "আরো পুরাতন ফেসবুক রেট ৳$fbVeryOld সেভ হয়েছে!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = MidnightDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("আরো পুরাতন আইডি রেট সেভ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminPaymentAndLogoSettingsSection(
    configs: Map<String, String>,
    onUpdateConfig: (String, String) -> Unit
) {
    val context = LocalContext.current

    // Logos
    var appLogoUrl by remember { mutableStateOf(configs["app_logo_url"] ?: "") }
    var bkashLogoUrl by remember { mutableStateOf(configs["bkash_logo_url"] ?: "") }
    var nagadLogoUrl by remember { mutableStateOf(configs["nagad_logo_url"] ?: "") }
    var rocketLogoUrl by remember { mutableStateOf(configs["rocket_logo_url"] ?: "") }
    var binanceLogoUrl by remember { mutableStateOf(configs["binance_logo_url"] ?: "") }

    // Numbers & Binance Pay
    var bkashNumber by remember { mutableStateOf(configs["bkash_number"] ?: "01789-567890") }
    var nagadNumber by remember { mutableStateOf(configs["nagad_number"] ?: "01812-345678") }
    var rocketNumber by remember { mutableStateOf(configs["rocket_number"] ?: "01934-567891") }
    var binanceId by remember { mutableStateOf(configs["binance_id"] ?: "87013689") }
    var binanceUsdt by remember { mutableStateOf(configs["binance_usdt_address"] ?: "TYeK8Q3LqW4vH1m9PbzE9102XUsdtTrc20") }
    var adminTelegram by remember { mutableStateOf(configs["admin_telegram"] ?: "https://t.me/ItsSaddam9") }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // App Branding Logo
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "অ্যাপের অফিশিয়াল লোগো (Header & Payment Logo)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LuxuryGold
                )
                Text(
                    text = "অনলাইনে হোস্ট করা যেকোনো ছবির URL দিন। ফাঁকা রাখলে ডিফল্ট সুন্দর গোল্ডেন লোগো দেখাবে।",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = appLogoUrl,
                    onValueChange = { appLogoUrl = it },
                    label = { Text("App Logo Image URL (https://...)") },
                    placeholder = { Text("https://example.com/logo.png") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        onUpdateConfig("app_logo_url", appLogoUrl)
                        copyToClipboard(context, appLogoUrl, "অ্যাপ লোগো আপডেট হয়েছে!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold, contentColor = MidnightDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("অ্যাপ লোগো সেভ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Payment Methods (bKash, Nagad, Rocket, Binance)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "পেমেন্ট মেথড ও কাস্টম লোগো কনফিগ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "বিকাশ, নগদ, রকেট এবং Binance এর নিজস্ব লোগো ও একাউন্ট নম্বর এখানে পরিবর্তন করতে পারবেন।",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // --- bKash ---
                Text("১. বিকাশ সেটিংস", fontWeight = FontWeight.Bold, color = BkashPink, fontSize = 13.sp)
                OutlinedTextField(
                    value = bkashNumber,
                    onValueChange = { bkashNumber = it },
                    label = { Text("বিকাশ পার্সোনাল নাম্বার") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = bkashLogoUrl,
                    onValueChange = { bkashLogoUrl = it },
                    label = { Text("বিকাশ কাস্টম লোগো URL (ঐচ্ছিক)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        onUpdateConfig("bkash_number", bkashNumber)
                        onUpdateConfig("bkash_logo_url", bkashLogoUrl)
                        copyToClipboard(context, bkashNumber, "বিকাশ তথ্য সেভ হয়েছে!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BkashPink, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("বিকাশ তথ্য সেভ", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Nagad ---
                Text("২. নগদ সেটিংস", fontWeight = FontWeight.Bold, color = NagadOrange, fontSize = 13.sp)
                OutlinedTextField(
                    value = nagadNumber,
                    onValueChange = { nagadNumber = it },
                    label = { Text("নগদ পার্সোনাল নাম্বার") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = nagadLogoUrl,
                    onValueChange = { nagadLogoUrl = it },
                    label = { Text("নগদ কাস্টম লোগো URL (ঐচ্ছিক)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        onUpdateConfig("nagad_number", nagadNumber)
                        onUpdateConfig("nagad_logo_url", nagadLogoUrl)
                        copyToClipboard(context, nagadNumber, "নগদ তথ্য সেভ হয়েছে!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NagadOrange, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("নগদ তথ্য সেভ", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Rocket ---
                Text("৩. রকেট সেটিংস", fontWeight = FontWeight.Bold, color = RocketPurple, fontSize = 13.sp)
                OutlinedTextField(
                    value = rocketNumber,
                    onValueChange = { rocketNumber = it },
                    label = { Text("রকেট পার্সোনাল নাম্বার") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = rocketLogoUrl,
                    onValueChange = { rocketLogoUrl = it },
                    label = { Text("রকেট কাস্টম লোগো URL (ঐচ্ছিক)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        onUpdateConfig("rocket_number", rocketNumber)
                        onUpdateConfig("rocket_logo_url", rocketLogoUrl)
                        copyToClipboard(context, rocketNumber, "রকেট তথ্য সেভ হয়েছে!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RocketPurple, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("রকেট তথ্য সেভ", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Binance ---
                Text("৪. Binance সেটিংস", fontWeight = FontWeight.Bold, color = Color(0xFFF0B90B), fontSize = 13.sp)
                OutlinedTextField(
                    value = binanceId,
                    onValueChange = { binanceId = it },
                    label = { Text("Binance Pay ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = binanceUsdt,
                    onValueChange = { binanceUsdt = it },
                    label = { Text("Binance USDT Address (TRC20/BEP20)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = binanceLogoUrl,
                    onValueChange = { binanceLogoUrl = it },
                    label = { Text("Binance কাস্টম লোগো URL (ঐচ্ছিক)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        onUpdateConfig("binance_id", binanceId)
                        onUpdateConfig("binance_usdt_address", binanceUsdt)
                        onUpdateConfig("binance_logo_url", binanceLogoUrl)
                        copyToClipboard(context, binanceId, "Binance তথ্য সেভ হয়েছে!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0B90B), contentColor = Color(0xFF1E2329)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Binance তথ্য সেভ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Support Telegram link
                Text("৫. এডমিন সাপোর্ট টেলিগ্রাম লিঙ্ক", fontWeight = FontWeight.Bold, color = TelegramBlue, fontSize = 13.sp)
                OutlinedTextField(
                    value = adminTelegram,
                    onValueChange = { adminTelegram = it },
                    label = { Text("Telegram Link (https://t.me/...)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        onUpdateConfig("admin_telegram", adminTelegram)
                        copyToClipboard(context, adminTelegram, "সাপোর্ট লিংক সেভ হয়েছে!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TelegramBlue, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("টেলিগ্রাম লিঙ্ক সেভ", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(
    order: GroupOrder,
    onApprove: (String) -> Unit,
    onReject: () -> Unit
) {
    var showApproveDialog by remember { mutableStateOf(false) }
    var inviteLinkInput by remember { mutableStateOf("https://t.me/+slk_vip_direct_link") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.groupTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    color = if (order.status == "APPROVED") SuccessGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = order.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (order.status == "APPROVED") SuccessGreen else WarningAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "ইউজার: ${order.userTelegram} • মূল্য: ৳${order.amount}",
                fontSize = 12.sp,
                color = ElectricCyan
            )

            Text(
                text = "পেমেন্ট: ${order.paymentMethod} • প্রেরক: ${order.senderNumber}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "TrxID: ${order.trxId}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LuxuryGold
            )

            if (order.status == "PENDING") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showApproveDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen,
                            contentColor = MidnightDark
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Approve", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("এপ্রুভ করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Reject", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("বাতিল", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showApproveDialog) {
        AlertDialog(
            onDismissRequest = { showApproveDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            title = { Text("গ্রুপ ইনভাইট লিংক দিন", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column {
                    Text("ইউজারের কাছে এই ইনভাইট লিংক পাঠানো হবে:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inviteLinkInput,
                        onValueChange = { inviteLinkInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onApprove(inviteLinkInput)
                        showApproveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = MidnightDark)
                ) {
                    Text("কনফার্ম ও এপ্রুভ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApproveDialog = false }) { Text("বাতিল") }
            }
        )
    }
}

@Composable
fun AdminGmailCard(
    submission: GmailSubmission,
    onApprove: () -> Unit,
    onReject: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = submission.gmail,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    color = if (submission.status == "APPROVED") SuccessGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = submission.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (submission.status == "APPROVED") SuccessGreen else WarningAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "পাসওয়ার্ড: ${submission.password}", fontSize = 12.sp, color = ElectricCyan)
            if (submission.recoveryEmail.isNotBlank()) {
                Text(text = "রিকভারি: ${submission.recoveryEmail}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = "টাইপ: ${submission.accountType} • ইউজার পাবে: ৳${submission.rewardAmount}", fontSize = 11.sp, color = LuxuryGold)
            Text(text = "টেলিগ্রাম: ${submission.userTelegram}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (submission.status == "PENDING") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = MidnightDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("এপ্রুভ (+৳${submission.rewardAmount})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onReject("পাসওয়ার্ড ভুল বা ২-স্টেপ অন ছিল") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("বাতিল", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminFacebookCard(
    submission: FacebookSubmission,
    onApprove: () -> Unit,
    onReject: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "লগইন: ${submission.phoneOrEmail}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    color = if (submission.status == "APPROVED") SuccessGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = submission.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (submission.status == "APPROVED") SuccessGreen else WarningAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "পাসওয়ার্ড: ${submission.password}", fontSize = 12.sp, color = ElectricCyan)
            if (submission.profileLinkOrUid.isNotBlank()) {
                Text(text = "UID/Link: ${submission.profileLinkOrUid}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (submission.twoFactorCode.isNotBlank()) {
                Text(text = "2FA কোড: ${submission.twoFactorCode}", fontSize = 11.sp, color = WarningAmber)
            }
            Text(text = "বয়স: ${submission.accountYear} • রেট: ৳${submission.rewardAmount}", fontSize = 11.sp, color = LuxuryGold)

            if (submission.status == "PENDING") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = MidnightDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("এপ্রুভ (+৳${submission.rewardAmount})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onReject("লগইন হয়নি") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("বাতিল", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminWithdrawalCard(
    withdrawal: Withdrawal,
    onApprove: (String) -> Unit,
    onReject: () -> Unit
) {
    var showTrxDialog by remember { mutableStateOf(false) }
    var adminTrxInput by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "৳${withdrawal.amount} (${withdrawal.paymentMethod})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = LuxuryGold
                )

                Surface(
                    color = if (withdrawal.status == "APPROVED") SuccessGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = withdrawal.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (withdrawal.status == "APPROVED") SuccessGreen else WarningAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "প্রাপক নাম্বার: ${withdrawal.accountNumber}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "ইউজার টেলিগ্রাম: ${withdrawal.userTelegram}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (withdrawal.adminTrxId.isNotBlank()) {
                Text(text = "প্রেরিত TrxID: ${withdrawal.adminTrxId}", fontSize = 11.sp, color = SuccessGreen)
            }

            if (withdrawal.status == "PENDING") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showTrxDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = MidnightDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("পেইড মার্ক করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("বাতিল ও রিফান্ড", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showTrxDialog) {
        AlertDialog(
            onDismissRequest = { showTrxDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            title = { Text("টাকা পাঠানোর TrxID দিন", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                OutlinedTextField(
                    value = adminTrxInput,
                    onValueChange = { adminTrxInput = it },
                    label = { Text("Transaction ID") },
                    placeholder = { Text("e.g. BK789XYZ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onApprove(adminTrxInput)
                        showTrxDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = MidnightDark)
                ) {
                    Text("নিশ্চিত করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTrxDialog = false }) { Text("বাতিল") }
            }
        )
    }
}

@Composable
fun AdminAddNewGroupForm(
    onAddNewGroup: (title: String, category: String, desc: String, members: String, price: Int, origPrice: Int, link: String, badge: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Movies & Web Series") }
    var desc by remember { mutableStateOf("") }
    var members by remember { mutableStateOf("45,000+") }
    var priceText by remember { mutableStateOf("300") }
    var origPriceText by remember { mutableStateOf("500") }
    var link by remember { mutableStateOf("https://t.me/+slk_new_group") }
    var badge by remember { mutableStateOf("NEW VIP") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "মার্কেটপ্লেসে নতুন টেলিগ্রাম গ্রুপ যোগ করুন",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = LuxuryGold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("গ্রুপের নাম / শিরোনাম") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("ক্যাটাগরি (Movies, VIP, OTT, Tech)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("গ্রুপের বিবরণ / ডেসক্রিপশন") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("মূল্য (৳)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = origPriceText,
                    onValueChange = { origPriceText = it },
                    label = { Text("আসল মূল্য (৳)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = members,
                onValueChange = { members = it },
                label = { Text("সদস্য সংখ্যা (e.g. 50,000+)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = link,
                onValueChange = { link = it },
                label = { Text("টেলিগ্রাম ইনভাইট লিংক") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = badge,
                onValueChange = { badge = it },
                label = { Text("ব্যাজ (HOT VIP, 4K, BESTSELLER)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val p = priceText.toIntOrNull() ?: 300
                    val op = origPriceText.toIntOrNull() ?: 500
                    onAddNewGroup(title, category, desc, members, p, op, link, badge)
                    title = ""
                    desc = ""
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold, contentColor = MidnightDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(6.dp))
                Text("গ্রুপ পাবলিশ করুন", fontWeight = FontWeight.Bold)
            }
        }
    }
}
