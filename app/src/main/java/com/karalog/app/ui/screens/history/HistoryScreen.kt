package com.karalog.app.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.model.SingingRecordWithSong
import com.karalog.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    records: List<SingingRecordWithSong>,
    sessionStartTime: Long,
    onDeleteRecord: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
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
        // Tab Selector: [今日のセッション] vs [全履歴]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgPrimary)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("SESSION" to "今日のセッション", "ALL" to "全履歴 (${records.size})").forEach { (tabCode, label) ->
                val isSel = selectedTab == tabCode
                Box(
                    modifier = Modifier
                        .clickable { selectedTab = tabCode }
                        .background(if (isSel) TextMain else BgSecondary)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = if (isSel) BgPrimary else TextSub,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        // Summary Header Grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSecondary)
        ) {
            listOf(
                "歌唱数" to "$totalCount 曲",
                "平均点" to avgScore,
                "最高得点" to maxScore
            ).forEach { (label, value) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = label, fontSize = 10.sp, color = TextSub)
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMain
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        // MVP Song Banner (if in session and has record)
        if (selectedTab == "SESSION" && totalCount > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "👑 今日のMVP曲:", fontSize = 11.sp, color = TextSub)
                Text(text = mvpSong ?: "-", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))
        }

        // Records List
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (displayedRecords.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedTab == "SESSION") "本日の歌唱記録はまだありません" else "歌唱履歴がありません",
                            fontSize = 12.sp,
                            color = TextSub
                        )
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

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                Text(
                                    text = item.songTitle.ifEmpty { "不明な曲" },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextMain
                                )
                                Row(
                                    modifier = Modifier.padding(top = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (item.songArtist.isNotEmpty()) {
                                        Text(text = item.songArtist, fontSize = 11.sp, color = TextSub)
                                        Text(text = "/", fontSize = 10.sp, color = BorderLight)
                                    }
                                    val mach = if (rec.machineType.contains("DAM")) "DAM" else "JOY"
                                    Text(text = mach, fontSize = 11.sp, color = TextSub)
                                    Text(text = "/", fontSize = 10.sp, color = BorderLight)
                                    Text(text = "キー $keyStr", fontSize = 11.sp, color = TextSub)
                                    Text(text = "/", fontSize = 10.sp, color = BorderLight)
                                    Text(text = dateStr, fontSize = 10.sp, color = TextSub)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = String.format("%.3f", rec.score),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMain
                                )
                                IconButton(
                                    onClick = { onDeleteRecord(rec.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = IconInactive,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))
                    }
                }
            }
        }
    }
}