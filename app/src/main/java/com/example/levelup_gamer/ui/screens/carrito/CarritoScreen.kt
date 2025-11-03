package com.example.levelup_gamer.ui.screens.carrito

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.viewmodel.CarritoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    onVolverAlCatalogo: () -> Unit,
    onConfirmarPago: () -> Unit,
    viewModel: CarritoViewModel
) {
    val carrito by viewModel.carrito.collectAsState(initial = emptyMap())
    val total by viewModel.total.collectAsState(initial = 0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = onVolverAlCatalogo) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Total: $${"%.2f".format(total)}")
                    Button(onClick = onConfirmarPago) {
                        Text("Pagar")
                    }
                }
            }
        }
    ) { paddingValues ->
        if (carrito.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Tu carrito está vacío.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(carrito.entries.toList()) { (producto, cantidad) ->
                    CarritoItem(
                        producto = producto,
                        cantidad = cantidad,
                        onAgregar = { viewModel.agregarAlCarrito(producto) },
                        onEliminar = { viewModel.eliminarDelCarrito(producto) }
                    )
                }
            }
        }
    }
}

@Composable
fun CarritoItem(
    producto: Producto,
    cantidad: Int,
    onAgregar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text("Precio: $${producto.precio}", style = MaterialTheme.typography.bodyMedium)
                Text("Cantidad: $cantidad", style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Remove, contentDescription = "Eliminar")
                }
                IconButton(onClick = onAgregar) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }
        }
    }
}
