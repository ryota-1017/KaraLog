package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BgPrimary)
    ) {
        // 1px top border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderLight)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { (tab, filledIcon, outlinedIcon) ->
                val isSelected = currentTab == tab
                val icon = if (isSelected) filledIcon else outlinedIcon
                val tint = if (isSelected) IconActive else IconInactive

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
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
