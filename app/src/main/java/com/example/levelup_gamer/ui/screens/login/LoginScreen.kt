package com.example.levelup_gamer.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginExitoso: (rol: String) -> Unit,
    onIrRegistro: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rolSeleccionado by remember { mutableStateOf("cliente") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Inicio de Sesión", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Rol:")
            Spacer(modifier = Modifier.width(8.dp))
            DropdownMenuDemo(selected = rolSeleccionado, onSelect = { rolSeleccionado = it })
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onLoginExitoso(rolSeleccionado) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onIrRegistro) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}

@Composable
fun DropdownMenuDemo(selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val roles = listOf("cliente", "admin")

    Box {
        Button(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            roles.forEach { rol ->
                DropdownMenuItem(
                    text = { Text(rol) },
                    onClick = {
                        onSelect(rol)
                        expanded = false
                    }
                )
            }
        }
    }
}
