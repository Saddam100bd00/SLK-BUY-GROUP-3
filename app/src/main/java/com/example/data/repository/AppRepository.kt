package com.example.data.repository

import com.example.data.dao.AppDao
import com.example.data.model.AdminConfig
import com.example.data.model.FacebookSubmission
import com.example.data.model.GmailSubmission
import com.example.data.model.GroupOrder
import com.example.data.model.TelegramGroup
import com.example.data.model.WalletProfile
import com.example.data.model.Withdrawal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(private val dao: AppDao) {

    val allGroups: Flow<List<TelegramGroup>> = dao.getAllGroups()
    val allOrders: Flow<List<GroupOrder>> = dao.getAllOrders()
    val allGmailSubmissions: Flow<List<GmailSubmission>> = dao.getAllGmailSubmissions()
    val allFacebookSubmissions: Flow<List<FacebookSubmission>> = dao.getAllFacebookSubmissions()
    val allWithdrawals: Flow<List<Withdrawal>> = dao.getAllWithdrawals()
    val walletProfile: Flow<WalletProfile?> = dao.getWalletProfile()
    val allConfigs: Flow<List<AdminConfig>> = dao.getAllConfig()

    fun getGroupById(id: Long): Flow<TelegramGroup?> = dao.getGroupById(id)

    suspend fun ensureInitialDataLoaded() {
        val currentGroups = allGroups.firstOrNull()
        if (currentGroups.isNullOrEmpty()) {
            val initialGroups = listOf(
                TelegramGroup(
                    title = "SLK VIP Movies & Web Series 4K",
                    category = "Movies & Web Series",
                    description = "লেটেস্ট হলিউড, বলিউড, সাউথ ও বাংলা ৪K আল্ট্রা এইচডি মুভি ও প্রিমিয়াম ওয়েব সিরিজ কালেকশন। প্রতিদিন আনলিমিটেড আপডেট।",
                    membersCount = "48,500+",
                    price = 350,
                    originalPrice = 600,
                    badge = "BESTSELLER",
                    rating = 4.9f,
                    previewGradientKey = "CYAN_INDIGO",
                    samplePostTitle = "Avatar 3 Ultra HD 4K Remux + Mega Pack",
                    samplePostDuration = "3h 12m • 2160p HDR Dolby Atmos",
                    inviteLink = "https://t.me/+slk_vip_movies_4k_join",
                    isHot = true
                ),
                TelegramGroup(
                    title = "Exclusive 18+ VIP Premium Vault",
                    category = "VIP Exclusive Content",
                    description = "টেলিগ্রামের সবচেয়ে বড় ও প্রিমিয়াম প্রাইভেট গ্রুপ। আনসেন্সরড ফুল এইচডি ভিডিও, রিলস ও ডেইলি এক্সক্লুসিভ মেগা লিংক।",
                    membersCount = "82,300+",
                    price = 500,
                    originalPrice = 850,
                    badge = "HOT VIP",
                    rating = 5.0f,
                    previewGradientKey = "ROSE_PURPLE",
                    samplePostTitle = "Private Premium VIP Channel Daily 25+ Clips",
                    samplePostDuration = "Full HD 1080p • Fast Download",
                    inviteLink = "https://t.me/+slk_exclusive_vip_vault",
                    isHot = true
                ),
                TelegramGroup(
                    title = "All OTT & Netflix Daily Mega Channel",
                    category = "OTT & Streaming",
                    description = "নেটফ্লিক্স, অ্যামাজন প্রাইম, হইচই, চরকি ও ডিজনি প্লাসের সকল পেইড সিরিজ ও ড্রামা সরাসরি ফ্রিতে ডাউনলোড লিংক।",
                    membersCount = "29,400+",
                    price = 250,
                    originalPrice = 450,
                    badge = "POPULAR",
                    rating = 4.8f,
                    previewGradientKey = "EMERALD_TEAL",
                    samplePostTitle = "Stranger Things Season 5 All Episodes 4K Dual Audio",
                    samplePostDuration = "8 Episodes • 1080p Dual Audio",
                    inviteLink = "https://t.me/+slk_ott_daily_channel",
                    isHot = false
                ),
                TelegramGroup(
                    title = "Software, Mod APKs & Premium Tools",
                    category = "Tech & Software",
                    description = "প্রিমিয়াম ভিপিএন, ভিডিও এডিটর মোড এপিকে (Canva Pro, CapCut Pro), উইন্ডোজ সফটওয়্যার ও ফুল ক্র্যাক টুলস।",
                    membersCount = "63,100+",
                    price = 400,
                    originalPrice = 700,
                    badge = "PRO TECH",
                    rating = 4.9f,
                    previewGradientKey = "AMBER_GOLD",
                    samplePostTitle = "Adobe Master Collection 2026 + Canva Pro Lifetime",
                    samplePostDuration = "Direct Google Drive Link",
                    inviteLink = "https://t.me/+slk_tech_mod_tools",
                    isHot = false
                ),
                TelegramGroup(
                    title = "Korean Drama & Anime Full HD Hub",
                    category = "Anime & K-Drama",
                    description = "সর্বশেষ ট্রেন্ডিং কে-ড্রামা ও এনিমে সিরিজ বাংলা ও ইংরেজি সাবটাইটেল সহ সবার আগে দেখার সেরা ভিআইপি প্ল্যাটফর্ম।",
                    membersCount = "36,800+",
                    price = 300,
                    originalPrice = 500,
                    badge = "TRENDING",
                    rating = 4.8f,
                    previewGradientKey = "VIOLET_BLUE",
                    samplePostTitle = "Solo Leveling Season 2 Complete 1080p Dual Audio",
                    samplePostDuration = "12 Episodes Full Pack",
                    inviteLink = "https://t.me/+slk_kdrama_anime_vip",
                    isHot = false
                )
            )
            dao.insertGroups(initialGroups)
        }

        val wallet = dao.getWalletProfileSync()
        if (wallet == null) {
            dao.insertOrUpdateWalletProfile(
                WalletProfile(
                    id = 1,
                    currentBalance = 100,
                    totalEarned = 100,
                    totalWithdrawn = 0,
                    referralCode = "SLK-8701",
                    totalReferrals = 2,
                    userTelegram = "@ItsSaddam9_member"
                )
            )
        }

        // Ensure default admin configs exist
        val defaultConfigs = mapOf(
            "bkash_number" to "01789-567890",
            "nagad_number" to "01812-345678",
            "rocket_number" to "01934-567891",
            "binance_id" to "87013689",
            "binance_usdt_address" to "TYeK8Q3LqW4vH1m9PbzE9102XUsdtTrc20",
            "app_logo_url" to "",
            "bkash_logo_url" to "",
            "nagad_logo_url" to "",
            "rocket_logo_url" to "",
            "binance_logo_url" to "",
            "gmail_price_fresh" to "15",
            "gmail_price_old_6m" to "30",
            "gmail_price_old_1yr" to "50",
            "fb_price_new" to "40",
            "fb_price_old" to "70",
            "fb_price_very_old" to "120",
            "admin_telegram" to "https://t.me/ItsSaddam9",
            "admin_telegram_id" to "8701368956",
            "admin_username" to "ItsSaddam9",
            "notice_text" to "📢 স্বাগতম SLK BUY GROUP এ! টেলিগ্রাম প্রিমিয়াম গ্রুপ কিনুন এবং জিমেইল ও ফেসবুক বিক্রি করে প্রতিদিন আনলিমিটেড টাকা আয় করুন। বিকাশ, নগদ, রকেট ও Binance এ নিরাপদ পেমেন্ট।"
        )
        for ((key, value) in defaultConfigs) {
            val existing = dao.getConfigValue(key)
            if (existing == null) {
                dao.setConfig(AdminConfig(key, value))
            }
        }
    }

    suspend fun addReferralReward(amount: Int) {
        val currentWallet = dao.getWalletProfileSync() ?: WalletProfile()
        val updatedWallet = currentWallet.copy(
            currentBalance = currentWallet.currentBalance + amount,
            totalEarned = currentWallet.totalEarned + amount,
            totalReferrals = currentWallet.totalReferrals + 1
        )
        dao.insertOrUpdateWalletProfile(updatedWallet)
    }

    suspend fun placeGroupOrder(
        userTelegram: String,
        groupId: Long,
        groupTitle: String,
        amount: Int,
        paymentMethod: String,
        senderNumber: String,
        trxId: String
    ): Long {
        val order = GroupOrder(
            userTelegram = userTelegram,
            groupId = groupId,
            groupTitle = groupTitle,
            amount = amount,
            paymentMethod = paymentMethod,
            senderNumber = senderNumber,
            trxId = trxId,
            status = "PENDING"
        )
        return dao.insertOrder(order)
    }

    suspend fun submitGmail(
        userTelegram: String,
        gmail: String,
        password: String,
        recoveryEmail: String,
        accountType: String,
        rewardAmount: Int
    ): Long {
        val submission = GmailSubmission(
            userTelegram = userTelegram,
            gmail = gmail,
            password = password,
            recoveryEmail = recoveryEmail,
            accountType = accountType,
            rewardAmount = rewardAmount,
            status = "PENDING"
        )
        return dao.insertGmailSubmission(submission)
    }

    suspend fun submitFacebook(
        userTelegram: String,
        profileLinkOrUid: String,
        phoneOrEmail: String,
        password: String,
        twoFactorCode: String,
        accountYear: String,
        rewardAmount: Int
    ): Long {
        val submission = FacebookSubmission(
            userTelegram = userTelegram,
            profileLinkOrUid = profileLinkOrUid,
            phoneOrEmail = phoneOrEmail,
            password = password,
            twoFactorCode = twoFactorCode,
            accountYear = accountYear,
            rewardAmount = rewardAmount,
            status = "PENDING"
        )
        return dao.insertFacebookSubmission(submission)
    }

    suspend fun requestWithdrawal(
        userTelegram: String,
        paymentMethod: String,
        accountNumber: String,
        amount: Int
    ): Result<Long> {
        val currentWallet = dao.getWalletProfileSync() ?: WalletProfile()
        if (currentWallet.currentBalance < amount) {
            return Result.failure(Exception("পর্যাপ্ত ব্যালেন্স নেই! আপনার বর্তমান ব্যালেন্স ৳${currentWallet.currentBalance}"))
        }

        // Deduct from balance
        val updatedWallet = currentWallet.copy(
            currentBalance = currentWallet.currentBalance - amount
        )
        dao.insertOrUpdateWalletProfile(updatedWallet)

        val withdrawal = Withdrawal(
            userTelegram = userTelegram,
            paymentMethod = paymentMethod,
            accountNumber = accountNumber,
            amount = amount,
            status = "PENDING"
        )
        val id = dao.insertWithdrawal(withdrawal)
        return Result.success(id)
    }

    suspend fun addWalletRecharge(amount: Int) {
        val currentWallet = dao.getWalletProfileSync() ?: WalletProfile()
        val updated = currentWallet.copy(
            currentBalance = currentWallet.currentBalance + amount,
            totalEarned = currentWallet.totalEarned + amount
        )
        dao.insertOrUpdateWalletProfile(updated)
    }

    // === Admin Operations ===

    suspend fun adminApproveOrder(order: GroupOrder, inviteLink: String) {
        val updated = order.copy(
            status = "APPROVED",
            inviteLinkSent = inviteLink
        )
        dao.updateOrder(updated)
    }

    suspend fun adminRejectOrder(order: GroupOrder) {
        val updated = order.copy(status = "REJECTED")
        dao.updateOrder(updated)
    }

    suspend fun adminApproveGmail(submission: GmailSubmission) {
        val updated = submission.copy(status = "APPROVED")
        dao.updateGmailSubmission(updated)

        // Credit to user's wallet
        val currentWallet = dao.getWalletProfileSync() ?: WalletProfile()
        val updatedWallet = currentWallet.copy(
            currentBalance = currentWallet.currentBalance + submission.rewardAmount,
            totalEarned = currentWallet.totalEarned + submission.rewardAmount
        )
        dao.insertOrUpdateWalletProfile(updatedWallet)
    }

    suspend fun adminRejectGmail(submission: GmailSubmission, note: String = "তথ্য ভুল বা ২-স্টেপ চালু ছিল") {
        val updated = submission.copy(status = "REJECTED", adminNote = note)
        dao.updateGmailSubmission(updated)
    }

    suspend fun adminApproveFacebook(submission: FacebookSubmission) {
        val updated = submission.copy(status = "APPROVED")
        dao.updateFacebookSubmission(updated)

        // Credit to user's wallet
        val currentWallet = dao.getWalletProfileSync() ?: WalletProfile()
        val updatedWallet = currentWallet.copy(
            currentBalance = currentWallet.currentBalance + submission.rewardAmount,
            totalEarned = currentWallet.totalEarned + submission.rewardAmount
        )
        dao.insertOrUpdateWalletProfile(updatedWallet)
    }

    suspend fun adminRejectFacebook(submission: FacebookSubmission, note: String = "আইডি লগইন হয়নি বা ভুল পাসওয়ার্ড") {
        val updated = submission.copy(status = "REJECTED", adminNote = note)
        dao.updateFacebookSubmission(updated)
    }

    suspend fun adminApproveWithdrawal(withdrawal: Withdrawal, adminTrxId: String) {
        val updated = withdrawal.copy(
            status = "APPROVED",
            adminTrxId = adminTrxId
        )
        dao.updateWithdrawal(updated)

        val currentWallet = dao.getWalletProfileSync() ?: WalletProfile()
        val updatedWallet = currentWallet.copy(
            totalWithdrawn = currentWallet.totalWithdrawn + withdrawal.amount
        )
        dao.insertOrUpdateWalletProfile(updatedWallet)
    }

    suspend fun adminRejectWithdrawal(withdrawal: Withdrawal) {
        val updated = withdrawal.copy(status = "REJECTED")
        dao.updateWithdrawal(updated)

        // Refund back to user's balance
        val currentWallet = dao.getWalletProfileSync() ?: WalletProfile()
        val updatedWallet = currentWallet.copy(
            currentBalance = currentWallet.currentBalance + withdrawal.amount
        )
        dao.insertOrUpdateWalletProfile(updatedWallet)
    }

    suspend fun addNewGroup(group: TelegramGroup): Long {
        return dao.insertGroup(group)
    }

    suspend fun updateGroup(group: TelegramGroup) {
        dao.updateGroup(group)
    }

    suspend fun deleteGroup(group: TelegramGroup) {
        dao.deleteGroup(group)
    }

    suspend fun updateConfig(key: String, value: String) {
        dao.setConfig(AdminConfig(key, value))
    }
}
