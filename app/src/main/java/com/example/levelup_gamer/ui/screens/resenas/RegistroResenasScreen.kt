package com.example.levelup_gamer.ui.screens.resenas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.model.Resena
import com.example.levelup_gamer.viewmodel.ResenasViewModel
import androidx.compose.ui.graphics.Color
import com.example.levelup_gamer.model.Producto


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroResenasScreen(
    productoNombreInicial: String? = null,
    productIdInicial: String? = null,
    viewModel: ResenasViewModel = viewModel(),
    productos: List<Producto> = emptyList(),
    onBack: () -> Unit = {}
) {
    val resenas by viewModel.resenas.collectAsState()
    val productosCatalogo by remember(productos) {
        mutableStateOf(
            productos
                .distinctBy { it.nombre }
        )
    }

    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var selectedProducto by remember { mutableStateOf(productoNombreInicial ?: "") }
    var selectedProductId by remember { mutableStateOf(productIdInicial ?: "") }

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarResenas()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reseñas de Catálogo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text("Selecciona el producto:", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {

                OutlinedTextField(
                    value = selectedProducto,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Producto a reseñar") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    productosCatalogo.forEach { producto ->
                        DropdownMenuItem(
                            text = { Text(producto.nombre) },
                            onClick = {
                                selectedProducto = producto.nombre
                                selectedProductId = producto.id
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Deja tu reseña:", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Comentario") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Selecciona rating:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row {
                (1..5).forEach { star ->
                    IconButton(onClick = { rating = star }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating $star",
                            tint = if (star <= rating) Color(0xFFFFC107) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (comment.isNotBlank() && selectedProductId.isNotBlank()) {
                        viewModel.agregarResena(
                            productoNombre = selectedProducto,
                            productId = selectedProductId,
                            rating = rating,
                            comment = comment
                        )
                        comment = ""
                        rating = 5
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Guardar reseña")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("Reseñas registradas:", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(resenas) { resena ->
                    ResenaItem(resena = resena)
                }
            }
        }
    }
}


@Composable
fun ResenaItem(resena: Resena) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            val nombreProducto = resena.producto.ifBlank { "Producto sin nombre" }

            Text(
                text = "⭐ ${resena.rating} - $nombreProducto",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(2.dp))

            val autor = resena.userName.ifBlank { resena.userEmail }
            if (autor.isNotBlank()) {
                Text(
                    text = "Por: $autor",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            Text(
                text = resena.comment,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
