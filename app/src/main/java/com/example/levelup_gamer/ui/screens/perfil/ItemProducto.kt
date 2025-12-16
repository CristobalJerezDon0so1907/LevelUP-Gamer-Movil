package com.example.levelup_gamer.ui.screens.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.levelup_gamer.model.Producto

private val PrimaryColor = Color(0xFF4CAF50)
private val Burdeo = Color(0xFF7A1E3A)

@Composable
fun ItemProducto(
    producto: Producto,
    cantidadEnCarrito: Int,
    onAgregar: () -> Unit,
    onRemover: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Imagen
            if (producto.imagenUrl.isNotBlank()) {
                AsyncImage(
                    model = producto.imagenUrl,
                    contentDescription = "Imagen de ${producto.nombre}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.06f)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Nombre
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Precio
            Text(
                text = "$${"%.2f".format(producto.precio)}",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryColor,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Stock
            when {
                producto.stock <= 0 -> {
                    Text(
                        text = "Agotado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFF6B6B),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                producto.stock < 10 -> {
                    Text(
                        text = "Últimas ${producto.stock} unidades",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFFC107),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                else -> {
                    Text(
                        text = "Stock disponible: ${producto.stock}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Controles carrito
            if (cantidadEnCarrito > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "En carrito: $cantidadEnCarrito",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onRemover,
                            modifier = Modifier.size(36.dp),
                            enabled = cantidadEnCarrito > 0
                        ) {
                            Icon(
                                Icons.Outlined.Remove,
                                contentDescription = "Quitar uno",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = onAgregar,
                            modifier = Modifier.size(36.dp),
                            enabled = producto.stock > cantidadEnCarrito
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Agregar uno",
                                tint = Color.White
                            )
                        }
                    }
                }
            } else {
                Button(
                    onClick = onAgregar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    enabled = producto.stock > 0,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Burdeo,
                        contentColor = Color.White,
                        disabledContainerColor = Color.White.copy(alpha = 0.15f),
                        disabledContentColor = Color.White.copy(alpha = 0.65f)
                    )
                ) {
                    Text(
                        if (producto.stock > 0) "Agregar al Carrito" else "Agotado",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
