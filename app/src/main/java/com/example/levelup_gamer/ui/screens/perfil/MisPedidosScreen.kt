package com.example.levelup_gamer.ui.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.levelup_gamer.R

private val PrimaryColor = Color(0xFF4CAF50)
private val ManagementColor1 = Color(0xFF5E548E)
private val ManagementColor2 = Color(0xFF4C427B)
private val ManagementColor3 = Color(0xFF3B3068)
private val CardBackgroundColor = ManagementColor1
private val BackgroundColor = Color(0xFF1F1B3B)

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

        db.collection("pedidos")
            .whereEqualTo("correoUsuario", correoUsuario)
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
                        total = doc.getDouble("total") ?: 0.0
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
                title = { Text("Mis pedidos", color = Color.White) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Volver", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackgroundColor,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            Image(
                painter = painterResource(id = R.drawable.fondo),
                contentDescription = "Fondo de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            when {
                cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryColor
                    )
                }
                pedidos.isEmpty() -> {
                    Text(
                        text = "Todavía no tienes pedidos registrados",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pedidos) { pedido ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = CardBackgroundColor
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Pedido #${pedido.id.takeLast(6)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    if (pedido.fecha.isNotBlank()) {
                                        Text("Fecha: ${pedido.fecha}", color = Color.White.copy(alpha = 0.8f))
                                    }
                                    Text("Estado: ${pedido.estado}", color = Color.White.copy(alpha = 0.8f))
                                    Text(
                                        text = "Total: $${String.format("%.2f", pedido.total)}",
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryColor
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