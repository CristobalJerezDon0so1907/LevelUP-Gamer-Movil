package com.example.levelup_gamer.ui.screens.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.example.levelup_gamer.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productoId: String,
    onVolver: () -> Unit,
    onAgregarCarrito: (Producto) -> Unit
) {
    var producto by remember { mutableStateOf<Producto?>(null) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(productoId) {
        FirebaseFirestore.getInstance()
            .collection("producto")
            .document(productoId)
            .get()
            .addOnSuccessListener { doc ->
                producto = doc.toObject(Producto::class.java)?.copy(id = doc.id)
                cargando = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    TextButton(onClick = onVolver) { Text("Volver") }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when {
                cargando -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                producto == null -> Text(
                    "Producto no encontrado",
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        AsyncImage(
                            model = producto!!.imagenUrl,
                            contentDescription = producto!!.nombre,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            producto!!.nombre,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(producto!!.descripcion)

                        Spacer(Modifier.height(8.dp))

                        Text("Precio: $${producto!!.precio}", fontWeight = FontWeight.Bold)

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = { onAgregarCarrito(producto!!) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Agregar al carrito")
                        }
                    }
                }
            }
        }
    }
}
