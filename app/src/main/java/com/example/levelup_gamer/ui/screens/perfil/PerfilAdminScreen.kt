package com.example.levelup_gamer.ui.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PerfilAdminScreen(
    onVerReportes: () -> Unit,
    onGestionUsuarios: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Panel del Administrador ⚙️", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onVerReportes) {
            Text("Ver Reportes")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onGestionUsuarios) {
            Text("Gestionar Usuarios")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onCerrarSesion) {
            Text("Cerrar Sesión")
        }
    }
}
