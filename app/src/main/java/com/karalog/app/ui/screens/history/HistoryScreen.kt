package com.karalog.app.ui.screens.history

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.model.SingingRecordWithSong
import com.karalog.app.data.model.SongWithStats
import com.karalog.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    records: List<SingingRecordWithSong>,
    songsWithStats: List<SongWithStats>,
    modifier: Modifier = Modifier
) {
    val totalSings = records.size
    val bestRecord = records.maxByOrNull { it.record.score }
    val avgScore = if (totalSings > 0) records.map { it.record.score }.average() else 0.0
    val topSongs = songsWithStats.filter { it.maxScore != null }.sortedByDescending { it.maxScore }.take(5)

    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.JAPAN)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineDark, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\u901a\u7b97\u6b4c\u5531", fontSize = 11.sp, color = Color(0xFF90A4AE))
                        Text("$totalSings \u56de", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                    VerticalDivider(modifier = Modifier.height(30.dp), color = OutlineDark)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\u6700\u9ad8\u5f97\u70b9", fontSize = 11.sp, color = Color(0xFF90A4AE))
                        Text(
                            text = if (bestRecord != null) String.format("%.3f", bestRecord.record.score) else "--",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan
                        )
                    }
                    VerticalDivider(modifier = Modifier.height(30.dp), color = OutlineDark)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\u5e73\u5747\u5f97\u70b9", fontSize = 11.sp, color = Color(0xFF90A4AE))
                        Text(
                            text = if (totalSings > 0) String.format("%.1f", avgScore) else "--",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = VibrantPurple
                        )
                    }
                }
            }
        }

        if (topSongs.isNotEmpty()) {
            item {
                Text("\u81ea\u5df1\u30d9\u30b9\u30c8\u30e9\u30f3\u30ad\u30f3\u30b0 TOP 5", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AmberStar)
            }
            items(topSongs) { item ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    modifier = Modifier.fillMaxWidth().border(1.dp, OutlineDark, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(item.artist.ifBlank { "\u30a2\u30fc\u30c6\u30a3\u30b9\u30c8\u672a\u8a2d\u5b9a" }, fontSize = 12.sp, color = Color(0xFF90A4AE))
                        }
                        Text(
                            text = String.format("%.3f\u70b9", item.maxScore ?: 0.0),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan
                        )
                    }
                }
            }
        }

        item {
            Text("\u6b4c\u5531\u5c65\u6b74\u4e00\u89a7", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (records.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("\u307e\u3060\u6b4c\u5531\u5c65\u6b74\u304c\u3042\u308a\u307e\u305b\u3093", color = Color(0xFF90A4AE), fontSize = 13.sp)
                }
            }
        } else {
            items(records) { item ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    modifier = Modifier.fillMaxWidth().border(1.dp, OutlineDark, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.songTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                text = "${dateFormat.format(Date(item.record.date))} \u00b7 ${item.record.machineType}",
                                fontSize = 11.sp,
                                color = Color(0xFF90A4AE)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = String.format("%.3f\u70b9", item.record.score),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonCyan
                            )
                            val keyLabel = when {
                                item.record.keyChange > 0 -> "+${item.record.keyChange}"
                                item.record.keyChange == 0 -> "\u00b10"
                                else -> "${item.record.keyChange}"
                            }
                            Text("\u30ad\u30fc: $keyLabel", fontSize = 11.sp, color = Color(0xFF90A4AE))
                        }
                    }
                }
            }
        }
    }
}