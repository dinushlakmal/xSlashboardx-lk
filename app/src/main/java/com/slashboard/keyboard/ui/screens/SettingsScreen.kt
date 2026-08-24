package com.slashboard.keyboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.slashboard.keyboard.SlashboardApp
import com.slashboard.keyboard.util.ImeHelper

@Composable
fun SettingsScreen(
    onNavigateToSetup: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val prefs = SlashboardApp.instance.preferencesRepository
    val settings by prefs.settingsFlow.collectAsState()

    var isEnabled by remember { mutableStateOf(ImeHelper.isImeEnabled(context)) }
    var isSelected by remember { mutableStateOf(ImeHelper.isImeSelected(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isEnabled = ImeHelper.isImeEnabled(context)
                isSelected = ImeHelper.isImeSelected(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val isSetupComplete = isEnabled && isSelected

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Preferences & Feedback",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Fine-tune haptics, typing assistance, and keyboard activation.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
            )
        }

        // Quick Keyboard Setup & Status Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (isSetupComplete) Color(0xFF10B981).copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onNavigateToSetup() }
                    .testTag("settings_setup_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSetupComplete) Color(0xFF10B981).copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSetupComplete) Icons.Default.CheckCircle else Icons.Default.Tune,
                            contentDescription = "Setup",
                            tint = if (isSetupComplete) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Keyboard Activation Status",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isSetupComplete) "✓ Enabled and Active as Default"
                            else if (!isEnabled) "Tap to Enable in System Settings"
                            else "Tap to Select as Active Keyboard",
                            fontSize = 12.sp,
                            color = if (isSetupComplete) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open Setup",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Keyboard Dimensions Card
        item {
            SettingsCategoryHeader(title = "Dimensions & Layout")
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("dimensions_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Keyboard Height Scale",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Adjust vertical key size",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Text(
                            text = "${(settings.heightScale * 100).toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Slider(
                        value = settings.heightScale,
                        onValueChange = { prefs.updateHeightScale(it) },
                        valueRange = 0.8f..1.3f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("height_slider")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Key Corner Rounding",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Curve radius for key shapes",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Text(
                            text = "${settings.keyCornerRadius} dp",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Slider(
                        value = settings.keyCornerRadius.toFloat(),
                        onValueChange = { prefs.updateKeyCornerRadius(it.toInt()) },
                        valueRange = 2f..20f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("radius_slider")
                    )
                }
            }
        }

        // Haptic & Sound Feedback Card
        item {
            SettingsCategoryHeader(title = "Haptics & Feedback")
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("haptics_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SettingsToggleRow(
                        title = "Haptic Vibration on Keypress",
                        subtitle = "Tactile physical vibration when touching keys",
                        checked = settings.hapticFeedback,
                        testTag = "toggle_haptic",
                        onCheckedChange = { prefs.updateHapticFeedback(it) }
                    )

                    if (settings.hapticFeedback) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Vibration Intensity",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${settings.hapticIntensity} ms",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Slider(
                            value = settings.hapticIntensity.toFloat(),
                            onValueChange = { prefs.updateHapticIntensity(it.toInt()) },
                            valueRange = 10f..100f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("vibration_slider")
                        )
                    }

                    SettingsToggleRow(
                        title = "Sound Click on Keypress",
                        subtitle = "Audible system click sound effect",
                        checked = settings.soundFeedback,
                        testTag = "toggle_sound",
                        onCheckedChange = { prefs.updateSoundFeedback(it) }
                    )

                    SettingsToggleRow(
                        title = "Keypress Magnifier Popup",
                        subtitle = "Floating character bubble above pressed key",
                        checked = settings.popupOnKeypress,
                        testTag = "toggle_popup",
                        onCheckedChange = { prefs.updatePopupOnKeypress(it) }
                    )
                }
            }
        }

        // Layout Elements & Toolbar Card
        item {
            SettingsCategoryHeader(title = "Key Elements & Toolbar")
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("elements_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SettingsToggleRow(
                        title = "Show Top Action Toolbar",
                        subtitle = "Quick bar with emojis, theme changer, cursor controls",
                        checked = settings.toolbarVisible,
                        testTag = "toggle_toolbar",
                        onCheckedChange = { prefs.updateToolbarVisible(it) }
                    )

                    SettingsToggleRow(
                        title = "Dedicated Number Row",
                        subtitle = "Always show 1-0 numeric keys row above letters",
                        checked = settings.showNumberRow,
                        testTag = "toggle_number_row",
                        onCheckedChange = { prefs.updateShowNumberRow(it) }
                    )

                    SettingsToggleRow(
                        title = "Secondary Symbol Hints",
                        subtitle = "Display long-press symbols on top right of keys",
                        checked = settings.showSecondaryLabels,
                        testTag = "toggle_secondary_labels",
                        onCheckedChange = { prefs.updateShowSecondaryLabels(it) }
                    )

                    SettingsToggleRow(
                        title = "Key Borders",
                        subtitle = "Show visible outline borders around individual keys",
                        checked = settings.showKeyBorders,
                        testTag = "toggle_key_borders",
                        onCheckedChange = { prefs.updateShowKeyBorders(it) }
                    )
                }
            }
        }

        // Smart Input & Corrections Card
        item {
            SettingsCategoryHeader(title = "Typing & Corrections")
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("corrections_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SettingsToggleRow(
                        title = "Auto Capitalization",
                        subtitle = "Capitalize first word of sentences automatically",
                        checked = settings.autoCapitalization,
                        testTag = "toggle_auto_cap",
                        onCheckedChange = { prefs.updateAutoCapitalization(it) }
                    )

                    SettingsToggleRow(
                        title = "Double Space for Period",
                        subtitle = "Double-tapping spacebar automatically inserts a period followed by a space",
                        checked = settings.doubleSpacePeriod,
                        testTag = "toggle_double_space",
                        onCheckedChange = { prefs.updateDoubleSpacePeriod(it) }
                    )

                    SettingsToggleRow(
                        title = "Smart Suggestions & Autocorrect",
                        subtitle = "Show dynamic word completions and text shortcut suggestions",
                        checked = settings.autoCorrect,
                        testTag = "toggle_auto_correct",
                        onCheckedChange = { prefs.updateAutoCorrect(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
