package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val bestScore = songWithStats.maxScore
    val keyStr = when {
        song.defaultKey == 0 -> "ﾂｱ0"
        song.defaultKey > 0 -> "+ ${song.defaultKey}"
        else -> "${song.defaultKey}"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BgPrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSongClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = song.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMain,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (bestScore != null) {
                        Text(
                            text = String.format("%.3f", bestScore),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMain
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = song.artist,
                        fontSize = 12.sp,
                        color = TextSub,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = "/", fontSize = 10.sp, color = BorderLight)
                    Text(text = keyStr, fontSize = 11.sp, color = TextSub)
                    if (song.requestNumber.isNotEmpty()) {
                        Text(text = "/", fontSize = 10.sp, color = BorderLight)
                        Text(text = song.requestNumber, fontSize = 11.sp, color = TextSub)
                    }
                    Text(text = "/", fontSize = 10.sp, color = BorderLight)
                    val machineStr = when {
                        song.isDamAvailable && song.isJoysoundAvailable -> "DAMﾂｷJOY"
                        song.isDamAvailable -> "DAM"
                        else -> "JOY"
                    }
                    Text(text = machineStr, fontSize = 11.sp, color = TextSub)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onKeepToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (song.isKept) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Keep",
                        tint = if (song.isKept) TextMain else IconInactive,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onSingClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Sing",
                        tint = TextMain,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderLight)
        )
    }
}

