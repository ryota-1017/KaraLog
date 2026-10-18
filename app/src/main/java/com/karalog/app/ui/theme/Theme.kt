package com.karalog.app.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val MidnightBlack = Color(0xFF0B0F19)
val SurfaceDark = Color(0xFF131A2B)
val SurfaceVariantDark = Color(0xFF1C263D)
val OutlineDark = Color(0xFF2E3D5C)

val NeonCyan = Color(0xFF00E5FF)
val NeonCyanDark = Color(0xFF00B0FF)
val VibrantBlue = Color(0xFF2979FF)
val VibrantPurple = Color(0xFF7C4DFF)
val SuccessGreen = Color(0xFF00E676)
val AmberStar = Color(0xFFFFD600)
val CoralRed = Color(0xFFFF5252)

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = MidnightBlack,
    primaryContainer = VibrantBlue.copy(alpha = 0.25f),
    onPrimaryContainer = NeonCyan,
    secondary = VibrantPurple,
    onSecondary = Color.White,
    secondaryContainer = VibrantPurple.copy(alpha = 0.2f),
    onSecondaryContainer = Color(0xFFD1C4E9),
    tertiary = AmberStar,
    background = MidnightBlack,
    onBackground = Color(0xFFF0F4F8),
    surface = SurfaceDark,
    onSurface = Color(0xFFF0F4F8),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFF90A4AE),
    outline = OutlineDark,
    outlineVariant = Color(0xFF263238),
    error = CoralRed
)

@Composable
fun KaraLogTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MidnightBlack.toArgb()
            window.navigationBarColor = MidnightBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }
    MaterialTheme(colorScheme = DarkColorScheme, typography = Typography(), content = content)
}