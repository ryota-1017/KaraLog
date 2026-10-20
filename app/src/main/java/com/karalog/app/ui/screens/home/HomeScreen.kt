package com.karalog.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.data.model.SongWithStats
import com.karalog.app.ui.components.SongCard
import com.karalog.app.ui.theme.*

@Composable
fun HomeScreen(
    keptSongs: List<SongWithStats>,
    allSongs: List<SongWithStats>,
    onSongClick: (SongEntity) -> Unit,
    onKeepToggle: (SongEntity) -> Unit,
    onSingClick: (SongEntity) -> Unit,
    modifier: Modifier = Modifier
) {
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "キープ (" + keptSongs.size + ")",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMain
                )
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))
        }

        if (keptSongs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("キープされた曲がありません", fontSize = 12.sp, color = TextSub)
                }
            }
        } else {
            items(keptSongs, key = { it.song.id }) { item ->
                SongCard(
                    songWithStats = item,
                    onSongClick = { onSongClick(item.song) },
                    onKeepToggle = { onKeepToggle(item.song) },
                    onSingClick = { onSingClick(item.song) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSecondary)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "すべての曲 (" + allSongs.size + ")",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMain
                )
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))
        }

        items(allSongs, key = { it.song.id }) { item ->
            SongCard(
                songWithStats = item,
                onSongClick = { onSongClick(item.song) },
                onKeepToggle = { onKeepToggle(item.song) },
                onSingClick = { onSingClick(item.song) }
            )
        }
    }
}
