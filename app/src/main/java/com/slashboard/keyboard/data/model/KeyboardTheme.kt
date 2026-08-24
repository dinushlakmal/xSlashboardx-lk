package com.slashboard.keyboard.data.model

import androidx.compose.ui.graphics.Color

data class KeyboardTheme(
    val id: String,
    val name: String,
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val keyBackground: Color,
    val keyPressedBackground: Color,
    val functionalKeyBackground: Color,
    val keyTextColor: Color,
    val secondaryTextColor: Color,
    val accentColor: Color,
    val keyBorderColor: Color = Color.Transparent,
    val keyRadiusDp: Int = 8
) {
    companion object {
        val CyberViolet = KeyboardTheme(
            id = "cyber_violet",
            name = "Cyber Violet",
            isDark = true,
            background = Color(0xFF0F0B1E),
            surface = Color(0xFF1E1738),
            keyBackground = Color(0xFF2B2052),
            keyPressedBackground = Color(0xFF4C1D95),
            functionalKeyBackground = Color(0xFF191233),
            keyTextColor = Color(0xFFF3F4F6),
            secondaryTextColor = Color(0xFF9CA3AF),
            accentColor = Color(0xFFA855F7),
            keyBorderColor = Color(0x33A855F7),
            keyRadiusDp = 8
        )

        val DeepAmoled = KeyboardTheme(
            id = "deep_amoled",
            name = "Deep AMOLED",
            isDark = true,
            background = Color(0xFF000000),
            surface = Color(0xFF121212),
            keyBackground = Color(0xFF1E1E1E),
            keyPressedBackground = Color(0xFF333333),
            functionalKeyBackground = Color(0xFF121212),
            keyTextColor = Color(0xFFFFFFFF),
            secondaryTextColor = Color(0xFF888888),
            accentColor = Color(0xFF38BDF8),
            keyBorderColor = Color(0x22FFFFFF),
            keyRadiusDp = 8
        )

        val SunsetGlow = KeyboardTheme(
            id = "sunset_glow",
            name = "Sunset Glow",
            isDark = true,
            background = Color(0xFF1A0B1E),
            surface = Color(0xFF2C1233),
            keyBackground = Color(0xFF451952),
            keyPressedBackground = Color(0xFF662549),
            functionalKeyBackground = Color(0xFF220E28),
            keyTextColor = Color(0xFFFDE2F3),
            secondaryTextColor = Color(0xFFE5B8F4),
            accentColor = Color(0xFFF39F5A),
            keyBorderColor = Color(0x33F39F5A),
            keyRadiusDp = 10
        )

        val EmeraldForest = KeyboardTheme(
            id = "emerald_forest",
            name = "Emerald Forest",
            isDark = true,
            background = Color(0xFF061A14),
            surface = Color(0xFF0D2E24),
            keyBackground = Color(0xFF134234),
            keyPressedBackground = Color(0xFF1C5E4A),
            functionalKeyBackground = Color(0xFF0A231B),
            keyTextColor = Color(0xFFE6F4EA),
            secondaryTextColor = Color(0xFFA3D9C9),
            accentColor = Color(0xFF34D399),
            keyBorderColor = Color(0x3334D399),
            keyRadiusDp = 8
        )

        val ElectricBlue = KeyboardTheme(
            id = "electric_blue",
            name = "Electric Blue",
            isDark = true,
            background = Color(0xFF0A1128),
            surface = Color(0xFF14213D),
            keyBackground = Color(0xFF1C315E),
            keyPressedBackground = Color(0xFF254687),
            functionalKeyBackground = Color(0xFF0E1A38),
            keyTextColor = Color(0xFFF1F5F9),
            secondaryTextColor = Color(0xFF94A3B8),
            accentColor = Color(0xFF38BDF8),
            keyBorderColor = Color(0x3338BDF8),
            keyRadiusDp = 8
        )

        val CleanLight = KeyboardTheme(
            id = "clean_light",
            name = "Clean Light",
            isDark = false,
            background = Color(0xFFF1F5F9),
            surface = Color(0xFFE2E8F0),
            keyBackground = Color(0xFFFFFFFF),
            keyPressedBackground = Color(0xFFCBD5E1),
            functionalKeyBackground = Color(0xFFE2E8F0),
            keyTextColor = Color(0xFF0F172A),
            secondaryTextColor = Color(0xFF64748B),
            accentColor = Color(0xFF7C3AED),
            keyBorderColor = Color(0x1E000000),
            keyRadiusDp = 8
        )

        val PastelCoral = KeyboardTheme(
            id = "pastel_coral",
            name = "Pastel Coral",
            isDark = false,
            background = Color(0xFFFFF1F2),
            surface = Color(0xFFFFE4E6),
            keyBackground = Color(0xFFFFFFFF),
            keyPressedBackground = Color(0xFFFECDD3),
            functionalKeyBackground = Color(0xFFFFE4E6),
            keyTextColor = Color(0xFF881337),
            secondaryTextColor = Color(0xFF9F1239),
            accentColor = Color(0xFFFB7185),
            keyBorderColor = Color(0x22FB7185),
            keyRadiusDp = 10
        )

        val PresetThemes = listOf(
            CyberViolet,
            DeepAmoled,
            SunsetGlow,
            EmeraldForest,
            ElectricBlue,
            CleanLight,
            PastelCoral
        )

        fun getThemeById(id: String): KeyboardTheme {
            return PresetThemes.find { it.id == id } ?: CyberViolet
        }
    }
}
