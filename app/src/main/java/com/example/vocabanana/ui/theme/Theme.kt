package com.example.vocabanana.ui.theme

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
    primary = BananaPrimaryDark,
    onPrimary = BananaOnPrimaryDark,
    primaryContainer = Color(0xFF524600),
    onPrimaryContainer = Color(0xFFFFE135),

    background = BananaBackgroundDark,
    surface = BananaSurfaceDark,
    onBackground = Color(0xFFE6E2D9),
    onSurface = Color(0xFFE6E2D9),

    secondary = Color(0xFFD2C6A1),
    onSecondary = Color(0xFF373016)
)

private val LightColorScheme = lightColorScheme(
    primary = BananaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE135),
    onPrimaryContainer = Color(0xFF221B00),

    background = Color(0xFFFFFBFF), // Slightly warm white
    surface = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1D1B16),
    onSurface = Color(0xFF1D1B16),

    secondary = Color(0xFF645E44),
    onSecondary = Color(0xFFFFFFFF)
)

@Composable
fun VocabBananaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),


    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}