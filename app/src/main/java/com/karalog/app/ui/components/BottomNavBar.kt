package com.karalog.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.karalog.app.ui.theme.*

enum class BottomTab {
    HOME, SEARCH, SING, HISTORY, SETTINGS
}

@Composable
fun BottomNavBar(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val tabs = listOf(
        Triple(BottomTab.HOME, Icons.Filled.Home, Icons.Outlined.Home),
        Triple(BottomTab.SEARCH, Icons.Filled.Search, Icons.Outlined.Search),
        Triple(BottomTab.SING, Icons.Filled.Mic, Icons.Outlined.Mic),
        Triple(BottomTab.HISTORY, Icons.Filled.Schedule, Icons.Outlined.Schedule),
        Triple(BottomTab.SETTINGS, Icons.Filled.Tune, Icons.Outlined.Tune)
    )

    val dockBgColor = if (isDark) Color(0xFF16181D) else Color(0xFFFFFFFF)
    val dockBorderColor = if (isDark) Color(0x38FFFFFF) else Color(0x1F000000)
    val dockShadowColor = if (isDark) Color(0x8C000000) else Color(0x26000000)
    val activeIconColor = if (isDark) Color.White else Color(0xFF16181D)
    val inactiveIconColor = if (isDark) Color(0xFF8E95A5) else Color(0xFF8A92A0)
    val activePillBg = if (isDark) Color(0xFF282C35) else Color(0xFFEFF2F6)

    // True Floating Island Dock with subtle transparent backdrop
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    if (isDark) listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.3f),
                        Color.Black.copy(alpha = 0.85f)
                    ) else listOf(
                        Color.Transparent,
                        Color(0xFFF6F7FA).copy(alpha = 0.3f),
                        Color(0xFFF6F7FA).copy(alpha = 0.85f)
                    )
                )
            )
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating 3D Capsule Dock
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = dockBgColor,
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = dockShadowColor,
                    spotColor = dockShadowColor
                )
                .border(
                    width = 1.2.dp,
                    color = dockBorderColor,
                    shape = RoundedCornerShape(32.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { (tab, filledIcon, outlinedIcon) ->
                    val isSelected = currentTab == tab
                    val icon = if (isSelected) filledIcon else outlinedIcon
                    val tint by animateColorAsState(
                        if (isSelected) activeIconColor else inactiveIconColor,
                        label = "tabTint"
                    )
                    val pillBg by animateColorAsState(
                        if (isSelected) activePillBg else Color.Transparent,
                        label = "tabBg"
                    )

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(pillBg)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onTabSelected(tab) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
