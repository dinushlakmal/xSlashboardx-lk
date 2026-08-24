package com.slashboard.keyboard.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class SmartbarAction(val id: String, val title: String, val iconResName: String) {
    LANGUAGE_SWITCH("language_switch", "Language", "ic_globe"),
    EMOJI("emoji", "Emoji", "ic_emoji_smile"),
    SETTINGS("settings", "Settings", "ic_settings_gear"),
    CLIPBOARD("clipboard", "Clipboard", "ic_content_paste"),
    VOICE_MIC("voice_mic", "Voice Input", "ic_mic"),
    THEME_PICKER("theme_picker", "Themes", "ic_settings_gear"), // Just reusing settings gear for now
    TEXT_EDIT("text_edit", "Text Editing", "ic_text_format"),
    COLLAPSE("collapse", "Hide Keyboard", "ic_keyboard_hide");

    companion object {
        val DEFAULT_ACTIVE = listOf(LANGUAGE_SWITCH, SETTINGS, VOICE_MIC, CLIPBOARD, COLLAPSE)
        val DEFAULT_DISABLED = listOf(EMOJI, THEME_PICKER, TEXT_EDIT)
        
        fun fromId(id: String): SmartbarAction? {
            return values().find { it.id == id }
        }
    }
}
