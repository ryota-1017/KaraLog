package com.karalog.app.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    modifier: Modifier = Modifier
) {
    val totalCount = records.size
    val avgScore = if (totalCount > 0) String.format("%.3f", records.map { it.record.score }.average()) else "-"
    val maxScore = if (totalCount > 0) String.format("%.3f", records.maxOf { it.record.score }) else "-"
    val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.JAPAN)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSecondary)
        ) {
            listOf(
                "歌唱数" to totalCount.toString(),
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

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (records.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("歌唱履歴がありません", fontSize = 12.sp, color = TextSub)
                    }
                }
            } else {
                items(records, key = { it.record.id }) { item ->
                    val rec = item.record
                    val dateStr = dateFormat.format(Date(rec.date))
                    val keyStr = when {
                        rec.keyChange == 0 -> "±0"
                        rec.keyChange > 0 -> "+ " + rec.keyChange
                        else -> rec.keyChange.toString()
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
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = item.songArtist, fontSize = 11.sp, color = TextSub)
                                    Text(text = "/", fontSize = 10.sp, color = BorderLight)
                                    val mach = if (rec.machineType.contains("DAM")) "DAM" else "JOY"
                                    Text(text = mach, fontSize = 11.sp, color = TextSub)
                                    Text(text = "/", fontSize = 10.sp, color = BorderLight)
                                    Text(text = keyStr, fontSize = 11.sp, color = TextSub)
                                    Text(text = "/", fontSize = 10.sp, color = BorderLight)
                                    Text(text = dateStr.split(" ")[0], fontSize = 11.sp, color = TextSub)
                                }
                            }
                            Text(
                                text = String.format("%.3f", rec.score),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMain
                            )
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))
                    }
                }
            }
        }
    }
}
