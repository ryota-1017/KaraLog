package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isBest) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TextMain,
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
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
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BgSecondary
                    ) {
                        Text(
                            text = "RECORD SAVED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSub,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = songTitle,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain,
                    maxLines = 2
                )

                Text(
                    text = String.format("%.3f", score),
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = TextMain,
                    letterSpacing = (-1).sp
                )

                // 比較情報 (Rounded 3D Card)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BgSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (diff != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("前回スコア比", fontSize = 12.sp, color = TextSub)
                                val sign = if (diff >= 0) "+" else ""
                                Text(
                                    text = String.format("%s%.3f 点", sign, diff),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMain
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("自己ベスト", fontSize = 12.sp, color = TextSub)
                            val bestScore = maxOf(score, maxScoreBefore ?: score)
                            Text(
                                text = String.format("%.3f 点", bestScore),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = TextMain
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 導線ボタン (Rounded 3D Pills)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = BgSecondary,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                            .clickable {
                                HapticHelper.playClick(context)
                                onReplaySinging()
                            }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = null, tint = TextMain, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("もう1回", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = TextMain,
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                HapticHelper.playClick(context)
                                onNextSong()
                            }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("次の曲へ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BgPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = BgPrimary, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
        }
    }
}
