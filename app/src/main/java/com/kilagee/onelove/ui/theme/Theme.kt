package com.kilagee.onelove.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Primary colors
val Primary = Color(0xFFFF5252)
val PrimaryLight = Color(0xFFFF867F)
val PrimaryDark = Color(0xFFC50E29)

// Secondary colors
val Secondary = Color(0xFF2979FF)
val SecondaryLight = Color(0xFF75A7FF)
val SecondaryDark = Color(0xFF004ECB)

// Verified badge color
val Verified = Color(0xFF00BFA5)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    primaryContainer = PrimaryLight.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryDark,
    secondary = Secondary,
    secondaryContainer = SecondaryLight.copy(alpha = 0.2f),
    onSecondaryContainer = SecondaryDark,
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    error = Color(0xFFB3261E)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    primaryContainer = Primary.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryLight,
    secondary = SecondaryLight,
    secondaryContainer = Secondary.copy(alpha = 0.2f),
    onSecondaryContainer = SecondaryLight,
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    error = Color(0xFFF2B8B5)
)

@Composable
fun OneLoveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}