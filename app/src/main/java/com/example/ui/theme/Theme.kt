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

private val LightColorScheme =
  androidx.compose.material3.lightColorScheme(
    primary = ElectricCyanDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = LuxuryGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFFB45309),
    tertiary = TelegramBlue,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCardHover,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep bespoke luxury theme consistent
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
