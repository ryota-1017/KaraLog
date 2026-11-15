package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.data.model.SongWithStats
import com.karalog.app.ui.theme.*

@Composable
fun SongCard(
    songWithStats: SongWithStats,
    onSongClick: () -> Unit,
    onKeepToggle: () -> Unit,
    onSingClick: () -> Unit,
    onAddToQueue: (() -> Unit)? = null,
    onDeleteSong: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val song = songWithStats.song
    var showListenSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = CardBg,
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Song Info Area (Clickable to inspect / sing)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSongClick() }
                    .padding(end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Rounded 3D Thumbnail
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BgSecondary,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .size(44.dp)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = IconInactive,
                            modifier = Modifier.size(20.dp)
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

            // Right Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 予約キューに追加ボタン (＋予約)
                if (onAddToQueue != null) {
                    Surface(
                        shape = CircleShape,
                        color = BgSecondary,
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.dp, CardBorder, CircleShape),
                        onClick = onAddToQueue
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PlaylistAdd,
                                contentDescription = "予約ストックに追加",
                                tint = TextMain,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }

                // 試聴ボタン
                Surface(
                    shape = CircleShape,
                    color = BgSecondary,
                    modifier = Modifier
                        .size(32.dp)
                        .border(1.dp, CardBorder, CircleShape),
                    onClick = { showListenSheet = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "原曲を聴く",
                            tint = TextSub,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // キープボタン（確実に反応するネイティブ onClick）
                Surface(
                    shape = CircleShape,
                    color = if (song.isKept) TextMain else BgSecondary,
                    modifier = Modifier
                        .size(32.dp)
                        .border(1.dp, if (song.isKept) TextMain else CardBorder, CircleShape),
                    onClick = onKeepToggle
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (song.isKept) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "キープ",
                            tint = if (song.isKept) BgPrimary else IconInactive,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // 削除ボタン (ゴミ箱)
                if (onDeleteSong != null) {
                    Surface(
                        shape = CircleShape,
                        color = BgSecondary,
                        modifier = Modifier
                            .size(32.dp)
                            .border(1.dp, CardBorder, CircleShape),
                        onClick = { showDeleteDialog = true }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "曲を削除",
                                tint = IconInactive,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // 歌うボタン
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = TextMain,
                    shadowElevation = 2.dp,
                    modifier = Modifier.height(30.dp),
                    onClick = onSingClick
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        Text(
                            text = "歌う",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BgPrimary
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

    if (showDeleteDialog && onDeleteSong != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("曲の削除", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = { Text("「${song.title}」をライブラリから削除しますか？", fontSize = 13.sp, color = TextSub) },
            confirmButton = {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = TextMain,
                    onClick = {
                        onDeleteSong()
                        showDeleteDialog = false
                    }
                ) {
                    Text("削除する", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BgPrimary, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("キャンセル", fontSize = 12.sp, color = TextSub)
                }
            },
            containerColor = CardBg
        )
    }
}
