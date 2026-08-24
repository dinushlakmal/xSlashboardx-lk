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
            keyBorderColor = Color(0x88A855F7),
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
            keyBorderColor = Color(0x66FFFFFF),
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
            keyBorderColor = Color(0x88F39F5A),
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
            keyBorderColor = Color(0x8834D399),
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
            keyBorderColor = Color(0x8838BDF8),
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
            keyBorderColor = Color(0x40000000),
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
            keyBorderColor = Color(0x66FB7185),
            keyRadiusDp = 10
        )

        val GlassDark = KeyboardTheme(
            id = "glass_dark",
            name = "Glass Dark",
            isDark = true,
            background = Color(0x80000000),
            surface = Color(0x40FFFFFF),
            keyBackground = Color(0x33FFFFFF),
            keyPressedBackground = Color(0x66FFFFFF),
            functionalKeyBackground = Color(0x1AFFFFFF),
            keyTextColor = Color(0xFFFFFFFF),
            secondaryTextColor = Color(0xB3FFFFFF),
            accentColor = Color(0xFF38BDF8),
            keyBorderColor = Color(0x1AFFFFFF),
            keyRadiusDp = 12
        )

        val GlassLight = KeyboardTheme(
            id = "glass_light",
            name = "Glass Light",
            isDark = false,
            background = Color(0x80FFFFFF),
            surface = Color(0x40000000),
            keyBackground = Color(0x99FFFFFF),
            keyPressedBackground = Color(0xCCFFFFFF),
            functionalKeyBackground = Color(0x66FFFFFF),
            keyTextColor = Color(0xFF000000),
            secondaryTextColor = Color(0x99000000),
            accentColor = Color(0xFF7C3AED),
            keyBorderColor = Color(0x1A000000),
            keyRadiusDp = 12
        )

        val NeonCyanGlass = KeyboardTheme(
            id = "neon_cyan_glass",
            name = "Neon Cyan Glass",
            isDark = true,
            background = Color(0x66001219),
            surface = Color(0x33005F73),
            keyBackground = Color(0x4D0A9396),
            keyPressedBackground = Color(0x8094D2BD),
            functionalKeyBackground = Color(0x33005F73),
            keyTextColor = Color(0xFFE9D8A6),
            secondaryTextColor = Color(0xB3E9D8A6),
            accentColor = Color(0xFF0A9396),
            keyBorderColor = Color(0x330A9396),
            keyRadiusDp = 10
        )

        val HackerMatrixGlass = KeyboardTheme(
            id = "hacker_matrix_glass",
            name = "Matrix Glass",
            isDark = true,
            background = Color(0x80001A00),
            surface = Color(0x40003300),
            keyBackground = Color(0x33004D00),
            keyPressedBackground = Color(0x66009900),
            functionalKeyBackground = Color(0x1A003300),
            keyTextColor = Color(0xFF00FF00),
            secondaryTextColor = Color(0xB300FF00),
            accentColor = Color(0xFF00FF00),
            keyBorderColor = Color(0x3300FF00),
            keyRadiusDp = 8
        )

        val FrostedPlum = KeyboardTheme(
            id = "frosted_plum",
            name = "Frosted Plum",
            isDark = true,
            background = Color(0x8023152F),
            surface = Color(0x403A2649),
            keyBackground = Color(0x4D4F3566),
            keyPressedBackground = Color(0x806A4C87),
            functionalKeyBackground = Color(0x333A2649),
            keyTextColor = Color(0xFFF3E8FF),
            secondaryTextColor = Color(0xB3F3E8FF),
            accentColor = Color(0xFFC084FC),
            keyBorderColor = Color(0x33C084FC),
            keyRadiusDp = 14
        )

        val PresetThemes = listOf(
            CyberViolet,
            DeepAmoled,
            SunsetGlow,
            EmeraldForest,
            ElectricBlue,
            CleanLight,
            PastelCoral,
            GlassDark,
            GlassLight,
            NeonCyanGlass,
            HackerMatrixGlass,
            FrostedPlum
        )

        fun getThemeById(id: String): KeyboardTheme {
            return PresetThemes.find { it.id == id } ?: CyberViolet
        }
    }
}
