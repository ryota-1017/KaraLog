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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
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
        when (char) {
            "BS" -> {
                if (input.isNotEmpty()) {
                    input = input.dropLast(1)
                }
            }
            "." -> {
                if (!input.contains(".") && input.isNotEmpty()) {
                    input += "."
                }
            }
            else -> {
                val parts = input.split(".")
                val integerPart = parts[0]
                val decimalPart = if (parts.size > 1) parts[1] else null

                if (decimalPart != null) {
                    if (decimalPart.length < 3) {
                        input += char
                    }
                } else {
                    if (integerPart.length < 2) {
                        input += char
                    } else if (integerPart.length == 2 && char == "0" && integerPart == "10") {
                        input += char
                    } else if (input.length == 2) {
                        input += ".$char"
                    }
                }
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = CardBg,
        shadowElevation = 10.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.2.dp, CardBorder, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Drag handle pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = CardBorder,
                    modifier = Modifier.size(width = 36.dp, height = 4.dp)
                ) {}
            }

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "スコアを入力", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextMain)
                    Text(text = songTitle, fontSize = 12.sp, color = TextSub)
                }
                Surface(
                    shape = CircleShape,
                    color = BgSecondary,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onDismiss() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = IconInactive, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Score Display (Rounded 3D Display)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = BgSecondary,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isOcrProcessing) {
                        CircularProgressIndicator(color = TextMain, modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = if (input.isEmpty()) "00.000" else input,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (input.isEmpty()) TextSub else TextMain,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Camera OCR Quick Pill Bar
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CardBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .clickable { imagePickerLauncher.launch("image/*") }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = TextMain, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("採点画面のスクショ/写真から自動読取", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
                }
            }

            // Keypad (Rounded 3D Tactile Keys)
            val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "BS")
            keys.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { k ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = BgSecondary,
                            shadowElevation = 1.5.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                .clickable { onKey(k) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (k == "BS") {
                                    Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "BS", tint = TextMain, modifier = Modifier.size(18.dp))
                                } else {
                                    Text(
                                        text = k,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = TextMain
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Detail Parameters Accordion (Rounded Pill)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CardBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(0.8.dp, CardBorder, RoundedCornerShape(14.dp))
                    .clickable { isDetailExpanded = !isDetailExpanded }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("詳細パラメータ（音程正確率・メモ）", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSub)
                    Icon(
                        if (isDetailExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextSub,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isDetailExpanded) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BgSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = pitchInput,
                            onValueChange = { if (it.length <= 5) pitchInput = it },
                            label = { Text("音程正確率 (%)", fontSize = 10.sp) },
                            placeholder = { Text("例: 89.2", fontSize = 11.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("歌唱メモ (ビブラート・キー感触など)", fontSize = 10.sp) },
                            placeholder = { Text("例: ビブラート重視、サビの高音安定", fontSize = 11.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(22.dp),
                color = if (input.isNotEmpty()) TextMain else BgSecondary,
                shadowElevation = if (input.isNotEmpty()) 3.dp else 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .clickable(enabled = input.isNotEmpty()) {
                        val score = input.toDoubleOrNull()
                        if (score != null && score in 0.0..100.0) {
                            val pitch = pitchInput.toDoubleOrNull()
                            HapticHelper.playSuccess(context)
                            onScoreSubmit(score, pitch, notesInput)
                        }
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "スコアを確定して記録",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (input.isNotEmpty()) BgPrimary else IconInactive
                    )
                }
            }
        }
    }
}
