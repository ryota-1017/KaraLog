package com.karalog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.karalog.app.ui.theme.*

@Composable
fun StoriesActionTray(
    onSearchClick: () -> Unit,
    onSingClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StoryCircleItem(icon = Icons.Default.Search, onClick = onSearchClick)
        StoryCircleItem(icon = Icons.Default.Mic, onClick = onSingClick)
        StoryCircleItem(icon = Icons.Default.Schedule, onClick = onHistoryClick)
        StoryCircleItem(icon = Icons.Default.Add, onClick = onAddClick)
    }
}

@Composable
private fun StoryCircleItem(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(BgPrimary)
            .border(1.5.dp, TextMain, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextMain,
            modifier = Modifier.size(24.dp)
        )
    }
}