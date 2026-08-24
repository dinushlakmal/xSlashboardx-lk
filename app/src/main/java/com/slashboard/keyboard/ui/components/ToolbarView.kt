package com.slashboard.keyboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slashboard.keyboard.data.model.KeyboardTheme

@Composable
fun ToolbarView(
    theme: KeyboardTheme,
    activeLayoutName: String,
    modifier: Modifier = Modifier,
    onOpenClipboard: () -> Unit,
    onOpenEmoji: () -> Unit,
    onCycleTheme: () -> Unit,
    onCycleLayout: () -> Unit,
    onCursorLeft: () -> Unit,
    onCursorRight: () -> Unit,
    onClearAll: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(theme.surface)
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ToolbarIconButton(
                icon = Icons.AutoMirrored.Filled.Assignment,
                contentDescription = "Clipboard",
                theme = theme,
                testTag = "toolbar_clipboard",
                onClick = onOpenClipboard
            )

            ToolbarIconButton(
                icon = Icons.Default.Mood,
                contentDescription = "Emojis",
                theme = theme,
                testTag = "toolbar_emoji",
                onClick = onOpenEmoji
            )

            ToolbarIconButton(
                icon = Icons.Default.Palette,
                contentDescription = "Theme",
                theme = theme,
                testTag = "toolbar_theme",
                onClick = onCycleTheme
            )

            // Layout indicator badge / toggle
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(theme.keyBackground)
                    .clickable { onCycleLayout() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("toolbar_layout"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Layout",
                        tint = theme.accentColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = activeLayoutName,
                        color = theme.keyTextColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            ToolbarIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Cursor Left",
                theme = theme,
                testTag = "toolbar_cursor_left",
                onClick = onCursorLeft
            )

            ToolbarIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Cursor Right",
                theme = theme,
                testTag = "toolbar_cursor_right",
                onClick = onCursorRight
            )

            ToolbarIconButton(
                icon = Icons.Default.Clear,
                contentDescription = "Clear All",
                theme = theme,
                testTag = "toolbar_clear",
                onClick = onClearAll
            )

            ToolbarIconButton(
                icon = Icons.Default.Settings,
                contentDescription = "Settings",
                theme = theme,
                testTag = "toolbar_settings",
                onClick = onOpenSettings
            )
        }
    }
}

@Composable
private fun ToolbarIconButton(
    icon: ImageVector,
    contentDescription: String,
    theme: KeyboardTheme,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = theme.keyTextColor,
            modifier = Modifier.size(17.dp)
        )
    }
}
