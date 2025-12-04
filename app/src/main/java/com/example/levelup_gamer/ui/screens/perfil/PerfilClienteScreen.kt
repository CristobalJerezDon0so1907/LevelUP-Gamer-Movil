package com.example.levelup_gamer.ui.screens.perfil

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.model.EstadoPedido
import com.example.levelup_gamer.state.PedidoEstadoHolder
import com.example.levelup_gamer.viewmodel.CarritoViewModel
import com.example.levelup_gamer.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PerfilClienteScreen(
    nombre: String = "Cliente",
    onLogout: () -> Unit = {},
    onVerCarrito: () -> Unit = {},
    onAgregarResena: () -> Unit = {},
    onEditarPerfil: () -> Unit = {},
    onVerPedidos: () -> Unit = {},
    viewModel: CarritoViewModel
) {
    val loginViewModel: LoginViewModel = viewModel()
    val userState by loginViewModel.user.collectAsState()

    val productos by viewModel.productos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val carrito by viewModel.carrito.collectAsState()
    val estadoPedido by PedidoEstadoHolder.estadoPedido.collectAsState()

    val lazyListState = rememberLazyListState()

    // Cargar estado del último pedido desde Firestore
    LaunchedEffect(userState?.correo) {
        val correo = userState?.correo ?: return@LaunchedEffect

        FirebaseFirestore.getInstance()
            .collection("pedidos")                  // cambia si tu colección se llama distinto
            .whereEqualTo("correoUsuario", correo)  // cambia el campo si es otro
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents.last()
                    val estadoStr = doc.getString("estado") ?: "PENDIENTE"

                    val nuevoEstado = when (estadoStr.uppercase()) {
                        "PENDIENTE" -> EstadoPedido.PENDIENTE
                        "EN_CAMINO", "EN CAMINO" -> EstadoPedido.EN_CAMINO
                        "ENTREGADO" -> EstadoPedido.ENTREGADO
                        else -> null
                    }
                    PedidoEstadoHolder.actualizarEstado(nuevoEstado)
                } else {
                    PedidoEstadoHolder.actualizarEstado(null)
                }
            }
            .addOnFailureListener {
                PedidoEstadoHolder.actualizarEstado(null)
            }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
            .collectLatest { visibleItems ->
                if (visibleItems.isNotEmpty()) {
                    val lastVisibleItem = visibleItems.last()
                    val loadMoreThreshold = 5
                    if (lastVisibleItem.index >= productos.size - loadMoreThreshold) {
                        viewModel.cargarMasProductos()
                    }
                }
            }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header + icono editar perfil
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bienvenido ${userState?.nombre ?: nombre}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "Rol: Cliente",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar perfil",
                        modifier = Modifier
                            .size(28.dp)
                            .padding(start = 8.dp)
                            .clickable { onEditarPerfil() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fila de botones principales (Carrito + Reseña)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgedBox(
                        modifier = Modifier.weight(1f),
                        badge = {
                            val cantidadTotal = carrito.sumOf { it.cantidad }
                            if (cantidadTotal > 0) {
                                Badge { Text(cantidadTotal.toString()) }
                            }
                        }
                    ) {
                        FilledTonalButton(
                            onClick = onVerCarrito,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Icon(
                                Icons.Filled.ShoppingCart,
                                contentDescription = "Carrito"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ver Carrito")
                        }
                    }

                    FilledTonalButton(
                        onClick = onAgregarResena,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Agregar reseña"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar reseña")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 NUEVO: botón "Mis pedidos" bonito, similar a Ver Carrito
                FilledTonalButton(
                    onClick = onVerPedidos,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF4CAF50) // mismo verde del carrito
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = "Mis pedidos"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mis pedidos")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Texto de estado actual
                Text(
                    text = when (estadoPedido) {
                        EstadoPedido.PENDIENTE -> "Estado de tu pedido: Pendiente"
                        EstadoPedido.EN_CAMINO -> "Estado de tu pedido: En camino"
                        EstadoPedido.ENTREGADO -> "Estado de tu pedido: Entregado"
                        null -> "No tienes pedidos activos"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onLogout) {
                        Text("Cerrar Sesión")
                    }
                }
            }
        }

        // Catálogo
        Text(
            text = "Catálogo de Productos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                items(items = productos) { producto ->
                    ItemProducto(
                        producto = producto,
                        cantidadEnCarrito = carrito.find { it.producto.id == producto.id }?.cantidad
                            ?: 0,
                        onAgregar = {
                            if (producto.stock > 0) {
                                viewModel.agregarAlCarrito(producto)
                            }
                        },
                        onRemover = { viewModel.removerDelCarrito(producto) }
                    )
                }

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
