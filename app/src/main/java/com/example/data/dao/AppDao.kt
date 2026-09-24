package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminConfig
import com.example.data.model.AppManager
import com.example.data.model.FacebookSubmission
import com.example.data.model.GmailSubmission
import com.example.data.model.GroupOrder
import com.example.data.model.ReferralEntry
import com.example.data.model.TelegramGroup
import com.example.data.model.WalletProfile
import com.example.data.model.Withdrawal
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // === Telegram Groups ===
    @Query("SELECT * FROM telegram_groups ORDER BY id ASC")
    fun getAllGroups(): Flow<List<TelegramGroup>>

    @Query("SELECT * FROM telegram_groups WHERE id = :id LIMIT 1")
    fun getGroupById(id: Long): Flow<TelegramGroup?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<TelegramGroup>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: TelegramGroup): Long

    @Delete
    suspend fun deleteGroup(group: TelegramGroup)

    @Update
    suspend fun updateGroup(group: TelegramGroup)

    // === Group Orders ===
    @Query("SELECT * FROM group_orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<GroupOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: GroupOrder): Long

    @Update
    suspend fun updateOrder(order: GroupOrder)

    // === Gmail Submissions ===
    @Query("SELECT * FROM gmail_submissions ORDER BY timestamp DESC")
    fun getAllGmailSubmissions(): Flow<List<GmailSubmission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGmailSubmission(submission: GmailSubmission): Long

    @Update
    suspend fun updateGmailSubmission(submission: GmailSubmission)

    // === Facebook Submissions ===
    @Query("SELECT * FROM facebook_submissions ORDER BY timestamp DESC")
    fun getAllFacebookSubmissions(): Flow<List<FacebookSubmission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFacebookSubmission(submission: FacebookSubmission): Long

    @Update
    suspend fun updateFacebookSubmission(submission: FacebookSubmission)

    // === Withdrawals ===
    @Query("SELECT * FROM withdrawals ORDER BY timestamp DESC")
    fun getAllWithdrawals(): Flow<List<Withdrawal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: Withdrawal): Long

    @Update
    suspend fun updateWithdrawal(withdrawal: Withdrawal)

    // === Wallet Profile ===
    @Query("SELECT * FROM wallet_profile WHERE id = 1 LIMIT 1")
    fun getWalletProfile(): Flow<WalletProfile?>

    @Query("SELECT * FROM wallet_profile WHERE id = 1 LIMIT 1")
    suspend fun getWalletProfileSync(): WalletProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWalletProfile(profile: WalletProfile)

    // === Admin Config ===
    @Query("SELECT * FROM admin_config")
    fun getAllConfig(): Flow<List<AdminConfig>>

    @Query("SELECT value FROM admin_config WHERE `key` = :key LIMIT 1")
    suspend fun getConfigValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setConfig(config: AdminConfig)

    // === App Managers ===
    @Query("SELECT * FROM app_managers ORDER BY id ASC")
    fun getAllManagers(): Flow<List<AppManager>>

    @Query("SELECT * FROM app_managers ORDER BY id ASC")
    suspend fun getAllManagersSync(): List<AppManager>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManager(manager: AppManager): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManagers(managers: List<AppManager>)

    @Update
    suspend fun updateManager(manager: AppManager)

    @Delete
    suspend fun deleteManager(manager: AppManager)

    // === Referral History ===
    @Query("SELECT * FROM referral_history ORDER BY joinedTimestamp DESC")
    fun getAllReferrals(): Flow<List<ReferralEntry>>

    @Query("SELECT * FROM referral_history WHERE referrerCode = :code ORDER BY joinedTimestamp DESC")
    fun getReferralsByCode(code: String): Flow<List<ReferralEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferral(entry: ReferralEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferrals(entries: List<ReferralEntry>)
}
