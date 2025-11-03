package com.example.levelup_gamer.ui.screens.perfil

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.viewmodel.CarritoViewModel

@Composable
fun PerfilClienteScreen(
    nombre: String = "Cliente",
    onLogout: () -> Unit = {},
    onVerCarrito: () -> Unit = {},
    onVerResenas: () -> Unit = {},
    onAgregarResena: () -> Unit = {},
    viewModel: CarritoViewModel,
    onEscanearProducto: () -> Unit
) {
    val productos by viewModel.productos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val carrito by viewModel.carrito.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Bienvenido $nombre", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF4CAF50))
                        Text("Rol: Cliente", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                    TextButton(onClick = onLogout) { Text("Cerrar Sesión") }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BotonAnimado(onClick = onVerCarrito, color = Color(0xFF4CAF50), imageVector = Icons.Default.ShoppingCart, text = "Carrito", modifier = Modifier.weight(1f))
                    BotonAnimado(onClick = onVerResenas, color = Color(0xFF2196F3), imageVector = Icons.Default.Reviews, text = "Reseñas", modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BotonAnimado(onClick = onAgregarResena, color = Color(0xFFFF9800), imageVector = Icons.Default.Add, text = "Opinión", modifier = Modifier.weight(1f))
                    BotonAnimado(onClick = onEscanearProducto, color = Color(0xFF9C27B0), imageVector = Icons.Default.QrCodeScanner, text = "Escanear", modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Catálogo de Productos", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))

        // Contenido de la lista
        if (cargando && productos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (productos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay productos disponibles") }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(productos, key = { it.id }) { producto ->
                    val itemEnCarrito = carrito.find { it.producto.id == producto.id }
                    ItemProducto(
                        producto = producto,
                        cantidadEnCarrito = itemEnCarrito?.cantidad ?: 0,
                        onAgregar = { viewModel.agregarAlCarrito(producto) },
                        onRemover = { itemEnCarrito?.let { viewModel.removerDelCarrito(it) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun BotonAnimado(
    onClick: () -> Unit,
    color: Color,
    imageVector: ImageVector?,
    text: String,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed) 0.9f else 1f, animationSpec = tween(120), label = "")
    val rememberedOnClick by rememberUpdatedState(onClick)

    Button(
        onClick = {
            pressed = true
            rememberedOnClick()
        },
        modifier = modifier.scale(scale),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = MaterialTheme.shapes.medium
    ) {
        if (imageVector != null) {
            Icon(imageVector, contentDescription = text)
            Spacer(Modifier.width(6.dp))
        }
        Text(text)
    }
}

@Composable
fun ItemProducto(
    producto: Producto,
    cantidadEnCarrito: Int,
    onAgregar: () -> Unit,
    onRemover: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(model = producto.imagenUrl, contentDescription = "Imagen del producto", modifier = Modifier.fillMaxWidth().height(180.dp), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = producto.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "\$${String.format("%.2f", producto.precio)}", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            when {
                producto.stock <= 0 -> Text("Agotado", color = Color.Red, fontWeight = FontWeight.Bold)
                producto.stock < 10 -> Text("Últimas ${producto.stock} unidades", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
                else -> Text("Stock disponible: ${producto.stock}", color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (cantidadEnCarrito > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("En carrito: $cantidadEnCarrito", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onRemover, enabled = cantidadEnCarrito > 0) { Icon(Icons.Outlined.Remove, contentDescription = "Quitar uno") }
                        IconButton(onClick = onAgregar, enabled = producto.stock > cantidadEnCarrito) { Icon(Icons.Default.Add, contentDescription = "Agregar uno") }
                    }
                }
            } else {
                Button(onClick = onAgregar, modifier = Modifier.fillMaxWidth(), enabled = producto.stock > 0) {
                    Text(if (producto.stock > 0) "Agregar al Carrito" else "Agotado")
                }
            }
        }
    }
}