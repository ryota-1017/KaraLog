package com.karalog.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.data.model.SingingRecordWithSong
import com.karalog.app.data.model.SongWithStats
import com.karalog.app.ui.components.SongCard
import com.karalog.app.ui.components.StoriesActionTray
import com.karalog.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    recommendedSongs: List<SongWithStats>,
    keptSongs: List<SongWithStats>,
    rankingRecords: List<SingingRecordWithSong>,
    onSongClick: (SongEntity) -> Unit,
    onKeepToggle: (SongEntity) -> Unit,
    onSingClick: (SongEntity) -> Unit,
    onSearchClick: () -> Unit,
    onBestClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("yyyy.M.d", Locale.JAPAN)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgPrimary),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. 3D Circular Action Tray
        item {
            StoriesActionTray(
                onSearchClick = onSearchClick,
                onSingClick = { onSongClick(SongEntity(title = "フリー歌唱")) },
                onHistoryClick = onBestClick,
                onAddClick = onAddClick
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        // 2. 本日のキープ（歌いたい曲）
        if (keptSongs.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CardBg,
                            modifier = Modifier.size(22.dp).border(0.8.dp, CardBorder, CircleShape)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = TextMain,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Text(
                            text = "歌唱候補 (${keptSongs.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                    }
                    Text(
                        text = "タップで即歌唱",
                        fontSize = 11.sp,
                        color = TextSub
                    )
                }
            }

            items(keptSongs.take(5), key = { it.song.id }) { item ->
                SongCard(
                    songWithStats = item,
                    onSongClick = { onSongClick(item.song) },
                    onKeepToggle = { onKeepToggle(item.song) },
                    onSingClick = { onSingClick(item.song) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // 3. オススメ曲 Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "オススメ曲",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Text(
                    text = "全曲を見る ➔",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSub,
                    modifier = Modifier.clickable { onSearchClick() }
                )
            }
        }

        items(recommendedSongs.take(4), key = { "rec_${it.song.id}" }) { item ->
            SongCard(
                songWithStats = item,
                onSongClick = { onSongClick(item.song) },
                onKeepToggle = { onKeepToggle(item.song) },
                onSingClick = { onSingClick(item.song) }
            )
        }

        // 4. 過去のランキング Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = CardBg,
                    modifier = Modifier.size(22.dp).border(0.8.dp, CardBorder, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = TextMain,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
                Text(
                    text = "過去のランキング",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
            }
        }

        if (rankingRecords.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = CardBg,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("歌唱記録がまだありません", fontSize = 12.sp, color = TextSub)
                    }
                }
            }
        } else {
            val topRankings = rankingRecords.sortedByDescending { it.record.score }.take(5)
            items(topRankings.mapIndexed { index, item -> Pair(index + 1, item) }) { (rank, item) ->
                val rec = item.record
                val dateStr = dateFormat.format(Date(rec.date))

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = CardBg,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Rank Circle Badge
                        Surface(
                            shape = CircleShape,
                            color = if (rank == 1) TextMain else BgSecondary,
                            modifier = Modifier
                                .size(28.dp)
                                .border(1.dp, if (rank == 1) TextMain else CardBorder, CircleShape)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$rank",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (rank == 1) BgPrimary else TextMain
                                )
                            }
                        }

                        // Thumbnail
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BgSecondary,
                            modifier = Modifier
                                .size(46.dp)
                                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = IconInactive,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.songTitle.ifEmpty { "不明な曲" },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextMain
                                )
                                if (item.songArtist.isNotEmpty()) {
                                    Text(
                                        text = " · " + item.songArtist,
                                        fontSize = 12.sp,
                                        color = TextSub
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = dateStr,
                                    fontSize = 11.sp,
                                    color = TextSub
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BgSecondary,
                                    modifier = Modifier.border(0.6.dp, CardBorder, RoundedCornerShape(6.dp))
                                ) {
                                    Text(
                                        text = rec.machineType,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSub,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        // Score Pill Badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BgSecondary,
                            modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                        ) {
                            Text(
                                text = String.format("%.1f 点", rec.score),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = TextMain,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
