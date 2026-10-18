package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper

@Composable
fun NowSingingView(
    song: SongEntity,
    currentMachine: String,
    onCancel: () -> Unit,
    onInputScore: () -> Unit,
    onKeyChange: (newKey: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceVariantDark
                ) {
                    Text(
                        text = currentMachine,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                IconButton(
                    onClick = {
                        HapticHelper.playClick(context)
                        onCancel()
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "\u30ad\u30e3\u30f3\u30bb\u30eb", tint = Color(0xFF90A4AE))
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(NeonCyan, VibrantBlue))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = MidnightBlack, modifier = Modifier.size(36.dp))
                }

                Text(
                    text = song.title,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 38.sp
                )

                Text(
                    text = song.artist.ifBlank { "\u30a2\u30fc\u30c6\u30a3\u30b9\u30c8\u672a\u8a2d\u5b9a" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF90A4AE),
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceDark,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .border(1.dp, OutlineDark, RoundedCornerShape(18.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("\u30c7\u30f3\u30e2\u30af\u306e\u30ad\u30fc: ", fontSize = 13.sp, color = Color(0xFF90A4AE))
                            IconButton(onClick = { onKeyChange(song.defaultKey - 1) }, modifier = Modifier.size(32.dp)) {
                                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Text(
                                text = when {
                                    song.defaultKey > 0 -> "+${song.defaultKey}"
                                    song.defaultKey == 0 -> "\u00b10"
                                    else -> "${song.defaultKey}"
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonCyan
                            )
                            IconButton(onClick = { onKeyChange(song.defaultKey + 1) }, modifier = Modifier.size(32.dp)) {
                                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        if (song.requestNumber.isNotBlank()) {
                            VerticalDivider(modifier = Modifier.height(24.dp), color = OutlineDark)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("\u4e88\u7d04\u756a\u53f7", fontSize = 10.sp, color = Color(0xFF90A4AE))
                                Text(song.requestNumber, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AmberStar)
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        HapticHelper.playClick(context)
                        onCancel()
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineDark)
                ) {
                    Text("\u30ad\u30e3\u30f3\u30bb\u30eb", color = Color(0xFF90A4AE), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Button(
                    onClick = {
                        HapticHelper.playHeavyClick(context)
                        onInputScore()
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = MidnightBlack),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("\u5165\u529b\u3059\u308b", fontWeight = FontWeight.Black, fontSize = 17.sp)
                }
            }
        }
    }
}