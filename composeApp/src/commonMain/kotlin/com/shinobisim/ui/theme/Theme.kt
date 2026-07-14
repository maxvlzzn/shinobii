package com.shinobisim.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AccentRed,
    onPrimary = ButtonPrimaryText,
    primaryContainer = AccentRedDark,
    onPrimaryContainer = ButtonPrimaryText,
    secondary = AccentGold,
    onSecondary = TextPrimary,
    tertiary = AccentBlue,
    onTertiary = ButtonPrimaryText,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = ButtonPrimaryText,
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
        darkNavy = Background,
        deepNavy = SurfaceVariant,
        panelNavy = PanelColor,
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
        lightning = LightningColor,
        buttonPrimary = ButtonPrimary,
        buttonPrimaryText = ButtonPrimaryText,
        buttonSecondary = ButtonSecondary,
        buttonSecondaryText = ButtonSecondaryText,
        buttonDisabled = ButtonDisabled,
        buttonDisabledText = ButtonDisabledText
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
    val lightning: Color,
    val buttonPrimary: Color,
    val buttonPrimaryText: Color,
    val buttonSecondary: Color,
    val buttonSecondaryText: Color,
    val buttonDisabled: Color,
    val buttonDisabledText: Color
)

@Composable
fun ShinobiTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppColors provides LocalAppColors.current) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            content = content
        )
    }
}
