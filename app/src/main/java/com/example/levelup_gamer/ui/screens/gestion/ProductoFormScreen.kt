package com.example.levelup_gamer.ui.screens.gestion

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.viewmodel.GestionProductosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    viewModel: GestionProductosViewModel,
    producto: Producto?,
    onBack: () -> Unit
) {
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var precio by remember { mutableStateOf(producto?.precio?.toString() ?: "") }
    var stock by remember { mutableStateOf(producto?.stock?.toString() ?: "") }
    var imagen by remember { mutableStateOf(producto?.imagenUrl ?: "") }

    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (producto?.id?.isNotEmpty() == true) "Editar Producto" else "Nuevo Producto")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val productoActualizado = Producto(
                                id = producto?.id ?: "",
                                nombre = nombre,
                                precio = precio.toDoubleOrNull() ?: 0.0,
                                stock = stock.toIntOrNull() ?: 0,
                                imagenUrl = imagen
                            )

                            if (producto?.id?.isNotEmpty() == true) {
                                viewModel.actualizarProducto(producto.id, productoActualizado)
                            } else {
                                viewModel.crearProducto(productoActualizado)
                            }
                            onBack()
                        },
                        enabled = !cargando && nombre.isNotEmpty() && precio.toDoubleOrNull() != null
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
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = imagen,
                onValueChange = { imagen = it },
                label = { Text("URL de imagen (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}