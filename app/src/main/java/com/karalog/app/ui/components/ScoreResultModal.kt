package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper

@Composable
fun ScoreResultModal(
    songTitle: String,
    score: Double,
    previousScore: Double?,
    maxScoreBefore: Double?,
    onNextSong: () -> Unit,
    onReplaySinging: () -> Unit
) {
    val context = LocalContext.current
    val isBest = maxScoreBefore == null || score > maxScoreBefore
    val diff = if (previousScore != null) score - previousScore else null

    Dialog(onDismissRequest = onNextSong) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgPrimary)
                .border(1.dp, BorderLight)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isBest) {
                    Box(
                        modifier = Modifier
                            .background(TextMain)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = BgPrimary, modifier = Modifier.size(14.dp))
                            Text(
                                "NEW PERSONAL BEST!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BgPrimary,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = "RECORD SAVED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSub,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = songTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMain,
                    maxLines = 2
                )

                Text(
                    text = String.format("%.3f", score),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain,
                    letterSpacing = (-1).sp
                )

                // 比較情報
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgSecondary)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (diff != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("前回スコア比", fontSize = 11.sp, color = TextSub)
                            val sign = if (diff >= 0) "+" else ""
                            Text(
                                text = String.format("%s%.3f 点", sign, diff),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMain
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("自己ベスト", fontSize = 11.sp, color = TextSub)
                        val bestScore = maxOf(score, maxScoreBefore ?: score)
                        Text(
                            text = String.format("%.3f 点", bestScore),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMain
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 導線ボタン
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            HapticHelper.playClick(context)
                            onReplaySinging()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMain),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        shape = RectangleShape,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("もう1回", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            HapticHelper.playClick(context)
                            onNextSong()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = BgPrimary),
                        shape = RectangleShape,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                    ) {
                        Text("次の曲を探す", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}
