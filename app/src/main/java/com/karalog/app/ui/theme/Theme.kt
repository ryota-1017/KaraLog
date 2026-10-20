package com.karalog.app.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Strict Monotone Palette
val BgPrimary = Color(0xFFFFFFFF)
val BgSecondary = Color(0xFFFAFAFA)
val BorderLight = Color(0xFFE5E5E5)
val TextMain = Color(0xFF171717)
val TextSub = Color(0xFF737373)
val IconInactive = Color(0xFF8E8E93)
val IconActive = Color(0xFF171717)

// Backward compatibility mappings
val MidnightBlack = BgPrimary
val SurfaceDark = BgPrimary
val SurfaceVariantDark = BgSecondary
val OutlineDark = BorderLight
val NeonCyan = TextMain
val NeonCyanDark = TextMain
val VibrantBlue = TextMain
val VibrantPurple = TextMain
val SuccessGreen = TextMain
val AmberStar = TextMain
val CoralRed = TextSub

private val LightColorScheme = lightColorScheme(
    primary = TextMain,
    onPrimary = BgPrimary,
    background = BgPrimary,
    onBackground = TextMain,
    surface = BgPrimary,
    onSurface = TextMain,
    surfaceVariant = BgSecondary,
    onSurfaceVariant = TextSub,
    outline = BorderLight
)

@Composable
fun KaraLogTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BgPrimary.toArgb()
            window.navigationBarColor = BgPrimary.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
