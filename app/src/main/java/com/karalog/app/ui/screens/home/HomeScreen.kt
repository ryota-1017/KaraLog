package com.karalog.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    reservedQueue: List<SongEntity> = emptyList(),
    onSongClick: (SongEntity) -> Unit,
    onKeepToggle: (SongEntity) -> Unit,
    onSingClick: (SongEntity) -> Unit,
    onAddToQueue: (SongEntity) -> Unit = {},
    onMoveQueueUp: (Int) -> Unit = {},
    onMoveQueueDown: (Int) -> Unit = {},
    onRemoveFromQueue: (Long) -> Unit = {},
    onDeleteSong: (Long) -> Unit = {},
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
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. 3D Circular Action Tray
        item {
            StoriesActionTray(
                onSearchClick = onSearchClick,
                onSingClick = { onSongClick(SongEntity(title = "フリー歌唱")) },
                onHistoryClick = onBestClick,
                onAddClick = onAddClick
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 2. 🎤 予約ストックリスト（順番入れ替え可能！）
        if (reservedQueue.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TextMain,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.QueueMusic,
                                    contentDescription = null,
                                    tint = BgPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Text(
                            text = "予約ストック (${reservedQueue.size}曲)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                    }
                    Text(
                        text = "▲▼で順序入れ替え",
                        fontSize = 11.sp,
                        color = TextSub
                    )
                }
            }

            itemsIndexed(reservedQueue, key = { _, s -> "queue_${s.id}" }) { index, song ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CardBg,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 3.dp)
                        .border(1.2.dp, CardBorder, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Order Number badge
                        Surface(
                            shape = CircleShape,
                            color = if (index == 0) TextMain else BgSecondary,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (index == 0) BgPrimary else TextMain
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Title & Artist
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMain,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (song.artist.isNotEmpty()) {
                                Text(
                                    text = song.artist,
                                    fontSize = 11.sp,
                                    color = TextSub,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Reorder and Action Buttons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Move Up
                            if (index > 0) {
                                Surface(
                                    shape = CircleShape,
                                    color = BgSecondary,
                                    modifier = Modifier.size(28.dp),
                                    onClick = { onMoveQueueUp(index) }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "上へ", tint = TextMain, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            // Move Down
                            if (index < reservedQueue.size - 1) {
                                Surface(
                                    shape = CircleShape,
                                    color = BgSecondary,
                                    modifier = Modifier.size(28.dp),
                                    onClick = { onMoveQueueDown(index) }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "下へ", tint = TextMain, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            // Sing this song button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TextMain,
                                shadowElevation = 1.dp,
                                modifier = Modifier.height(28.dp),
                                onClick = {
                                    onRemoveFromQueue(song.id)
                                    onSingClick(song)
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                                    Text("歌う", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BgPrimary)
                                }
                            }

                            // Remove from queue button
                            Surface(
                                shape = CircleShape,
                                color = BgSecondary,
                                modifier = Modifier.size(28.dp),
                                onClick = { onRemoveFromQueue(song.id) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Close, contentDescription = "予約解除", tint = IconInactive, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // 3. キープ曲（歌いたい曲）
        if (keptSongs.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp),
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
                            text = "キープ曲 (${keptSongs.size}曲)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                    }
                    Text(
                        text = "タップで歌唱",
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
                    onSingClick = { onSingClick(item.song) },
                    onAddToQueue = { onAddToQueue(item.song) },
                    onDeleteSong = { onDeleteSong(item.song.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // 4. オススメ曲 Section
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
                onSingClick = { onSingClick(item.song) },
                onAddToQueue = { onAddToQueue(item.song) },
                onDeleteSong = { onDeleteSong(item.song.id) }
            )
        }

        // 5. 過去のランキング Section
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
