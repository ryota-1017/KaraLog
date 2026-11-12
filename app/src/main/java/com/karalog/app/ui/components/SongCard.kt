package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.model.SongWithStats
import com.karalog.app.ui.theme.*

@Composable
fun SongCard(
    songWithStats: SongWithStats,
    onSongClick: () -> Unit,
    onKeepToggle: () -> Unit,
    onSingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val song = songWithStats.song
    var showListenSheet by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSongClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f).padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(BgSecondary)
                    .border(0.8.dp, BorderLight, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = IconInactive,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMain,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (song.artist.isNotEmpty()) song.artist else "未設定",
                    fontSize = 12.sp,
                    color = TextSub,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Listen Music Button (YouTube Music / Spotify)
            IconButton(
                onClick = { showListenSheet = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Headphones,
                    contentDescription = "原曲を聴く",
                    tint = TextSub,
                    modifier = Modifier.size(17.dp)
                )
            }

            // Keep bookmark button
            IconButton(
                onClick = onKeepToggle,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (song.isKept) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "キープ",
                    tint = if (song.isKept) TextMain else IconInactive,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    if (showListenSheet) {
        ListenMusicSheet(
            title = song.title,
            artist = song.artist,
            onDismiss = { showListenSheet = false }
        )
    }
}
