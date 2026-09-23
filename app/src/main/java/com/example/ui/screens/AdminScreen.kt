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
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.model.FacebookSubmission
import com.example.data.model.GmailSubmission
import com.example.data.model.GroupOrder
import com.example.data.model.TelegramGroup
import com.example.data.model.Withdrawal
import com.example.ui.components.AppLogoBadge
import com.example.ui.components.PaymentBrandLogo
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
                colors = CardDefaults.cardColors(containerColor = MidnightCard),
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
                "নতুন গ্রুপ",
                "পেমেন্ট সেটিংস",
                "প্রাইজ রেট"
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

        // 5. ADD NEW GROUP TAB
        else if (selectedTab == 4) {
            item {
                AdminAddNewGroupForm(onAddNewGroup = onAddNewGroup)
            }
        }

        // 6. PAYMENT SETTINGS TAB
        else if (selectedTab == 5) {
            item {
                AdminPaymentSettingsSection(
                    configs = adminConfigs,
                    onUpdateNumber = onUpdatePaymentNumber
                )
            }
        }

        // 7. PRICE RATE SETTINGS TAB (নতুন, পুরাতন, আরো পুরাতন)
        else if (selectedTab == 6) {
            item {
                AdminPriceRateSettingsSection(
                    configs = adminConfigs,
                    onUpdateConfig = onUpdatePaymentNumber
                )
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

@Composable
fun AdminPaymentSettingsSection(
    configs: Map<String, String>,
    onUpdateNumber: (String, String) -> Unit
) {
    var appLogo by remember { mutableStateOf(configs["app_logo_url"] ?: "") }
    var bkash by remember { mutableStateOf(configs["bkash_number"] ?: "01789-567890") }
    var bkashLogo by remember { mutableStateOf(configs["payment_logo_bkash"] ?: configs["bkash_logo_url"] ?: "") }

    var nagad by remember { mutableStateOf(configs["nagad_number"] ?: "01812-345678") }
    var nagadLogo by remember { mutableStateOf(configs["payment_logo_nagad"] ?: configs["nagad_logo_url"] ?: "") }

    var rocket by remember { mutableStateOf(configs["rocket_number"] ?: "01934-567891") }
    var rocketLogo by remember { mutableStateOf(configs["payment_logo_rocket"] ?: configs["rocket_logo_url"] ?: "") }

    var binance by remember { mutableStateOf(configs["binance_id"] ?: configs["binance_address"] ?: "8701368956") }
    var binanceLogo by remember { mutableStateOf(configs["payment_logo_binance"] ?: configs["binance_logo_url"] ?: "") }

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
                text = "পেমেন্ট গেটওয়ে সেটিংস ও লোগো কনফিগ",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = LuxuryGold
            )
            Text(
                text = "এখানে অ্যাপ লোগো এবং বিকাশ, নগদ, রকেট, Binance এর নাম্বার ও কাস্টম লোগো URL সেট করুন",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // === Live Logo Preview Box ===
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MidnightSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyanDark)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "লাইভ লোগো প্রিভিউ (ইউজাররা যেমন দেখবে):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AppLogoBadge(customLogoUrl = appLogo.ifBlank { null }, size = 36.dp)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("অ্যাপ লোগো", fontSize = 9.sp, color = TextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PaymentBrandLogo(method = "bKash", customLogoUrl = bkashLogo.ifBlank { null }, size = 36.dp)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("বিকাশ", fontSize = 9.sp, color = BkashPink)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PaymentBrandLogo(method = "Nagad", customLogoUrl = nagadLogo.ifBlank { null }, size = 36.dp)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("নগদ", fontSize = 9.sp, color = NagadOrange)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PaymentBrandLogo(method = "Rocket", customLogoUrl = rocketLogo.ifBlank { null }, size = 36.dp)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("রকেট", fontSize = 9.sp, color = RocketPurple)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PaymentBrandLogo(method = "Binance", customLogoUrl = binanceLogo.ifBlank { null }, size = 36.dp)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("Binance", fontSize = 9.sp, color = WarningAmber)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 0. App Logo URL
            Text("০. অ্যাপের অফিশিয়াল লোগো (App Logo)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = appLogo,
                onValueChange = { appLogo = it },
                label = { Text("অ্যাপ লোগো ইমেজ URL (ঐচ্ছিক)") },
                placeholder = { Text("https://example.com/app-logo.png") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    onUpdateNumber("app_logo_url", appLogo)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyanDark, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("অ্যাপ লোগো সেভ করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // bKash
            Text("১. বিকাশ (bKash)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BkashPink)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = bkash,
                onValueChange = { bkash = it },
                label = { Text("বিকাশ পার্সোনাল নাম্বার") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = bkashLogo,
                onValueChange = { bkashLogo = it },
                label = { Text("বিকাশ লোগো URL (ঐচ্ছিক)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    onUpdateNumber("bkash_number", bkash)
                    onUpdateNumber("bkash_logo_url", bkashLogo)
                    onUpdateNumber("payment_logo_bkash", bkashLogo)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BkashPink, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("বিকাশ তথ্য ও লোগো সেভ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Nagad
            Text("২. নগদ (Nagad)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NagadOrange)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = nagad,
                onValueChange = { nagad = it },
                label = { Text("নগদ পার্সোনাল নাম্বার") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = nagadLogo,
                onValueChange = { nagadLogo = it },
                label = { Text("নগদ লোগো URL (ঐচ্ছিক)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    onUpdateNumber("nagad_number", nagad)
                    onUpdateNumber("nagad_logo_url", nagadLogo)
                    onUpdateNumber("payment_logo_nagad", nagadLogo)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NagadOrange, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("নগদ তথ্য ও লোগো সেভ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Rocket
            Text("৩. রকেট (Rocket)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RocketPurple)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = rocket,
                onValueChange = { rocket = it },
                label = { Text("রকেট পার্সোনাল নাম্বার") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = rocketLogo,
                onValueChange = { rocketLogo = it },
                label = { Text("রকেট লোগো URL (ঐচ্ছিক)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    onUpdateNumber("rocket_number", rocket)
                    onUpdateNumber("rocket_logo_url", rocketLogo)
                    onUpdateNumber("payment_logo_rocket", rocketLogo)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RocketPurple, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("রকেট তথ্য ও লোগো সেভ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Binance Pay
            Text("৪. বাইন্যান্স (Binance Pay / USDT)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = binance,
                onValueChange = { binance = it },
                label = { Text("বাইন্যান্স পে আইডি বা টিআরসি২০ এড্রেস") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = binanceLogo,
                onValueChange = { binanceLogo = it },
                label = { Text("বাইন্যান্স লোগো URL (ঐচ্ছিক)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    onUpdateNumber("binance_id", binance)
                    onUpdateNumber("binance_address", binance)
                    onUpdateNumber("binance_logo_url", binanceLogo)
                    onUpdateNumber("payment_logo_binance", binanceLogo)
                },
                colors = ButtonDefaults.buttonColors(containerColor = WarningAmber, contentColor = MidnightDark),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("বাইন্যান্স তথ্য ও লোগো সেভ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Master Save All Button
            Button(
                onClick = {
                    if (appLogo.isNotBlank()) onUpdateNumber("app_logo_url", appLogo)
                    onUpdateNumber("bkash_number", bkash)
                    onUpdateNumber("bkash_logo_url", bkashLogo)
                    onUpdateNumber("payment_logo_bkash", bkashLogo)

                    onUpdateNumber("nagad_number", nagad)
                    onUpdateNumber("nagad_logo_url", nagadLogo)
                    onUpdateNumber("payment_logo_nagad", nagadLogo)

                    onUpdateNumber("rocket_number", rocket)
                    onUpdateNumber("rocket_logo_url", rocketLogo)
                    onUpdateNumber("payment_logo_rocket", rocketLogo)

                    onUpdateNumber("binance_id", binance)
                    onUpdateNumber("binance_address", binance)
                    onUpdateNumber("binance_logo_url", binanceLogo)
                    onUpdateNumber("payment_logo_binance", binanceLogo)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold, contentColor = MidnightDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save All")
                Spacer(modifier = Modifier.width(6.dp))
                Text("সব পেমেন্ট ও লোগো সেটিংস একবারে সেভ করুন", fontSize = 13.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun AdminPriceRateSettingsSection(
    configs: Map<String, String>,
    onUpdateConfig: (String, String) -> Unit
) {
    // Gmail prices: fresh, old, very_old
    var gmailFresh by remember { mutableStateOf(configs["price_gmail_fresh"] ?: "15") }
    var gmailOld by remember { mutableStateOf(configs["price_gmail_old"] ?: "30") }
    var gmailVeryOld by remember { mutableStateOf(configs["price_gmail_very_old"] ?: "50") }

    // Facebook prices: fresh, old, very_old
    var fbFresh by remember { mutableStateOf(configs["price_fb_fresh"] ?: "40") }
    var fbOld by remember { mutableStateOf(configs["price_fb_old"] ?: "70") }
    var fbVeryOld by remember { mutableStateOf(configs["price_fb_very_old"] ?: "120") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Gmail Pricing Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Mail, contentDescription = "Gmail", tint = ElectricCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "জিমেইল ক্রয় মূল্য সেটিংস (টাকা)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                }
                Text(
                    text = "ইউজাররা জিমেইল বিক্রি করার সময় এই রেট দেখতে পাবে ও ওয়ালেটে টাকা যোগ হবে",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = gmailFresh,
                    onValueChange = { gmailFresh = it },
                    label = { Text("নতুন ফ্রেশ জিমেইল রেট (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = gmailOld,
                    onValueChange = { gmailOld = it },
                    label = { Text("পুরাতন ৬ মাস+ জিমেইল রেট (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = gmailVeryOld,
                    onValueChange = { gmailVeryOld = it },
                    label = { Text("আরো পুরাতন ১ বছর+ জিমেইল রেট (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        onUpdateConfig("price_gmail_fresh", gmailFresh)
                        onUpdateConfig("price_gmail_old", gmailOld)
                        onUpdateConfig("price_gmail_very_old", gmailVeryOld)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = MidnightDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("জিমেইলের সব রেট সেভ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Facebook Pricing Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MidnightCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = "Facebook", tint = TelegramBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ফেসবুক আইডি ক্রয় মূল্য সেটিংস (টাকা)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TelegramBlue
                    )
                }
                Text(
                    text = "ইউজাররা ফেসবুক আইডি বিক্রি করার সময় এই রেট দেখতে পাবে ও ওয়ালেটে টাকা যোগ হবে",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = fbFresh,
                    onValueChange = { fbFresh = it },
                    label = { Text("নতুন ফেসবুক আইডি রেট (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fbOld,
                    onValueChange = { fbOld = it },
                    label = { Text("পুরাতন ফেসবুক আইডি রেট (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fbVeryOld,
                    onValueChange = { fbVeryOld = it },
                    label = { Text("আরো পুরাতন ফ্রেন্ডস সহ আইডি রেট (৳)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        onUpdateConfig("price_fb_fresh", fbFresh)
                        onUpdateConfig("price_fb_old", fbOld)
                        onUpdateConfig("price_fb_very_old", fbVeryOld)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TelegramBlue, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ফেসবুকের সব রেট সেভ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
