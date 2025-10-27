package com.example.levelup_gamer.ui.screens.perfil

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun PerfilAdminScreen(
    nombre: String = "Administrador",
    onLogout: () -> Unit = {},
    onOptionSelected: (String) -> Unit = {},
    onVerResenas: () -> Unit = {}
) {
    // Lista de opciones del admin
    val opciones = listOf(
        "Gestionar usuarios",
        "Ver reportes",
        "Configuraciones",
        "Soporte"
    )

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

                // === BOTÓN PARA VER RESEÑAS ===
                FilledTonalButton(
                    onClick = onVerResenas,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF2196F3)
                    )
                ) {
                    Icon(Icons.Default.Reviews, contentDescription = "Ver reseñas")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver Reseñas de Clientes")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // === LISTA DE OPCIONES DEL ADMIN ===
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
                            .clickable { onOptionSelected(opcion) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opcion,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                    }
                    // No mostrar divisor después del último elemento
                    if (index < opciones.size - 1) {
                        Divider()
                    }
                }
            }
        }
    }
}