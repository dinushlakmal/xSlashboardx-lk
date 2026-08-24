package com.slashboard.keyboard.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import com.slashboard.keyboard.data.model.KeyboardLayout
import com.slashboard.keyboard.data.model.KeyboardTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class KeyboardSettings(
    val themeId: String = "cyber_violet",
    val layoutId: String = "qwerty",
    val hapticFeedback: Boolean = true,
    val hapticIntensity: Int = 50,
    val soundFeedback: Boolean = false,
    val popupOnKeypress: Boolean = true,
    val heightScale: Float = 1.0f,
    val autoCapitalization: Boolean = true,
    val doubleSpacePeriod: Boolean = true,
    val autoCorrect: Boolean = true,
    val showNumberRow: Boolean = false,
    val showSecondaryLabels: Boolean = true,
    val toolbarVisible: Boolean = true,
    val keyCornerRadius: Int = 8,
    val showKeyBorders: Boolean = true,
    val customWallpaperPath: String? = null,
    val wallpaperDim: Float = 0.45f,
    val hasCompletedSetup: Boolean = false
)

class KeyboardPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<KeyboardSettings> = _settingsFlow.asStateFlow()

    private fun loadSettings(): KeyboardSettings {
        return KeyboardSettings(
            themeId = prefs.getString(KEY_THEME_ID, "cyber_violet") ?: "cyber_violet",
            layoutId = prefs.getString(KEY_LAYOUT_ID, "qwerty") ?: "qwerty",
            hapticFeedback = prefs.getBoolean(KEY_HAPTIC, true),
            hapticIntensity = prefs.getInt(KEY_HAPTIC_INTENSITY, 50),
            soundFeedback = prefs.getBoolean(KEY_SOUND, false),
            popupOnKeypress = prefs.getBoolean(KEY_POPUP, true),
            heightScale = prefs.getFloat(KEY_HEIGHT_SCALE, 1.0f),
            autoCapitalization = prefs.getBoolean(KEY_AUTO_CAP, true),
            doubleSpacePeriod = prefs.getBoolean(KEY_DOUBLE_SPACE, true),
            autoCorrect = prefs.getBoolean(KEY_AUTO_CORRECT, true),
            showNumberRow = prefs.getBoolean(KEY_NUMBER_ROW, false),
            showSecondaryLabels = prefs.getBoolean(KEY_SECONDARY_LABELS, true),
            toolbarVisible = prefs.getBoolean(KEY_TOOLBAR, true),
            keyCornerRadius = prefs.getInt(KEY_KEY_RADIUS, 8),
            showKeyBorders = prefs.getBoolean(KEY_KEY_BORDERS, true),
            customWallpaperPath = prefs.getString(KEY_CUSTOM_WALLPAPER, null),
            wallpaperDim = prefs.getFloat(KEY_WALLPAPER_DIM, 0.45f),
            hasCompletedSetup = prefs.getBoolean(KEY_HAS_COMPLETED_SETUP, false)
        )
    }

    fun updateThemeId(themeId: String) {
        prefs.edit().putString(KEY_THEME_ID, themeId).apply()
        _settingsFlow.value = _settingsFlow.value.copy(themeId = themeId)
    }

    fun updateLayoutId(layoutId: String) {
        prefs.edit().putString(KEY_LAYOUT_ID, layoutId).apply()
        _settingsFlow.value = _settingsFlow.value.copy(layoutId = layoutId)
    }

    fun updateHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(hapticFeedback = enabled)
    }

    fun updateHapticIntensity(intensity: Int) {
        prefs.edit().putInt(KEY_HAPTIC_INTENSITY, intensity).apply()
        _settingsFlow.value = _settingsFlow.value.copy(hapticIntensity = intensity)
    }

    fun updateSoundFeedback(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(soundFeedback = enabled)
    }

    fun updatePopupOnKeypress(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_POPUP, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(popupOnKeypress = enabled)
    }

    fun updateHeightScale(scale: Float) {
        val clamped = scale.coerceIn(0.8f, 1.3f)
        prefs.edit().putFloat(KEY_HEIGHT_SCALE, clamped).apply()
        _settingsFlow.value = _settingsFlow.value.copy(heightScale = clamped)
    }

    fun updateAutoCapitalization(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_CAP, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(autoCapitalization = enabled)
    }

    fun updateDoubleSpacePeriod(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DOUBLE_SPACE, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(doubleSpacePeriod = enabled)
    }

    fun updateAutoCorrect(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_CORRECT, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(autoCorrect = enabled)
    }

    fun updateShowNumberRow(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NUMBER_ROW, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showNumberRow = enabled)
    }

    fun updateShowSecondaryLabels(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SECONDARY_LABELS, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showSecondaryLabels = enabled)
    }

    fun updateToolbarVisible(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_TOOLBAR, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(toolbarVisible = enabled)
    }

    fun updateKeyCornerRadius(radius: Int) {
        val clamped = radius.coerceIn(2, 20)
        prefs.edit().putInt(KEY_KEY_RADIUS, clamped).apply()
        _settingsFlow.value = _settingsFlow.value.copy(keyCornerRadius = clamped)
    }

    fun updateShowKeyBorders(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_KEY_BORDERS, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showKeyBorders = enabled)
    }

    fun updateCustomWallpaper(wallpaperPath: String?) {
        if (wallpaperPath == null) {
            prefs.edit().remove(KEY_CUSTOM_WALLPAPER).apply()
        } else {
            prefs.edit().putString(KEY_CUSTOM_WALLPAPER, wallpaperPath).apply()
        }
        _settingsFlow.value = _settingsFlow.value.copy(customWallpaperPath = wallpaperPath)
    }

    fun updateWallpaperDim(dim: Float) {
        val clamped = dim.coerceIn(0.1f, 0.9f)
        prefs.edit().putFloat(KEY_WALLPAPER_DIM, clamped).apply()
        _settingsFlow.value = _settingsFlow.value.copy(wallpaperDim = clamped)
    }

    fun setSetupCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_HAS_COMPLETED_SETUP, completed).apply()
        _settingsFlow.value = _settingsFlow.value.copy(hasCompletedSetup = completed)
    }

    fun hasCompletedSetup(): Boolean {
        return prefs.getBoolean(KEY_HAS_COMPLETED_SETUP, false)
    }

    fun getActiveTheme(): KeyboardTheme {
        val currentThemeId = _settingsFlow.value.themeId
        val baseTheme = KeyboardTheme.getThemeById(currentThemeId)
        val showBorders = _settingsFlow.value.showKeyBorders
        val borderColor = if (showBorders) {
            if (baseTheme.keyBorderColor != Color.Transparent) baseTheme.keyBorderColor else Color(0x33FFFFFF)
        } else {
            Color.Transparent
        }
        return baseTheme.copy(
            keyRadiusDp = _settingsFlow.value.keyCornerRadius,
            keyBorderColor = borderColor
        )
    }

    fun getActiveLayout(): KeyboardLayout {
        return KeyboardLayout.getLayoutById(_settingsFlow.value.layoutId)
    }

    companion object {
        private const val PREFS_NAME = "slashboard_prefs"
        private const val KEY_THEME_ID = "theme_id"
        private const val KEY_LAYOUT_ID = "layout_id"
        private const val KEY_HAPTIC = "haptic_feedback"
        private const val KEY_HAPTIC_INTENSITY = "haptic_intensity"
        private const val KEY_SOUND = "sound_feedback"
        private const val KEY_POPUP = "popup_on_keypress"
        private const val KEY_HEIGHT_SCALE = "height_scale"
        private const val KEY_AUTO_CAP = "auto_capitalization"
        private const val KEY_DOUBLE_SPACE = "double_space_period"
        private const val KEY_AUTO_CORRECT = "auto_correct"
        private const val KEY_NUMBER_ROW = "show_number_row"
        private const val KEY_SECONDARY_LABELS = "show_secondary_labels"
        private const val KEY_TOOLBAR = "toolbar_visible"
        private const val KEY_KEY_RADIUS = "key_corner_radius"
        private const val KEY_KEY_BORDERS = "show_key_borders"
        private const val KEY_CUSTOM_WALLPAPER = "custom_wallpaper_path"
        private const val KEY_WALLPAPER_DIM = "wallpaper_dim_alpha"
        private const val KEY_HAS_COMPLETED_SETUP = "has_completed_setup"
    }
}
