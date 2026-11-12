package com.karalog.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

data class AppColors(
    val bgPrimary: Color,
    val bgSecondary: Color,
    val borderLight: Color,
    val textMain: Color,
    val textSub: Color,
    val iconInactive: Color,
    val iconActive: Color
)

val LightColors = AppColors(
    bgPrimary = Color(0xFFFFFFFF),
    bgSecondary = Color(0xFFFAFAFA),
    borderLight = Color(0xFFE5E5E5),
    textMain = Color(0xFF171717),
    textSub = Color(0xFF737373),
    iconInactive = Color(0xFF8E8E93),
    iconActive = Color(0xFF171717)
)

val DarkColors = AppColors(
    bgPrimary = Color(0xFF000000),      // True Black
    bgSecondary = Color(0xFF121212),    // Subtle dark surface
    borderLight = Color(0xFF262626),    // Ultra-fine border
    textMain = Color(0xFFFFFFFF),       // Crisp white
    textSub = Color(0xFFA3A3A3),        // Muted gray
    iconInactive = Color(0xFF737373),
    iconActive = Color(0xFFFFFFFF)
)

val LocalAppColors = staticCompositionLocalOf { LightColors }

// Dynamic properties that automatically switch between Light and Dark
val BgPrimary: Color @Composable get() = LocalAppColors.current.bgPrimary
val BgSecondary: Color @Composable get() = LocalAppColors.current.bgSecondary
val BorderLight: Color @Composable get() = LocalAppColors.current.borderLight
val TextMain: Color @Composable get() = LocalAppColors.current.textMain
val TextSub: Color @Composable get() = LocalAppColors.current.textSub
val IconInactive: Color @Composable get() = LocalAppColors.current.iconInactive
val IconActive: Color @Composable get() = LocalAppColors.current.iconActive

// Backward compatibility mappings
val MidnightBlack: Color @Composable get() = BgPrimary
val SurfaceDark: Color @Composable get() = BgPrimary
val SurfaceVariantDark: Color @Composable get() = BgSecondary
val OutlineDark: Color @Composable get() = BorderLight
val NeonCyan: Color @Composable get() = TextMain
val NeonCyanDark: Color @Composable get() = TextMain
val VibrantBlue: Color @Composable get() = TextMain
val VibrantPurple: Color @Composable get() = TextMain
val SuccessGreen: Color @Composable get() = TextMain
val AmberStar: Color @Composable get() = TextMain
val CoralRed: Color @Composable get() = TextSub

@Composable
fun KaraLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.textMain,
            onPrimary = colors.bgPrimary,
            background = colors.bgPrimary,
            onBackground = colors.textMain,
            surface = colors.bgPrimary,
            onSurface = colors.textMain,
            surfaceVariant = colors.bgSecondary,
            onSurfaceVariant = colors.textSub,
            outline = colors.borderLight
        )
    } else {
        lightColorScheme(
            primary = colors.textMain,
            onPrimary = colors.bgPrimary,
            background = colors.bgPrimary,
            onBackground = colors.textMain,
            surface = colors.bgPrimary,
            onSurface = colors.textMain,
            surfaceVariant = colors.bgSecondary,
            onSurfaceVariant = colors.textSub,
            outline = colors.borderLight
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.bgPrimary.toArgb()
            window.navigationBarColor = colors.bgPrimary.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
