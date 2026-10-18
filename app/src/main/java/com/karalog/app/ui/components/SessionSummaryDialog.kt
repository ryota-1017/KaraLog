package com.karalog.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
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
import com.karalog.app.data.local.entity.SingingRecordEntity
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper

@Composable
fun SessionSummaryDialog(
    sessionRecords: List<Pair<String, SingingRecordEntity>>,
    onFinishSession: () -> Unit
) {
    val context = LocalContext.current
    val count = sessionRecords.size
    val maxRecord = sessionRecords.maxByOrNull { it.second.score }
    val avgScore = if (count > 0) sessionRecords.map { it.second.score }.average() else 0.0

    Dialog(onDismissRequest = onFinishSession) {
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
                Icon(Icons.Default.Celebration, contentDescription = null, tint = AmberStar, modifier = Modifier.size(40.dp))

                Text(
                    text = "\u672c\u65e5\u306e\u30ab\u30e9\u30aa\u30b1\u7d50\u679c",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MidnightBlack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("\u6b4c\u5531\u66f2\u6570", fontSize = 13.sp, color = Color(0xFF90A4AE))
                            Text("$count \u66f2", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("\u672c\u65e5\u306e\u5e73\u5747\u70b9", fontSize = 13.sp, color = Color(0xFF90A4AE))
                            Text(String.format("%.1f \u70b9", avgScore), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        }
                        if (maxRecord != null) {
                            HorizontalDivider(color = OutlineDark)
                            Column {
                                Text("\u672c\u65e5\u306e\u30d9\u30b9\u30c8\u30bd\u30f3\u30b0", fontSize = 11.sp, color = AmberStar)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(maxRecord.first, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(String.format("%.3f \u70b9", maxRecord.second.score), fontSize = 15.sp, fontWeight = FontWeight.Black, color = AmberStar)
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        HapticHelper.playClick(context)
                        onFinishSession()
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = MidnightBlack),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("\u304a\u3064\u304b\u308c\u3055\u307e\u3067\u3057\u305f\uff01", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }
        }
    }
}