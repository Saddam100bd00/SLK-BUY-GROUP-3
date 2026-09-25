/**
 * SLK BUY GROUP - 24/7 High-Performance Telegram Bot & Web Server
 * Runs on Port 3000 (Behind Nginx reverse proxy on Port 8080)
 * Fully integrates with Firebase Realtime Database & Telegram Bot API
 */

const http = require('http');
const https = require('https');
const fs = require('fs');
const path = require('path');
const url = require('url');

const PORT = 3000;
const FIREBASE_DB_URL = "https://premium-trial-bot-default-rtdb.firebaseio.com";
const DEFAULT_ADMIN_CHAT_ID = "8701368956";
const DEFAULT_ADMIN_USERNAME = "itssaddam9";

// Cached state
let cachedConfig = {
  appName: "SLK BUY GROUP",
  botToken: "",
  chatId: DEFAULT_ADMIN_CHAT_ID,
  botEnabled: true,
  minWithdraw: 100,
  gmailRates: { new: 15, old: 25, aged: 40 },
  fbRates: { new: 20, old: 60, aged: 110 },
  paymentGateways: {
    bkash: { number: "01789-567890", type: "Personal" },
    nagad: { number: "01812-345678", type: "Personal" },
    rocket: { number: "01912-345678", type: "Personal" },
    binance: { number: "8701368956 (Pay ID)", type: "PayID" }
  },
  groups: [],
  comboBundles: [],
  supportLink: "https://t.me/ItsSaddam9"
};

let serverPollingActive = false;
let serverPollingOffset = 0;
let lastConfigFetchTime = 0;

// =========================================================================
// FIREBASE HELPER FUNCTIONS (REST API)
// =========================================================================

async function fetchFromFirebase(endpoint) {
  try {
    const res = await fetch(`${FIREBASE_DB_URL}/${endpoint}.json`);
    if (!res.ok) return null;
    return await res.json();
  } catch (err) {
    console.error(`Firebase fetch error [${endpoint}]:`, err.message);
    return null;
  }
}

async function putToFirebase(endpoint, data) {
  try {
    const res = await fetch(`${FIREBASE_DB_URL}/${endpoint}.json`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return await res.json();
  } catch (err) {
    console.error(`Firebase PUT error [${endpoint}]:`, err.message);
    return null;
  }
}

async function patchToFirebase(endpoint, data) {
  try {
    const res = await fetch(`${FIREBASE_DB_URL}/${endpoint}.json`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return await res.json();
  } catch (err) {
    console.error(`Firebase PATCH error [${endpoint}]:`, err.message);
    return null;
  }
}

// Refresh Config from Firebase
async function refreshServerConfig() {
  try {
    const appConfig = await fetchFromFirebase("appConfig");
    if (appConfig) {
      if (appConfig.appName) cachedConfig.appName = appConfig.appName;
      if (appConfig.botToken) cachedConfig.botToken = appConfig.botToken;
      if (appConfig.adminTelegramBotToken) cachedConfig.botToken = appConfig.adminTelegramBotToken;
      if (appConfig.botChatId) cachedConfig.chatId = String(appConfig.botChatId);
      if (appConfig.botEnabled !== undefined) cachedConfig.botEnabled = Boolean(appConfig.botEnabled);
      if (appConfig.minWithdraw) cachedConfig.minWithdraw = Number(appConfig.minWithdraw);
      if (appConfig.gmailRates) cachedConfig.gmailRates = { ...cachedConfig.gmailRates, ...appConfig.gmailRates };
      if (appConfig.fbRates) cachedConfig.fbRates = { ...cachedConfig.fbRates, ...appConfig.fbRates };
      if (appConfig.paymentGateways) cachedConfig.paymentGateways = { ...cachedConfig.paymentGateways, ...appConfig.paymentGateways };
      if (appConfig.supportLink) cachedConfig.supportLink = appConfig.supportLink;
    }

    const groupsData = await fetchFromFirebase("groups");
    if (groupsData) {
      cachedConfig.groups = Array.isArray(groupsData) ? groupsData : Object.values(groupsData);
    }

    lastConfigFetchTime = Date.now();
  } catch (e) {
    console.error("Config refresh error:", e.message);
  }
}

// Ensure User Exists in Firebase
async function getOrCreateUser(userId, userDetails = {}) {
  const uid = String(userId);
  try {
    let user = await fetchFromFirebase(`users/${uid}`);
    if (!user) {
      user = {
        id: uid,
        userId: uid,
        name: `${userDetails.first_name || ''} ${userDetails.last_name || ''}`.trim() || 'টেলিগ্রাম মেম্বার',
        username: userDetails.username ? `@${userDetails.username}` : '',
        balance: 0,
        totalEarned: 0,
        totalWithdrawn: 0,
        referralCount: 0,
        createdAt: Date.now(),
        updatedAt: Date.now()
      };
      await putToFirebase(`users/${uid}`, user);
    } else {
      // update username if changed
      if (userDetails.username && user.username !== `@${userDetails.username}`) {
        user.username = `@${userDetails.username}`;
        await patchToFirebase(`users/${uid}`, { username: user.username, updatedAt: Date.now() });
      }
    }
    return user;
  } catch (err) {
    console.error("getOrCreateUser error:", err);
    return { id: uid, userId: uid, balance: 0, referralCount: 0 };
  }
}

// Adjust user balance in Firebase
async function adjustUserBalance(userId, deltaAmount) {
  const uid = String(userId);
  const amt = Number(deltaAmount) || 0;
  if (amt === 0) return 0;
  try {
    const user = await getOrCreateUser(uid);
    const newBal = Math.max(0, (Number(user.balance) || 0) + amt);
    const updates = { balance: newBal, updatedAt: Date.now() };
    if (amt > 0) {
      updates.totalEarned = (Number(user.totalEarned) || 0) + amt;
    } else {
      updates.totalWithdrawn = (Number(user.totalWithdrawn) || 0) + Math.abs(amt);
    }
    await patchToFirebase(`users/${uid}`, updates);
    return newBal;
  } catch (err) {
    console.error("adjustUserBalance error:", err);
    return 0;
  }
}

// =========================================================================
// TELEGRAM API HELPERS
// =========================================================================

async function callTelegramApi(token, method, payload) {
  if (!token) return null;
  try {
    const res = await fetch(`https://api.telegram.org/bot${token}/${method}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    return await res.json();
  } catch (err) {
    console.error(`Telegram API error [${method}]:`, err.message);
    return null;
  }
}

async function sendTelegramMessage(token, chatId, text, replyMarkup = null) {
  return await callTelegramApi(token, 'sendMessage', {
    chat_id: chatId,
    text: text,
    parse_mode: 'HTML',
    reply_markup: replyMarkup || undefined
  });
}

async function answerCallbackQuery(token, callbackQueryId, text = '') {
  return await callTelegramApi(token, 'answerCallbackQuery', {
    callback_query_id: callbackQueryId,
    text: text
  });
}

function getAppLiveUrl(req) {
  const host = (req && req.headers && (req.headers['x-forwarded-host'] || req.headers['host'])) || 'ais-dev-x7vbvluvmvrtgq6lewbh2w-475005245439.asia-southeast1.run.app';
  return `https://${host}`;
}

// =========================================================================
// TELEGRAM BOT COMMAND & INTERACTION HANDLER
// =========================================================================

async function handleTelegramUpdate(update, token, appUrl) {
  if (!token) return;

  // Refresh config periodically
  if (Date.now() - lastConfigFetchTime > 15000) {
    await refreshServerConfig();
  }

  const isBotEnabled = cachedConfig.botEnabled !== false;

  // 1. Process Messages (Text, Commands)
  if (update.message) {
    const msg = update.message;
    const chatId = msg.chat?.id;
    const senderId = String(msg.from?.id || chatId);
    const senderName = `${msg.from?.first_name || ''} ${msg.from?.last_name || ''}`.trim() || 'মেম্বার';
    const senderUsername = msg.from?.username ? `@${msg.from.username}` : '';
    const text = (msg.text || '').trim();
    const lower = text.toLowerCase();

    if (!chatId) return;

    const isAdmin = (
      senderId === cachedConfig.chatId ||
      senderId === DEFAULT_ADMIN_CHAT_ID ||
      (senderUsername && senderUsername.toLowerCase().replace('@','') === DEFAULT_ADMIN_USERNAME)
    );

    // If bot is disabled and user is not admin
    if (!isBotEnabled && !isAdmin) {
      const maintenanceMsg = `⏸️ <b>${cachedConfig.appName} বট সাময়িকভাবে বন্ধ আছে</b>\n\nসম্মানিত গ্রাহক, সিস্টেম আপগ্রেডের কাজ চলছে। অতি দ্রুত বট আবার চালু করা হবে।\n\nজরুরি প্রয়োজনে এডমিন ইনবক্সে যোগাযোগ করুন: ${cachedConfig.supportLink}`;
      await sendTelegramMessage(token, chatId, maintenanceMsg);
      return;
    }

    // Register / update user in Firebase
    await getOrCreateUser(senderId, msg.from);

    // --- /start ---
    if (lower.startsWith('/start') || lower === 'start' || lower === 'শুরু') {
      // Check referral
      if (text.includes('ref_')) {
        const refOwnerId = text.split('ref_')[1].split(' ')[0].trim();
        if (refOwnerId && refOwnerId !== senderId) {
          try {
            const refOwner = await fetchFromFirebase(`users/${refOwnerId}`);
            if (refOwner) {
              const newRefCount = (Number(refOwner.referralCount) || 0) + 1;
              const bonusEarned = (Number(refOwner.balance) || 0) + 5; // ৳5 per verified referral
              await patchToFirebase(`users/${refOwnerId}`, {
                referralCount: newRefCount,
                balance: bonusEarned,
                updatedAt: Date.now()
              });
              // Notify referral owner
              await sendTelegramMessage(token, refOwnerId, `🎉 <b>নতুন রেফারেল বোনাস!</b>\n\nআপনার রেফারেল লিংকে <b>${senderName}</b> জয়েন করেছেন!\nআপনার ওয়ালেটে ৳৫ যোগ হয়েছে। মোট রেফারেল: ${newRefCount} জন।`);
            }
          } catch(e) {}
        }
      }

      const welcomeText = `👋 আসসালামু আলাইকুম <b>${senderName}</b>!

🔥 <b>${cachedConfig.appName} মার্কেটপ্লেস বটে স্বাগতম!</b>

আপনার বিশ্বস্ত প্রিমিয়াম বট। অ্যাপ ওপেন না থাকলেও এখান থেকেই সরাসরি সবকিছু করতে পারবেন:

🛍️ <b>প্রিমিয়াম গ্রুপ ক্রয়:</b> /groups
💰 <b>ব্যালেন্স চেক:</b> /balance
💳 <b>টাকা ডিপোজিট / রিচার্জ:</b> /deposit
📥 <b>টাকা উত্তোলন / উইথড্র:</b> /withdraw
📧 <b>জিমেইল বিক্রয়:</b> /sell_gmail
👤 <b>ফেসবুক আইডি বিক্রয়:</b> /sell_fb
🎁 <b>মেগা কম্বো অফার:</b> /combo
👥 <b>রেফারেল লিংক ও আয়:</b> /ref
📞 <b>হেল্পলাইন ও সহায়তা:</b> /help

👇 <b>নিচের বোতামগুলো চেপে সরাসরি সেবা গ্রহণ করুন অথবা Mini App ওপেন করুন:</b>`;

      const keyboard = {
        inline_keyboard: [
          [{ text: "📱 ওপেন Mini App (ফুল স্ক্রিন)", web_app: { url: appUrl } }],
          [
            { text: "🛍️ গ্রুপ কিনুন", callback_data: "cmd_groups" },
            { text: "💰 ব্যালেন্স চেক", callback_data: "cmd_balance" }
          ],
          [
            { text: "💳 টাকা ডিপোজিট", callback_data: "cmd_deposit" },
            { text: "📥 টাকা উইথড্র", callback_data: "cmd_withdraw" }
          ],
          [
            { text: "📧 জিমেইল বিক্রি", callback_data: "cmd_sell_gmail" },
            { text: "👤 ফেসবুক বিক্রি", callback_data: "cmd_sell_fb" }
          ],
          [
            { text: "🎁 মেগা কম্বো অফার", callback_data: "cmd_combos" },
            { text: "👥 রেফারেল লিংক", callback_data: "cmd_ref" }
          ],
          [
            { text: "📞 হেল্পলাইন ও সাপোর্ট", url: cachedConfig.supportLink }
          ]
        ]
      };

      if (isAdmin) {
        keyboard.inline_keyboard.push([
          { text: "👑 এডমিন কন্ট্রোল ড্যাশবোর্ড", callback_data: "cmd_admin" }
        ]);
      }

      await sendTelegramMessage(token, chatId, welcomeText, keyboard);
      return;
    }

    // --- /balance ---
    if (lower.startsWith('/balance') || lower.startsWith('/wallet') || lower === 'ব্যালেন্স') {
      const user = await getOrCreateUser(senderId, msg.from);
      const balText = `💰 <b>আপনার ওয়ালেট তথ্য (${senderName}):</b>

💵 বর্তমান ব্যালেন্স: <b>৳${user.balance || 0} BDT</b>
📈 মোট আয়: <b>৳${user.totalEarned || 0} BDT</b>
💸 মোট উত্তোলন: <b>৳${user.totalWithdrawn || 0} BDT</b>
👥 সফল রেফারেল: <b>${user.referralCount || 0} জন</b>

👇 <i>টাকা জমা দিতে বা তুলতে নিচের বাটন ব্যবহার করুন:</i>`;

      const keyboard = {
        inline_keyboard: [
          [
            { text: "💳 টাকা ডিপোজিট", callback_data: "cmd_deposit" },
            { text: "📥 টাকা উত্তোলন", callback_data: "cmd_withdraw" }
          ],
          [
            { text: "📱 ওয়ালেট পেজ ওপেন করুন", web_app: { url: appUrl } }
          ]
        ]
      };
      await sendTelegramMessage(token, chatId, balText, keyboard);
      return;
    }

    // --- /groups ---
    if (lower.startsWith('/groups') || lower.startsWith('/shop') || lower === 'গ্রুপ') {
      const groups = cachedConfig.groups || [];
      let listText = `🛍️ <b>সকল প্রিমিয়াম টেলিগ্রাম ভিআইপি গ্রুপ:</b>\n\n`;
      const groupButtons = [];

      groups.forEach((g, idx) => {
        listText += `${idx + 1}. <b>${g.title}</b>\n   💵 মূল্য: <b>৳${g.price} BDT</b> (<s>৳${g.originalPrice || g.price * 1.5}</s>)\n   👥 মেম্বার: ${g.membersCount || '10,000+'}\n   🏷️ ক্যাটাগরি: ${g.category || 'VIP'}\n\n`;
        groupButtons.push([{
          text: `🛒 কিনুন: ${g.title.slice(0, 16)} (৳${g.price})`,
          callback_data: `buy_group_${g.id}`
        }]);
      });

      listText += `👇 <i>গ্রুপ কিনতে নিচের বাটনে ক্লিক করুন অথবা Mini App ওপেন করুন:</i>`;

      groupButtons.push([
        { text: "📱 Mini App থেকে কিনুন", web_app: { url: appUrl } },
        { text: "💳 আগে ব্যালেন্স রিচার্জ করুন", callback_data: "cmd_deposit" }
      ]);

      await sendTelegramMessage(token, chatId, listText, { inline_keyboard: groupButtons });
      return;
    }

    // --- /deposit ---
    if (lower.startsWith('/deposit') || lower.startsWith('/addmoney') || lower === 'ডিপোজিট') {
      const parts = text.split(/\s+/);
      const pg = cachedConfig.paymentGateways || {};
      const bkashNum = pg.bkash?.number || '01789-567890';
      const nagadNum = pg.nagad?.number || '01812-345678';
      const rocketNum = pg.rocket?.number || '01912-345678';
      const binanceId = pg.binance?.number || '8701368956 (Pay ID)';

      // Check if user submitted full format: /deposit [method] [senderNumber] [amount] [trxId]
      if (parts.length >= 5) {
        const method = parts[1].toUpperCase();
        const senderNum = parts[2];
        const amount = Number(parts[3]);
        const trxId = parts.slice(4).join(' ').trim().toUpperCase();

        if (isNaN(amount) || amount <= 0) {
          await sendTelegramMessage(token, chatId, "❌ টাকার পরিমাণ সঠিক নয়! যেমন: <code>/deposit bkash 01712345678 500 9AB3K56L</code>");
          return;
        }

        const depositId = Date.now();
        const depRecord = {
          id: depositId,
          userId: senderId,
          telegramName: senderName,
          telegramUsername: senderUsername,
          method: method,
          senderNumber: senderNum,
          amount: amount,
          trxId: trxId,
          status: 'PENDING',
          createdAt: Date.now()
        };

        await putToFirebase(`deposits/${depositId}`, depRecord);

        // Alert Admin
        const adminAlert = `🔔 <b>নতুন ডিপোজিট রিকোয়েস্ট জমা হয়েছে!</b>\n\n👤 ইউজার: <b>${senderName}</b> (${senderUsername || senderId})\n💳 মেথড: <b>${method}</b>\n📱 নাম্বার: <code>${senderNum}</code>\n💵 পরিমাণ: <b>৳${amount} BDT</b>\n🔖 TrxID: <code>${trxId}</code>\n\n👇 নিচের বাটনে চাপ দিয়ে এখনই অনুমোদন করতে পারেন:`;
        
        await sendTelegramMessage(token, cachedConfig.chatId, adminAlert, {
          inline_keyboard: [
            [
              { text: `✅ অনুমোদন (৳${amount})`, callback_data: `admin_appr_dep_${depositId}` },
              { text: `❌ বাতিল`, callback_data: `admin_rej_dep_${depositId}` }
            ]
          ]
        });

        const successMsg = `✅ <b>আপনার ডিপোজিট রিকোয়েস্ট জমা হয়েছে!</b>

💳 মেথড: <b>${method}</b>
📱 প্রেরক নাম্বার: <code>${senderNum}</code>
💵 পরিমাণ: <b>৳${amount} BDT</b>
🔖 TrxID: <code>${trxId}</code>
⏳ স্ট্যাটাস: <b>যাচাই চলছে (Pending)</b>

এডমিন ৫ মিনিটের মধ্যে ভেরিফাই করে আপনার ওয়ালেটে টাকা যোগ করে দেবে। ব্যালেন্স চেক করতে লিখুন: /balance`;

        await sendTelegramMessage(token, chatId, successMsg);
        return;
      }

      // Show deposit instructions
      const depGuide = `💳 <b>টাকা ডিপোজিট / ওয়ালেট রিচার্জ নিয়মাবলী:</b>

নিচের যেকোনো একাউন্টে 'Send Money' করুন:
🌸 <b>বিকাশ (Personal):</b> <code>${bkashNum}</code>
🔥 <b>নগদ (Personal):</b> <code>${nagadNum}</code>
🟣 <b>রকেট (Personal):</b> <code>${rocketNum}</code>
🟡 <b>Binance Pay ID:</b> <code>${binanceId}</code>

📌 <b>টাকা পাঠানোর পর জমা দেওয়ার নিয়ম:</b>
এই চ্যাটে নিচের ফরমেটে মেসেজ পাঠান:
<code>/deposit [মেথড] [আপনার নাম্বার] [টাকার পরিমাণ] [TrxID]</code>

<b>উদাহরণ:</b>
<code>/deposit bkash 01712345678 500 9AB3K56L</code>
<code>/deposit nagad 01812345678 1000 8C3X9KL2</code>

👇 <i>অথবা সরাসরি ফর্ম পূরণ করতে Mini App ওপেন করুন:</i>`;

      const keyboard = {
        inline_keyboard: [
          [{ text: "📝 সরাসরি ডিপোজিট ফর্ম ওপেন করুন", web_app: { url: appUrl } }],
          [
            { text: "🌸 বিকাশ দিয়েছি", callback_data: "guide_deposit_bkash" },
            { text: "🔥 নগদ দিয়েছি", callback_data: "guide_deposit_nagad" }
          ]
        ]
      };
      await sendTelegramMessage(token, chatId, depGuide, keyboard);
      return;
    }

    // --- /withdraw ---
    if (lower.startsWith('/withdraw') || lower.startsWith('/cashout') || lower === 'উইথড্র') {
      const parts = text.split(/\s+/);
      const minW = cachedConfig.minWithdraw || 100;

      // Check if user submitted full format: /withdraw [method] [accountNumber] [amount]
      if (parts.length >= 4) {
        const method = parts[1].toUpperCase();
        const accNum = parts[2];
        const amount = Number(parts[3]);

        if (isNaN(amount) || amount < minW) {
          await sendTelegramMessage(token, chatId, `❌ সর্বনিম্ন উইথড্র পরিমাণ <b>৳${minW} BDT</b>!`);
          return;
        }

        const user = await getOrCreateUser(senderId, msg.from);
        const curBal = Number(user.balance) || 0;

        if (curBal < amount) {
          await sendTelegramMessage(token, chatId, `❌ আপনার পর্যাপ্ত ব্যালেন্স নেই!\nবর্তমান ব্যালেন্স: <b>৳${curBal} BDT</b>\nউইথড্র রিকোয়েস্ট: <b>৳${amount} BDT</b>`);
          return;
        }

        // Deduct balance immediately so user cannot double-withdraw
        await adjustUserBalance(senderId, -amount);

        const withdrawId = Date.now();
        const withRecord = {
          id: withdrawId,
          userId: senderId,
          telegramName: senderName,
          telegramUsername: senderUsername,
          method: method,
          accountNumber: accNum,
          amount: amount,
          status: 'PENDING',
          createdAt: Date.now()
        };

        await putToFirebase(`withdrawals/${withdrawId}`, withRecord);

        // Alert Admin
        const adminAlert = `💸 <b>নতুন উইথড্র রিকোয়েস্ট জমা হয়েছে!</b>\n\n👤 ইউজার: <b>${senderName}</b> (${senderUsername || senderId})\n🏦 মেথড: <b>${method}</b>\n📱 নাম্বার: <code>${accNum}</code>\n💵 পরিমাণ: <b>৳${amount} BDT</b>\n\n👇 নিচের বাটনে চাপ দিয়ে অনুমোদন করুন:`;

        await sendTelegramMessage(token, cachedConfig.chatId, adminAlert, {
          inline_keyboard: [
            [
              { text: `✅ উইথড্র অনুমোদন (৳${amount})`, callback_data: `admin_appr_with_${withdrawId}` },
              { text: `❌ বাতিল ও রিফান্ড`, callback_data: `admin_rej_with_${withdrawId}` }
            ]
          ]
        });

        const successMsg = `✅ <b>উইথড্র রিকোয়েস্ট সফলভাবে গৃহীত হয়েছে!</b>

🏦 মেথড: <b>${method}</b>
📱 একাউন্ট নাম্বার: <code>${accNum}</code>
💵 উত্তোলনের পরিমাণ: <b>৳${amount} BDT</b>
💰 অবশিষ্ট ব্যালেন্স: <b>৳${curBal - amount} BDT</b>
⏳ পেমেন্ট সময়: <b>১০-৩০ মিনিটের মধ্যে</b>

এডমিন আপনার নাম্বারে টাকা পাঠিয়ে দিলে সাথে সাথে কনফার্মেশন পাবেন।`;

        await sendTelegramMessage(token, chatId, successMsg);
        return;
      }

      const user = await getOrCreateUser(senderId, msg.from);
      const withGuide = `📥 <b>টাকা উত্তোলন / উইথড্র নির্দেশিকা:</b>

💵 আপনার বর্তমান ব্যালেন্স: <b>৳${user.balance || 0} BDT</b>
⚡ সর্বনিম্ন উত্তোলন: <b>৳${minW} BDT</b>
🏦 সাপোর্ট মেথড: বিকাশ, নগদ, রকেট, Binance

📌 <b>উইথড্র করার নিয়ম:</b>
এই চ্যাটে নিচের ফরমেটে মেসেজ পাঠান:
<code>/withdraw [মেথড] [আপনার নাম্বার] [টাকার পরিমাণ]</code>

<b>উদাহরণ:</b>
<code>/withdraw bkash 01712345678 200</code>
<code>/withdraw nagad 01812345678 500</code>

👇 <i>অথবা Mini App থেকে এক ক্লিকে উইথড্র করুন:</i>`;

      const keyboard = {
        inline_keyboard: [
          [{ text: "🏧 উইথড্র পেজ ওপেন করুন", web_app: { url: appUrl } }]
        ]
      };
      await sendTelegramMessage(token, chatId, withGuide, keyboard);
      return;
    }

    // --- /sell_gmail ---
    if (lower.startsWith('/sell_gmail') || lower.startsWith('/sellgmail') || lower === 'জিমেইল') {
      const parts = text.split(/\s+/);
      const rates = cachedConfig.gmailRates || { new: 15, old: 25, aged: 40 };

      // Format: /sell_gmail [email] [password] [recovery] [type: new/old/aged]
      if (parts.length >= 5) {
        const mail = parts[1].trim();
        const pass = parts[2].trim();
        const recovery = parts[3].trim();
        const typeKey = parts[4].toLowerCase().trim();

        let reward = rates.new || 15;
        let typeName = "নতুন জিমেইল";
        if (typeKey.includes('old') || typeKey.includes('পুরাতন')) {
          reward = rates.old || 25;
          typeName = "পুরাতন জিমেইল (১+ বছর)";
        } else if (typeKey.includes('aged') || typeKey.includes('ভেরিফাইড')) {
          reward = rates.aged || 40;
          typeName = "ভেরিফাইড পুরনো জিমেইল";
        }

        const subId = Date.now();
        const subRecord = {
          id: subId,
          userId: senderId,
          telegramName: senderName,
          telegramUsername: senderUsername,
          category: 'GMAIL',
          email: mail,
          password: pass,
          recovery: recovery,
          type: typeName,
          reward: reward,
          status: 'PENDING',
          createdAt: Date.now()
        };

        await putToFirebase(`submissions/${subId}`, subRecord);

        // Alert Admin
        const adminAlert = `📧 <b>নতুন জিমেইল বিক্রয় রিকোয়েস্ট!</b>\n\n👤 ইউজার: <b>${senderName}</b> (${senderUsername || senderId})\n📧 ইমেইল: <code>${mail}</code>\n🔑 পাসওয়ার্ড: <code>${pass}</code>\n🛡️ রিকভারি: <code>${recovery}</code>\n🏷️ ক্যাটাগরি: <b>${typeName}</b>\n💵 প্রাপ্য টাকা: <b>৳${reward}</b>\n\n👇 অনুমোদন করুন:`;
        
        await sendTelegramMessage(token, cachedConfig.chatId, adminAlert, {
          inline_keyboard: [
            [
              { text: `✅ অনুমোদন (৳${reward})`, callback_data: `admin_appr_sub_${subId}` },
              { text: `❌ বাতিল`, callback_data: `admin_rej_sub_${subId}` }
            ]
          ]
        });

        const successMsg = `✅ <b>আপনার জিমেইল সফলভাবে জমা হয়েছে!</b>

📧 ইমেইল: <code>${mail}</code>
🏷️ ধরণ: <b>${typeName}</b>
💵 অনুমোদন পেলে পাবেন: <b>৳${reward} BDT</b>
⏳ স্ট্যাটাস: <b>যাচাই চলছে (Pending)</b>

এডমিন চেক করে ৫-১৫ মিনিটের মধ্যে আপনার ওয়ালেটে ৳${reward} যোগ করে দেবে।`;

        await sendTelegramMessage(token, chatId, successMsg);
        return;
      }

      const gGuide = `📧 <b>জিমেইল একাউন্ট বিক্রয় রেট ও নিয়মাবলী:</b>

• নতুন জিমেইল: <b>৳${rates.new}</b> প্রতি পিস
• পুরাতন জিমেইল (১+ বছর): <b>৳${rates.old}</b> প্রতি পিস
• পুরনো ভেরিফাইড জিমেইল: <b>৳${rates.aged}</b> প্রতি পিস

📌 <b>জিমেইল সাবমিট করার নিয়ম:</b>
এই চ্যাটে নিচের ফরমেটে মেসেজ পাঠান:
<code>/sell_gmail [ইমেইল] [পাসওয়ার্ড] [রিকভারি ইমেইল] [ধরণ: new / old / aged]</code>

<b>উদাহরণ:</b>
<code>/sell_gmail user123@gmail.com pass@1234 rec@yahoo.com new</code>
<code>/sell_gmail olduser@gmail.com pass@5678 rec@gmail.com old</code>

👇 <i>অথবা Mini App থেকে ফর্ম পূরণ করুন:</i>`;

      const keyboard = {
        inline_keyboard: [
          [{ text: "📝 জিমেইল সাবমিট ফর্ম ওপেন করুন", web_app: { url: appUrl } }]
        ]
      };
      await sendTelegramMessage(token, chatId, gGuide, keyboard);
      return;
    }

    // --- /sell_fb ---
    if (lower.startsWith('/sell_fb') || lower.startsWith('/sellfb') || lower === 'ফেসবুক') {
      const parts = text.split(/\s+/);
      const rates = cachedConfig.fbRates || { new: 20, old: 60, aged: 110 };

      // Format: /sell_fb [uid_or_mail] [password] [2fa_key] [year]
      if (parts.length >= 5) {
        const uid = parts[1].trim();
        const pass = parts[2].trim();
        const twoFa = parts[3].trim();
        const year = parts[4].trim();

        let reward = rates.new || 20;
        let typeName = "নতুন ফেসবুক আইডি";
        const yNum = parseInt(year);
        if (yNum && yNum <= 2022 && yNum >= 2019) {
          reward = rates.old || 60;
          typeName = `পুরাতন আইডি (${year})`;
        } else if (yNum && yNum < 2019) {
          reward = rates.aged || 110;
          typeName = `পুরনো রিয়েল আইডি (${year})`;
        }

        const subId = Date.now();
        const subRecord = {
          id: subId,
          userId: senderId,
          telegramName: senderName,
          telegramUsername: senderUsername,
          category: 'FACEBOOK',
          uid: uid,
          password: pass,
          twoFa: twoFa,
          year: year,
          type: typeName,
          reward: reward,
          status: 'PENDING',
          createdAt: Date.now()
        };

        await putToFirebase(`submissions/${subId}`, subRecord);

        // Alert Admin
        const adminAlert = `👤 <b>নতুন ফেসবুক আইডি বিক্রয় রিকোয়েস্ট!</b>\n\n👤 ইউজার: <b>${senderName}</b> (${senderUsername || senderId})\n🆔 UID/ইমেইল: <code>${uid}</code>\n🔑 পাসওয়ার্ড: <code>${pass}</code>\n🔐 2FA: <code>${twoFa}</code>\n📅 তৈরি: <b>${year}</b> (${typeName})\n💵 প্রাপ্য টাকা: <b>৳${reward}</b>\n\n👇 অনুমোদন করুন:`;

        await sendTelegramMessage(token, cachedConfig.chatId, adminAlert, {
          inline_keyboard: [
            [
              { text: `✅ অনুমোদন (৳${reward})`, callback_data: `admin_appr_sub_${subId}` },
              { text: `❌ বাতিল`, callback_data: `admin_rej_sub_${subId}` }
            ]
          ]
        });

        const successMsg = `✅ <b>আপনার ফেসবুক আইডি সফলভাবে জমা হয়েছে!</b>

🆔 UID/লগইন: <code>${uid}</code>
📅 সাল: <b>${year}</b> (${typeName})
💵 অনুমোদন পেলে পাবেন: <b>৳${reward} BDT</b>
⏳ স্ট্যাটাস: <b>যাচাই চলছে (Pending)</b>

এডমিন চেক করে ৫-১৫ মিনিটের মধ্যে আপনার ওয়ালেটে ৳${reward} যোগ করে দেবে।`;

        await sendTelegramMessage(token, chatId, successMsg);
        return;
      }

      const fbGuide = `👤 <b>ফেসবুক আইডি বিক্রয় রেট ও নিয়মাবলী:</b>

• নতুন আইডি: <b>৳${rates.new}</b>
• পুরাতন আইডি (২০১৯-২০২২): <b>৳${rates.old}</b>
• পুরাতন রিয়েল আইডি (২০১৮ এর আগে): <b>৳${rates.aged}</b>

📌 <b>ফেসবুক আইডি সাবমিট করার নিয়ম:</b>
এই চ্যাটে নিচের ফরমেটে মেসেজ পাঠান:
<code>/sell_fb [UID বা ইমেইল] [পাসওয়ার্ড] [2FA সিকিউরিটি কোড] [সাল]</code>

<b>উদাহরণ:</b>
<code>/sell_fb 100089283726 Pass@1234 4XAB6Y7Z2022 2021</code>

👇 <i>অথবা Mini App থেকে সাবমিট করুন:</i>`;

      const keyboard = {
        inline_keyboard: [
          [{ text: "👤 ফেসবুক আইডি সাবমিট ফর্ম", web_app: { url: appUrl } }]
        ]
      };
      await sendTelegramMessage(token, chatId, fbGuide, keyboard);
      return;
    }

    // --- /combo ---
    if (lower.startsWith('/combo') || lower === 'কম্বো') {
      const combos = cachedConfig.comboBundles || [
        { id: 'combo_1', title: '🔥 ৩-ইন-১ মেগা ভিআইপি কম্বো', price: 650, originalPrice: 1100, desc: '৩টি সেরা টেলিগ্রাম ভিআইপি গ্রুপ একসাথে পেয়ে যান' }
      ];

      let comboText = `🎁 <b>মেগা কম্বো বান্ডেল অফার:</b>\n\n`;
      const buttons = [];

      combos.forEach(c => {
        comboText += `• <b>${c.title}</b>\n  💵 অফার মূল্য: <b>৳${c.price}</b> (<s>৳${c.originalPrice || 1000}</s>)\n  ✨ ${c.desc || c.subtitle || 'মেগা ডিসকাউন্ট প্যাকেজ'}\n\n`;
        buttons.push([{
          text: `🎁 কম্বো কিনুন (৳${c.price})`,
          callback_data: `buy_combo_${c.id}`
        }]);
      });

      buttons.push([{ text: "📱 Mini App থেকে কিনুন", web_app: { url: appUrl } }]);

      await sendTelegramMessage(token, chatId, comboText, { inline_keyboard: buttons });
      return;
    }

    // --- /ref ---
    if (lower.startsWith('/ref') || lower === 'রেফার') {
      const user = await getOrCreateUser(senderId, msg.from);
      const botUserRes = await callTelegramApi(token, 'getMe', {});
      const botUName = botUserRes?.result?.username || 'PREMIUM_GROUP_BUY_BOT';
      const refLink = `https://t.me/${botUName}?start=ref_${senderId}`;

      const refText = `👥 <b>রেফারেল বোনাস ও আনলিমিটেড ইনকাম:</b>

আপনার ইউনিক রেফারেল লিংক:
<code>${refLink}</code>

📌 <b>রেফারেল অফার:</b>
• প্রতি ভেরিফাইড রেফারে পাবেন <b>৳৫ নগদ বোনাস</b>!
• ২০ জন বন্ধুকে রেফার করলে যেকোনো প্রিমিয়াম গ্রুপ সম্পূর্ণ <b>ফ্রি</b>!

📊 আপনার মোট রেফারেল: <b>${user.referralCount || 0} জন</b>`;

      const keyboard = {
        inline_keyboard: [
          [{ text: "📤 বন্ধুদের টেলিগ্রামে শেয়ার করুন", url: `https://t.me/share/url?url=${encodeURIComponent(refLink)}&text=${encodeURIComponent('🔥 সেরা টেলিগ্রাম ভিআইপি গ্রুপ ও অনলাইনে আয়ের সেরা প্লাটফর্ম!')}` }],
          [{ text: "📱 রেফারেল ড্যাশবোর্ড", web_app: { url: appUrl } }]
        ]
      };
      await sendTelegramMessage(token, chatId, refText, keyboard);
      return;
    }

    // --- /help ---
    if (lower.startsWith('/help') || lower === 'সহায়তা' || lower === 'help') {
      const helpMsg = `📞 <b>২৪/৭ কাস্টমার সাপোর্ট ও হেল্পলাইন:</b>

যেকোনো সমস্যা, গ্রুপ এক্সেস, পেমেন্ট অথবা বিশেষ অর্ডারের জন্য আমাদের অফিশিয়াল এডমিনের সাথে যোগাযোগ করুন।

👤 <b>সুপার এডমিন:</b> সাদ্দাম ভাই (@ItsSaddam9)
💬 <b>সরাসরি সাপোর্ট:</b> ${cachedConfig.supportLink}
📢 <b>অফিশিয়াল চ্যানেল:</b> ${cachedConfig.appName} Official

কমান্ড তালিকা দেখতে লিখুন: /start`;

      const keyboard = {
        inline_keyboard: [
          [{ text: "💬 এডমিন ইনবক্সে কথা বলুন", url: cachedConfig.supportLink }],
          [{ text: "📱 Mini App ওপেন করুন", web_app: { url: appUrl } }]
        ]
      };
      await sendTelegramMessage(token, chatId, helpMsg, keyboard);
      return;
    }

    // --- /admin ---
    if (lower.startsWith('/admin') || lower === 'admin') {
      if (!isAdmin) {
        await sendTelegramMessage(token, chatId, "⚠️ আপনি এই বটের এডমিন নন। শুধুমাত্র অনুমোদিত এডমিন এই কমান্ড ব্যবহার করতে পারবেন।");
        return;
      }

      const pOrders = await fetchFromFirebase("orders") || {};
      const pDeposits = await fetchFromFirebase("deposits") || {};
      const pSubs = await fetchFromFirebase("submissions") || {};
      const pWiths = await fetchFromFirebase("withdrawals") || {};

      const pendingOrdersCount = Object.values(pOrders).filter(o => o.status === 'PENDING').length;
      const pendingDepCount = Object.values(pDeposits).filter(d => d.status === 'PENDING').length;
      const pendingSubCount = Object.values(pSubs).filter(s => s.status === 'PENDING').length;
      const pendingWithCount = Object.values(pWiths).filter(w => w.status === 'PENDING').length;

      const adminDashboard = `👑 <b>এডমিন লাইভ বট কন্ট্রোল ড্যাশবোর্ড:</b>

🤖 বট স্ট্যাটাস: <b>${isBotEnabled ? '🟢 সক্রিয় (Active)' : '🔴 বন্ধ (Paused)'}</b>
📦 পেন্ডিং অর্ডার: <b>${pendingOrdersCount}টি</b>
💳 পেন্ডিং ডিপোজিট: <b>${pendingDepCount}টি</b>
📧 পেন্ডিং একাউন্ট: <b>${pendingSubCount}টি</b>
💸 পেন্ডিং উইথড্র: <b>${pendingWithCount}টি</b>

👇 <i>১-ক্লিকে একশন নিতে নিচের অপশন বেছে নিন:</i>`;

      const keyboard = {
        inline_keyboard: [
          [
            { text: isBotEnabled ? "🔴 বট সাময়িকভাবে বন্ধ করুন" : "🟢 বট চালু করুন", callback_data: isBotEnabled ? "admin_toggle_bot_off" : "admin_toggle_bot_on" }
          ],
          [
            { text: "💳 পেন্ডিং ডিপোজিট তালিকা", callback_data: "admin_list_deposits" },
            { text: "💸 পেন্ডিং উইথড্র তালিকা", callback_data: "admin_list_withdraws" }
          ],
          [
            { text: "📧 পেন্ডিং সাবমিশন", callback_data: "admin_list_subs" },
            { text: "📦 পেন্ডিং অর্ডার", callback_data: "admin_list_orders" }
          ],
          [
            { text: "👑 সম্পূর্ণ এডমিন প্যানেল (Mini App)", web_app: { url: appUrl } }
          ]
        ]
      };
      await sendTelegramMessage(token, chatId, adminDashboard, keyboard);
      return;
    }
  }

  // 2. Process Callback Queries (Inline Button Clicks)
  if (update.callback_query) {
    const cb = update.callback_query;
    const cbId = cb.id;
    const chatId = cb.message?.chat?.id;
    const senderId = String(cb.from?.id || chatId);
    const senderName = `${cb.from?.first_name || ''} ${cb.from?.last_name || ''}`.trim() || 'মেম্বার';
    const data = cb.data;

    // Answer callback
    answerCallbackQuery(token, cbId).catch(console.warn);

    if (!chatId) return;

    const isAdmin = (
      senderId === cachedConfig.chatId ||
      senderId === DEFAULT_ADMIN_CHAT_ID ||
      (cb.from?.username && cb.from.username.toLowerCase() === DEFAULT_ADMIN_USERNAME)
    );

    // Standard User Command Callbacks
    if (data === 'cmd_groups') {
      const groups = cachedConfig.groups || [];
      let listText = `🛍️ <b>সকল প্রিমিয়াম টেলিগ্রাম ভিআইপি গ্রুপ:</b>\n\n`;
      const groupButtons = [];
      groups.forEach((g, idx) => {
        listText += `${idx + 1}. <b>${g.title}</b> — <b>৳${g.price} BDT</b>\n`;
        groupButtons.push([{
          text: `🛒 কিনুন: ${g.title.slice(0, 16)} (৳${g.price})`,
          callback_data: `buy_group_${g.id}`
        }]);
      });
      groupButtons.push([{ text: "📱 Mini App থেকে কিনুন", web_app: { url: appUrl } }]);
      await sendTelegramMessage(token, chatId, listText, { inline_keyboard: groupButtons });
      return;
    }

    if (data === 'cmd_balance') {
      const user = await getOrCreateUser(senderId, cb.from);
      const balText = `💰 <b>আপনার ওয়ালেট তথ্য (${senderName}):</b>\n\n💵 বর্তমান ব্যালেন্স: <b>৳${user.balance || 0} BDT</b>\n📈 মোট আয়: <b>৳${user.totalEarned || 0} BDT</b>\n💸 মোট উত্তোলন: <b>৳${user.totalWithdrawn || 0} BDT</b>`;
      await sendTelegramMessage(token, chatId, balText, {
        inline_keyboard: [
          [{ text: "💳 টাকা ডিপোজিট", callback_data: "cmd_deposit" }, { text: "📥 টাকা উইথড্র", callback_data: "cmd_withdraw" }],
          [{ text: "📱 ওয়ালেট পেজ ওপেন করুন", web_app: { url: appUrl } }]
        ]
      });
      return;
    }

    if (data === 'cmd_deposit') {
      const pg = cachedConfig.paymentGateways || {};
      const depMsg = `💳 <b>টাকা ডিপোজিট / ওয়ালেট রিচার্জ:</b>\n\n🌸 বিকাশ: <code>${pg.bkash?.number || '01789-567890'}</code>\n🔥 নগদ: <code>${pg.nagad?.number || '01812-345678'}</code>\n🟣 রকেট: <code>${pg.rocket?.number || '01912-345678'}</code>\n🟡 Binance: <code>${pg.binance?.number || '8701368956'}</code>\n\nটাকা পাঠিয়ে এই চ্যাটে লিখুন:\n<code>/deposit bkash [আপনার_নাম্বার] [পরিমাণ] [TrxID]</code>`;
      await sendTelegramMessage(token, chatId, depMsg, {
        inline_keyboard: [[{ text: "📝 সরাসরি ডিপোজিট ফর্ম", web_app: { url: appUrl } }]]
      });
      return;
    }

    if (data === 'cmd_withdraw') {
      const user = await getOrCreateUser(senderId, cb.from);
      const withMsg = `📥 <b>টাকা উত্তোলন / উইথড্র:</b>\n\n💵 আপনার ব্যালেন্স: <b>৳${user.balance || 0} BDT</b>\n⚡ সর্বনিম্ন উত্তোলন: <b>৳${cachedConfig.minWithdraw || 100} BDT</b>\n\nউইথড্র করতে এই চ্যাটে লিখুন:\n<code>/withdraw bkash [নাম্বার] [পরিমাণ]</code>\n<code>/withdraw nagad [নাম্বার] [পরিমাণ]</code>`;
      await sendTelegramMessage(token, chatId, withMsg, {
        inline_keyboard: [[{ text: "🏧 উইথড্র পেজ ওপেন করুন", web_app: { url: appUrl } }]]
      });
      return;
    }

    if (data === 'cmd_sell_gmail') {
      const rates = cachedConfig.gmailRates || { new: 15, old: 25, aged: 40 };
      const gMsg = `📧 <b>জিমেইল বিক্রি রেট:</b>\n• নতুন: ৳${rates.new}\n• পুরাতন: ৳${rates.old}\n• ভেরিফাইড: ৳${rates.aged}\n\nসাবমিট করতে চ্যাটে লিখুন:\n<code>/sell_gmail [ইমেইল] [পাসওয়ার্ড] [রিকভারি] [new/old/aged]</code>`;
      await sendTelegramMessage(token, chatId, gMsg, {
        inline_keyboard: [[{ text: "📝 জিমেইল সাবমিট ফর্ম", web_app: { url: appUrl } }]]
      });
      return;
    }

    if (data === 'cmd_sell_fb') {
      const rates = cachedConfig.fbRates || { new: 20, old: 60, aged: 110 };
      const fbMsg = `👤 <b>ফেসবুক আইডি বিক্রি রেট:</b>\n• নতুন: ৳${rates.new}\n• পুরাতন: ৳${rates.old}\n• রিয়েল ফ্রেন্ডস: ৳${rates.aged}\n\nসাবমিট করতে চ্যাটে লিখুন:\n<code>/sell_fb [UID_বা_ইমেইল] [পাসওয়ার্ড] [2FA] [সাল]</code>`;
      await sendTelegramMessage(token, chatId, fbMsg, {
        inline_keyboard: [[{ text: "👤 ফেসবুক আইডি সাবমিট ফর্ম", web_app: { url: appUrl } }]]
      });
      return;
    }

    if (data === 'cmd_combos') {
      const combos = cachedConfig.comboBundles || [{ id: 'combo_1', title: '🔥 ৩-ইন-১ মেগা ভিআইপি কম্বো', price: 650 }];
      let cText = `🎁 <b>মেগা কম্বো বান্ডেল:</b>\n\n`;
      combos.forEach(c => {
        cText += `• <b>${c.title}</b> — <b>৳${c.price}</b>\n`;
      });
      await sendTelegramMessage(token, chatId, cText, {
        inline_keyboard: [[{ text: "🎁 কম্বো কিনুন (Mini App)", web_app: { url: appUrl } }]]
      });
      return;
    }

    if (data === 'cmd_ref') {
      const botUserRes = await callTelegramApi(token, 'getMe', {});
      const botUName = botUserRes?.result?.username || 'PREMIUM_GROUP_BUY_BOT';
      const refLink = `https://t.me/${botUName}?start=ref_${senderId}`;
      const refMsg = `👥 <b>রেফারেল লিংক:</b>\n<code>${refLink}</code>\n\n২০ জন বন্ধুকে রেফার করলে যেকোনো প্রিমিয়াম গ্রুপ ফ্রি!`;
      await sendTelegramMessage(token, chatId, refMsg, {
        inline_keyboard: [
          [{ text: "📤 শেয়ার করুন", url: `https://t.me/share/url?url=${encodeURIComponent(refLink)}` }]
        ]
      });
      return;
    }

    // Buying Group with Balance
    if (data.startsWith('buy_group_')) {
      const groupId = data.replace('buy_group_', '');
      const group = (cachedConfig.groups || []).find(g => String(g.id) === String(groupId));

      if (!group) {
        await sendTelegramMessage(token, chatId, "❌ দুঃখিত, গ্রুপটি খুঁজে পাওয়া যায়নি!");
        return;
      }

      const user = await getOrCreateUser(senderId, cb.from);
      const price = Number(group.price) || 0;
      const curBal = Number(user.balance) || 0;

      if (curBal < price) {
        const needed = price - curBal;
        const shortBalMsg = `⚠️ <b>পর্যাপ্ত ব্যালেন্স নেই!</b>\n\nগ্রুপ: <b>${group.title}</b>\nমূল্য: <b>৳${price} BDT</b>\nআপনার ব্যালেন্স: <b>৳${curBal} BDT</b>\n\nআপনার আরও <b>৳${needed} BDT</b> ডিপোজিট প্রয়োজন।`;
        await sendTelegramMessage(token, chatId, shortBalMsg, {
          inline_keyboard: [
            [{ text: "💳 টাকা ডিপোজিট করুন", callback_data: "cmd_deposit" }],
            [{ text: "📱 Mini App থেকে কিনুন", web_app: { url: appUrl } }]
          ]
        });
        return;
      }

      // Deduct balance
      await adjustUserBalance(senderId, -price);

      // Create order
      const orderId = Date.now();
      const orderRecord = {
        id: orderId,
        userId: senderId,
        telegramName: senderName,
        telegramUsername: cb.from?.username ? `@${cb.from.username}` : '',
        groupTitle: group.title,
        groupId: group.id,
        price: price,
        paymentMethod: 'WALLET_BALANCE',
        status: 'APPROVED',
        deliveryLink: group.link || group.inviteLink || "https://t.me/+slk_vip_private_group",
        createdAt: Date.now()
      };

      await putToFirebase(`orders/${orderId}`, orderRecord);

      const purchaseSuccessMsg = `🎉 <b>অভিনন্দন! আপনার গ্রুপ ক্রয় সম্পন্ন হয়েছে!</b>

🛍️ গ্রুপ: <b>${group.title}</b>
💵 পরিশোধিত: <b>৳${price} BDT</b>
💰 অবশিষ্ট ব্যালেন্স: <b>৳${curBal - price} BDT</b>

🔗 <b>আপনার প্রাইভেট ভিআইপি গ্রুপ জয়েন লিংক:</b>
👉 ${orderRecord.deliveryLink}

<i>(লিংকটি সুরক্ষিত রাখুন এবং এখনই জয়েন করে নিন)</i>`;

      await sendTelegramMessage(token, chatId, purchaseSuccessMsg);
      return;
    }

    // Admin Interactive Actions
    if (isAdmin) {
      if (data === 'cmd_admin') {
        const isBotActive = cachedConfig.botEnabled !== false;
        await sendTelegramMessage(token, chatId, `👑 <b>এডমিন বট কন্ট্রোল মেনু:</b>\nস্ট্যাটাস: ${isBotActive ? '🟢 সক্রিয়' : '🔴 বন্ধ'}`, {
          inline_keyboard: [
            [{ text: isBotActive ? "🔴 বট বন্ধ করুন" : "🟢 বট চালু করুন", callback_data: isBotActive ? "admin_toggle_bot_off" : "admin_toggle_bot_on" }],
            [{ text: "💳 ডিপোজিট রিকোয়েস্ট", callback_data: "admin_list_deposits" }],
            [{ text: "💸 উইথড্র রিকোয়েস্ট", callback_data: "admin_list_withdraws" }]
          ]
        });
        return;
      }

      // Toggle Bot ON/OFF
      if (data === 'admin_toggle_bot_on') {
        cachedConfig.botEnabled = true;
        await patchToFirebase("appConfig", { botEnabled: true });
        await sendTelegramMessage(token, chatId, "🟢 <b>বট সফলভাবে চালু করা হয়েছে!</b>\nএখন মেম্বাররা বটের সকল কমান্ড ব্যবহার করতে পারবে।");
        return;
      }

      if (data === 'admin_toggle_bot_off') {
        cachedConfig.botEnabled = false;
        await patchToFirebase("appConfig", { botEnabled: false });
        await sendTelegramMessage(token, chatId, "🔴 <b>বট সাময়িকভাবে বন্ধ করা হয়েছে!</b>\nমেম্বাররা মেসেজ দিলে মেইনটেন্যান্স নোটিশ দেখতে পাবে।");
        return;
      }

      // Approve Deposit
      if (data.startsWith('admin_appr_dep_')) {
        const depId = data.replace('admin_appr_dep_', '');
        const dep = await fetchFromFirebase(`deposits/${depId}`);
        if (dep && dep.status === 'PENDING') {
          await patchToFirebase(`deposits/${depId}`, { status: 'APPROVED', approvedAt: Date.now() });
          await adjustUserBalance(dep.userId, dep.amount);

          await sendTelegramMessage(token, chatId, `✅ ডিপোজিট #${depId} সফলভাবে অনুমোদিত হয়েছে এবং ইউজারের ওয়ালেটে ৳${dep.amount} যোগ করা হয়েছে।`);
          // Notify User
          await sendTelegramMessage(token, dep.userId, `🎉 <b>ডিপোজিট সফল হয়েছে!</b>\n\nআপনার <b>৳${dep.amount} BDT</b> ডিপোজিট এডমিন কর্তৃক অনুমোদিত হয়েছে। টাকা আপনার ওয়ালেটে যোগ হয়েছে। ব্যালেন্স দেখতে লিখুন: /balance`);
        } else {
          await sendTelegramMessage(token, chatId, "⚠️ এই ডিপোজিটটি ইতিমধ্যে প্রসেস করা হয়েছে।");
        }
        return;
      }

      // Reject Deposit
      if (data.startsWith('admin_rej_dep_')) {
        const depId = data.replace('admin_rej_dep_', '');
        await patchToFirebase(`deposits/${depId}`, { status: 'REJECTED', rejectedAt: Date.now() });
        await sendTelegramMessage(token, chatId, `❌ ডিপোজিট #${depId} বাতিল করা হয়েছে।`);
        return;
      }

      // Approve Withdrawal
      if (data.startsWith('admin_appr_with_')) {
        const withId = data.replace('admin_appr_with_', '');
        const withRecord = await fetchFromFirebase(`withdrawals/${withId}`);
        if (withRecord && withRecord.status === 'PENDING') {
          await patchToFirebase(`withdrawals/${withId}`, { status: 'APPROVED', approvedAt: Date.now() });
          await sendTelegramMessage(token, chatId, `✅ উইথড্র #${withId} সফলভাবে সম্পন্ন হয়েছে।`);
          // Notify User
          await sendTelegramMessage(token, withRecord.userId, `🎉 <b>উইথড্র পেমেন্ট সফল হয়েছে!</b>\n\nআপনার <b>৳${withRecord.amount} BDT</b> এর উত্তোলনের টাকা ${withRecord.method} নাম্বারে (${withRecord.accountNumber}) পাঠানো হয়েছে। ধন্যবাদ!`);
        } else {
          await sendTelegramMessage(token, chatId, "⚠️ এই উইথড্র ইতিমধ্যে প্রসেস করা হয়েছে।");
        }
        return;
      }

      // Reject Withdrawal & Refund
      if (data.startsWith('admin_rej_with_')) {
        const withId = data.replace('admin_rej_with_', '');
        const withRecord = await fetchFromFirebase(`withdrawals/${withId}`);
        if (withRecord && withRecord.status === 'PENDING') {
          await patchToFirebase(`withdrawals/${withId}`, { status: 'REJECTED', rejectedAt: Date.now() });
          // Refund balance to user
          await adjustUserBalance(withRecord.userId, withRecord.amount);
          await sendTelegramMessage(token, chatId, `❌ উইথড্র #${withId} বাতিল করা হয়েছে এবং ইউজারের ব্যালেন্স রিফান্ড করা হয়েছে।`);
          // Notify User
          await sendTelegramMessage(token, withRecord.userId, `⚠️ <b>উইথড্র বাতিল ও রিফান্ড!</b>\n\nআপনার উইথড্র রিকোয়েস্ট বাতিল করা হয়েছে এবং <b>৳${withRecord.amount} BDT</b> আপনার ওয়ালেটে ফেরত দেওয়া হয়েছে।`);
        }
        return;
      }

      // Approve Submission (Gmail/Facebook)
      if (data.startsWith('admin_appr_sub_')) {
        const subId = data.replace('admin_appr_sub_', '');
        const sub = await fetchFromFirebase(`submissions/${subId}`);
        if (sub && sub.status === 'PENDING') {
          await patchToFirebase(`submissions/${subId}`, { status: 'APPROVED', approvedAt: Date.now() });
          await adjustUserBalance(sub.userId, sub.reward);
          await sendTelegramMessage(token, chatId, `✅ সাবমিশন #${subId} অনুমোদিত হয়েছে এবং ইউজারের ওয়ালেটে ৳${sub.reward} যোগ হয়েছে।`);
          // Notify User
          await sendTelegramMessage(token, sub.userId, `🎉 <b>একাউন্ট বিক্রয় সফল হয়েছে!</b>\n\nআপনার জমা দেওয়া ${sub.category} একাউন্ট অনুমোদিত হয়েছে এবং <b>৳${sub.reward} BDT</b> আপনার ওয়ালেটে জমা হয়েছে!`);
        }
        return;
      }
    }
  }
}

// =========================================================================
// BACKGROUND SERVER-SIDE POLLER (Fallback when Webhook is not configured)
// =========================================================================

async function startServerSidePoller() {
  if (serverPollingActive) return;
  serverPollingActive = true;
  console.log("🚀 Starting Server-side Telegram Bot Poller...");

  async function pollLoop() {
    if (!serverPollingActive) return;
    try {
      const token = cachedConfig.botToken;
      if (token) {
        const res = await fetch(`https://api.telegram.org/bot${token}/getUpdates?offset=${serverPollingOffset}&timeout=15`);
        const data = await res.json();
        if (data.ok && Array.isArray(data.result)) {
          for (const update of data.result) {
            serverPollingOffset = update.update_id + 1;
            await handleTelegramUpdate(update, token, `https://ais-dev-x7vbvluvmvrtgq6lewbh2w-475005245439.asia-southeast1.run.app`);
          }
        }
      }
    } catch (err) {
      // network delay / timeout
    }
    setTimeout(pollLoop, 1500);
  }

  pollLoop();
}

// =========================================================================
// HTTP SERVER (Port 3000)
// =========================================================================

const server = http.createServer(async (req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;

  // Set standard CORS & Frame headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    res.writeHead(200);
    res.end();
    return;
  }

  // --- 1. Telegram Webhook Endpoint ---
  if (pathname === '/api/telegram-webhook' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', async () => {
      try {
        const update = JSON.parse(body);
        const token = cachedConfig.botToken;
        const appUrl = getAppLiveUrl(req);
        // Process update asynchronously without delaying response
        handleTelegramUpdate(update, token, appUrl).catch(console.error);
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ ok: true }));
      } catch (err) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ ok: false, error: err.message }));
      }
    });
    return;
  }

  // --- 2. Set Webhook API Endpoint ---
  if (pathname === '/api/set-webhook' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', async () => {
      try {
        const parsed = JSON.parse(body || '{}');
        const token = parsed.token || cachedConfig.botToken;
        const appUrl = getAppLiveUrl(req);
        const webhookUrl = `${appUrl}/api/telegram-webhook`;

        if (!token) {
          res.writeHead(400, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ ok: false, error: 'Token missing' }));
          return;
        }

        const tgRes = await callTelegramApi(token, 'setWebhook', {
          url: webhookUrl,
          drop_pending_updates: false
        });

        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ ok: true, telegramResponse: tgRes, webhookUrl: webhookUrl }));
      } catch (e) {
        res.writeHead(500, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ ok: false, error: e.message }));
      }
    });
    return;
  }

  // --- 3. Toggle Bot Status API Endpoint ---
  if (pathname === '/api/toggle-bot' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', async () => {
      try {
        const parsed = JSON.parse(body || '{}');
        const enabled = Boolean(parsed.enabled);
        cachedConfig.botEnabled = enabled;
        await patchToFirebase("appConfig", { botEnabled: enabled });
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ ok: true, botEnabled: enabled }));
      } catch (e) {
        res.writeHead(500, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ ok: false, error: e.message }));
      }
    });
    return;
  }

  // --- 4. Server & Bot Status API Endpoint ---
  if (pathname === '/api/status') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      ok: true,
      appName: cachedConfig.appName,
      botConfigured: Boolean(cachedConfig.botToken),
      botEnabled: cachedConfig.botEnabled !== false,
      serverTime: Date.now()
    }));
    return;
  }

  // --- 5. Serve index.html and static files ---
  let filePath = path.join(__dirname, pathname === '/' ? 'index.html' : pathname);

  // Security check: stay within __dirname
  if (!fs.existsSync(filePath) || fs.statSync(filePath).isDirectory()) {
    filePath = path.join(__dirname, 'index.html');
  }

  const ext = path.extname(filePath).toLowerCase();
  const mimeTypes = {
    '.html': 'text/html; charset=utf-8',
    '.js': 'application/javascript; charset=utf-8',
    '.css': 'text/css; charset=utf-8',
    '.json': 'application/json; charset=utf-8',
    '.png': 'image/png',
    '.jpg': 'image/jpeg',
    '.jpeg': 'image/jpeg',
    '.svg': 'image/svg+xml',
    '.ico': 'image/x-icon'
  };

  const contentType = mimeTypes[ext] || 'application/octet-stream';

  fs.readFile(filePath, (err, content) => {
    if (err) {
      res.writeHead(500, { 'Content-Type': 'text/plain' });
      res.end('Server Error');
      return;
    }
    res.writeHead(200, { 'Content-Type': contentType });
    res.end(content);
  });
});

// Start Server
server.listen(PORT, '0.0.0.0', async () => {
  console.log(`🌐 SLK Server running on port ${PORT}`);
  await refreshServerConfig();
  if (cachedConfig.botToken) {
    startServerSidePoller();
  }
});
