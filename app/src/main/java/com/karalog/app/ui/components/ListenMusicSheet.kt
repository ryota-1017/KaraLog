package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper
import com.karalog.app.util.IntentHelper

@Composable
fun ListenMusicSheet(
    title: String,
    artist: String = "",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgPrimary)
                .border(1.dp, BorderLight)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Headphones,
                            contentDescription = null,
                            tint = TextMain,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "原曲を聴く",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMain,
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = IconInactive,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Song Info
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgSecondary)
                        .padding(12.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain,
                        maxLines = 1
                    )
                    if (artist.isNotEmpty()) {
                        Text(
                            text = artist,
                            fontSize = 12.sp,
                            color = TextSub,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Options List (Instagram / Threads clean rows)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 1. YouTube Music
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgSecondary)
                            .border(1.dp, BorderLight)
                            .clickable {
                                HapticHelper.playClick(context)
                                IntentHelper.openYouTubeMusic(context, title, artist)
                                onDismiss()
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "YouTube Music",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMain
                            )
                            Text(
                                text = "YouTube Music アプリまたはWebで聴く",
                                fontSize = 10.sp,
                                color = TextSub
                            )
                        }
                        Icon(
                            Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = TextSub,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // 2. Spotify
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgSecondary)
                            .border(1.dp, BorderLight)
                            .clickable {
                                HapticHelper.playClick(context)
                                IntentHelper.openSpotify(context, title, artist)
                                onDismiss()
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Spotify",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMain
                            )
                            Text(
                                text = "Spotify アプリまたはWebで聴く",
                                fontSize = 10.sp,
                                color = TextSub
                            )
                        }
                        Icon(
                            Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = TextSub,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // 3. YouTube (MV / Live)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgSecondary)
                            .border(1.dp, BorderLight)
                            .clickable {
                                HapticHelper.playClick(context)
                                IntentHelper.openYouTube(context, title, artist)
                                onDismiss()
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "YouTube (MV / 動画)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMain
                            )
                            Text(
                                text = "公式MVやライブ映像で確認する",
                                fontSize = 10.sp,
                                color = TextSub
                            )
                        }
                        Icon(
                            Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = TextSub,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
