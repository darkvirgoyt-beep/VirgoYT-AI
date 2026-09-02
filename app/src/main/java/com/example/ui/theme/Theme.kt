package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.virgoyt.data.model.AppThemeMode

val VirgoDarkColorScheme = darkColorScheme(
    primary = VirgoIndigo,
    onPrimary = VirgoWhite,
    primaryContainer = VirgoSlate850,
    onPrimaryContainer = VirgoIndigoLight,
    secondary = VirgoEmerald,
    onSecondary = VirgoWhite,
    secondaryContainer = VirgoSlate800,
    onSecondaryContainer = VirgoGreen,
    tertiary = VirgoCyan,
    onTertiary = VirgoSlate950,
    background = VirgoSlate950,
    onBackground = VirgoSlate200,
    surface = VirgoSlate900,
    onSurface = VirgoWhite,
    surfaceVariant = VirgoSlate850,
    onSurfaceVariant = VirgoSlate400,
    outline = VirgoSlate800,
    outlineVariant = VirgoSlate700,
    error = VirgoRed,
    onError = VirgoWhite
)

val VirgoEngineerLightColorScheme = lightColorScheme(
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

// Legacy Aliases
val ManusDarkColorScheme = VirgoDarkColorScheme
val ManusEngineerLightColorScheme = VirgoEngineerLightColorScheme

data class VirgoThemeColors(
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

typealias ManusThemeColors = VirgoThemeColors

val DarkVirgoThemeColors = VirgoThemeColors(
    canvas = VirgoSlate950,
    surface = VirgoSlate900,
    card = VirgoSlate850,
    cardSubtle = VirgoSlate800,
    border = SleekBorder,
    borderStrong = VirgoSlate700,
    textPrimary = VirgoWhite,
    textSecondary = VirgoSlate200,
    textMuted = VirgoSlate400,
    primary = VirgoIndigo,
    primaryDark = VirgoIndigoDark,
    primaryLight = VirgoIndigoLight,
    primaryBg = VirgoIndigoBg,
    accentEmerald = VirgoEmerald,
    accentCyan = VirgoCyan,
    accentAmber = VirgoAmber,
    accentRed = VirgoRed,
    termBg = TermBg,
    termHeaderBg = TermHeaderBg,
    termText = TermText,
    isDark = true
)

val LightEngineerVirgoThemeColors = VirgoThemeColors(
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

val DarkManusThemeColors = DarkVirgoThemeColors
val LightEngineerManusThemeColors = LightEngineerVirgoThemeColors

val LocalAppThemeMode = staticCompositionLocalOf { AppThemeMode.HOLOGRAPHIC_DARK }
val LocalVirgoThemeColors = staticCompositionLocalOf { DarkVirgoThemeColors }
val LocalManusThemeColors = LocalVirgoThemeColors

object VirgoTheme {
    val colors: VirgoThemeColors
        @Composable
        get() = LocalVirgoThemeColors.current

    val mode: AppThemeMode
        @Composable
        get() = LocalAppThemeMode.current
}

typealias ManusTheme = VirgoTheme

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.HOLOGRAPHIC_DARK,
    content: @Composable () -> Unit
) {
    val isDark = themeMode.isDark
    val colorScheme = if (isDark) VirgoDarkColorScheme else VirgoEngineerLightColorScheme
    val customColors = if (isDark) DarkVirgoThemeColors else LightEngineerVirgoThemeColors

    CompositionLocalProvider(
        LocalAppThemeMode provides themeMode,
        LocalVirgoThemeColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

