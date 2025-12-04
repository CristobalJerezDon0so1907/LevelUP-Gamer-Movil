package com.example.levelup_gamer.ui.screens.splash

// Importaciones necesarias
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color // Ya no es estrictamente necesario, pero se mantiene si lo usas en otro lado
import androidx.compose.ui.layout.ContentScale // <-- NUEVA IMPORTACIÓN
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.levelup_gamer.R

@Composable
fun SplashSplash(
    modifier: Modifier = Modifier
) {
    // Animación de escala
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 900,
                easing = EaseOutBack
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        //

        Image(
            painter = painterResource(id = R.drawable.logofeo),
            contentDescription = "Logo LevelUP Gamer",
            modifier = Modifier
                .size(300.dp)
                .scale(scale.value)
        )
    }
}