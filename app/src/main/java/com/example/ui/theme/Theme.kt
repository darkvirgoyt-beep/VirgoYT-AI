package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.manus.data.model.AppThemeMode

val ManusDarkColorScheme = darkColorScheme(
    primary = ManusIndigo,
    onPrimary = ManusWhite,
    primaryContainer = ManusSlate850,
    onPrimaryContainer = ManusIndigoLight,
    secondary = ManusEmerald,
    onSecondary = ManusWhite,
    secondaryContainer = ManusSlate800,
    onSecondaryContainer = ManusGreen,
    tertiary = ManusCyan,
    onTertiary = ManusSlate950,
    background = ManusSlate950,
    onBackground = ManusSlate200,
    surface = ManusSlate900,
    onSurface = ManusWhite,
    surfaceVariant = ManusSlate850,
    onSurfaceVariant = ManusSlate400,
    outline = ManusSlate800,
    outlineVariant = ManusSlate700,
    error = ManusRed,
    onError = ManusWhite
)

val ManusEngineerLightColorScheme = lightColorScheme(
    primary = EngineerLightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = EngineerLightPrimaryDark,
    secondary = EngineerLightEmerald,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = EngineerLightCyan,
    onTertiary = Color.White,
    background = EngineerLightCanvas,
    onBackground = EngineerLightTextPrimary,
    surface = EngineerLightSurface,
    onSurface = EngineerLightTextPrimary,
    surfaceVariant = EngineerLightCard,
    onSurfaceVariant = EngineerLightTextSecondary,
    outline = EngineerLightBorder,
    outlineVariant = EngineerLightBorderStrong,
    error = EngineerLightRed,
    onError = Color.White
)

data class ManusThemeColors(
    val canvas: Color,
    val surface: Color,
    val card: Color,
    val cardSubtle: Color,
    val border: Color,
    val borderStrong: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primary: Color,
    val primaryDark: Color,
    val primaryLight: Color,
    val primaryBg: Color,
    val accentEmerald: Color,
    val accentCyan: Color,
    val accentAmber: Color,
    val accentRed: Color,
    val termBg: Color,
    val termHeaderBg: Color,
    val termText: Color,
    val isDark: Boolean
)

val DarkManusThemeColors = ManusThemeColors(
    canvas = ManusSlate950,
    surface = ManusSlate900,
    card = ManusSlate850,
    cardSubtle = ManusSlate800,
    border = SleekBorder,
    borderStrong = ManusSlate700,
    textPrimary = ManusWhite,
    textSecondary = ManusSlate200,
    textMuted = ManusSlate400,
    primary = ManusIndigo,
    primaryDark = ManusIndigoDark,
    primaryLight = ManusIndigoLight,
    primaryBg = ManusIndigoBg,
    accentEmerald = ManusEmerald,
    accentCyan = ManusCyan,
    accentAmber = ManusAmber,
    accentRed = ManusRed,
    termBg = TermBg,
    termHeaderBg = TermHeaderBg,
    termText = TermText,
    isDark = true
)

val LightEngineerManusThemeColors = ManusThemeColors(
    canvas = EngineerLightCanvas,
    surface = EngineerLightSurface,
    card = EngineerLightCard,
    cardSubtle = EngineerLightSubtle,
    border = EngineerLightBorder,
    borderStrong = EngineerLightBorderStrong,
    textPrimary = EngineerLightTextPrimary,
    textSecondary = EngineerLightTextSecondary,
    textMuted = EngineerLightTextMuted,
    primary = EngineerLightPrimary,
    primaryDark = EngineerLightPrimaryDark,
    primaryLight = EngineerLightPrimaryLight,
    primaryBg = EngineerLightPrimaryBg,
    accentEmerald = EngineerLightEmerald,
    accentCyan = EngineerLightCyan,
    accentAmber = EngineerLightAmber,
    accentRed = EngineerLightRed,
    termBg = EngineerLightTermBg,
    termHeaderBg = EngineerLightTermHeaderBg,
    termText = EngineerLightTermText,
    isDark = false
)

val LocalAppThemeMode = staticCompositionLocalOf { AppThemeMode.HOLOGRAPHIC_DARK }
val LocalManusThemeColors = staticCompositionLocalOf { DarkManusThemeColors }

object ManusTheme {
    val colors: ManusThemeColors
        @Composable
        get() = LocalManusThemeColors.current

    val mode: AppThemeMode
        @Composable
        get() = LocalAppThemeMode.current
}

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.HOLOGRAPHIC_DARK,
    content: @Composable () -> Unit
) {
    val isDark = themeMode.isDark
    val colorScheme = if (isDark) ManusDarkColorScheme else ManusEngineerLightColorScheme
    val customColors = if (isDark) DarkManusThemeColors else LightEngineerManusThemeColors

    CompositionLocalProvider(
        LocalAppThemeMode provides themeMode,
        LocalManusThemeColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

