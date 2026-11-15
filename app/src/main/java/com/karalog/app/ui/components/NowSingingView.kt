package com.karalog.app.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper
import com.karalog.app.util.IntentHelper

@Composable
fun NowSingingView(
    song: SongEntity?,
    currentKey: Int,
    selectedMachine: String,
    onKeyChange: (Int) -> Unit,
    onMachineChange: (String) -> Unit,
    onInputScoreClick: () -> Unit,
    onSongChangeClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showListenSheet by remember { mutableStateOf(false) }

    // Keep screen on while singing
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    val keyStr = when {
        currentKey == 0 -> "±0"
        currentKey > 0 -> "+$currentKey"
        else -> "$currentKey"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // ステータスヘッダー
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CardBg,
                    modifier = Modifier.border(0.8.dp, CardBorder, RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = TextMain, modifier = Modifier.size(15.dp))
                        Text(
                            text = "歌唱モード",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BgSecondary,
                    modifier = Modifier
                        .border(0.8.dp, CardBorder, RoundedCornerShape(14.dp))
                        .clickable { onSongChangeClick() }
                ) {
                    Text(
                        text = "曲を変更 ➔",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSub,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // 楽曲情報カード (3D Rounded Card)
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = CardBg,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(22.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = song?.title ?: "フリー歌唱",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain,
                        maxLines = 2
                    )

                    Text(
                        text = if (song != null && song.artist.isNotEmpty()) song.artist else "アーティスト未設定",
                        fontSize = 13.sp,
                        color = TextSub,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    if (song != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        // Spotify & YouTube Music Rounded 3D Pills
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = BgSecondary,
                                modifier = Modifier
                                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                    .clickable {
                                        HapticHelper.playClick(context)
                                        IntentHelper.openSpotify(context, song.title, song.artist, song.spotifyUrl)
                                    }
                            ) {
                                Text(
                                    text = "▶ Spotify",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextMain,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = BgSecondary,
                                modifier = Modifier
                                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                    .clickable {
                                        HapticHelper.playClick(context)
                                        IntentHelper.openYouTubeMusic(context, song.title, song.artist, song.youtubeMusicUrl)
                                    }
                            ) {
                                Text(
                                    text = "▶ YT Music",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextMain,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = BgSecondary,
                                modifier = Modifier
                                    .size(30.dp)
                                    .border(1.dp, CardBorder, CircleShape)
                                    .clickable { showListenSheet = true }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Headphones, contentDescription = "詳細試聴", tint = TextSub, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "デンモク予約番号",
                        fontSize = 10.sp,
                        color = TextSub,
                        fontWeight = FontWeight.Medium
                    )

                    val reqNumber = song?.requestNumber?.ifEmpty { null } ?: "-"
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BgSecondary,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .border(0.8.dp, CardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = reqNumber,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = TextMain,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 機種選択 (Rounded 3D Segmented Card)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = CardBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "カラオケ機種",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSub,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("DAM", "JOYSOUND").forEach { machine ->
                            val isSelected = selectedMachine.contains(machine)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) TextMain else BgSecondary,
                                border = if (isSelected) null else BorderStroke(1.dp, CardBorder),
                                shadowElevation = if (isSelected) 2.dp else 0.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                onClick = { onMachineChange(machine) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = machine,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) BgPrimary else TextMain
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // キー微調整 (3D Rounded Controller)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = CardBg,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "キー設定", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSub)
                        if (song != null) {
                            val defaultKeyStr = when {
                                song.defaultKey == 0 -> "±0"
                                song.defaultKey > 0 -> "+${song.defaultKey}"
                                else -> "${song.defaultKey}"
                            }
                            Text(
                                text = "登録マイキー: $defaultKeyStr",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMain
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BgSecondary,
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .size(38.dp)
                                .border(1.dp, CardBorder, CircleShape)
                                .clickable { onKeyChange(currentKey - 1) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Remove, contentDescription = "-", tint = TextMain, modifier = Modifier.size(18.dp))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BgSecondary,
                            modifier = Modifier.border(0.8.dp, CardBorder, RoundedCornerShape(10.dp))
                        ) {
                            Text(
                                text = keyStr,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = TextMain,
                                modifier = Modifier
                                    .widthIn(min = 44.dp)
                                    .padding(vertical = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = BgSecondary,
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .size(38.dp)
                                .border(1.dp, CardBorder, CircleShape)
                                .clickable { onKeyChange(currentKey + 1) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Add, contentDescription = "+", tint = TextMain, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // 下部アクションボタン (Rounded 3D Action Buttons)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = TextMain,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .clickable { onInputScoreClick() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "歌い終わった！スコアを入力",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BgPrimary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardBg,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .clickable { onCancelClick() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "歌唱をキャンセル",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSub
                    )
                }
            }
        }
    }

    if (showListenSheet && song != null) {
        ListenMusicSheet(
            title = song.title,
            artist = song.artist,
            spotifyUrl = song.spotifyUrl,
            youtubeMusicUrl = song.youtubeMusicUrl,
            onDismiss = { showListenSheet = false }
        )
    }
}
