package com.shinobisim.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentRed,
    onPrimary = TextPrimary,
    primaryContainer = AccentRedDark,
    onPrimaryContainer = TextPrimary,
    secondary = AccentGold,
    onSecondary = DarkNavy,
    tertiary = AccentBlue,
    onTertiary = DarkNavy,
    background = DarkNavy,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = PanelNavy,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextPrimary,
    outline = Divider
)

object AppDimensions {
    val cardCorner = 16
    val buttonCorner = 12
    val smallPadding = 8
    val mediumPadding = 16
    val largePadding = 24
}

val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        darkNavy = DarkNavy,
        deepNavy = DeepNavy,
        panelNavy = PanelNavy,
        accentRed = AccentRed,
        accentOrange = AccentOrange,
        accentGold = AccentGold,
        accentBlue = AccentBlue,
        accentGreen = AccentGreen,
        accentGreenDark = AccentGreenDark,
        textPrimary = TextPrimary,
        textSecondary = TextSecondary,
        textDim = TextDim,
        divider = Divider,
        surface = Surface,
        fire = FireColor,
        water = WaterColor,
        wind = WindColor,
        lightning = LightningColor
    )
}

data class AppColors(
    val darkNavy: Color,
    val deepNavy: Color,
    val panelNavy: Color,
    val accentRed: Color,
    val accentOrange: Color,
    val accentGold: Color,
    val accentBlue: Color,
    val accentGreen: Color,
    val accentGreenDark: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDim: Color,
    val divider: Color,
    val surface: Color,
    val fire: Color,
    val water: Color,
    val wind: Color,
    val lightning: Color
)

@Composable
fun ShinobiTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppColors provides LocalAppColors.current) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = Typography,
            content = content
        )
    }
}
