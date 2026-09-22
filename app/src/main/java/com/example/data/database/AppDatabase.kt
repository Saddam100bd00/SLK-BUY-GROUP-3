package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AppDao
import com.example.data.model.AdminConfig
import com.example.data.model.FacebookSubmission
import com.example.data.model.GmailSubmission
import com.example.data.model.GroupOrder
import com.example.data.model.TelegramGroup
import com.example.data.model.WalletProfile
import com.example.data.model.Withdrawal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TelegramGroup::class,
        GroupOrder::class,
        GmailSubmission::class,
        FacebookSubmission::class,
        Withdrawal::class,
        WalletProfile::class,
        AdminConfig::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "slk_buy_group_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.appDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: AppDao) {
                // Initial Telegram Groups
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

                // Initial Wallet Profile
                val initialWallet = WalletProfile(
                    id = 1,
                    currentBalance = 100, // ৳100 Free Welcome bonus
                    totalEarned = 100,
                    totalWithdrawn = 0,
                    referralCode = "SLK-8701",
                    totalReferrals = 2,
                    userTelegram = "@ItsSaddam9_member"
                )
                dao.insertOrUpdateWalletProfile(initialWallet)

                // Initial Admin Configs
                val configs = listOf(
                    AdminConfig("bkash_number", "01789-567890"),
                    AdminConfig("nagad_number", "01812-345678"),
                    AdminConfig("rocket_number", "01934-567891"),
                    AdminConfig("admin_telegram", "https://t.me/ItsSaddam9"),
                    AdminConfig("admin_telegram_id", "8701368956"),
                    AdminConfig("admin_username", "ItsSaddam9"),
                    AdminConfig("notice_text", "📢 স্বাগতম SLK BUY GROUP এ! টেলিগ্রাম প্রিমিয়াম গ্রুপ কিনুন এবং জিমেইল ও ফেসবুক বিক্রি করে প্রতিদিন আনলিমিটেড টাকা আয় করুন। বিকাশ, নগদ ও রকেটে দ্রুত পেমেন্ট দেওয়া হয়।")
                )
                for (config in configs) {
                    dao.setConfig(config)
                }
            }
        }
    }
}
