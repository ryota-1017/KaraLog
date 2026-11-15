package com.karalog.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.unit.dp
import com.karalog.app.ui.theme.*

enum class BottomTab {
    HOME, SEARCH, SING, HISTORY, SETTINGS
}

@Composable
fun BottomNavBar(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        Triple(BottomTab.HOME, Icons.Filled.Home, Icons.Outlined.Home),
        Triple(BottomTab.SEARCH, Icons.Filled.Search, Icons.Outlined.Search),
        Triple(BottomTab.SING, Icons.Filled.Mic, Icons.Outlined.Mic),
        Triple(BottomTab.HISTORY, Icons.Filled.Schedule, Icons.Outlined.Schedule),
        Triple(BottomTab.SETTINGS, Icons.Filled.Tune, Icons.Outlined.Tune)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BgPrimary)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating 3D Rounded Island Dock
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = CardBg,
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .border(1.2.dp, CardBorder, RoundedCornerShape(32.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { (tab, filledIcon, outlinedIcon) ->
                    val isSelected = currentTab == tab
                    val icon = if (isSelected) filledIcon else outlinedIcon
                    val tint by animateColorAsState(if (isSelected) TextMain else IconInactive, label = "tabTint")
                    val pillBg by animateColorAsState(if (isSelected) BgSecondary else CardBg, label = "tabBg")

                    Box(
                        modifier = Modifier
                            .size(44.dp)
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
