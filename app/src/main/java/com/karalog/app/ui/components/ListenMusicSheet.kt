package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
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
    spotifyUrl: String = "",
    youtubeMusicUrl: String = "",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = CardBg,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.2.dp, CardBorder, RoundedCornerShape(26.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BgSecondary,
                            modifier = Modifier.size(28.dp).border(0.8.dp, CardBorder, CircleShape)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Headphones,
                                    contentDescription = null,
                                    tint = TextMain,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Text(
                            text = "原曲を聴く",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = BgSecondary,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onDismiss() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = IconInactive,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Song Info (Rounded 3D Card)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BgSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = title,
                            fontSize = 16.sp,
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
                }

                // 3 Music Service Rounded 3D Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. YouTube Music
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CardBg,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                HapticHelper.playClick(context)
                                IntentHelper.openYouTubeMusic(context, title, artist, youtubeMusicUrl)
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "YouTube Music",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMain
                                    )
                                    if (youtubeMusicUrl.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = BgSecondary
                                        ) {
                                            Text(
                                                text = "登録リンク済",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextSub,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "YouTube Music アプリまたはWebで聴く",
                                    fontSize = 10.sp,
                                    color = TextSub
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = TextSub,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // 2. Spotify
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CardBg,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                HapticHelper.playClick(context)
                                IntentHelper.openSpotify(context, title, artist, spotifyUrl)
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Spotify",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMain
                                    )
                                    if (spotifyUrl.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = BgSecondary
                                        ) {
                                            Text(
                                                text = "登録リンク済",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextSub,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Spotify アプリまたはWebで聴く",
                                    fontSize = 10.sp,
                                    color = TextSub
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = TextSub,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // 3. YouTube (Official MV / Live)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CardBg,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                HapticHelper.playClick(context)
                                IntentHelper.openYouTube(context, title, artist)
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "YouTube (MV / 動画)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMain
                                )
                                Text(
                                    text = "公式MVやライブ動画でメロディを確認",
                                    fontSize = 10.sp,
                                    color = TextSub
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = TextSub,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
