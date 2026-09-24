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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppManager
import com.example.data.model.FacebookSubmission
import com.example.data.model.GmailSubmission
import com.example.data.model.GroupOrder
import com.example.data.model.Withdrawal
import com.example.ui.components.NetworkImageOrFallback
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
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AdminRole

@Composable
fun AdminScreen(
    adminRole: AdminRole,
    loggedInAdminName: String,
    orders: List<GroupOrder>,
    gmailSubmissions: List<GmailSubmission>,
    fbSubmissions: List<FacebookSubmission>,
    withdrawals: List<Withdrawal>,
    managers: List<AppManager>,
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
    onUpdatePaymentNumber: (String, String) -> Unit,
    onAddManager: (name: String, telegram: String, passcode: String) -> Unit,
    onDeleteManager: (AppManager) -> Unit,
    onUpdateReferralBaseUrl: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val pendingOrdersCount = orders.count { it.status == "PENDING" }
    val pendingGmailsCount = gmailSubmissions.count { it.status == "PENDING" }
    val pendingFbsCount = fbSubmissions.count { it.status == "PENDING" }
    val pendingWithdrawalsCount = withdrawals.count { it.status == "PENDING" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDark),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // === Top Admin Header with Role Badge ===
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (adminRole == AdminRole.OWNER) LuxuryGold else ElectricCyan
                )
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
                                .background(if (adminRole == AdminRole.OWNER) LuxuryGold else ElectricCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (adminRole == AdminRole.OWNER) Icons.Default.AdminPanelSettings else Icons.Default.Shield,
                                contentDescription = "Admin",
                                tint = MidnightDark,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (adminRole == AdminRole.OWNER) "👑 আসল অনার প্যানেল" else "🛡️ ম্যানেজার প্যানেল",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (adminRole == AdminRole.OWNER) LuxuryGold else ElectricCyan
                                )
                            }
                            Text(
                                text = if (adminRole == AdminRole.OWNER) "আসল অনার: 8701368956 (@ItsSaddam9)" else "লগইন: $loggedInAdminName",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = onLockAdmin,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MidnightSurface)
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
                colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
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
                "ম্যানেজার রোল (${managers.size})",
                "রেফারেল ও পেমেন্ট",
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
                        color = if (isSelected) LuxuryGold else MidnightCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) LuxuryGold else MidnightBorder
                        )
                    ) {
                        Text(
                            text = tabs[index],
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MidnightDark else TextPrimary
                        )
                    }
                }
            }
        }

        // === TAB CONTENT ===

        // 1. ORDERS TAB
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

        // 2. GMAIL SUBMISSIONS TAB
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

        // 3. FACEBOOK SUBMISSIONS TAB
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

        // 4. WITHDRAWALS TAB
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

        // 5. MANAGERS & ROLES TAB
        else if (selectedTab == 4) {
            item {
                AdminManagerManagementSection(
                    adminRole = adminRole,
                    managers = managers,
                    onAddManager = onAddManager,
                    onDeleteManager = onDeleteManager
                )
            }
        }

        // 6. REFERRAL & PAYMENT SETTINGS TAB
        else if (selectedTab == 5) {
            item {
                AdminPaymentAndReferralSettingsSection(
                    configs = adminConfigs,
                    onUpdateNumber = onUpdatePaymentNumber,
                    onUpdateReferralBaseUrl = onUpdateReferralBaseUrl
                )
            }
        }

        // 7. ADD NEW GROUP TAB
        else if (selectedTab == 6) {
            item {
                AdminAddNewGroupForm(onAddNewGroup = onAddNewGroup)
            }
        }
    }
}

@Composable
fun AdminManagerManagementSection(
    adminRole: AdminRole,
    managers: List<AppManager>,
    onAddManager: (String, String, String) -> Unit,
    onDeleteManager: (AppManager) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var managerName by remember { mutableStateOf("") }
    var managerTelegram by remember { mutableStateOf("") }
    var managerPasscode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Owner Identity & Security Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(LuxuryGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = "Owner", tint = MidnightDark)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("👑 আসল অনার (Super Owner)", fontWeight = FontWeight.Bold, color = LuxuryGold, fontSize = 14.sp)
                            Text("সাদ্দাম ভাই (@ItsSaddam9)", fontSize = 12.sp, color = TextPrimary)
                        }
                    }

                    Surface(
                        color = SuccessGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen)
                    ) {
                        Text(
                            text = "সুরক্ষিত (Protected)",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = MidnightSurface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🔒 রুলস: আসল অনার চাইলে যে কাউকে ম্যানেজার করতে পারবেন। ম্যানেজাররা সব অর্ডার এপ্রুভ/রিজেক্ট, জিমেইল/ফেসবুক ভেরিফাই করতে পারবে, কিন্তু কোনো ম্যানেজার আসল অনারকে ডিলিট/পরিবর্তন করতে পারবে না এবং নতুন কোনো ম্যানেজার যোগ করতে পারবে না।",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(10.dp),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Add Manager Button (Only Active for Owner)
        if (adminRole == AdminRole.OWNER) {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = MidnightDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Manager")
                Spacer(modifier = Modifier.width(6.dp))
                Text("নতুন ম্যানেজার যুক্ত করুন", fontWeight = FontWeight.Bold)
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.4f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = WarningAmber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🛡️ আপনি ম্যানেজার রোলে আছেন। শুধুমাত্র আসল অনার (@ItsSaddam9) নতুন ম্যানেজার যুক্ত বা রিমুভ করতে পারেন।",
                        fontSize = 11.sp,
                        color = WarningAmber,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Text(
            text = "বর্তমান ম্যানেজার তালিকা (${managers.size} জন)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        if (managers.isEmpty()) {
            Text("কোনো অতিরিক্ত ম্যানেজার যুক্ত নেই।", fontSize = 12.sp, color = TextSecondary)
        } else {
            managers.forEach { mgr ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ElectricCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = "Mgr", tint = ElectricCyan, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(mgr.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("টেলিগ্রাম/আইডি: @${mgr.telegramIdOrUsername}", fontSize = 11.sp, color = ElectricCyan)
                                if (mgr.passcode.isNotBlank() && adminRole == AdminRole.OWNER) {
                                    Text("পাসকোড: ${mgr.passcode}", fontSize = 10.sp, color = LuxuryGold)
                                }
                            }
                        }

                        if (adminRole == AdminRole.OWNER) {
                            IconButton(
                                onClick = { onDeleteManager(mgr) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DangerRed.copy(alpha = 0.15f))
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = MidnightCard,
            title = { Text("নতুন ম্যানেজার যোগ করুন", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("ম্যানেজার যে আইডি বা ইউজারনেম দিয়ে এডমিন প্যানেলে ঢুকতে পারবে:", fontSize = 12.sp, color = TextSecondary)

                    OutlinedTextField(
                        value = managerName,
                        onValueChange = { managerName = it },
                        label = { Text("ম্যানেজারের নাম") },
                        placeholder = { Text("e.g. ম্যানেজার তানভীর") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = managerTelegram,
                        onValueChange = { managerTelegram = it },
                        label = { Text("টেলিগ্রাম ইউজারনেম / টেলিগ্রাম আইডি") },
                        placeholder = { Text("manager_tanvir অথবা 123456789") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = managerPasscode,
                        onValueChange = { managerPasscode = it },
                        label = { Text("লগইন পাসকোড (ঐচ্ছিক)") },
                        placeholder = { Text("123456") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddManager(managerName, managerTelegram, managerPasscode)
                        showAddDialog = false
                        managerName = ""
                        managerTelegram = ""
                        managerPasscode = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = MidnightDark)
                ) {
                    Text("ম্যানেজার তৈরি করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("বাতিল", color = TextSecondary) }
            }
        )
    }
}

@Composable
fun AdminPaymentAndReferralSettingsSection(
    configs: Map<String, String>,
    onUpdateNumber: (String, String) -> Unit,
    onUpdateReferralBaseUrl: (String) -> Unit
) {
    var referralBaseUrl by remember(configs["referral_base_url"]) {
        mutableStateOf(configs["referral_base_url"] ?: "https://t.me/PREMIUM_GROUP_BUY_BOT?startapp=ref_")
    }

    var appName by remember(configs["app_name"]) { mutableStateOf(configs["app_name"] ?: "SLK BUY GROUP") }
    var appLogoUrl by remember(configs["app_logo_url"]) { mutableStateOf(configs["app_logo_url"] ?: "") }

    var bkashNumber by remember(configs["bkash_number"]) { mutableStateOf(configs["bkash_number"] ?: "01789-567890") }
    var bkashLogo by remember(configs["bkash_logo"]) { mutableStateOf(configs["bkash_logo"] ?: "") }

    var nagadNumber by remember(configs["nagad_number"]) { mutableStateOf(configs["nagad_number"] ?: "01812-345678") }
    var nagadLogo by remember(configs["nagad_logo"]) { mutableStateOf(configs["nagad_logo"] ?: "") }

    var rocketNumber by remember(configs["rocket_number"]) { mutableStateOf(configs["rocket_number"] ?: "01934-567891") }
    var rocketLogo by remember(configs["rocket_logo"]) { mutableStateOf(configs["rocket_logo"] ?: "") }

    var binanceNumber by remember(configs["binance_number"]) { mutableStateOf(configs["binance_number"] ?: "8701368956") }
    var binanceLogo by remember(configs["binance_logo"]) { mutableStateOf(configs["binance_logo"] ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // === 1. Referral Link Config Box ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Link, contentDescription = "Referral", tint = ElectricCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "টেলিগ্রাম রেফারেল লিংক সেটিংস",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        }
                        Text(
                            text = "ইউজারদের অ্যাপে অটোমেটিক এই লিংকে তাদের রেফার কোড বসবে",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = referralBaseUrl,
                    onValueChange = { referralBaseUrl = it },
                    label = { Text("রেফারেল বট বা অ্যাপ লিংক ফরম্যাট") },
                    placeholder = { Text("https://t.me/PREMIUM_GROUP_BUY_BOT?startapp=ref_") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dynamic Live Preview of the link
                Surface(
                    color = MidnightSurface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "🔍 প্রিভিউ (ইউজাররা যে লিংক পাবে):", fontSize = 10.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (referralBaseUrl.contains("{USER_ID}")) {
                                referralBaseUrl.replace("{USER_ID}", "SLK-8701")
                            } else {
                                "${referralBaseUrl}SLK-8701"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxuryGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onUpdateReferralBaseUrl(referralBaseUrl)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = MidnightDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("রেফারেল লিংক সেভ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }

        // === 2. App Header Logo & Branding ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "অ্যাপ হেডার লোগো ও ব্র্যান্ডিং",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxuryGold
                        )
                        Text(
                            text = "পেমেন্ট হেডার ও টপবারে যে লোগো ও নাম দেখাবে",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(2.dp, LuxuryGold, CircleShape)
                            .background(MidnightSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        NetworkImageOrFallback(
                            url = appLogoUrl,
                            contentDescription = "Header Logo Preview",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        ) {
                            Text(
                                text = "SLK",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = LuxuryGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = appName,
                    onValueChange = { appName = it },
                    label = { Text("অ্যাপের নাম (App Name)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = appLogoUrl,
                    onValueChange = { appLogoUrl = it },
                    label = { Text("হেডার অ্যাপস লোগো ইমেজ URL (https://...)") },
                    placeholder = { Text("https://example.com/logo.png") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onUpdateNumber("app_name", appName)
                        onUpdateNumber("app_logo_url", appLogoUrl)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold, contentColor = MidnightDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("অ্যাপ হেডার লোগো ও নাম সেভ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }

        // === 3. bKash Settings ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "বিকাশ (bKash) সেটিংস",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BkashPink
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, BkashPink, CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        NetworkImageOrFallback(
                            url = bkashLogo,
                            contentDescription = "bKash Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        ) {
                            Text("BK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BkashPink)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = bkashNumber,
                    onValueChange = { bkashNumber = it },
                    label = { Text("বিকাশ পার্সোনাল নাম্বার") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bkashLogo,
                    onValueChange = { bkashLogo = it },
                    label = { Text("বিকাশ লোগো ইমেজ URL (Logo URL)") },
                    placeholder = { Text("https://example.com/bkash.png") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onUpdateNumber("bkash_number", bkashNumber)
                        onUpdateNumber("bkash_logo", bkashLogo)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BkashPink, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("বিকাশ সেটিংস সেভ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }

        // === 4. Nagad Settings ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "নগদ (Nagad) সেটিংস",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NagadOrange
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, NagadOrange, CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        NetworkImageOrFallback(
                            url = nagadLogo,
                            contentDescription = "Nagad Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        ) {
                            Text("NG", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NagadOrange)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nagadNumber,
                    onValueChange = { nagadNumber = it },
                    label = { Text("নগদ পার্সোনাল নাম্বার") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nagadLogo,
                    onValueChange = { nagadLogo = it },
                    label = { Text("নগদ লোগো ইমেজ URL (Logo URL)") },
                    placeholder = { Text("https://example.com/nagad.png") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onUpdateNumber("nagad_number", nagadNumber)
                        onUpdateNumber("nagad_logo", nagadLogo)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NagadOrange, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("নগদ সেটিংস সেভ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }

        // === 5. Rocket Settings ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "রকেট (Rocket) সেটিংস",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RocketPurple
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, RocketPurple, CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        NetworkImageOrFallback(
                            url = rocketLogo,
                            contentDescription = "Rocket Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        ) {
                            Text("RK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RocketPurple)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = rocketNumber,
                    onValueChange = { rocketNumber = it },
                    label = { Text("রকেট পার্সোনাল নাম্বার") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = rocketLogo,
                    onValueChange = { rocketLogo = it },
                    label = { Text("রকেট লোগো ইমেজ URL (Logo URL)") },
                    placeholder = { Text("https://example.com/rocket.png") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onUpdateNumber("rocket_number", rocketNumber)
                        onUpdateNumber("rocket_logo", rocketLogo)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RocketPurple, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("রকেট সেটিংস সেভ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }

        // === 6. Binance Settings ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Binance (বাইনান্স) সেটিংস",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = LuxuryGold
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, LuxuryGold, CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        NetworkImageOrFallback(
                            url = binanceLogo,
                            contentDescription = "Binance Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        ) {
                            Text("BN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LuxuryGold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = binanceNumber,
                    onValueChange = { binanceNumber = it },
                    label = { Text("Binance Pay ID অথবা USDT Address") },
                    placeholder = { Text("8701368956 অথবা TXXX...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = binanceLogo,
                    onValueChange = { binanceLogo = it },
                    label = { Text("Binance লোগো ইমেজ URL (Logo URL)") },
                    placeholder = { Text("https://example.com/binance.png") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onUpdateNumber("binance_number", binanceNumber)
                        onUpdateNumber("binance_logo", binanceLogo)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold, contentColor = MidnightDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Binance সেটিংস সেভ করুন", fontWeight = FontWeight.Bold)
                }
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
fun AdminOrderCard(
    order: GroupOrder,
    onApprove: (String) -> Unit,
    onReject: () -> Unit
) {
    var showApproveDialog by remember { mutableStateOf(false) }
    var inviteLinkInput by remember { mutableStateOf("https://t.me/+slk_vip_direct_link") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MidnightCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
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
                    color = TextPrimary
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
                color = TextPrimary
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
            containerColor = MidnightCard,
            title = { Text("গ্রুপ ইনভাইট লিংক দিন", color = TextPrimary) },
            text = {
                Column {
                    Text("ইউজারের কাছে এই ইনভাইট লিংক পাঠানো হবে:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inviteLinkInput,
                        onValueChange = { inviteLinkInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MidnightSurface,
                            unfocusedContainerColor = MidnightSurface,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
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
                TextButton(onClick = { showApproveDialog = false }) { Text("বাতিল", color = TextSecondary) }
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
        colors = CardDefaults.cardColors(containerColor = MidnightCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
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
                    color = TextPrimary
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
                Text(text = "রিকভারি: ${submission.recoveryEmail}", fontSize = 11.sp, color = TextSecondary)
            }
            Text(text = "টাইপ: ${submission.accountType} • ইউজারকে পাবে: ৳${submission.rewardAmount}", fontSize = 11.sp, color = LuxuryGold)
            Text(text = "টেলিগ্রাম: ${submission.userTelegram}", fontSize = 11.sp, color = TextSecondary)

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
        colors = CardDefaults.cardColors(containerColor = MidnightCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
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
                    color = TextPrimary
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
                Text(text = "UID/Link: ${submission.profileLinkOrUid}", fontSize = 11.sp, color = TextSecondary)
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
        colors = CardDefaults.cardColors(containerColor = MidnightCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
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

            Text(text = "প্রাপক নাম্বার: ${withdrawal.accountNumber}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = "ইউজার টেলিগ্রাম: ${withdrawal.userTelegram}", fontSize = 11.sp, color = TextSecondary)
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
            containerColor = MidnightCard,
            title = { Text("টাকা পাঠানোর TrxID দিন", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = adminTrxInput,
                    onValueChange = { adminTrxInput = it },
                    label = { Text("Transaction ID") },
                    placeholder = { Text("e.g. BK789XYZ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MidnightSurface,
                        unfocusedContainerColor = MidnightSurface,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
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
                TextButton(onClick = { showTrxDialog = false }) { Text("বাতিল", color = TextSecondary) }
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
        colors = CardDefaults.cardColors(containerColor = MidnightCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
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
