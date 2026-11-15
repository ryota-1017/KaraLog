package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = CardBg,
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .clickable { onSongClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Rounded 3D Thumbnail
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BgSecondary,
                    shadowElevation = 1.dp,
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
                    Text(
                        text = song.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMain,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (song.artist.isNotEmpty()) song.artist else "未設定",
                        fontSize = 12.sp,
                        color = TextSub,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right Round Pill Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Listen Music Button (Circular 3D)
                Surface(
                    shape = CircleShape,
                    color = BgSecondary,
                    modifier = Modifier
                        .size(34.dp)
                        .border(1.dp, CardBorder, CircleShape)
                        .clickable { showListenSheet = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "原曲を聴く",
                            tint = TextSub,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Keep Bookmark Button (Circular 3D)
                Surface(
                    shape = CircleShape,
                    color = if (song.isKept) TextMain else BgSecondary,
                    modifier = Modifier
                        .size(34.dp)
                        .border(1.dp, if (song.isKept) TextMain else CardBorder, CircleShape)
                        .clickable { onKeepToggle() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (song.isKept) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "キープ",
                            tint = if (song.isKept) BgPrimary else IconInactive,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }

    if (showListenSheet) {
        ListenMusicSheet(
            title = song.title,
            artist = song.artist,
            spotifyUrl = song.spotifyUrl,
            youtubeMusicUrl = song.youtubeMusicUrl,
            onDismiss = { showListenSheet = false }
        )
    }
}
