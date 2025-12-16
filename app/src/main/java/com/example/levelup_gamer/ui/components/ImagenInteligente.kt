package com.example.levelup_gamer.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ImagenInteligente(
    uri: Uri?,
    modifier: Modifier = Modifier
) {
    if (uri != null) {
        // Si hay un Uri, muestra la imagen
        Image(
            painter = rememberAsyncImagePainter(model = uri),
            contentDescription = "Imagen de perfil",
            modifier = modifier
                .size(150.dp)
                .clip(CircleShape), // Muestra la imagen circular
            contentScale = ContentScale.Crop
        )
    } else {
        // Si no hay Uri, muestra el ícono por defecto
        Image(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Ícono de perfil por defecto",
            modifier = modifier.size(150.dp),
            contentScale = ContentScale.Crop
        )
    }
}