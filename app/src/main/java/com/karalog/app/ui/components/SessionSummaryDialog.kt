package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StopCircle
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
                // Header Circular 3D Icon
                Surface(
                    shape = CircleShape,
                    color = BgSecondary,
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .size(54.dp)
                        .border(1.2.dp, CardBorder, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.StopCircle,
                            contentDescription = null,
                            tint = TextMain,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "本日のカラオケを終了",
                        fontSize = 19.sp,
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

                // Summary Stats Box (Rounded 3D Card)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = BgSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("歌唱曲数", fontSize = 12.sp, color = TextSub)
                            Text("$count 曲", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMain)
                        }

                        if (count > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("平均スコア", fontSize = 12.sp, color = TextSub)
                                Text(
                                    String.format("%.1f 点", avgScore),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMain
                                )
                            }
                        }

                        if (maxRecord != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(CardBorder)
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
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = CardBg,
                                        modifier = Modifier.border(0.8.dp, CardBorder, RoundedCornerShape(8.dp))
                                    ) {
                                        Text(
                                            text = String.format("%.3f 点", maxRecord.second.score),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = TextMain,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Rounded 3D Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = TextMain,
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .clickable {
                                HapticHelper.playHeavyClick(context)
                                onFinishSession()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "カラオケを終了する (Finish)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BgPrimary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CardBg,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                            .clickable { onDismiss() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "まだ続ける (キャンセル)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSub
                            )
                        }
                    }
                }
            }
        }
    }
}
