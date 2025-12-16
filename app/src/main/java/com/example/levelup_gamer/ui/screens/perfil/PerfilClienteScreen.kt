package com.example.levelup_gamer.ui.screens.perfil

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.levelup_gamer.R
import com.example.levelup_gamer.model.EstadoPedido
import com.example.levelup_gamer.notifications.PedidoNotificationHelper
import com.example.levelup_gamer.ui.screens.perfil.ItemProducto
import com.example.levelup_gamer.viewmodel.CarritoViewModel
import com.example.levelup_gamer.viewmodel.LoginViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest

// region Theme Colors
private val PrimaryColor = Color(0xFF4CAF50)
private val SecondaryButtonColor = Color(0xFF555555)
private val CardBackgroundColor = Color.White
// endregion

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
    // region ViewModels & State
    val loginViewModel: LoginViewModel = viewModel()
    val userState by loginViewModel.user.collectAsState()

    val productos by viewModel.productos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val carrito by viewModel.carrito.collectAsState()

    val lazyListState = rememberLazyListState()
    var fotoUrl by remember { mutableStateOf<String?>(null) }
    // endregion

    // ============================
    // 🔔 NOTIFICACIONES: SOLO CUANDO CAMBIA EL ESTADO DEL PEDIDO
    // ============================
    val context = LocalContext.current
    val pedidoNotifier = remember { PedidoNotificationHelper(context) }

    val correoCliente = userState?.correo

    // Estado previo por pedido (para detectar cambios reales)
    val ultimoEstadoPorPedido = remember(correoCliente) { mutableStateMapOf<String, EstadoPedido>() }
    var primeraCarga by remember(correoCliente) { mutableStateOf(true) }

    if (!correoCliente.isNullOrBlank()) {
        DisposableEffect(correoCliente) {
            val reg = FirebaseFirestore.getInstance()
                .collection("pedidos")
                .whereEqualTo("correoCliente", correoCliente)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener

                    // ✅ DEBUG: confirma que llegan eventos y qué trae el estado
                    Log.d("NOTI", "docs=${snapshot.documents.size} changes=${snapshot.documentChanges.size}")
                    snapshot.documentChanges.forEach { c ->
                        Log.d("NOTI", "type=${c.type} id=${c.document.id} estado=${c.document.getString("estado")}")
                    }

                    // 1) Primera carga: sincroniza estados, NO notifica
                    if (primeraCarga) {
                        snapshot.documents.forEach { doc ->
                            val estado = parseEstadoPedido(doc.getString("estado"))
                            if (estado != null) {
                                ultimoEstadoPorPedido[doc.id] = estado
                            }
                        }
                        primeraCarga = false
                        return@addSnapshotListener
                    }

                    // 2) Cambios: notifica solo si se modificó/agregó y cambió el estado
                    snapshot.documentChanges.forEach { change ->
                        // ✅ CAMBIO: aceptar MODIFIED o ADDED
                        if (
                            change.type != DocumentChange.Type.MODIFIED &&
                            change.type != DocumentChange.Type.ADDED
                        ) return@forEach

                        val pedidoId = change.document.id
                        val nuevoEstado = parseEstadoPedido(change.document.getString("estado")) ?: return@forEach

                        val anterior = ultimoEstadoPorPedido[pedidoId]
                        if (anterior != null && anterior != nuevoEstado) {
                            pedidoNotifier.mostrarNotificacionPedido(nuevoEstado)
                        }

                        // Si es ADDED (nuevo pedido) y quieres notificar SOLO cambios,
                        // igual guardamos el estado para comparar en futuras modificaciones:
                        ultimoEstadoPorPedido[pedidoId] = nuevoEstado
                    }
                }

            onDispose {
                reg.remove()
            }
        }
    }
    // ============================

    // region Load profile photo from Firestore
    LaunchedEffect(userState?.correo) {
        val correo = userState?.correo ?: return@LaunchedEffect
        FirebaseFirestore.getInstance()
            .collection("usuario")
            .whereEqualTo("correo", correo)
            .limit(1)
            .get()
            .addOnSuccessListener { query ->
                fotoUrl = query.documents.firstOrNull()?.getString("fotoUrl")
            }
    }
    // endregion

    // region Infinite scroll: load more products
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
    // endregion

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // region Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp)
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!fotoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = Uri.parse(fotoUrl),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }

                            Column {
                                Text(
                                    text = "Bienvenido ${userState?.nombre ?: nombre}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = PrimaryColor
                                )
                                Text(
                                    text = "Rol: Cliente",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar perfil",
                            tint = PrimaryColor,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(start = 8.dp)
                                .clickable { onEditarPerfil() }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

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
                                    Badge(containerColor = Color(0xFFD32F2F)) {
                                        Text(cantidadTotal.toString())
                                    }
                                }
                            }
                        ) {
                            FilledTonalButton(
                                onClick = onVerCarrito,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = PrimaryColor,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ShoppingCart,
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
                                .fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = SecondaryButtonColor,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Agregar reseña"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Agregar reseña")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FilledTonalButton(
                        onClick = onVerPedidos,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = PrimaryColor,
                            contentColor = Color.White
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

                    Text(
                        text = "Consulta el estado de tus pedidos en 'Mis pedidos'",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onLogout) {
                            Text("Cerrar Sesión", color = Color(0xFFD32F2F))
                        }
                    }
                }
            }
            // endregion

            Text(
                text = "Catálogo de Productos",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
            )

            when {
                cargando && productos.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryColor)
                    }
                }

                productos.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay productos disponibles", color = Color.White)
                    }
                }

                else -> {
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
                                    CircularProgressIndicator(color = PrimaryColor)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ✅ Conversión más tolerante (EN CAMINO o EN_CAMINO)
private fun parseEstadoPedido(valor: String?): EstadoPedido? {
    val v = valor?.trim()?.uppercase() ?: return null
    return when {
        v == "PENDIENTE" || v == "PENDIENTE." -> EstadoPedido.PENDIENTE
        v == "EN_CAMINO" || v == "EN CAMINO" -> EstadoPedido.EN_CAMINO
        v == "ENTREGADO" || v == "ENTREGADO." -> EstadoPedido.ENTREGADO
        else -> null
    }
}
