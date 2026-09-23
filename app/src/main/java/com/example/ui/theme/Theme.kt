package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
    darkColorScheme(
        primary = ElectricCyan,
        onPrimary = MidnightDark,
        primaryContainer = ElectricCyanDark,
        onPrimaryContainer = Color.White,
        secondary = LuxuryGold,
        onSecondary = MidnightDark,
        secondaryContainer = MidnightCardHover,
        onSecondaryContainer = LuxuryGoldLight,
        tertiary = TelegramBlue,
        background = MidnightDark,
        onBackground = TextPrimary,
        surface = MidnightSurface,
        onSurface = TextPrimary,
        surfaceVariant = MidnightCard,
        onSurfaceVariant = TextSecondary,
        outline = MidnightBorder
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Color(0xFF0284C7),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE0F2FE),
        onPrimaryContainer = Color(0xFF0369A1),
        secondary = Color(0xFFD97706),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFEF3C7),
        onSecondaryContainer = Color(0xFF92400E),
        tertiary = TelegramBlue,
        background = LightBackground,
        onBackground = LightTextPrimary,
        surface = LightSurface,
        onSurface = LightTextPrimary,
        surfaceVariant = LightCard,
        onSurfaceVariant = LightTextSecondary,
        outline = LightBorder
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
