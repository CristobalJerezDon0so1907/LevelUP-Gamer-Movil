package com.example.levelup_gamer.ui.screens.perfil

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
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

private val PrimaryColor = Color(0xFF4CAF50)
private val SmallButtonHeight = 40.dp
private val SecondaryButtonColor = Color(0xFF2F2F2F)
private val Burdeo = Color(0xFF7A1E3A)
private val CardBackgroundColor = Color.Black.copy(alpha = 0.35f)
private val CardBorderColor = Color.White.copy(alpha = 0.10f)

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

    val lazyListState = rememberLazyListState()
    var fotoUrl by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val pedidoNotifier = remember { PedidoNotificationHelper(context) }

    val correoCliente = userState?.correo
    val ultimoEstadoPorPedido = remember(correoCliente) { mutableStateMapOf<String, EstadoPedido>() }
    var primeraCarga by remember(correoCliente) { mutableStateOf(true) }

    if (!correoCliente.isNullOrBlank()) {
        DisposableEffect(correoCliente) {
            val reg = FirebaseFirestore.getInstance()
                .collection("pedidos")
                .whereEqualTo("correoCliente", correoCliente)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) return@addSnapshotListener

                    Log.d("NOTI", "docs=${snapshot.documents.size} changes=${snapshot.documentChanges.size}")
                    snapshot.documentChanges.forEach { c ->
                        Log.d("NOTI", "type=${c.type} id=${c.document.id} estado=${c.document.getString("estado")}")
                    }

                    if (primeraCarga) {
                        snapshot.documents.forEach { doc ->
                            val estado = parseEstadoPedido(doc.getString("estado"))
                            if (estado != null) ultimoEstadoPorPedido[doc.id] = estado
                        }
                        primeraCarga = false
                        return@addSnapshotListener
                    }

                    snapshot.documentChanges.forEach { change ->
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
                        ultimoEstadoPorPedido[pedidoId] = nuevoEstado
                    }
                }

            onDispose { reg.remove() }
        }
    }

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

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.20f))
        )

        Column(modifier = Modifier.fillMaxSize()) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp)
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
                border = BorderStroke(1.dp, CardBorderColor)
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
                                        .background(Color.White.copy(alpha = 0.15f))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }

                            Column {
                                Text(
                                    text = "Bienvenido ${userState?.nombre ?: nombre}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White
                                )
                                Text(
                                    text = "Rol: Cliente",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.75f)
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

                    Spacer(modifier = Modifier.height(14.dp))

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
                                    Badge(containerColor = Burdeo) { Text(cantidadTotal.toString()) }
                                }
                            }
                        ) {
                            FilledTonalButton(
                                onClick = onVerCarrito,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(SmallButtonHeight),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = PrimaryColor,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ShoppingCart,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ver Carrito", style = MaterialTheme.typography.bodySmall, maxLines = 1)
                            }
                        }

                        FilledTonalButton(
                            onClick = onAgregarResena,
                            modifier = Modifier
                                .weight(1.2f)
                                .height(SmallButtonHeight),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = SecondaryButtonColor,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Agregar reseña",
                                maxLines = 1,
                                softWrap = false,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ✅ MIS PEDIDOS VA FUERA DEL ROW
                    FilledTonalButton(
                        onClick = onVerPedidos,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(SmallButtonHeight),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = PrimaryColor,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mis pedidos", style = MaterialTheme.typography.bodySmall, maxLines = 1)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Consulta el estado de tus pedidos en 'Mis pedidos'",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.75f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onLogout) {
                            Text("Cerrar Sesión", color = Burdeo)
                        }
                    }
                }
            }

            Text(
                text = "Catálogo de Productos",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)
            )

            when {
                cargando && productos.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryColor)
                    }
                }

                productos.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay productos disponibles", color = Color.White)
                    }
                }

                else -> {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items = productos) { producto ->
                            ItemProducto(
                                producto = producto,
                                cantidadEnCarrito = carrito.find { it.producto.id == producto.id }?.cantidad ?: 0,
                                onAgregar = { if (producto.stock > 0) viewModel.agregarAlCarrito(producto) },
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

private fun parseEstadoPedido(valor: String?): EstadoPedido? {
    val v = valor?.trim()?.uppercase() ?: return null
    return when {
        v == "PENDIENTE" || v == "PENDIENTE." -> EstadoPedido.PENDIENTE
        v == "EN_CAMINO" || v == "EN CAMINO" -> EstadoPedido.EN_CAMINO
        v == "ENTREGADO" || v == "ENTREGADO." -> EstadoPedido.ENTREGADO
        else -> null
    }
}
