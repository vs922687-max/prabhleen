package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SleekPrimaryContainer,
    secondary = SleekSecondaryContainer,
    tertiary = SuccessGreen,
    background = SlateDark,
    surface = SurfaceDark,
    onPrimary = SleekOnPrimaryContainer,
    onSecondary = SleekOnSecondaryContainer,
    onBackground = Color(0xFFECEFF1),
    onSurface = Color(0xFFECEFF1),
    primaryContainer = SleekPrimary,
    secondaryContainer = SleekSecondary,
    onPrimaryContainer = Color.White,
    onSecondaryContainer = Color.White,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = Color(0xFFC4C6D0),
    outline = SleekOutline
)

private val LightColorScheme = lightColorScheme(
    primary = SleekPrimary,
    secondary = SleekSecondary,
    tertiary = SuccessGreen,
    background = SleekBackground,
    surface = SleekSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = SleekOnBackground,
    onSurface = SleekOnSurface,
    primaryContainer = SleekPrimaryContainer,
    secondaryContainer = SleekSecondaryContainer,
    onPrimaryContainer = SleekOnPrimaryContainer,
    onSecondaryContainer = SleekOnSecondaryContainer,
    surfaceVariant = SleekSurfaceVariant,
    onSurfaceVariant = SleekOnSurfaceVariant,
    outline = SleekOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+ (turn off by default to guarantee our beautiful Emerald brand styling!)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
