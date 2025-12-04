package com.example.levelup_gamer.ui.screens.gestion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PedidoAdminUI(
    val idDoc: String,
    val correoUsuario: String,
    val estado: String,
    val fecha: Long,
    val total: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionPedidosScreen(
    onBack: () -> Unit = {}
) {
    val db = remember { FirebaseFirestore.getInstance() }
    var pedidos by remember { mutableStateOf<List<PedidoAdminUI>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    // Cargamos los pedidos en tiempo real
    LaunchedEffect(Unit) {
        db.collection("pedidos")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    cargando = false
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents?.map { doc ->
                    PedidoAdminUI(
                        idDoc = doc.id,
                        correoUsuario = doc.getString("correoUsuario") ?: "",
                        estado = doc.getString("estado") ?: "Pendiente",
                        fecha = doc.getLong("fecha") ?: 0L,
                        total = doc.getDouble("total") ?: 0.0
                    )
                } ?: emptyList()

                pedidos = lista
                cargando = false
            }
    }

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de pedidos") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Volver") }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                cargando -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                pedidos.isEmpty() -> Text(
                    "No hay pedidos registrados",
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pedidos) { pedido ->
                            PedidoAdminCard(
                                pedido = pedido,
                                onCambiarEstado = { nuevoEstado ->
                                    db.collection("pedidos")
                                        .document(pedido.idDoc)
                                        .update("estado", nuevoEstado)
                                },
                                formatoFecha = formatoFecha
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PedidoAdminCard(
    pedido: PedidoAdminUI,
    onCambiarEstado: (String) -> Unit,
    formatoFecha: SimpleDateFormat
) {
    var expanded by remember { mutableStateOf(false) }

    val fechaStr = if (pedido.fecha != 0L) {
        formatoFecha.format(Date(pedido.fecha))
    } else ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Pedido #${pedido.idDoc.takeLast(6)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))

            Text("Correo: ${pedido.correoUsuario}")
            if (fechaStr.isNotEmpty()) Text("Fecha: $fechaStr")
            Text("Total: $${String.format("%.2f", pedido.total)}")

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado: ${pedido.estado}",
                    fontWeight = FontWeight.Bold
                )

                Box {
                    TextButton(onClick = { expanded = true }) {
                        Text("Cambiar estado")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("Pendiente", "En camino", "Entregado").forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    expanded = false
                                    onCambiarEstado(opcion)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
