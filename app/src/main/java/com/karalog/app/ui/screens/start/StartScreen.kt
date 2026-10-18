package com.karalog.app.ui.screens.start

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper

@Composable
fun StartScreen(
    onStartSession: (machine: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedMachine by remember { mutableStateOf("DAM (\u7cbe\u5bc6\u63a1\u70b9Ai)") }

    val machines = listOf(
        "DAM (\u7cbe\u5bc6\u63a1\u70b9Ai)",
        "DAM (\u7cbe\u5bc6\u63a1\u70b9DX-G)",
        "JOYSOUND (\u5206\u6790\u63a1\u70b9Ai)",
        "JOYSOUND (\u5206\u6790\u63a1\u70b9MAX)"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Brush.linearGradient(listOf(NeonCyan, VibrantBlue))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = MidnightBlack, modifier = Modifier.size(36.dp))
                }
                Text(
                    text = "KaraLog",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "\u4eca\u65e5\u306e\u30ab\u30e9\u30aa\u30b1\u3092\u306f\u3058\u3081\u307e\u3057\u3087\u3046",
                    fontSize = 14.sp,
                    color = Color(0xFF90A4AE)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "\u6a5f\u7a2e\u3092\u9078\u629e\u3057\u3066\u304f\u3060\u3055\u3044",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                machines.forEach { machine ->
                    val isSelected = selectedMachine == machine
                    val isDam = machine.contains("DAM")
                    val accentColor = if (isDam) NeonCyan else VibrantPurple

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) SurfaceVariantDark else SurfaceDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) accentColor else OutlineDark,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable {
                                HapticHelper.playClick(context)
                                selectedMachine = machine
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(accentColor)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = machine,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    HapticHelper.playClick(context)
                                    selectedMachine = machine
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = accentColor)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    HapticHelper.playHeavyClick(context)
                    onStartSession(selectedMachine)
                },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = MidnightBlack),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("\u30ab\u30e9\u30aa\u30b1\u3092\u958b\u59cb\u3059\u308b", fontSize = 17.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}