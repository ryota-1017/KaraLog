package com.karalog.app.ui.screens.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.data.model.SongWithStats
import com.karalog.app.ui.components.SongCard
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper

enum class RecommendCategory(val title: String) {
    HIGH_SCORE("\u5341\u516b\u756a (\u9ad8\u5f97\u70b9)"),
    UNTRIED("\u672a\u6311\u6226"),
    LONG_TIME("\u4e45\u3057\u3076\u308a"),
    RANDOM("\u304a\u307e\u304b\u305b")
}

@Composable
fun RecommendationsScreen(
    songsWithStats: List<SongWithStats>,
    onStartSinging: (song: SongEntity) -> Unit,
    onKeyChange: (songId: Long, newKey: Int) -> Unit,
    onToggleKept: (songId: Long, currentKept: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecommendCategory.values().forEach { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            HapticHelper.playClick(context)
                            selectedCategory = cat
                        },
                        label = { Text(cat.title, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        shape = RoundedCornerShape(14.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = MidnightBlack,
                            containerColor = SurfaceDark,
                            labelColor = Color(0xFF90A4AE)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = OutlineDark
                        )
                    )
                }
            }
        }

        if (recommendedSongs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("\u8a72\u5f53\u3059\u308b\u66f2\u304c\u3042\u308a\u307e\u305b\u3093", color = Color(0xFF90A4AE), fontSize = 14.sp)
                }
            }
        } else {
            items(recommendedSongs, key = { it.song.id }) { item ->
                SongCard(
                    item = item,
                    onStartSinging = { onStartSinging(item.song) },
                    onKeyChange = { newKey -> onKeyChange(item.song.id, newKey) },
                    onToggleKept = { onToggleKept(item.song.id, item.song.isKept) }
                )
            }
        }
    }
}