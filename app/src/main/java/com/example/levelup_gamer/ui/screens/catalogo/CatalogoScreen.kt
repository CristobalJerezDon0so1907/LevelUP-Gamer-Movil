@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.levelup_gamer.ui.screens.catalogo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.repository.ProductoRepository
import com.example.levelup_gamer.viewmodel.CarritoViewModel
import com.example.levelup_gamer.viewmodel.CarritoViewModelFactory

@Composable
fun CatalogoScreen(
    onVerCarrito: () -> Unit = {}
) {
    // CORRECCIÓN: Creación segura del ViewModel con su Factory para evitar que la app se cierre.
    val productoRepository = remember { ProductoRepository() }
    val factory = CarritoViewModelFactory(productoRepository)
    val viewModel: CarritoViewModel = viewModel(factory = factory)

    val productos by viewModel.productos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val carrito by viewModel.carrito.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Catálogo de Productos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            BadgedBox(
                badge = {
                    if (carrito.isNotEmpty()) {
                        Badge { Text(carrito.sumOf { it.cantidad }.toString()) }
                    }
                }
            ) {
                IconButton(onClick = onVerCarrito) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (cargando && productos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(productos, key = { it.id }) { producto ->
                    val itemEnCarrito = carrito.find { it.producto.id == producto.id }

                    ProductoItem(
                        producto = producto,
                        // CORRECCIÓN: Optimizando las funciones para eliminar advertencias de rendimiento.
                        onAgregar = remember { { viewModel.agregarAlCarrito(producto) } },
                        onEliminar = remember { { itemEnCarrito?.let { viewModel.removerDelCarrito(it) } } },
                        cantidadEnCarrito = itemEnCarrito?.cantidad ?: 0
                    )
                }
            }
        }
    }
}

@Composable
fun ProductoItem(
    producto: Producto,
    onAgregar: () -> Unit,
    onEliminar: () -> Unit,
    cantidadEnCarrito: Int
) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\$${String.format("%.2f", producto.precio)}", // CORREGIDO: Sintaxis del símbolo '$'
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stock: ${producto.stock}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (cantidadEnCarrito > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = onEliminar, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar uno")
                        }

                        Text(
                            text = cantidadEnCarrito.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(onClick = onAgregar, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar uno más")
                        }
                    }
                } else {
                    Button(onClick = onAgregar, enabled = producto.stock > 0) {
                        Text(if (producto.stock > 0) "Agregar al carrito" else "Agotado")
                    }
                }
            }
        }
    }
}