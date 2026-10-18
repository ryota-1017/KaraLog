package com.karalog.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper

@Composable
fun ScoreInputSheet(
    songTitle: String,
    onDismissRequest: () -> Unit,
    onSubmit: (score: Double, memo: String) -> Unit
) {
    val context = LocalContext.current
    var rawDigits by remember { mutableStateOf("") }
    var showDetail by remember { mutableStateOf(false) }
    var memo by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val displayScore = remember(rawDigits) {
        when {
            rawDigits.isEmpty() -> "--.---"
            rawDigits.length == 1 -> "$rawDigits"
            rawDigits.length == 2 -> "$rawDigits."
            else -> {
                val integerPart = rawDigits.substring(0, 2)
                val decimalPart = rawDigits.substring(2).take(3)
                "$integerPart.$decimalPart"
            }
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = songTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    IconButton(onClick = onDismissRequest, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "\u9589\u3058\u308b", tint = Color(0xFF90A4AE))
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MidnightBlack)
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("\u5f97\u70b9", fontSize = 11.sp, color = Color(0xFF90A4AE))
                    Text(
                        text = "$displayScore \u70b9",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = if (rawDigits.isNotEmpty()) NeonCyan else Color(0xFF546E7A)
                    )
                }

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = CoralRed, fontSize = 12.sp)
                }

                AnimatedVisibility(visible = showDetail) {
                    OutlinedTextField(
                        value = memo,
                        onValueChange = { memo = it },
                        placeholder = { Text("\u30e1\u30e2\uff08\u88cf\u58f0\u30fb\u97f3\u7a0b\u6b63\u78ba\u7387\u306a\u3069\uff09") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = OutlineDark
                        )
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val rows = listOf(
                        listOf("7", "8", "9"),
                        listOf("4", "5", "6"),
                        listOf("1", "2", "3"),
                        listOf("\u8a73\u7d30", "0", "DEL")
                    )

                    rows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { key ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = when (key) {
                                        "\u8a73\u7d30" -> if (showDetail) VibrantPurple.copy(alpha = 0.3f) else SurfaceVariantDark
                                        "DEL" -> SurfaceVariantDark
                                        else -> MidnightBlack
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .border(1.dp, OutlineDark, RoundedCornerShape(14.dp))
                                        .clickable {
                                            HapticHelper.playClick(context)
                                            when (key) {
                                                "DEL" -> {
                                                    if (rawDigits.isNotEmpty()) {
                                                        rawDigits = rawDigits.dropLast(1)
                                                    }
                                                }
                                                "\u8a73\u7d30" -> showDetail = !showDetail
                                                else -> {
                                                    if (rawDigits.length < 5) {
                                                        rawDigits += key
                                                    }
                                                }
                                            }
                                            errorMessage = null
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (key == "DEL") {
                                            Icon(Icons.Default.Backspace, contentDescription = null, tint = Color(0xFF90A4AE), modifier = Modifier.size(18.dp))
                                        } else {
                                            Text(
                                                text = key,
                                                fontSize = if (key == "\u8a73\u7d30") 13.sp else 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (key == "\u8a73\u7d30") NeonCyan else Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        val scoreDouble = when {
                            rawDigits.length >= 3 -> {
                                val intPart = rawDigits.substring(0, 2)
                                val decPart = rawDigits.substring(2)
                                "$intPart.$decPart".toDoubleOrNull()
                            }
                            rawDigits.isNotEmpty() -> rawDigits.toDoubleOrNull()
                            else -> null
                        }

                        if (scoreDouble == null || scoreDouble < 0.0 || scoreDouble > 100.0) {
                            errorMessage = "0\u301c100\u306e\u70b9\u6570\u3092\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044"
                            HapticHelper.playClick(context)
                        } else {
                            HapticHelper.playSuccess(context)
                            onSubmit(scoreDouble, memo.trim())
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = MidnightBlack),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("\u6c7a\u5b9a", fontSize = 17.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}