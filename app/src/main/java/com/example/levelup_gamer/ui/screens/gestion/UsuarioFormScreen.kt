package com.example.levelup_gamer.ui.screens.gestion

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.levelup_gamer.model.Usuario
import com.example.levelup_gamer.viewmodel.GestionUsuariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioFormScreen(
    viewModel: GestionUsuariosViewModel,
    usuario: Usuario?,
    onBack: () -> Unit
) {
    var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }
    var correo by remember { mutableStateOf(usuario?.correo ?: "") }
    var clave by remember { mutableStateOf(usuario?.clave ?: "") }
    var rol by remember { mutableStateOf(usuario?.rol ?: "cliente") }

    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (usuario?.correo?.isNotEmpty() == true) "Editar Usuario" else "Nuevo Usuario")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val usuarioActualizado = Usuario(
                                correo = correo,
                                clave = clave,
                                nombre = nombre,
                                rol = rol,
                            )

                            if (usuario?.correo?.isNotEmpty() == true) {
                                viewModel.actualizarUsuario(usuario.correo, usuarioActualizado)
                            } else {
                                viewModel.crearUsuario(usuarioActualizado)
                            }
                            onBack()
                        },
                        enabled = !cargando && nombre.isNotEmpty() && correo.isNotEmpty() && clave.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = usuario?.correo?.isEmpty() != false // Solo editable si es nuevo usuario
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de rol
            Text("Rol:", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = rol == "cliente",
                    onClick = { rol = "cliente" },
                    label = { Text("Cliente") }
                )
                FilterChip(
                    selected = rol == "admin",
                    onClick = { rol = "admin" },
                    label = { Text("Administrador") }
                )
            }
        }
    }
}