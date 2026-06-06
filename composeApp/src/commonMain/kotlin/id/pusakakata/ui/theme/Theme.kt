package id.pusakakata.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6D4C41), // Earthy Brown
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7CCC8),
    onPrimaryContainer = Color(0xFF3E2723),
    secondary = Color(0xFFBF360C), // Terracotta/Clay
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFCCBC),
    onSecondaryContainer = Color(0xFF212121),
    tertiary = Color(0xFFFBC02D), // Gold/Mustard
    onTertiary = Color(0xFF3E2723),
    surface = Color(0xFFFFFBF0), // Creamy paper
    background = Color(0xFFFFFBF0),
    error = Color(0xFFB00020)
)

@Composable
fun PusakaKataTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
