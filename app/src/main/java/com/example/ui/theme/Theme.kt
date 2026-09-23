package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep bespoke luxury theme consistent
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
