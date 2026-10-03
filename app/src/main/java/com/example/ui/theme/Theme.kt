package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkKumkumPrimary,
    onPrimary = KumkumDark,
    primaryContainer = Color(0xFF6E1414),
    onPrimaryContainer = PitambaraLight,
    secondary = DarkKesariyaSecondary,
    onSecondary = Color(0xFF4B1600),
    secondaryContainer = DarkTempleContainer,
    onSecondaryContainer = Color(0xFFFFDBCF),
    tertiary = DarkPitambaraTertiary,
    onTertiary = Color(0xFF3E2700),
    tertiaryContainer = Color(0xFF5A3B00),
    onTertiaryContainer = PitambaraLight,
    background = DarkTempleBackground,
    onBackground = Color(0xFFF9EBE5),
    surface = DarkTempleSurface,
    onSurface = Color(0xFFF9EBE5),
    surfaceVariant = DarkTempleContainer,
    onSurfaceVariant = Color(0xFFDFC1B7)
)

private val LightColorScheme = lightColorScheme(
    primary = KumkumMaroon,
    onPrimary = Color.White,
    primaryContainer = SandalwoodContainer,
    onPrimaryContainer = KumkumDark,
    secondary = KesariyaSaffron,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE4D6),
    onSecondaryContainer = Color(0xFF581A00),
    tertiary = PitambaraGold,
    onTertiary = DeepVedicBrown,
    tertiaryContainer = PitambaraLight,
    onTertiaryContainer = Color(0xFF422800),
    background = ChandanCream,
    onBackground = DeepVedicBrown,
    surface = ChandanSurface,
    onSurface = DeepVedicBrown,
    surfaceVariant = SandalwoodContainer,
    onSurfaceVariant = MutedVedicBrown
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
