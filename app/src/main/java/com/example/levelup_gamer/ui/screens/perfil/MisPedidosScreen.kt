package com.example.levelup_gamer.ui.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PedidoUI(
    val id: String,
    val fecha: String,
    val estado: String,
    val total: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPedidosScreen(
    correoUsuario: String,
    onBack: () -> Unit = {}
) {
    var pedidos by remember { mutableStateOf<List<PedidoUI>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(correoUsuario) {
        if (correoUsuario.isBlank()) {
            cargando = false
            return@LaunchedEffect
        }

        val db = FirebaseFirestore.getInstance()
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        db.collection("pedidos")                          // 👈 nombre de tu colección
            .whereEqualTo("correoUsuario", correoUsuario) // 👈 campo tal como está en tu screenshot
            .get()
            .addOnSuccessListener { snapshot ->
                pedidos = snapshot.documents.map { doc ->

                    val fechaMillis = doc.getLong("fecha")
                    val fechaStr = fechaMillis?.let { millis ->
                        formatoFecha.format(Date(millis))
                    } ?: ""

                    PedidoUI(
                        id = doc.id,
                        estado = doc.getString("estado") ?: "Sin estado",
                        fecha = fechaStr,
                        total = doc.getDouble("total") ?: 0.0 // si no tienes campo total, queda 0.0
                    )
                }
                cargando = false
            }
            .addOnFailureListener { e ->
                Log.e("MisPedidos", "Error al cargar pedidos", e)
                cargando = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis pedidos") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                pedidos.isEmpty() -> {
                    Text(
                        text = "Todavía no tienes pedidos registrados",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pedidos) { pedido ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = "Pedido #${pedido.id.takeLast(6)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    if (pedido.fecha.isNotBlank()) {
                                        Text("Fecha: ${pedido.fecha}")
                                    }
                                    Text("Estado: ${pedido.estado}")
                                    Text(
                                        text = "Total: $${String.format("%.2f", pedido.total)}",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
