package com.karalog.app.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Share
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
import com.karalog.app.data.model.SingingRecordWithSong
import com.karalog.app.ui.theme.*
import com.karalog.app.util.ShareCardHelper
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    records: List<SingingRecordWithSong>,
    sessionStartTime: Long,
    onDeleteRecord: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("SESSION") } // "SESSION" or "ALL"
    val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.JAPAN)

    val displayedRecords = remember(records, selectedTab, sessionStartTime) {
        if (selectedTab == "SESSION") {
            records.filter { it.record.date >= sessionStartTime }
        } else {
            records
        }
    }

    val totalCount = displayedRecords.size
    val avgScore = if (totalCount > 0) String.format("%.3f", displayedRecords.map { it.record.score }.average()) else "-"
    val maxScore = if (totalCount > 0) String.format("%.3f", displayedRecords.maxOf { it.record.score }) else "-"
    val mvpSong = if (totalCount > 0) displayedRecords.maxByOrNull { it.record.score }?.songTitle else "-"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        // Tab Selector & Share Button (Rounded 3D Pills)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("SESSION" to "今日のセッション", "ALL" to "全履歴 (${records.size})").forEach { (tabCode, label) ->
                    val isSel = selectedTab == tabCode
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) TextMain else CardBg,
                        shadowElevation = if (isSel) 2.dp else 1.dp,
                        modifier = Modifier
                            .border(1.dp, if (isSel) TextMain else CardBorder, RoundedCornerShape(16.dp))
                            .clickable { selectedTab = tabCode }
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            color = if (isSel) BgPrimary else TextSub,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            if (selectedTab == "SESSION" && displayedRecords.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CardBg,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .clickable {
                            ShareCardHelper.generateAndShareSetlist(
                                context = context,
                                records = displayedRecords,
                                sessionDate = sessionStartTime
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = TextMain, modifier = Modifier.size(13.dp))
                        Text("セトリ共有", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                    }
                }
            }
        }

        // Summary Header Card (Rounded 3D Card)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CardBg,
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf(
                    "歌唱数" to "$totalCount 曲",
                    "平均点" to avgScore,
                    "最高得点" to maxScore
                ).forEach { (label, value) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = label, fontSize = 11.sp, color = TextSub)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = value,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = TextMain
                        )
                    }
                }
            }
        }

        // MVP Song Banner (Rounded 3D Pill Banner)
        if (selectedTab == "SESSION" && totalCount > 0) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = BgSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .border(0.8.dp, CardBorder, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "👑 本日のMVP曲", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSub)
                    Text(text = mvpSong ?: "-", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Records List (Rounded 3D Cards)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BgPrimary),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            if (displayedRecords.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = CardBg,
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (selectedTab == "SESSION") "本日の歌唱記録はまだありません" else "歌唱履歴がありません",
                                fontSize = 13.sp,
                                color = TextSub
                            )
                        }
                    }
                }
            } else {
                items(displayedRecords, key = { it.record.id }) { item ->
                    val rec = item.record
                    val dateStr = dateFormat.format(Date(rec.date))
                    val keyStr = when {
                        rec.keyChange == 0 -> "±0"
                        rec.keyChange > 0 -> "+${rec.keyChange}"
                        else -> "${rec.keyChange}"
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = CardBg,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                Text(
                                    text = item.songTitle.ifEmpty { "不明な曲" },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextMain
                                )
                                Row(
                                    modifier = Modifier.padding(top = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (item.songArtist.isNotEmpty()) {
                                        Text(text = item.songArtist, fontSize = 11.sp, color = TextSub)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = BgSecondary,
                                        modifier = Modifier.border(0.6.dp, CardBorder, RoundedCornerShape(6.dp))
                                    ) {
                                        Text(
                                            text = rec.machineType,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextSub,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = BgSecondary,
                                        modifier = Modifier.border(0.6.dp, CardBorder, RoundedCornerShape(6.dp))
                                    ) {
                                        Text(
                                            text = keyStr,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = TextMain,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = dateStr,
                                    fontSize = 10.sp,
                                    color = TextSub,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Score Pill Badge
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = BgSecondary,
                                    modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                ) {
                                    Text(
                                        text = String.format("%.3f", rec.score),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = TextMain,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                // Delete Button (Circular 3D)
                                Surface(
                                    shape = CircleShape,
                                    color = BgSecondary,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .border(0.8.dp, CardBorder, CircleShape)
                                        .clickable { onDeleteRecord(rec.id) }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "削除",
                                            tint = IconInactive,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
