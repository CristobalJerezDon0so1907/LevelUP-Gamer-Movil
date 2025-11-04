package com.example.levelup_gamer.ui.theme

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

// Paleta de colores optimizada para el tema oscuro "Gamer Neon"
private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,                // Verde neón para botones de acción principal
    onPrimary = DeepSpaceBlack,         // Texto oscuro sobre botones primarios
    secondary = ElectricBlue,           // Azul eléctrico para enlaces y acentos
    onSecondary = DeepSpaceBlack,       // Texto oscuro sobre acentos secundarios
    tertiary = NeonPurple,              // Morado neón para otras acciones
    onTertiary = DeepSpaceBlack,        // Texto oscuro sobre acentos terciarios
    background = DeepSpaceBlack,        // Fondo principal oscuro
    onBackground = LightGray,           // Texto principal claro sobre el fondo
    surface = CardBackground,           // Fondo para tarjetas y campos de texto
    onSurface = LightGray,              // Texto principal sobre tarjetas
    surfaceVariant = MediumGray,        // Color para bordes y divisores
    onSurfaceVariant = LightGray,       // Color de texto secundario o placeholders
    error = Color(0xFFFF5252),          // Un rojo brillante para errores
    onError = Color.Black
)

// Paleta de colores para un tema claro (menos prioritario, pero coherente)
private val LightColorScheme = lightColorScheme(
    primary = NeonGreen,
    onPrimary = Color.Black,
    secondary = ElectricBlue,
    onSecondary = Color.Black,
    tertiary = NeonPurple,
    onTertiary = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF0F0F0),      // Un gris muy claro para tarjetas
    onSurface = Color.Black,
    surfaceVariant = LightGray,
    onSurfaceVariant = Color.DarkGray,
    error = Color(0xFFD32F2F),
    onError = Color.White
)

@Composable
fun LevelUPGamerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // El color dinámico no se usa para mantener una estética consistente.
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
