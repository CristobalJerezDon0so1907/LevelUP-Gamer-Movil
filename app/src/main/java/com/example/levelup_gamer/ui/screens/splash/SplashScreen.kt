package com.example.levelup_gamer.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.levelup_gamer.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    // Animable para la escala del logo
    val scale = remember { Animatable(0.5f) }

    // Duración de la animación
    val duration = 1500

    LaunchedEffect(Unit) {
        // Animación equivalente a:
        // from { transform: scale(0.5) } to { transform: scale(1.0) }
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = duration,
                easing = CubicBezierEasing(0.39f, 0.575f, 0.565f, 1.0f)
            )
        )

        // Pequeña pausa opcional antes de navegar al siguiente screen
        delay(800)
        // onSplashFinished() o acción de navegación si aplica
    }

    // Aplicamos la escala al logo dentro de un contenedor
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale.value,
                scaleY = scale.value,
                transformOrigin = TransformOrigin.Center
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.logofeo), // tu logo
            contentDescription = "Logo animado",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
