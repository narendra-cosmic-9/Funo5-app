package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CustomDarkColorScheme = darkColorScheme(
    primary = NeonPink,
    secondary = NeonCyan,
    tertiary = NeonPurple,
    background = SpaceBlack,
    surface = CardDark,
    onPrimary = SpaceBlack,
    onSecondary = SpaceBlack,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CustomDarkColorScheme,
        typography = Typography,
        content = content
    )
}
