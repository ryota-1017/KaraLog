package com.karalog.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karalog.app.ui.theme.*
import com.karalog.app.util.HapticHelper

enum class NavTab(val title: String, val icon: ImageVector) {
    SETTINGS("\u8a2d\u5b9a", Icons.Default.Settings),
    HISTORY("\u307e\u3068\u3081", Icons.Default.BarChart),
    HOME("\u30db\u30fc\u30e0", Icons.Default.Home),
    RECOMMEND("\u304a\u3059\u3059\u3081", Icons.Default.AutoAwesome),
    END_SESSION("\u7d42\u4e86", Icons.Default.Logout)
}

@Composable
fun BottomNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val navShape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = navShape,
            color = SurfaceDark.copy(alpha = 0.95f),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, OutlineDark, navShape)
                .shadow(12.dp, navShape, clip = false)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavTab.values().forEach { tab ->
                    val isSelected = tab == selectedTab
                    val isHome = tab == NavTab.HOME
                    val isEnd = tab == NavTab.END_SESSION

                    val pillColor by animateColorAsState(
                        targetValue = when {
                            isSelected && isHome -> NeonCyan.copy(alpha = 0.2f)
                            isSelected -> VibrantPurple.copy(alpha = 0.25f)
                            else -> Color.Transparent
                        },
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "pillBg"
                    )

                    val iconTint by animateColorAsState(
                        targetValue = when {
                            isSelected && isHome -> NeonCyan
                            isSelected -> VibrantPurple
                            isEnd -> CoralRed.copy(alpha = 0.85f)
                            else -> Color(0xFF78909C)
                        },
                        label = "iconTint"
                    )

                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.08f else 1.0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "iconScale"
                    )

                    Box(
                        modifier = Modifier
                            .weight(if (isHome) 1.2f else 1.0f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(pillColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                HapticHelper.playClick(context)
                                onTabSelected(tab)
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.scale(scale)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = iconTint,
                                modifier = Modifier.size(if (isHome) 26.dp else 22.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.title,
                                fontSize = if (isHome) 11.sp else 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = iconTint
                            )
                        }
                    }
                }
            }
        }
    }
}