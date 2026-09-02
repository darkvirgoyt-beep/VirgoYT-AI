package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
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

/**
 * Animate all Material 3 ColorScheme color tokens smoothly with 0.3s duration (fade transition).
 */
@Composable
fun animateVirgoColorScheme(
    target: ColorScheme,
    animationSpec: AnimationSpec<Color> = tween(durationMillis = 300, easing = FastOutSlowInEasing)
): ColorScheme {
    return target.copy(
        primary = animateColorAsState(target.primary, animationSpec, label = "primary").value,
        onPrimary = animateColorAsState(target.onPrimary, animationSpec, label = "onPrimary").value,
        primaryContainer = animateColorAsState(target.primaryContainer, animationSpec, label = "primaryContainer").value,
        onPrimaryContainer = animateColorAsState(target.onPrimaryContainer, animationSpec, label = "onPrimaryContainer").value,
        inversePrimary = animateColorAsState(target.inversePrimary, animationSpec, label = "inversePrimary").value,
        secondary = animateColorAsState(target.secondary, animationSpec, label = "secondary").value,
        onSecondary = animateColorAsState(target.onSecondary, animationSpec, label = "onSecondary").value,
        secondaryContainer = animateColorAsState(target.secondaryContainer, animationSpec, label = "secondaryContainer").value,
        onSecondaryContainer = animateColorAsState(target.onSecondaryContainer, animationSpec, label = "onSecondaryContainer").value,
        tertiary = animateColorAsState(target.tertiary, animationSpec, label = "tertiary").value,
        onTertiary = animateColorAsState(target.onTertiary, animationSpec, label = "onTertiary").value,
        tertiaryContainer = animateColorAsState(target.tertiaryContainer, animationSpec, label = "tertiaryContainer").value,
        onTertiaryContainer = animateColorAsState(target.onTertiaryContainer, animationSpec, label = "onTertiaryContainer").value,
        background = animateColorAsState(target.background, animationSpec, label = "background").value,
        onBackground = animateColorAsState(target.onBackground, animationSpec, label = "onBackground").value,
        surface = animateColorAsState(target.surface, animationSpec, label = "surface").value,
        onSurface = animateColorAsState(target.onSurface, animationSpec, label = "onSurface").value,
        surfaceVariant = animateColorAsState(target.surfaceVariant, animationSpec, label = "surfaceVariant").value,
        onSurfaceVariant = animateColorAsState(target.onSurfaceVariant, animationSpec, label = "onSurfaceVariant").value,
        surfaceTint = animateColorAsState(target.surfaceTint, animationSpec, label = "surfaceTint").value,
        inverseSurface = animateColorAsState(target.inverseSurface, animationSpec, label = "inverseSurface").value,
        inverseOnSurface = animateColorAsState(target.inverseOnSurface, animationSpec, label = "inverseOnSurface").value,
        error = animateColorAsState(target.error, animationSpec, label = "error").value,
        onError = animateColorAsState(target.onError, animationSpec, label = "onError").value,
        errorContainer = animateColorAsState(target.errorContainer, animationSpec, label = "errorContainer").value,
        onErrorContainer = animateColorAsState(target.onErrorContainer, animationSpec, label = "onErrorContainer").value,
        outline = animateColorAsState(target.outline, animationSpec, label = "outline").value,
        outlineVariant = animateColorAsState(target.outlineVariant, animationSpec, label = "outlineVariant").value,
        scrim = animateColorAsState(target.scrim, animationSpec, label = "scrim").value
    )
}

/**
 * Animate custom VirgoThemeColors tokens smoothly with 0.3s duration (fade transition).
 */
@Composable
fun animateVirgoThemeColors(
    target: VirgoThemeColors,
    animationSpec: AnimationSpec<Color> = tween(durationMillis = 300, easing = FastOutSlowInEasing)
): VirgoThemeColors {
    return VirgoThemeColors(
        canvas = animateColorAsState(target.canvas, animationSpec, label = "canvas").value,
        surface = animateColorAsState(target.surface, animationSpec, label = "surface").value,
        card = animateColorAsState(target.card, animationSpec, label = "card").value,
        cardSubtle = animateColorAsState(target.cardSubtle, animationSpec, label = "cardSubtle").value,
        border = animateColorAsState(target.border, animationSpec, label = "border").value,
        borderStrong = animateColorAsState(target.borderStrong, animationSpec, label = "borderStrong").value,
        textPrimary = animateColorAsState(target.textPrimary, animationSpec, label = "textPrimary").value,
        textSecondary = animateColorAsState(target.textSecondary, animationSpec, label = "textSecondary").value,
        textMuted = animateColorAsState(target.textMuted, animationSpec, label = "textMuted").value,
        primary = animateColorAsState(target.primary, animationSpec, label = "primary").value,
        primaryDark = animateColorAsState(target.primaryDark, animationSpec, label = "primaryDark").value,
        primaryLight = animateColorAsState(target.primaryLight, animationSpec, label = "primaryLight").value,
        primaryBg = animateColorAsState(target.primaryBg, animationSpec, label = "primaryBg").value,
        accentEmerald = animateColorAsState(target.accentEmerald, animationSpec, label = "accentEmerald").value,
        accentCyan = animateColorAsState(target.accentCyan, animationSpec, label = "accentCyan").value,
        accentAmber = animateColorAsState(target.accentAmber, animationSpec, label = "accentAmber").value,
        accentRed = animateColorAsState(target.accentRed, animationSpec, label = "accentRed").value,
        termBg = animateColorAsState(target.termBg, animationSpec, label = "termBg").value,
        termHeaderBg = animateColorAsState(target.termHeaderBg, animationSpec, label = "termHeaderBg").value,
        termText = animateColorAsState(target.termText, animationSpec, label = "termText").value,
        isDark = target.isDark
    )
}

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.HOLOGRAPHIC_DARK,
    content: @Composable () -> Unit
) {
    val isDark = themeMode.isDark
    val targetColorScheme = if (isDark) VirgoDarkColorScheme else VirgoEngineerLightColorScheme
    val targetCustomColors = if (isDark) DarkVirgoThemeColors else LightEngineerVirgoThemeColors

    // Smooth 0.3s fade transition on theme switch
    val animatedColorScheme = animateVirgoColorScheme(targetColorScheme)
    val animatedCustomColors = animateVirgoThemeColors(targetCustomColors)

    CompositionLocalProvider(
        LocalAppThemeMode provides themeMode,
        LocalVirgoThemeColors provides animatedCustomColors
    ) {
        MaterialTheme(
            colorScheme = animatedColorScheme,
            typography = Typography,
            content = content
        )
    }
}

