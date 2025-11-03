package com.example.levelup_gamer.ui.screens.catalogo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun CatalogoScreen(
    onVerCarrito: () -> Unit,
    onVerDetalleProducto: (String) -> Unit,
    viewModel: CarritoViewModel
) {
    val productos by viewModel.productos.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo") },
                actions = {
                    IconButton(onClick = onVerCarrito) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Ver carrito")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (productos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay productos disponibles.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(productos) { producto ->
                    ProductoItem(
                        producto = producto,
                        onVerDetalle = { onVerDetalleProducto(producto.id) },
                        onAgregarCarrito = { viewModel.agregarAlCarrito(producto) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductoItem(
    producto: Producto,
    onVerDetalle: () -> Unit,
    onAgregarCarrito: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
            Text(text = "Precio: $${producto.precio}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = onVerDetalle) {
                    Text("Ver Detalle")
                }
                Button(onClick = onAgregarCarrito) {
                    Text("Agregar")
                }
            }
        }
    }
}
