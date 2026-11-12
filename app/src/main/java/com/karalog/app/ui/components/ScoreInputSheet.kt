package com.karalog.app.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper
import com.karalog.app.util.ScoreOcrHelper

@Composable
fun ScoreInputSheet(
    songTitle: String,
    onScoreSubmit: (score: Double, pitch: Double?, notes: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var input by remember { mutableStateOf("") }
    var pitchInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }
    var isDetailExpanded by remember { mutableStateOf(false) }
    var isOcrProcessing by remember { mutableStateOf(false) }

    // Photo / Screenshot picker for OCR score recognition
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isOcrProcessing = true
            ScoreOcrHelper.recognizeScoreFromUri(
                context = context,
                imageUri = uri,
                onSuccess = { result ->
                    isOcrProcessing = false
                    if (result.score != null) {
                        input = String.format(java.util.Locale.ENGLISH, "%.3f", result.score)
                        if (result.pitchAccuracy != null) {
                            pitchInput = String.format(java.util.Locale.ENGLISH, "%.1f", result.pitchAccuracy)
                            isDetailExpanded = true
                        }
                        HapticHelper.playSuccess(context)
                        Toast.makeText(context, "採点結果を自動読み取りしました", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "数値を検出できませんでした。テンキーで入力してください", Toast.LENGTH_SHORT).show()
                    }
                },
                onError = {
                    isOcrProcessing = false
                    Toast.makeText(context, "画像解析エラー: " + it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    fun onKey(char: String) {
        HapticHelper.playClick(context)
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
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("採点スコア入力", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMain)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // OCR Camera Button
                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "OCR採点読取", tint = TextMain, modifier = Modifier.size(18.dp))
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = IconInactive, modifier = Modifier.size(18.dp))
                }
            }
        }

        Text(
            text = songTitle,
            fontSize = 11.sp,
            color = TextSub,
            maxLines = 1,
            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
        )

        // Main Score Display (Monospace tabular typography)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSecondary)
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isOcrProcessing) {
                CircularProgressIndicator(color = TextMain, modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    text = if (input.isEmpty()) "00.000" else input,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (input.isEmpty()) TextSub else TextMain,
                    letterSpacing = 1.sp
                )
            }
        }

        // Camera OCR Quick Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { imagePickerLauncher.launch("image/*") },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = TextSub, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("採点画面のスクショ/写真から自動読取", fontSize = 11.sp, color = TextSub)
        }

        // Keypad
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
                            .height(44.dp)
                            .background(BgSecondary)
                            .clickable { onKey(k) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (k == "BS") {
                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "BS", tint = TextMain, modifier = Modifier.size(18.dp))
                        } else {
                            Text(text = k, fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, color = TextMain)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Detail Parameters Accordion
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable { isDetailExpanded = !isDetailExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("詳細パラメータ（音程正確率・メモ）", fontSize = 11.sp, color = TextSub)
            Icon(
                if (isDetailExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = TextSub,
                modifier = Modifier.size(16.dp)
            )
        }

        AnimatedVisibility(visible = isDetailExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSecondary)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = pitchInput,
                    onValueChange = { if (it.length <= 5) pitchInput = it },
                    label = { Text("音程正確率 (%)", fontSize = 10.sp) },
                    placeholder = { Text("例: 89.2", fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextMain,
                        unfocusedBorderColor = BorderLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("歌唱メモ (ビブラート・キー感触など)", fontSize = 10.sp) },
                    placeholder = { Text("例: ビブラート重視、サビの高音安定", fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextMain,
                        unfocusedBorderColor = BorderLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val score = input.toDoubleOrNull()
                if (score != null && score in 0.0..100.0) {
                    val pitch = pitchInput.toDoubleOrNull()
                    HapticHelper.playSuccess(context)
                    onScoreSubmit(score, pitch, notesInput)
                }
            },
            enabled = input.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = TextMain, contentColor = BgPrimary),
            shape = RectangleShape,
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) {
            Text("スコアを確定して記録", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
