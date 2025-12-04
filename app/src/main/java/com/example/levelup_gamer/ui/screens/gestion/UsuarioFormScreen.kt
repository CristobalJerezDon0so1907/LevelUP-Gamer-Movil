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
    var rol by remember { mutableStateOf(usuario?.rol ?: "cliente") }

    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (usuario?.id?.isNotEmpty() == true)
                            "Editar usuario"
                        else
                            "Nuevo usuario"
                    )
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
                                id = usuario?.id ?: "",
                                nombre = nombre,
                                correo = correo,
                                rol = rol
                            )

                            viewModel.guardarUsuario(usuarioActualizado)
                            onBack()
                        },
                        enabled = !cargando && nombre.isNotBlank() && correo.isNotBlank()
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
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = rol,
                onValueChange = { rol = it },
                label = { Text("Rol (admin / cliente)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
