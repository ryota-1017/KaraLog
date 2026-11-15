package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.karalog.app.data.local.entity.SingingRecordEntity
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper

@Composable
fun SessionSummaryDialog(
    sessionRecords: List<Pair<String, SingingRecordEntity>>,
    onDismiss: () -> Unit,
    onFinishSession: () -> Unit
) {
    val context = LocalContext.current
    val count = sessionRecords.size
    val maxRecord = sessionRecords.maxByOrNull { it.second.score }
    val avgScore = if (count > 0) sessionRecords.map { it.second.score }.average() else 0.0

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = BgPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BgSecondary)
                        .border(1.dp, BorderLight, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.StopCircle,
                        contentDescription = null,
                        tint = TextMain,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "本日のカラオケを終了",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "セッションを締め切り、初期画面に戻ります",
                        fontSize = 12.sp,
                        color = TextSub
                    )
                }

                // Summary Stats Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgSecondary)
                        .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("歌唱曲数", fontSize = 12.sp, color = TextSub)
                            Text("$count 曲", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMain)
                        }

                        if (count > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("平均スコア", fontSize = 12.sp, color = TextSub)
                                Text(
                                    String.format("%.1f 点", avgScore),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMain
                                )
                            }
                        }

                        if (maxRecord != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(BorderLight)
                            )
                            Column {
                                Text("本日の最高得点曲", fontSize = 11.sp, color = TextSub)
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = maxRecord.first,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMain,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = String.format("%.3f 点", maxRecord.second.score),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMain
                                    )
                                }
                            }
                        }
                    }
                }

                // Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            HapticHelper.playHeavyClick(context)
                            onFinishSession()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TextMain,
                            contentColor = BgPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text(
                            text = "カラオケを終了する (Finish)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Text(
                            text = "まだ続ける (キャンセル)",
                            fontSize = 12.sp,
                            color = TextSub
                        )
                    }
                }
            }
        }
    }
}
