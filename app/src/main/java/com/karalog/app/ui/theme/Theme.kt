package com.karalog.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Brush
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
    val iconActive: Color,
    val cardBg: Color,
    val cardBorder: Color,
    val surfaceElevated: Color,
    val shadowColor: Color
)

val LightColors = AppColors(
    bgPrimary = Color(0xFFF6F7FA),      // Clean soft background so white 3D cards pop
    bgSecondary = Color(0xFFEEF1F5),    // Soft secondary fill
    borderLight = Color(0xFFE2E5EB),    // Subtle contour border
    textMain = Color(0xFF16181D),       // Deep slate charcoal
    textSub = Color(0xFF687182),        // Smooth gray
    iconInactive = Color(0xFF8C95A6),
    iconActive = Color(0xFF16181D),
    cardBg = Color(0xFFFFFFFF),         // Crisp white card
    cardBorder = Color(0x1A000000),     // Soft tactile outline
    surfaceElevated = Color(0xFFFFFFFF),
    shadowColor = Color(0x14000000)     // Soft gentle shadow
)

val DarkColors = AppColors(
    bgPrimary = Color(0xFF000000),      // True Black canvas
    bgSecondary = Color(0xFF14161A),    // Rich deep slate surface
    borderLight = Color(0xFF262A30),    // Sleek border line
    textMain = Color(0xFFFFFFFF),       // Crisp white text
    textSub = Color(0xFFA1A7B3),        // Muted metallic silver
    iconInactive = Color(0xFF6E7480),
    iconActive = Color(0xFFFFFFFF),
    cardBg = Color(0xFF16181D),         // Elevated 3D dark card
    cardBorder = Color(0x29FFFFFF),     // Subtle rim light border
    surfaceElevated = Color(0xFF20232A), // Top pop surface
    shadowColor = Color(0x66000000)     // Deep rich shadow
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
val CardBg: Color @Composable get() = LocalAppColors.current.cardBg
val CardBorder: Color @Composable get() = LocalAppColors.current.cardBorder
val SurfaceElevated: Color @Composable get() = LocalAppColors.current.surfaceElevated
val ShadowColor: Color @Composable get() = LocalAppColors.current.shadowColor

// 3D Dimensional Gradient Helpers
@Composable
fun card3dGradient(isDark: Boolean): Brush {
    return if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF20242B),
                Color(0xFF15171B)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFF9FAFC)
            )
        )
    }
}

@Composable
fun button3dGradient(isDark: Boolean): Brush {
    return if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFE2E4E8)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF22262E),
                Color(0xFF111317)
            )
        )
    }
}

// Backward compatibility mappings
val MidnightBlack: Color @Composable get() = BgPrimary
val SurfaceDark: Color @Composable get() = CardBg
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
            surface = colors.cardBg,
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
            surface = colors.cardBg,
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
