package id.pusakakata.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBrown,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7CCC8),
    onPrimaryContainer = BatikBrown,
    secondary = DeepGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF9C4),
    onSecondaryContainer = BatikBrown,
    tertiary = Crimson,
    onTertiary = Color.White,
    surface = Parchment,
    onSurface = BatikBrown,
    background = Parchment,
    onBackground = BatikBrown,
    surfaceVariant = Color(0xFFEFEBE9),
    onSurfaceVariant = BatikBrown,
    outline = SecondaryTerracotta
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD7CCC8),
    onPrimary = BatikBrown,
    secondary = SoftGold,
    background = BatikBrown,
    surface = BatikBrown,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun PusakaKataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
