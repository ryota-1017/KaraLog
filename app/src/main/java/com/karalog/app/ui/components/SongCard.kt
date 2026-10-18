package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.model.SongWithStats
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper
import com.karalog.app.util.IntentHelper

@Composable
fun SongCard(
    item: SongWithStats,
    onStartSinging: () -> Unit,
    onKeyChange: (newKey: Int) -> Unit,
    onToggleKept: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cardShape = RoundedCornerShape(20.dp)

    Surface(
        shape = cardShape,
        color = SurfaceDark,
        tonalElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, OutlineDark, cardShape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.artist.ifBlank { "\u30a2\u30fc\u30c6\u30a3\u30b9\u30c8\u672a\u8a2d\u5b9a" },
                        fontSize = 13.sp,
                        color = Color(0xFF90A4AE)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            HapticHelper.playClick(context)
                            onToggleKept()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isKept) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "\u30ad\u30fc\u30d7",
                            tint = if (item.isKept) AmberStar else Color(0xFF78909C)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Button(
                        onClick = {
                            HapticHelper.playHeavyClick(context)
                            onStartSinging()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = MidnightBlack
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("\u6b4c\u3046\u3057\uff01", fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.isDamAvailable) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("DAM", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Color(0xFF00E5FF).copy(alpha = 0.15f),
                            labelColor = NeonCyan
                        ),
                        border = null,
                        modifier = Modifier.height(24.dp)
                    )
                }
                if (item.isJoysoundAvailable) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("JOY", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Color(0xFF7C4DFF).copy(alpha = 0.15f),
                            labelColor = Color(0xFFB388FF)
                        ),
                        border = null,
                        modifier = Modifier.height(24.dp)
                    )
                }
                if (item.duration.isNotBlank()) {
                    Text(
                        text = "\u23f1 ${item.duration}",
                        fontSize = 11.sp,
                        color = Color(0xFF78909C)
                    )
                }
                if (item.requestNumber.isNotBlank()) {
                    Text(
                        text = "\u2116 ${item.requestNumber}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AmberStar.copy(alpha = 0.9f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariantDark)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "\u30ad\u30fc",
                        fontSize = 11.sp,
                        color = Color(0xFF78909C),
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    IconButton(
                        onClick = {
                            HapticHelper.playClick(context)
                            onKeyChange(item.defaultKey - 1)
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("-", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(
                        text = when {
                            item.defaultKey > 0 -> "+${item.defaultKey}"
                            item.defaultKey == 0 -> "\u00b10"
                            else -> "${item.defaultKey}"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.defaultKey != 0) NeonCyan else Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = {
                            HapticHelper.playClick(context)
                            onKeyChange(item.defaultKey + 1)
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.maxScore != null) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = String.format("%.3f\u70b9", item.maxScore),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonCyan
                            )
                            Text(
                                text = "${item.singCount}\u56de\u6b4c\u5531",
                                fontSize = 10.sp,
                                color = Color(0xFF78909C)
                            )
                        }
                    } else {
                        Text(
                            text = "\u672a\u6b4c\u5531",
                            fontSize = 12.sp,
                            color = Color(0xFF78909C)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = {
                            HapticHelper.playClick(context)
                            IntentHelper.openSpotifySearch(context, "${item.title} ${item.artist}")
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Spotify",
                            tint = SuccessGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            HapticHelper.playClick(context)
                            IntentHelper.openYouTubeMusicSearch(context, "${item.title} ${item.artist}")
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicVideo,
                            contentDescription = "YouTube Music",
                            tint = CoralRed,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}