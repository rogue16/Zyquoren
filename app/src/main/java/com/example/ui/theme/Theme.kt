package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonVioletLight,
    onPrimary = CosmicBlack,
    primaryContainer = DarkSurfaceElevated,
    onPrimaryContainer = TextPrimary,
    secondary = ElectricCyan,
    onSecondary = CosmicBlack,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = ElectricCyanLight,
    tertiary = CyberPink,
    onTertiary = CosmicBlack,
    background = CosmicBlack,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceContainerHighest = DarkSurfaceElevated,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = DarkColorScheme // Video app is styled with deep cinematic dark mode by design

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted cinematic scheme for immersive video feel
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
