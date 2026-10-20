package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.data.local.entity.SongEntity
import com.karalog.app.ui.theme.*

@Composable
fun NowSingingView(
    song: SongEntity?,
    currentKey: Int,
    selectedMachine: String,
    onKeyChange: (Int) -> Unit,
    onMachineChange: (String) -> Unit,
    onInputScoreClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyStr = when {
        currentKey == 0 -> "±0"
        currentKey > 0 -> "+ ${currentKey}"
        else -> "${currentKey}"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BgPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSecondary)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "歌唱中の楽曲", fontSize = 11.sp, color = TextSub)
                if (song != null) {
                    Text(
                        text = "曲を変更",
                        fontSize = 11.sp,
                        color = TextSub,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            Text(
                text = song?.title ?: "フリー歌唱 (曲指定なし)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMain,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (song != null) {
                val req = if (song.requestNumber.isNotEmpty()) song.requestNumber else "-"
                Text(
                    text = song.artist + " / 予約番号: " + req,
                    fontSize = 12.sp,
                    color = TextSub,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "機種:", fontSize = 12.sp, color = TextSub)
                listOf("DAM", "JOYSOUND").forEach { m ->
                    val isSel = selectedMachine.contains(m)
                    Text(
                        text = m,
                        fontSize = 11.sp,
                        color = if (isSel) BgPrimary else TextSub,
                        modifier = Modifier
                            .background(if (isSel) TextMain else BgSecondary)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "キー:", fontSize = 12.sp, color = TextSub)
                IconButton(
                    onClick = { onKeyChange(currentKey - 1) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "-", tint = TextMain, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = keyStr,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMain
                )
                IconButton(
                    onClick = { onKeyChange(currentKey + 1) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "+", tint = TextMain, modifier = Modifier.size(16.dp))
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancelClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMain),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shape = androidx.compose.ui.graphics.RectangleShape,
                modifier = Modifier.weight(1f).height(40.dp)
            ) {
                Text("キャンセル", fontSize = 12.sp)
            }
            Button(
                onClick = onInputScoreClick,
                colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = BgPrimary),
                shape = androidx.compose.ui.graphics.RectangleShape,
                modifier = Modifier.weight(1.5f).height(40.dp)
            ) {
                Text("スコアを入力", fontSize = 12.sp)
            }
        }
    }
}
