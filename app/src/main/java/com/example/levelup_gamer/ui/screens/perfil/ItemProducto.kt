package com.example.levelup_gamer.ui.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.levelup_gamer.model.Producto

@Composable
fun ItemProducto(
    producto: Producto,
    cantidadEnCarrito: Int,
    onAgregar: () -> Unit,
    onRemover: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Imagen
            if (producto.imagenUrl.isNotBlank()) {
                AsyncImage(
                    model = producto.imagenUrl,
                    contentDescription = "Imagen de ${producto.nombre}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$${"%.2f".format(producto.precio)}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF2196F3)
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                producto.stock <= 0 -> {
                    Text(
                        text = "Agotado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                }
                producto.stock < 10 -> {
                    Text(
                        text = "Últimas ${producto.stock} unidades",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFF9800)
                    )
                }
                else -> {
                    Text(
                        text = "Stock disponible: ${producto.stock}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (cantidadEnCarrito > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "En carrito: $cantidadEnCarrito",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row {
                        IconButton(
                            onClick = onRemover,
                            modifier = Modifier.size(36.dp),
                            enabled = cantidadEnCarrito > 0
                        ) {
                            Icon(Icons.Outlined.Remove, contentDescription = "Quitar uno")
                        }

                        IconButton(
                            onClick = onAgregar,
                            modifier = Modifier.size(36.dp),
                            enabled = producto.stock > cantidadEnCarrito
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Agregar uno")
                        }
                    }
                }
            } else {
                Button(
                    onClick = onAgregar,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = producto.stock > 0
                ) {
                    Text(if (producto.stock > 0) "Agregar al Carrito" else "Agotado")
                }
            }
        }
    }
}
