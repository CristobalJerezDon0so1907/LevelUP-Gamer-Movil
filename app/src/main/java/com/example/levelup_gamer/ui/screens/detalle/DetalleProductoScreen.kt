package com.example.levelup_gamer.ui.screens.detalle

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.levelup_gamer.model.Producto

@Composable
fun DetalleProductoScreen(
    productoId: String,
    onVolver: () -> Unit,
    onAgregarCarrito: (Producto) -> Unit
) {
    // 🔹 Ejemplo temporal — en un caso real obtendrías el producto desde un ViewModel o repositorio
    val productoDemo = Producto(
        id = productoId, // ✅ El id es String, no se convierte a Int
        nombre = "Producto #$productoId",
        descripcion = "Este es el detalle completo del producto con ID $productoId.",
        precio = 19990.0,
        imagenUrl = "",
        stock = 10
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen del producto
            Image(
                painter = rememberAsyncImagePainter(model = productoDemo.imagenUrl.ifEmpty { "https://via.placeholder.com/200" }),
                contentDescription = productoDemo.nombre,
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre
            Text(
                text = productoDemo.nombre,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Descripción
            Text(
                text = productoDemo.descripcion,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Precio
            Text(
                text = "Precio: $${"%.2f".format(productoDemo.precio)}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Agregar al carrito
            Button(onClick = { onAgregarCarrito(productoDemo) }) {
                Text("Agregar al carrito")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón Volver
            Button(onClick = onVolver) {
                Text("Volver")
            }
        }
    }
}
