package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.AppManager
import com.example.data.model.FacebookSubmission
import com.example.data.model.GmailSubmission
import com.example.data.model.GroupOrder
import com.example.data.model.ReferralEntry
import com.example.data.model.TelegramGroup
import com.example.data.model.WalletProfile
import com.example.data.model.Withdrawal
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    GROUPS,
    CHECKOUT,
    SELL_EARN,
    WALLET,
    PROFILE,
    ADMIN
}

enum class SellType {
    GMAIL,
    FACEBOOK
}

enum class AdminRole {
    NONE,
    OWNER,      // আসল অনার (Super Admin) - Can do EVERYTHING + manage managers
    MANAGER     // ম্যানেজার - Can edit, approve, reject, edit payment/referral link settings, but CANNOT edit/remove Owner and CANNOT add/remove managers
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = AppRepository(database.appDao())
        viewModelScope.launch {
            repository.ensureInitialDataLoaded()
        }
    }

    val allGroups: StateFlow<List<TelegramGroup>> = repository.allGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<GroupOrder>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGmailSubmissions: StateFlow<List<GmailSubmission>> = repository.allGmailSubmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFacebookSubmissions: StateFlow<List<FacebookSubmission>> = repository.allFacebookSubmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWithdrawals: StateFlow<List<Withdrawal>> = repository.allWithdrawals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val walletProfile: StateFlow<WalletProfile> = repository.walletProfile
        .map { it ?: WalletProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WalletProfile())

    val adminConfigs: StateFlow<Map<String, String>> = repository.allConfigs
        .map { list -> list.associate { it.key to it.value } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val allManagers: StateFlow<List<AppManager>> = repository.allManagers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReferrals: StateFlow<List<ReferralEntry>> = repository.allReferrals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI & Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedGroupForCheckout = MutableStateFlow<TelegramGroup?>(null)
    val selectedGroupForCheckout: StateFlow<TelegramGroup?> = _selectedGroupForCheckout.asStateFlow()

    private val _sellType = MutableStateFlow(SellType.GMAIL)
    val sellType: StateFlow<SellType> = _sellType.asStateFlow()

    // Secret Admin Gate & Role Management
    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    private val _adminRole = MutableStateFlow(AdminRole.NONE)
    val adminRole: StateFlow<AdminRole> = _adminRole.asStateFlow()

    private val _currentLoggedInAdminName = MutableStateFlow("Guest")
    val currentLoggedInAdminName: StateFlow<String> = _currentLoggedInAdminName.asStateFlow()

    // App Preferences
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isEnglish = MutableStateFlow(false)
    val isEnglish: StateFlow<Boolean> = _isEnglish.asStateFlow()

    private val _demoGroupScreenshot = MutableStateFlow<Pair<String, String>?>(null)
    val demoGroupScreenshot: StateFlow<Pair<String, String>?> = _demoGroupScreenshot.asStateFlow()

    private val _showNoticeDialog = MutableStateFlow(false)
    val showNoticeDialog: StateFlow<Boolean> = _showNoticeDialog.asStateFlow()

    private val _isLoadingSplash = MutableStateFlow(true)
    val isLoadingSplash: StateFlow<Boolean> = _isLoadingSplash.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1800)
            _isLoadingSplash.value = false
        }
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleLanguage() {
        _isEnglish.value = !_isEnglish.value
    }

    fun openDemoScreenshot(group: TelegramGroup) {
        _demoGroupScreenshot.value = Pair(group.title, group.screenshotUrl)
    }

    fun closeDemoScreenshot() {
        _demoGroupScreenshot.value = null
    }

    fun toggleNoticeDialog() {
        _showNoticeDialog.value = !_showNoticeDialog.value
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun openCheckout(group: TelegramGroup) {
        _selectedGroupForCheckout.value = group
        _currentScreen.value = AppScreen.CHECKOUT
    }

    fun setSellType(type: SellType) {
        _sellType.value = type
    }

    /**
     * Compute referral link based on admin configured base link:
     * e.g. "https://t.me/PREMIUM_GROUP_BUY_BOT?startapp=ref_"
     * or "https://t.me/PREMIUM_GROUP_BUY_BOT?startapp=ref_{USER_ID}"
     */
    fun computeReferralLink(referralCode: String): String {
        val baseUrl = adminConfigs.value["referral_base_url"]?.trim()
            ?: "https://t.me/PREMIUM_GROUP_BUY_BOT?startapp=ref_"

        return when {
            baseUrl.contains("{USER_ID}") -> baseUrl.replace("{USER_ID}", referralCode)
            baseUrl.contains("{REF_CODE}") -> baseUrl.replace("{REF_CODE}", referralCode)
            baseUrl.endsWith("ref_") || baseUrl.endsWith("startapp=") || baseUrl.endsWith("start=") || baseUrl.endsWith("=") -> "$baseUrl$referralCode"
            baseUrl.contains("?") -> "$baseUrl&ref=$referralCode"
            else -> "$baseUrl?startapp=ref_$referralCode"
        }
    }

    fun unlockAdmin(secretKeyOrTelegramId: String): Boolean {
        val trimmed = secretKeyOrTelegramId.trim()

        // 1. Check Owner (আসল অনার - সাদ্দাম ভাই)
        val isOwner = trimmed == "8701368956" ||
                trimmed.equals("ItsSaddam9", ignoreCase = true) ||
                trimmed.equals("@ItsSaddam9", ignoreCase = true) ||
                trimmed == "saddam8701"

        if (isOwner) {
            _adminRole.value = AdminRole.OWNER
            _currentLoggedInAdminName.value = "সাদ্দাম ভাই (আসল অনার)"
            _isAdminUnlocked.value = true
            _currentScreen.value = AppScreen.ADMIN
            viewModelScope.launch {
                _toastMessage.emit("👑 স্বাগতম সাদ্দাম ভাই! আসল অনার প্যানেলে প্রবেশ করেছেন।")
            }
            return true
        }

        // 2. Check Managers (ম্যানেজারদের চেক করুন)
        val cleanInput = trimmed.removePrefix("@")
        val matchedManager = allManagers.value.firstOrNull {
            (it.telegramIdOrUsername.equals(cleanInput, ignoreCase = true) ||
             it.passcode == cleanInput) && it.status == "ACTIVE"
        }

        if (matchedManager != null) {
            _adminRole.value = AdminRole.MANAGER
            _currentLoggedInAdminName.value = matchedManager.name
            _isAdminUnlocked.value = true
            _currentScreen.value = AppScreen.ADMIN
            viewModelScope.launch {
                _toastMessage.emit("🛡️ স্বাগতম ম্যানেজার '${matchedManager.name}'! আপনি সব অর্ডার, ডিপোজিট ও লিংক ম্যানেজ করতে পারবেন।")
            }
            return true
        }

        viewModelScope.launch {
            _toastMessage.emit("❌ ভুল এডমিন বা ম্যানেজার আইডি/পাসকোড! শুধুমাত্র অনুমোদিত এডমিনের জন্য।")
        }
        return false
    }

    fun lockAdmin() {
        _isAdminUnlocked.value = false
        _adminRole.value = AdminRole.NONE
        _currentLoggedInAdminName.value = "Guest"
        _currentScreen.value = AppScreen.HOME
    }

    // === Manager Management (Owner Only) ===
    fun addManager(name: String, telegramIdOrUsername: String, passcode: String) {
        if (_adminRole.value != AdminRole.OWNER) {
            viewModelScope.launch {
                _toastMessage.emit("⚠️ অনুমতি নেই! শুধুমাত্র আসল অনার নতুন ম্যানেজার যুক্ত করতে পারেন।")
            }
            return
        }

        if (name.isBlank() || telegramIdOrUsername.isBlank()) {
            viewModelScope.launch {
                _toastMessage.emit("ম্যানেজারের নাম এবং টেলিগ্রাম ইউজারনেম/আইডি লিখুন!")
            }
            return
        }

        viewModelScope.launch {
            repository.addManager(name, telegramIdOrUsername, passcode)
            _toastMessage.emit("✅ নতুন ম্যানেজার '$name' সফলভাবে যুক্ত হয়েছে!")
        }
    }

    fun deleteManager(manager: AppManager) {
        if (_adminRole.value != AdminRole.OWNER) {
            viewModelScope.launch {
                _toastMessage.emit("⚠️ অনুমতি নেই! শুধুমাত্র আসল অনার ম্যানেজার ডিলিট করতে পারেন।")
            }
            return
        }

        viewModelScope.launch {
            repository.deleteManager(manager)
            _toastMessage.emit("ম্যানেজার '${manager.name}' রিমুভ করা হয়েছে।")
        }
    }

    // === Referral Operations ===
    fun simulateNewReferral(name: String, telegram: String) {
        viewModelScope.launch {
            val validName = if (name.isBlank()) "মেম্বার ${System.currentTimeMillis().toString().takeLast(4)}" else name.trim()
            val validTg = if (telegram.isBlank()) "@user_${System.currentTimeMillis().toString().takeLast(5)}" else telegram.trim()
            val myRefCode = walletProfile.value.referralCode

            val newEntry = ReferralEntry(
                referrerCode = myRefCode,
                referredUserName = validName,
                referredUserTelegram = if (validTg.startsWith("@")) validTg else "@$validTg",
                joinedTimestamp = System.currentTimeMillis(),
                bonusAmount = 20,
                status = "সক্রিয় মেম্বার (Active)",
                commissionEarned = 0
            )
            repository.addReferralEntry(newEntry)
            _toastMessage.emit("🎉 অভিনন্দন! '$validName' আপনার রেফার লিংকে জয়েন করেছে। ৳২০ বোনাস ওয়ালেটে যোগ হয়েছে!")
        }
    }

    fun updateReferralBaseUrl(newBaseUrl: String) {
        viewModelScope.launch {
            if (newBaseUrl.isBlank()) {
                _toastMessage.emit("সঠিক রেফারেল লিংক দিন!")
                return@launch
            }
            repository.updateConfig("referral_base_url", newBaseUrl.trim())
            _toastMessage.emit("✅ রেফারেল লিংক ফরম্যাট সফলভাবে আপডেট করা হয়েছে!")
        }
    }

    fun placeOrder(
        userTelegram: String,
        group: TelegramGroup,
        paymentMethod: String,
        senderNumber: String,
        trxId: String
    ) {
        viewModelScope.launch {
            if (senderNumber.isBlank() || trxId.isBlank()) {
                _toastMessage.emit("অনুগ্রহ করে আপনার পেমেন্ট নাম্বার এবং TrxID দিন!")
                return@launch
            }
            repository.placeGroupOrder(
                userTelegram = if (userTelegram.isBlank()) "@guest_buyer" else userTelegram,
                groupId = group.id,
                groupTitle = group.title,
                amount = group.price,
                paymentMethod = paymentMethod,
                senderNumber = senderNumber,
                trxId = trxId
            )
            _toastMessage.emit("অর্ডার সফল হয়েছে! এডমিন TrxID যাচাই করে টেলিগ্রামে গ্রুপ ইনভাইট লিংক পাঠিয়ে দেবে।")
            _currentScreen.value = AppScreen.HOME
        }
    }

    fun submitGmail(
        userTelegram: String,
        gmail: String,
        pass: String,
        recovery: String,
        accType: String,
        rewardAmount: Int
    ) {
        viewModelScope.launch {
            if (gmail.isBlank() || pass.isBlank()) {
                _toastMessage.emit("জিমেইল এবং পাসওয়ার্ড দুটিই পূরণ করতে হবে!")
                return@launch
            }
            if (!gmail.contains("@gmail.com")) {
                _toastMessage.emit("সঠিক জিমেইল এড্রেস লিখুন (example@gmail.com)")
                return@launch
            }
            repository.submitGmail(
                userTelegram = if (userTelegram.isBlank()) "@seller_user" else userTelegram,
                gmail = gmail.trim(),
                password = pass.trim(),
                recoveryEmail = recovery.trim(),
                accountType = accType,
                rewardAmount = rewardAmount
            )
            _toastMessage.emit("জিমেইল সাবমিট সফল! এডমিন চেক করে এপ্রুভ করলে ৳$rewardAmount ব্যালেন্সে যোগ হবে।")
        }
    }

    fun submitFacebook(
        userTelegram: String,
        uidOrLink: String,
        phoneOrEmail: String,
        pass: String,
        twoFactor: String,
        accYear: String,
        rewardAmount: Int
    ) {
        viewModelScope.launch {
            if (phoneOrEmail.isBlank() || pass.isBlank()) {
                _toastMessage.emit("ফেসবুক লগইন আইডি এবং পাসওয়ার্ড পূরণ করুন!")
                return@launch
            }
            repository.submitFacebook(
                userTelegram = if (userTelegram.isBlank()) "@seller_user" else userTelegram,
                profileLinkOrUid = uidOrLink.trim(),
                phoneOrEmail = phoneOrEmail.trim(),
                password = pass.trim(),
                twoFactorCode = twoFactor.trim(),
                accountYear = accYear,
                rewardAmount = rewardAmount
            )
            _toastMessage.emit("ফেসবুক অ্যাকাউন্ট সাবমিট হয়েছে! এডমিন চেক করলে ৳$rewardAmount ওয়ালেটে যোগ হবে।")
        }
    }

    fun requestWithdraw(
        paymentMethod: String,
        accountNumber: String,
        amount: Int
    ) {
        viewModelScope.launch {
            if (accountNumber.isBlank() || amount <= 0) {
                _toastMessage.emit("সঠিক একাউন্ট নাম্বার এবং উইথড্র পরিমাণ লিখুন!")
                return@launch
            }
            if (amount < 50) {
                _toastMessage.emit("সর্বনিম্ন উইথড্র পরিমাণ ৳৫০!")
                return@launch
            }
            val currentTelegram = walletProfile.value.userTelegram
            val result = repository.requestWithdrawal(
                userTelegram = currentTelegram,
                paymentMethod = paymentMethod,
                accountNumber = accountNumber,
                amount = amount
            )
            if (result.isSuccess) {
                _toastMessage.emit("উইথড্র রিকোয়েস্ট সফল হয়েছে! খুব শীঘ্রই পেমেন্ট সম্পন্ন হবে।")
            } else {
                _toastMessage.emit(result.exceptionOrNull()?.message ?: "উইথড্র ব্যর্থ হয়েছে")
            }
        }
    }

    fun rechargeWallet(amount: Int) {
        viewModelScope.launch {
            repository.addWalletRecharge(amount)
            _toastMessage.emit("৳$amount ওয়ালেটে সফলভাবে যোগ করা হয়েছে!")
        }
    }

    // === Admin & Manager Allowed Actions ===

    fun adminApproveOrder(order: GroupOrder, inviteLink: String) {
        viewModelScope.launch {
            val link = if (inviteLink.isBlank()) "https://t.me/+slk_vip_approved_link" else inviteLink
            repository.adminApproveOrder(order, link)
            _toastMessage.emit("অর্ডার #${order.id} এপ্রুভ হয়েছে এবং ইনভাইট লিংক যুক্ত হয়েছে!")
        }
    }

    fun adminRejectOrder(order: GroupOrder) {
        viewModelScope.launch {
            repository.adminRejectOrder(order)
            _toastMessage.emit("অর্ডার #${order.id} বাতিল করা হয়েছে।")
        }
    }

    fun adminApproveGmail(sub: GmailSubmission) {
        viewModelScope.launch {
            repository.adminApproveGmail(sub)
            _toastMessage.emit("জিমেইল ${sub.gmail} এপ্রুভ হয়েছে! ইউজারের ওয়ালেটে ৳${sub.rewardAmount} যোগ হয়েছে।")
        }
    }

    fun adminRejectGmail(sub: GmailSubmission, reason: String) {
        viewModelScope.launch {
            repository.adminRejectGmail(sub, reason)
            _toastMessage.emit("জিমেইল ${sub.gmail} বাতিল করা হয়েছে।")
        }
    }

    fun adminApproveFacebook(sub: FacebookSubmission) {
        viewModelScope.launch {
            repository.adminApproveFacebook(sub)
            _toastMessage.emit("ফেসবুক অ্যাকাউন্ট এপ্রুভ হয়েছে! ইউজারের ওয়ালেটে ৳${sub.rewardAmount} যোগ হয়েছে।")
        }
    }

    fun adminRejectFacebook(sub: FacebookSubmission, reason: String) {
        viewModelScope.launch {
            repository.adminRejectFacebook(sub, reason)
            _toastMessage.emit("ফেসবুক সাবমিশন বাতিল করা হয়েছে।")
        }
    }

    fun adminApproveWithdrawal(withdrawal: Withdrawal, trxId: String) {
        viewModelScope.launch {
            val sentTrx = if (trxId.isBlank()) "BK${System.currentTimeMillis().toString().takeLast(8)}" else trxId
            repository.adminApproveWithdrawal(withdrawal, sentTrx)
            _toastMessage.emit("উইথড্র #${withdrawal.id} ৳${withdrawal.amount} পরিশোধিত মার্ক করা হয়েছে!")
        }
    }

    fun adminRejectWithdrawal(withdrawal: Withdrawal) {
        viewModelScope.launch {
            repository.adminRejectWithdrawal(withdrawal)
            _toastMessage.emit("উইথড্র #${withdrawal.id} বাতিল করা হয়েছে এবং টাকা ওয়ালেটে ফেরত দেওয়া হয়েছে।")
        }
    }

    fun addNewGroup(
        title: String,
        category: String,
        description: String,
        membersCount: String,
        price: Int,
        originalPrice: Int,
        inviteLink: String,
        badge: String,
        screenshotUrl: String = ""
    ) {
        viewModelScope.launch {
            if (title.isBlank() || price <= 0) {
                _toastMessage.emit("গ্রুপের নাম ও প্রাইস সঠিকভাবে দিন!")
                return@launch
            }
            val newGroup = TelegramGroup(
                title = title,
                category = if (category.isBlank()) "VIP Group" else category,
                description = description,
                membersCount = if (membersCount.isBlank()) "10,000+" else membersCount,
                price = price,
                originalPrice = if (originalPrice <= 0) price + 200 else originalPrice,
                inviteLink = if (inviteLink.isBlank()) "https://t.me/+slk_new_vip_group" else inviteLink,
                badge = if (badge.isBlank()) "NEW VIP" else badge,
                screenshotUrl = screenshotUrl.trim(),
                previewGradientKey = "CYAN_INDIGO"
            )
            repository.addNewGroup(newGroup)
            _toastMessage.emit("নতুন প্রিমিয়াম গ্রুপ সফলভাবে যুক্ত হয়েছে!")
        }
    }

    fun adminUpdateGroup(group: TelegramGroup) {
        viewModelScope.launch {
            repository.updateGroup(group)
            _toastMessage.emit("গ্রুপ '${group.title}' আপডেট হয়েছে!")
        }
    }

    fun adminDeleteGroup(group: TelegramGroup) {
        viewModelScope.launch {
            repository.deleteGroup(group)
            _toastMessage.emit("গ্রুপ সফলভাবে রিমুভ করা হয়েছে!")
        }
    }

    fun adminUpdateConfig(key: String, value: String) {
        viewModelScope.launch {
            repository.updateConfig(key, value.trim())
            _toastMessage.emit("কনফিগারেশন আপডেট সম্পন্ন!")
        }
    }

    fun updatePaymentNumber(key: String, number: String) {
        viewModelScope.launch {
            repository.updateConfig(key, number.trim())
            _toastMessage.emit("পেমেন্ট সেটিংস সফলভাবে সেভ হয়েছে!")
        }
    }
}
