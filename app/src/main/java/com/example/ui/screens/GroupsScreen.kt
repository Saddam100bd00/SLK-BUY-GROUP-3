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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TelegramGroup
import com.example.ui.components.GroupScreenshotCard
import com.example.ui.components.TelegramSupportCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDark
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MidnightBorder
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightCardHover
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GroupsScreen(
    groups: List<TelegramGroup>,
    onBuyGroup: (TelegramGroup) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("সবগুলো") }

    val categories = listOf("সবগুলো", "Movies", "VIP Exclusive", "OTT", "Tech", "Anime")

    val filteredGroups = groups.filter { group ->
        val matchesQuery = group.title.contains(searchQuery, ignoreCase = true) ||
                group.description.contains(searchQuery, ignoreCase = true) ||
                group.category.contains(searchQuery, ignoreCase = true)

        val matchesCategory = when (selectedCategory) {
            "সবগুলো" -> true
            "Movies" -> group.category.contains("Movies", ignoreCase = true)
            "VIP Exclusive" -> group.category.contains("VIP", ignoreCase = true) || group.category.contains("Exclusive", ignoreCase = true)
            "OTT" -> group.category.contains("OTT", ignoreCase = true) || group.category.contains("Streaming", ignoreCase = true)
            "Tech" -> group.category.contains("Tech", ignoreCase = true) || group.category.contains("Software", ignoreCase = true)
            "Anime" -> group.category.contains("Anime", ignoreCase = true) || group.category.contains("Drama", ignoreCase = true)
            else -> true
        }

        matchesQuery && matchesCategory
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDark),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        // === Page Header ===
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "টেলিগ্রাম প্রিমিয়াম গ্রুপ মার্কেটপ্লেস",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "লাইফটাইম এক্সেস • ইনস্ট্যান্ট অটো ইনভাইট লিংক",
                            fontSize = 12.sp,
                            color = ElectricCyan
                        )
                    }

                    Surface(
                        color = Color(0x2210B981),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Safe",
                                tint = SuccessGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "100% Safe",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("মুভি, সিরিজ বা টুলস গ্রুপ সার্চ করুন...", fontSize = 13.sp, color = TextSecondary)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ElectricCyan
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MidnightSurface,
                        unfocusedContainerColor = MidnightSurface,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Chips Horizontal Scroll
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            modifier = Modifier.clickable { selectedCategory = category },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) ElectricCyan else MidnightCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ElectricCyan else MidnightBorder
                            )
                        ) {
                            Text(
                                text = category,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MidnightDark else TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // === List of Telegram Group Screenshot Cards ===
        if (filteredGroups.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "কোন গ্রুপ পাওয়া যায়নি!",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(filteredGroups) { group ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 7.dp)
                ) {
                    GroupScreenshotCard(
                        group = group,
                        onBuyClick = { onBuyGroup(group) }
                    )
                }
            }
        }

        // Direct Telegram Support helpline at bottom
        item {
            Box(modifier = Modifier.padding(16.dp)) {
                TelegramSupportCard()
            }
        }
    }
}
