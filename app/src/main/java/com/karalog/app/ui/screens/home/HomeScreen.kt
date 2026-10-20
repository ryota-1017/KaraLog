package com.karalog.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    rankingRecords: List<SingingRecordWithSong>,
    onSongClick: (SongEntity) -> Unit,
    onKeepToggle: (SongEntity) -> Unit,
    onSingClick: (SongEntity) -> Unit,
    onSearchClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("yyyy.M.d", Locale.JAPAN)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        item {
            StoriesActionTray(
                onSearchClick = onSearchClick,
                onSingClick = { onSearchClick() },
                onHistoryClick = onHistoryClick,
                onAddClick = onAddClick
            )
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))
        }

        item {
            Text(
                text = "オススメ曲",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 6.dp)
            )
        }

        items(recommendedSongs.take(4), key = { it.song.id }) { item ->
            SongCard(
                songWithStats = item,
                onSongClick = { onSongClick(item.song) },
                onKeepToggle = { onKeepToggle(item.song) },
                onSingClick = { onSingClick(item.song) }
            )
        }

        item {
            Text(
                text = "過去のランキング",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain,
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
            )
        }

        if (rankingRecords.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("歌唱記録がまだありません", fontSize = 12.sp, color = TextSub)
                }
            }
        } else {
            val topRankings = rankingRecords.sortedByDescending { it.record.score }.take(5)
            items(topRankings.mapIndexed { index, item -> Pair(index + 1, item) }) { (rank, item) ->
                val rec = item.record
                val dateStr = dateFormat.format(Date(rec.date))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "$rank.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain,
                        modifier = Modifier.width(20.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(BgSecondary)
                            .border(0.8.dp, BorderLight, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = IconInactive,
                            modifier = Modifier.size(26.dp)
                        )
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
                                    text = " " + item.songArtist,
                                    fontSize = 12.sp,
                                    color = TextSub,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                        Text(
                            text = dateStr,
                            fontSize = 11.sp,
                            color = TextSub,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            text = String.format("%.1f", rec.score) + " (" + rec.machineType + ")",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMain,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}