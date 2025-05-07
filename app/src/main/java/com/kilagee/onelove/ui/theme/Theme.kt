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
import androidx.compose.ui.res.colorResource
import com.kilagee.onelove.R

/**
 * Composable function to setup the OneLove app theme with Material 3 design system
 */
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

// Light mode color scheme
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE91E63),         // Pink 500
    onPrimary = Color(0xFFFFFFFF),        // White
    primaryContainer = Color(0xFFFFD1E3), // Pink 100
    onPrimaryContainer = Color(0xFF3B0014), // Pink 900
    
    secondary = Color(0xFF673AB7),       // Deep Purple 500
    onSecondary = Color(0xFFFFFFFF),      // White
    secondaryContainer = Color(0xFFEDE7F6), // Deep Purple 50
    onSecondaryContainer = Color(0xFF311B92), // Deep Purple 900
    
    tertiary = Color(0xFF2196F3),        // Blue 500
    onTertiary = Color(0xFFFFFFFF),       // White
    tertiaryContainer = Color(0xFFE3F2FD), // Blue 50
    onTertiaryContainer = Color(0xFF0D47A1), // Blue 900
    
    error = Color(0xFFB00020),           // Error
    onError = Color(0xFFFFFFFF),          // White
    errorContainer = Color(0xFFFCDEDF),  // Light pink
    onErrorContainer = Color(0xFF601121), // Dark red
    
    background = Color(0xFFF5F5F5),      // Gray 100
    onBackground = Color(0xFF212121),     // Gray 900
    
    surface = Color(0xFFFFFFFF),         // White
    onSurface = Color(0xFF212121),        // Gray 900
    surfaceVariant = Color(0xFFE0E0E0),  // Gray 300
    onSurfaceVariant = Color(0xFF616161), // Gray 700
    
    outline = Color(0xFF9E9E9E)          // Gray 500
)

// Dark mode color scheme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFF48FB1),         // Pink 300
    onPrimary = Color(0xFF3B0014),        // Dark Pink
    primaryContainer = Color(0xFFC2185B), // Pink 700
    onPrimaryContainer = Color(0xFFFFD1E3), // Pink 100
    
    secondary = Color(0xFFB39DDB),       // Deep Purple 200
    onSecondary = Color(0xFF311B92),      // Deep Purple 900
    secondaryContainer = Color(0xFF512DA8), // Deep Purple 700
    onSecondaryContainer = Color(0xFFEDE7F6), // Deep Purple 50
    
    tertiary = Color(0xFF90CAF9),        // Blue 200
    onTertiary = Color(0xFF0D47A1),       // Blue 900
    tertiaryContainer = Color(0xFF1976D2), // Blue 700
    onTertiaryContainer = Color(0xFFE3F2FD), // Blue 50
    
    error = Color(0xFFCF6679),           // Error light
    onError = Color(0xFF000000),          // Black
    errorContainer = Color(0xFF8B0000),  // Dark red
    onErrorContainer = Color(0xFFF2B8B5), // Light red
    
    background = Color(0xFF121212),      // Dark background
    onBackground = Color(0xFFE0E0E0),     // Gray 300
    
    surface = Color(0xFF1E1E1E),         // Dark surface
    onSurface = Color(0xFFE0E0E0),        // Gray 300
    surfaceVariant = Color(0xFF424242),  // Gray 800
    onSurfaceVariant = Color(0xFFBDBDBD), // Gray 400
    
    outline = Color(0xFF757575)          // Gray 600
)