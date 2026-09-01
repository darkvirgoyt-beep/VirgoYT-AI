package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

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

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our sleek dark developer aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ManusDarkColorScheme,
        typography = Typography,
        content = content
    )
}
