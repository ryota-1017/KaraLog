package com.karalog.app.ui.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    totalSongsCount: Int,
    totalRecordsCount: Int,
    currentDefaultMachine: String,
    onDefaultMachineChange: (String) -> Unit,
    currentThemeMode: String = "SYSTEM",
    onThemeModeChange: (String) -> Unit = {},
    onResetSession: () -> Unit,
    onResetAllData: () -> Unit,
    onExportJson: suspend () -> String,
    onImportJson: suspend (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showResetDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgPrimary)
            .verticalScroll(rememberScrollState())
    ) {
        // Section: Appearance & Theme
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSecondary)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("外観 ＆ テーマ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text("テーマ設定", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMain)
            Text("暗いカラオケ部屋では「ダーク (True Black)」が眩しくなく快適です", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
            Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "SYSTEM" to "システム連動",
                    "DARK" to "ダーク (漆黒)",
                    "LIGHT" to "ライト"
                ).forEach { (mode, label) ->
                    val isSel = currentThemeMode == mode
                    Box(
                        modifier = Modifier
                            .clickable { onThemeModeChange(mode) }
                            .background(if (isSel) TextMain else BgSecondary)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) BgPrimary else TextSub
                        )
                    }
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        // Section: Environment
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSecondary)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("環境・機種設定", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        // Default Machine
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text("いつも使うカラオケ機種 (デフォルト)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMain)
            Text("歌唱モード開始時の初期選択になります", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
            Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("DAM", "JOYSOUND").forEach { m ->
                    val isSel = currentDefaultMachine.contains(m)
                    Box(
                        modifier = Modifier
                            .clickable { onDefaultMachineChange(m) }
                            .background(if (isSel) TextMain else BgSecondary)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = m,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) BgPrimary else TextSub
                        )
                    }
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        // Session Reset
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onResetSession()
                    Toast.makeText(context, "今日のセッション記録をリセットしました", Toast.LENGTH_SHORT).show()
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("今日のカラオケセッションを終了", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMain)
                Text("本日の歌唱履歴を締め切り、新しい入室を開始します", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
            }
            Text("リセット", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        // Section: Data & Backup
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSecondary)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("データ管理 ＆ バックアップ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        // Export JSON
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    coroutineScope.launch {
                        val json = onExportJson()
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("KaraLog Backup", json)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "全データをクリップボードにコピーしました", Toast.LENGTH_SHORT).show()
                    }
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("データをJSONで書き出し (エクスポート)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMain)
                Text("全楽曲・スコア履歴をクリップボードにコピーします", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
            }
            Text("コピー", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        // Import JSON
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showImportDialog = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("JSONからデータを復元 (インポート)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMain)
                Text("以前書き出したJSONから楽曲と履歴を復元します", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
            }
            Text("復元", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        // Reset All Data
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showResetDialog = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("全データを初期化", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMain)
                Text("登録された曲とすべての履歴を完全に削除します", fontSize = 11.sp, color = TextSub, modifier = Modifier.padding(top = 2.dp))
            }
            Text("初期化", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        // Section: Storage Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSecondary)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("ストレージ ＆ プライバシー", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text("完全ローカル・プライベート保護", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMain)
            Text(
                "登録曲: $totalSongsCount 曲 / 歌唱履歴: $totalRecordsCount 件\n" +
                        "データは端末内のSQLite/Roomにのみ保存され、外部通信なし・サーバー代0円・登録不要で利用可能です。",
                fontSize = 11.sp,
                color = TextSub,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderLight))

        // App Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("KaraLog v1.4.0 · Minimal Karaoke Assistant", fontSize = 11.sp, color = IconInactive)
        }
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("バックアップJSONの復元", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("コピーしたJSONテキストを貼り付けてください：", fontSize = 11.sp, color = TextSub)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importInput,
                        onValueChange = { importInput = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("{\"version\":\"1.4.0\"...}", fontSize = 10.sp) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val success = onImportJson(importInput)
                            if (success) {
                                Toast.makeText(context, "データを復元しました", Toast.LENGTH_SHORT).show()
                                showImportDialog = false
                            } else {
                                Toast.makeText(context, "JSON形式が正しくありません", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = BgPrimary),
                    shape = RectangleShape
                ) {
                    Text("復元実行", fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("キャンセル", fontSize = 12.sp, color = TextSub)
                }
            },
            containerColor = BgPrimary
        )
    }

    // Reset Confirm Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("全データの初期化", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Text("登録したすべての曲と歌唱記録が削除されます。本当によろしいですか？", fontSize = 12.sp, color = TextSub)
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetAllData()
                        Toast.makeText(context, "すべてのデータを初期化しました", Toast.LENGTH_SHORT).show()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = BgPrimary),
                    shape = RectangleShape
                ) {
                    Text("初期化する", fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("キャンセル", fontSize = 12.sp, color = TextSub)
                }
            },
            containerColor = BgPrimary
        )
    }
}
