package com.karalog.app.ui.screens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper

@Composable
fun SettingsScreen(
    onResetData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("\u30a2\u30d7\u30ea\u8a2d\u5b9a", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = SurfaceDark,
            modifier = Modifier.fillMaxWidth().border(1.dp, OutlineDark, RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("\u5b89\u5fc3\u306e\u5b8c\u5168\u30ed\u30fc\u30ab\u30eb\u4fdd\u5b58", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Text(
                    text = "\u3059\u3079\u3066\u306e\u66f2\u30c7\u30fc\u30bf\u3084\u6b4c\u5531\u5f97\u70b9\u306f\u3001\u304a\u4f7f\u3044\u306e\u7aef\u672b\u5185\u306b\u306e\u307f\u4fdd\u5b58\u3055\u308c\u307e\u3059\u3002\u5916\u90e8\u30b5\u30fc\u30d0\u30fc\u3078\u306e\u901a\u4fe1\u306f\u4e00\u5207\u306a\u304f\u3001\u30b5\u30fc\u30d0\u30fc\u8cbb\u7528\u306f\u5b8c\u51680\u5186\u3001\u30d7\u30e9\u30a4\u30d0\u30b7\u30fc\u3082\u5b88\u3089\u308c\u307e\u3059\u3002",
                    fontSize = 12.sp,
                    color = Color(0xFF90A4AE),
                    lineHeight = 18.sp
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = SurfaceDark,
            modifier = Modifier.fillMaxWidth().border(1.dp, CoralRed.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("\u30c7\u30fc\u30bf\u306e\u521d\u671f\u5316", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CoralRed)
                    Text("\u7aef\u672b\u5185\u306e\u5168\u8a18\u9332\u3068\u66f2\u3092\u30ea\u30bb\u30c3\u30c8\u3057\u307e\u3059", fontSize = 11.sp, color = Color(0xFF90A4AE))
                }
                Button(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("\u521d\u671f\u5316", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("\u30c7\u30fc\u30bf\u3092\u521d\u671f\u5316\u3057\u307e\u3059\u304b\uff1f") },
            text = { Text("\u3059\u3079\u3066\u306e\u767b\u9332\u66f2\u3068\u6b4c\u5531\u8a18\u9332\u304c\u5b8c\u5168\u306b\u524a\u9664\u3055\u308c\u307e\u3059\u3002\u3053\u306e\u64cd\u4f5c\u306f\u53d6\u308a\u6d88\u305b\u307e\u305b\u3093\u3002") },
            confirmButton = {
                Button(
                    onClick = {
                        HapticHelper.playHeavyClick(context)
                        showResetDialog = false
                        onResetData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("\u521d\u671f\u5316\u3059\u308b")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("\u30ad\u30e3\u30f3\u30bb\u30eb")
                }
            }
        )
    }
}