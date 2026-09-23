package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.SlkBottomNavBar
import com.example.ui.components.SlkTopBar
import com.example.ui.components.openTelegramLink
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.GroupsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SellAndEarnScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                SlkApp(viewModel = viewModel, isDarkMode = isDarkMode)
            }
        }
    }
}

@Composable
fun SlkApp(
    viewModel: MainViewModel = viewModel(),
    isDarkMode: Boolean = true
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val currentScreen by viewModel.currentScreen.collectAsState()
    val allGroups by viewModel.allGroups.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val gmailSubmissions by viewModel.allGmailSubmissions.collectAsState()
    val fbSubmissions by viewModel.allFacebookSubmissions.collectAsState()
    val withdrawals by viewModel.allWithdrawals.collectAsState()
    val walletProfile by viewModel.walletProfile.collectAsState()
    val adminConfigs by viewModel.adminConfigs.collectAsState()
    val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsState()
    val selectedGroupForCheckout by viewModel.selectedGroupForCheckout.collectAsState()
    val sellType by viewModel.sellType.collectAsState()

    // Collect Toast/Snackbar messages
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentScreen != AppScreen.CHECKOUT) {
                SlkTopBar(
                    currentBalance = walletProfile.currentBalance,
                    isAdminUnlocked = isAdminUnlocked,
                    onWalletClick = { viewModel.navigateTo(AppScreen.WALLET) },
                    onAdminClick = {
                        if (isAdminUnlocked) {
                            viewModel.navigateTo(AppScreen.ADMIN)
                        } else {
                            viewModel.navigateTo(AppScreen.PROFILE)
                        }
                    },
                    onSupportClick = {
                        openTelegramLink(context, adminConfigs["admin_telegram"] ?: "https://t.me/ItsSaddam9")
                    },
                    onToggleTheme = { viewModel.toggleDarkMode() },
                    isDarkMode = isDarkMode,
                    appLogoUrl = adminConfigs["app_logo_url"]
                )
            }
        },
        bottomBar = {
            if (currentScreen != AppScreen.CHECKOUT) {
                SlkBottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) },
                    isAdminUnlocked = isAdminUnlocked
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    AppScreen.HOME -> HomeScreen(
                        groups = allGroups,
                        onBuyGroup = { group -> viewModel.openCheckout(group) },
                        onNavigate = { target -> viewModel.navigateTo(target) },
                        onNavigateSell = { type ->
                            viewModel.setSellType(type)
                            viewModel.navigateTo(AppScreen.SELL_EARN)
                        },
                        noticeText = adminConfigs["notice_text"] ?: ""
                    )

                    AppScreen.GROUPS -> GroupsScreen(
                        groups = allGroups,
                        onBuyGroup = { group -> viewModel.openCheckout(group) }
                    )

                    AppScreen.CHECKOUT -> {
                        val group = selectedGroupForCheckout ?: allGroups.firstOrNull()
                        if (group != null) {
                            CheckoutScreen(
                                group = group,
                                adminConfigs = adminConfigs,
                                onBack = { viewModel.navigateTo(AppScreen.GROUPS) },
                                onSubmitOrder = { telegram, method, senderNumber, trxId ->
                                    viewModel.placeOrder(telegram, group, method, senderNumber, trxId)
                                }
                            )
                        } else {
                            viewModel.navigateTo(AppScreen.GROUPS)
                        }
                    }

                    AppScreen.SELL_EARN -> SellAndEarnScreen(
                        initialType = sellType,
                        gmailSubmissions = gmailSubmissions,
                        fbSubmissions = fbSubmissions,
                        adminConfigs = adminConfigs,
                        onSubmitGmail = { tg, mail, pass, rec, type, reward ->
                            viewModel.submitGmail(tg, mail, pass, rec, type, reward)
                        },
                        onSubmitFacebook = { tg, uid, login, pass, twoFa, year, reward ->
                            viewModel.submitFacebook(tg, uid, login, pass, twoFa, year, reward)
                        }
                    )

                    AppScreen.WALLET -> WalletScreen(
                        walletProfile = walletProfile,
                        withdrawals = withdrawals,
                        adminConfigs = adminConfigs,
                        onWithdraw = { method, number, amount ->
                            viewModel.requestWithdraw(method, number, amount)
                        },
                        onRecharge = { amount ->
                            viewModel.rechargeWallet(amount)
                        }
                    )

                    AppScreen.PROFILE -> ProfileScreen(
                        walletProfile = walletProfile,
                        orders = allOrders,
                        isAdminUnlocked = isAdminUnlocked,
                        isDarkMode = isDarkMode,
                        onToggleTheme = { viewModel.toggleDarkMode() },
                        adminConfigs = adminConfigs,
                        onUnlockAdmin = { input -> viewModel.unlockAdmin(input) },
                        onNavigate = { target -> viewModel.navigateTo(target) }
                    )

                    AppScreen.ADMIN -> {
                        if (isAdminUnlocked) {
                            AdminScreen(
                                orders = allOrders,
                                gmailSubmissions = gmailSubmissions,
                                fbSubmissions = fbSubmissions,
                                withdrawals = withdrawals,
                                adminConfigs = adminConfigs,
                                onLockAdmin = { viewModel.lockAdmin() },
                                onApproveOrder = { order, link -> viewModel.adminApproveOrder(order, link) },
                                onRejectOrder = { order -> viewModel.adminRejectOrder(order) },
                                onApproveGmail = { sub -> viewModel.adminApproveGmail(sub) },
                                onRejectGmail = { sub, reason -> viewModel.adminRejectGmail(sub, reason) },
                                onApproveFacebook = { sub -> viewModel.adminApproveFacebook(sub) },
                                onRejectFacebook = { sub, reason -> viewModel.adminRejectFacebook(sub, reason) },
                                onApproveWithdrawal = { item, trx -> viewModel.adminApproveWithdrawal(item, trx) },
                                onRejectWithdrawal = { item -> viewModel.adminRejectWithdrawal(item) },
                                onAddNewGroup = { title, cat, desc, mem, p, op, link, badge ->
                                    viewModel.addNewGroup(title, cat, desc, mem, p, op, link, badge)
                                },
                                onUpdatePaymentNumber = { key, num -> viewModel.updatePaymentNumber(key, num) }
                            )
                        } else {
                            viewModel.navigateTo(AppScreen.PROFILE)
                        }
                    }
                }
            }
        }
    }
}
