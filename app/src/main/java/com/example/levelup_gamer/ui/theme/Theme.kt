package com.example.levelup_gamer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyberBlue,
    secondary = CyberGreen,
    tertiary = CyberGray,
    background = CyberBlack,
    surface = CyberBlack,
    onPrimary = CyberWhite,
    onSecondary = CyberBlack,
    onBackground = CyberWhite,
    onSurface = CyberWhite
)

private val LightColorScheme = lightColorScheme(
    primary = CyberBlue,
    secondary = CyberGreen,
    tertiary = CyberGray,
    background = CyberWhite,
    surface = CyberWhite,
    onPrimary = CyberBlack,
    onSecondary = CyberBlack,
    onBackground = CyberBlack,
    onSurface = CyberBlack
)

@Composable
fun LevelUPGamerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desactivamos colores dinámicos para mantener nuestro tema
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