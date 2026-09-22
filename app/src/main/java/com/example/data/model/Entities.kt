package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "telegram_groups")
data class TelegramGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val description: String,
    val membersCount: String,
    val price: Int, // in BDT (৳)
    val originalPrice: Int,
    val badge: String = "VIP",
    val rating: Float = 4.9f,
    val previewGradientKey: String = "CYAN_INDIGO", // used for custom UK luxury visual card
    val samplePostTitle: String = "Latest Ultra 4K Release & Daily Packs",
    val samplePostDuration: String = "2h 45m • 1080p / 4K",
    val inviteLink: String = "https://t.me/+slk_vip_premium_invite",
    val isHot: Boolean = false
)

@Entity(tableName = "group_orders")
data class GroupOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userTelegram: String,
    val groupId: Long,
    val groupTitle: String,
    val amount: Int,
    val paymentMethod: String, // "bKash", "Nagad", "Rocket"
    val senderNumber: String,
    val trxId: String,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val inviteLinkSent: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "gmail_submissions")
data class GmailSubmission(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userTelegram: String,
    val gmail: String,
    val password: String,
    val recoveryEmail: String = "",
    val accountType: String = "Fresh (2024-2026)",
    val rewardAmount: Int = 15, // in BDT (৳)
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val adminNote: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "facebook_submissions")
data class FacebookSubmission(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userTelegram: String,
    val profileLinkOrUid: String,
    val phoneOrEmail: String,
    val password: String,
    val twoFactorCode: String = "",
    val accountYear: String = "2018-2022 (Old)",
    val rewardAmount: Int = 60, // in BDT (৳)
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val adminNote: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "withdrawals")
data class Withdrawal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userTelegram: String,
    val paymentMethod: String, // "bKash", "Nagad", "Rocket"
    val accountNumber: String,
    val amount: Int,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val adminTrxId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "wallet_profile")
data class WalletProfile(
    @PrimaryKey
    val id: Int = 1,
    val currentBalance: Int = 100, // starting welcome bonus ৳100
    val totalEarned: Int = 100,
    val totalWithdrawn: Int = 0,
    val referralCode: String = "SLK-8701",
    val totalReferrals: Int = 2,
    val userTelegram: String = "@user_guest"
)

@Entity(tableName = "admin_config")
data class AdminConfig(
    @PrimaryKey
    val key: String,
    val value: String
)
