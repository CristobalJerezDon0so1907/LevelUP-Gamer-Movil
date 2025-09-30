package com.example.levelup_gamer.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.material3.*
import com.example.levelup_gamer.R
@Composable
fun SplashScreen(
    onAnimationEnd: () -> Unit = {}
) {
    val rotationY = remember { Animatable(0f) }
    val opacity = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Lanzamos la animación
        rotationY.animateTo(
            targetValue = -180f,
            animationSpec = tween(
                durationMillis = 400,
                easing = CubicBezierEasing(0.455f, 0.03f, 0.515f, 0.955f)
            )
        )
        // Esperamos un momento y luego llamamos a una acción (ej: navegación)
        delay(300)
        onAnimationEnd()
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // <-- tu logo aquí
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    rotationY = rotationY.value
                    cameraDistance = 8 * density // Para dar más realismo 3D
                }
                .alpha(opacity.value)
        )
    }
}
