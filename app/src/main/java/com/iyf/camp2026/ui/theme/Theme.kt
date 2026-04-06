package com.iyf.camp2026.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val IYFLightColorScheme = lightColorScheme(
    primary = OrangeIYF,
    onPrimary = White,
    primaryContainer = OrangeBackground,
    onPrimaryContainer = OrangeDark,
    secondary = GreenDark,
    onSecondary = White,
    secondaryContainer = GreenLight.copy(alpha = 0.2f),
    onSecondaryContainer = GreenDark,
    tertiary = BlueIYF,
    onTertiary = White,
    background = OffWhite,
    onBackground = NearBlack,
    surface = White,
    onSurface = NearBlack,
    surfaceVariant = LightGray,
    onSurfaceVariant = DarkGray,
    error = ErrorRed,
    onError = White,
    outline = MediumGray,
    outlineVariant = LightGray
)

@Composable
fun IYFCamp2026Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Always use light theme for IYF brand consistency
    val colorScheme = IYFLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = OrangeIYF.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = IYFTypography,
        content = content
    )
}
