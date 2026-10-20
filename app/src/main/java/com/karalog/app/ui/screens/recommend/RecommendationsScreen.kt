package com.karalog.app.ui.screens.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.data.model.SongWithStats
import com.karalog.app.ui.components.SongCard
import com.karalog.app.ui.theme.*

enum class RecommendCategory(val title: String) {
    HIGH_SCORE("十八番 (高得点)"),
    UNTRIED("未挑戦"),
    LONG_TIME("久しぶり"),
    RANDOM("おまかせ")
}

@Composable
fun RecommendationsScreen(
    songsWithStats: List<SongWithStats>,
    onStartSinging: (song: SongEntity) -> Unit,
    onKeyChange: (songId: Long, newKey: Int) -> Unit,
    onToggleKept: (songId: Long, currentKept: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(RecommendCategory.HIGH_SCORE) }

    val recommendedSongs = remember(songsWithStats, selectedCategory) {
        when (selectedCategory) {
            RecommendCategory.HIGH_SCORE -> songsWithStats.filter { (it.maxScore ?: 0.0) >= 90.0 }.sortedByDescending { it.maxScore }
            RecommendCategory.UNTRIED -> songsWithStats.filter { it.singCount == 0 }
            RecommendCategory.LONG_TIME -> songsWithStats.filter { it.singCount > 0 }.sortedBy { it.lastSungDate ?: 0L }
            RecommendCategory.RANDOM -> songsWithStats.shuffled()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSecondary)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecommendCategory.values().forEach { cat ->
                    val isSel = cat == selectedCategory
                    Text(
                        text = cat.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSel) BgPrimary else TextSub,
                        modifier = Modifier
                            .background(if (isSel) TextMain else BgPrimary)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))
        }

        if (recommendedSongs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("該当する曲がありません", fontSize = 12.sp, color = TextSub)
                }
            }
        } else {
            items(recommendedSongs, key = { it.song.id }) { item ->
                SongCard(
                    songWithStats = item,
                    onSongClick = { onStartSinging(item.song) },
                    onKeepToggle = { onToggleKept(item.song.id, item.song.isKept) },
                    onSingClick = { onStartSinging(item.song) }
                )
            }
        }
    }
}
