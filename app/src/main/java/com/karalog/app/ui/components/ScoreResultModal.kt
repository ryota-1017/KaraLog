package com.karalog.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = SurfaceDark,
            tonalElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, OutlineDark, RoundedCornerShape(28.dp))
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
                        color = AmberStar.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberStar)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AmberStar, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("\u81ea\u5df1\u30d9\u30b9\u30c8\u66f4\u65b0\uff01", fontWeight = FontWeight.Black, fontSize = 13.sp, color = AmberStar)
                        }
                    }
                } else {
                    Text("\u8a18\u9332\u5b8c\u4e86", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF90A4AE))
                }

                Text(
                    text = songTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = String.format("%.3f\u70b9", score),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonCyan
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MidnightBlack,
                    modifier = Modifier.fillMaxWidth()
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
                                Text("\u524d\u56de\u30b9\u30b3\u30a2", fontSize = 12.sp, color = Color(0xFF90A4AE))
                                Text(
                                    text = String.format("%.3f\u70b9 (%s%.3f)", previousScore, if (diff >= 0) "+" else "", diff),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (diff >= 0) SuccessGreen else CoralRed
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("\u3053\u308c\u307e\u3067\u306e\u6700\u9ad8", fontSize = 12.sp, color = Color(0xFF90A4AE))
                            Text(
                                text = String.format("%.3f\u70b9", maxOf(score, maxScoreBefore ?: score)),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            HapticHelper.playHeavyClick(context)
                            onReplaySinging()
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineDark)
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp), tint = AmberStar)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("\u3082\u30461\u56de\uff01", color = AmberStar, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            HapticHelper.playClick(context)
                            onNextSong()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = MidnightBlack),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp)
                    ) {
                        Text("\u6b21\u306e\u66f2\u3078", fontWeight = FontWeight.Black, fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}