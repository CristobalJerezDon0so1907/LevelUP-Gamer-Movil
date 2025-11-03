package com.example.levelup_gamer.ui.screens.perfil

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PerfilAdminScreen(
    nombre: String = "Administrador",
    onLogout: () -> Unit = {},
    onVerResenas: () -> Unit = {},
    onGestionUsuarios: () -> Unit = {},
    onVerReportes: () -> Unit = {},
    onConfiguraciones: () -> Unit = {},
    onSoporte: () -> Unit = {},
    onEscanearProducto: () -> Unit
) {
    // Lista de opciones del administrador
    val opciones = listOf(
        "Gestionar usuarios",
        "Ver reportes",
        "Configuraciones",
        "Soporte"
    )

    @Composable
    fun BotonAnimado(
        onClick: () -> Unit,
        color: Color,
        icon: @Composable (() -> Unit)? = null,
        text: String,
        modifier: Modifier = Modifier
    ) {
        var pressed by remember { mutableStateOf(false) }

        val scale by animateFloatAsState(
            targetValue = if (pressed) 0.90f else 1f,
            animationSpec = tween(durationMillis = 120),
            label = ""
        )

        Button(
            onClick = {
                pressed = true
                onClick()
                pressed = false
            },
            modifier = modifier.scale(scale),
            colors = ButtonDefaults.buttonColors(containerColor = color),
            shape = MaterialTheme.shapes.medium
        ) {
            if (icon != null) {
                icon()
                Spacer(Modifier.width(6.dp))
            }
            Text(text)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // === HEADER DEL PERFIL ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Información del perfil
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bienvenido, $nombre",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "Rol: Administrador",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    // Botón de cerrar sesión
                    TextButton(onClick = onLogout) {
                        Text(
                            text = "Cerrar sesión",
                            color = Color(0xFFD32F2F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones
                BotonAnimado(
                    onClick = onVerResenas,
                    color = Color(0xFF2196F3),
                    icon = { Icon(Icons.Default.Reviews, contentDescription = null) },
                    text = "Ver Reseñas",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                BotonAnimado(
                    onClick = onEscanearProducto,
                    color = Color(0xFF9C27B0),
                    text = "Escanear",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )


            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Lista de Opciones
        Text(
            text = "Opciones del Administrador",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF4CAF50),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                opciones.forEachIndexed { index, opcion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (opcion) {
                                    "Gestionar usuarios" -> onGestionUsuarios()
                                    "Ver reportes" -> onVerReportes()
                                    "Configuraciones" -> onConfiguraciones()
                                    "Soporte" -> onSoporte()
                                }
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opcion,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                    }
                    if (index < opciones.size - 1) {
                        Divider()
                    }
                }
            }
        }
    }
}
