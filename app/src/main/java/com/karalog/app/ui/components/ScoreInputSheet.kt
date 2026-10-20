package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.ui.theme.*

@Composable
fun ScoreInputSheet(
    songTitle: String,
    onScoreSubmit: (Double) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var input by remember { mutableStateOf("") }

    fun onKey(char: String) {
        if (char == "BS") {
            if (input.isNotEmpty()) input = input.dropLast(1)
            return
        }
        if (char == "." && input.contains(".")) return
        if (input.length >= 6) return

        val next = input + char
        if (next.length == 3 && !next.contains(".")) {
            input = next.substring(0, 2) + "." + next.substring(2)
        } else {
            input = next
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BgPrimary)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("採点スコア入力", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = IconInactive, modifier = Modifier.size(18.dp))
            }
        }

        Text(
            text = songTitle,
            fontSize = 11.sp,
            color = TextSub,
            maxLines = 1,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSecondary)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (input.isEmpty()) "00.000" else input,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMain
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "BS")
        keys.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { k ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .background(BgSecondary)
                            .clickable { onKey(k) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (k == "BS") {
                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "BS", tint = TextMain, modifier = Modifier.size(18.dp))
                        } else {
                            Text(text = k, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextMain)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val score = input.toDoubleOrNull()
                if (score != null && score in 0.0..100.0) {
                    onScoreSubmit(score)
                }
            },
            enabled = input.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = BgPrimary),
            shape = RectangleShape,
            modifier = Modifier.fillMaxWidth().height(42.dp)
        ) {
            Text("保存", fontSize = 13.sp)
        }
    }
}
