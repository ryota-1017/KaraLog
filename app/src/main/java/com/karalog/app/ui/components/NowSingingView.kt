package com.karalog.app.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.ui.theme.*

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = TextMain, modifier = Modifier.size(16.dp))
                    Text(
                        text = "歌唱中 / 待機",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMain
                    )
                    Text(
                        text = "· 常時点灯中",
                        fontSize = 11.sp,
                        color = TextSub
                    )
                }

                Text(
                    text = "曲を変更",
                    fontSize = 12.sp,
                    color = TextSub,
                    modifier = Modifier.clickable { onSongChangeClick() }
                )
            }

            // 楽曲情報カード
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSecondary)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = song?.title ?: "フリー歌唱",
                        fontSize = 20.sp,
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

                    if (song != null && song.stageTag.isNotEmpty()) {
                        val stageLabel = when (song.stageTag) {
                            "WARMUP" -> "ウォーミングアップ"
                            "BEST" -> "勝負曲・十八番"
                            "CLIMAX" -> "終盤盛り上がり"
                            "PRACTICE" -> "練習中"
                            else -> song.stageTag
                        }
                        Text(
                            text = "# $stageLabel",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSub,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "デンモク予約番号",
                        fontSize = 10.sp,
                        color = TextSub,
                        fontWeight = FontWeight.Medium
                    )

                    val reqNumber = song?.requestNumber?.ifEmpty { null } ?: "-"
                    Text(
                        text = reqNumber,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = TextMain,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 機種選択
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSecondary)
                    .padding(14.dp)
            ) {
                Text(
                    text = "カラオケ機種",
                    fontSize = 11.sp,
                    color = TextSub,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("DAM", "JOYSOUND").forEach { machine ->
                        val isSelected = selectedMachine.contains(machine)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .background(if (isSelected) TextMain else BgPrimary)
                                .border(1.dp, BorderLight)
                                .clickable { onMachineChange(machine) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = machine,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) BgPrimary else TextMain
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // キー微調整
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSecondary)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "キー設定", fontSize = 11.sp, color = TextSub)
                        if (song != null) {
                            val defaultKeyStr = when {
                                song.defaultKey == 0 -> "±0"
                                song.defaultKey > 0 -> "+${song.defaultKey}"
                                else -> "${song.defaultKey}"
                            }
                            Text(
                                text = "登録マイキー: $defaultKeyStr",
                                fontSize = 10.sp,
                                color = TextSub
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = { onKeyChange(currentKey - 1) },
                            modifier = Modifier
                                .size(34.dp)
                                .background(BgPrimary)
                                .border(1.dp, BorderLight)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "-", tint = TextMain, modifier = Modifier.size(16.dp))
                        }

                        Text(
                            text = keyStr,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = TextMain,
                            modifier = Modifier.widthIn(min = 36.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        IconButton(
                            onClick = { onKeyChange(currentKey + 1) },
                            modifier = Modifier
                                .size(34.dp)
                                .background(BgPrimary)
                                .border(1.dp, BorderLight)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "+", tint = TextMain, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // 下部アクションボタン
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onInputScoreClick,
                colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = BgPrimary),
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("歌い終わった！スコアを入力", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = onCancelClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSub),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Text("歌唱をキャンセル", fontSize = 12.sp)
            }
        }
    }
}
