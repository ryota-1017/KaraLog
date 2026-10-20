package com.karalog.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.ui.theme.*

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSecondary)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("環境設定", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextMain)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text("ローカル保存モード", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMain)
            Text("データは端末のSQLite/Roomにのみ保存されます（外部通信0・サーバー代0円）", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text("デザインテーマ", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMain)
            Text("Contemporary Monotone (Instagram / Threads スタイル)", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("KaraLog v2.0 · Minimal Karaoke Assistant", fontSize = 11.sp, color = IconInactive)
        }
    }
}
