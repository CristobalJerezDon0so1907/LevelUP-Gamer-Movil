package com.example.levelup_gamer.ui.screens.perfil

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.levelup_gamer.viewmodel.CarritoViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale


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

    // Scroll infinito - cargar más productos cuando se llega al final
    val lazyListState = rememberLazyListState()

    // Observar cuando llegamos al final de la lista
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty()) {
                    val lastVisibleItem = visibleItems.last()
                    val loadMoreThreshold = 5 // Cargar más cuando faltan 5 elementos

                    if (lastVisibleItem.index >= productos.size - loadMoreThreshold) {
                        viewModel.cargarMasProductos()
                    }
                }
            }
    }

    @Composable
    fun BotonAnimado(
        onClick: () -> Unit,
        color: Color,
        icon: @Composable (() -> Unit)? = null,
        text: String,
        modifier: Modifier = Modifier
    ) {
        var pressed by remember { mutableStateOf(false) }

        val scale by animateFloatAsState(
            targetValue = if (pressed) 0.90f else 1f,
            animationSpec = tween(durationMillis = 120),
            label = ""
        )

        Button(
            onClick = {
                pressed = true
                onClick()
                pressed = false
            },
            modifier = modifier.scale(scale),
            colors = ButtonDefaults.buttonColors(containerColor = color),
            shape = MaterialTheme.shapes.medium
        ) {
            if (icon != null) {
                icon()
                Spacer(Modifier.width(6.dp))
            }
            Text(text)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Header fijo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            // Información del perfil
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Bienvenido $nombre",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            "Rol: Cliente",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    // Botón de cerrar sesión
                    TextButton(
                        onClick = onLogout,
                    ) {
                        Text("Cerrar Sesión")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acciones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    BotonAnimado(
                        onClick = onVerCarrito,
                        color = Color(0xFF4CAF50),
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        text = "Carrito",
                        modifier = Modifier.weight(1f)
                    )

                    BotonAnimado(
                        onClick = onVerResenas,
                        color = Color(0xFF2196F3),
                        icon = { Icon(Icons.Default.Reviews, contentDescription = null) },
                        text = "Reseñas",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    BotonAnimado(
                        onClick = onAgregarResena,
                        color = Color(0xFFFF9800),
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = "Opinión",
                        modifier = Modifier.weight(1f)
                    )

                    BotonAnimado(
                        onClick = onEscanearProducto,
                        color = Color(0xFF9C27B0),
                        text = "Escanear",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Título del catálogo
        Text(
            "Catálogo de Productos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de productos con scroll infinito
        if (cargando && productos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (productos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay productos disponibles")
            }
        } else {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(productos) { producto ->
                    ItemProducto(
                        producto = producto,
                        cantidadEnCarrito = carrito.find { it.producto.id == producto.id }?.cantidad ?: 0,
                        onAgregar = {
                            if (producto.stock > 0) {
                                viewModel.agregarAlCarrito(producto)
                            }
                        },
                        onRemover = { viewModel.removerDelCarrito(producto) }
                    )
                }

                // Indicador de carga para scroll infinito
                item {
                    if (cargando && productos.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemProducto(
    producto: com.example.levelup_gamer.model.Producto,
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

            AsyncImage(
                model = producto.imagenUrl,
                contentDescription = "Imagen del producto",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$${"%.2f".format(producto.precio)}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF2196F3),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Mostrar stock
            when {
                producto.stock <= 0 -> Text("Agotado", color = Color.Red, fontWeight = FontWeight.Bold)
                producto.stock < 10 -> Text("Últimas ${producto.stock} unidades", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
                else -> Text("Stock disponible: ${producto.stock}", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Controles del carrito
            if (cantidadEnCarrito > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "En carrito: $cantidadEnCarrito",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onRemover, enabled = cantidadEnCarrito > 0) {
                            Icon(Icons.Outlined.Remove, contentDescription = "Quitar uno")
                        }
                        IconButton(onClick = onAgregar, enabled = producto.stock > cantidadEnCarrito) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar uno")
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
