package com.example.levelup_gamer.ui.screens.resena

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.levelup_gamer.components.RatingBar
import com.example.levelup_gamer.model.Resena
import com.example.levelup_gamer.utils.showResenaNotification
import com.example.levelup_gamer.viewmodel.ResenaViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarResenaScreen(
    onVolver: () -> Unit,
    onResenaAgregada: () -> Unit,
    viewModel: ResenaViewModel
) {
    val context = LocalContext.current

    var rating by remember { mutableStateOf(0f) }
    var comentario by remember { mutableStateOf("") }
    var juego by remember { mutableStateOf("") }
    val userName by remember { mutableStateOf("Usuario") } // TODO: Obtener del usuario logueado

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Efecto para manejar la navegación y notificaciones post-acción
    LaunchedEffect(key1 = isLoading) {
        if (!isLoading && errorMessage == null) {
            // Si ha terminado de cargar y no hay error, podría ser el fin de una operación exitosa
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Escribir Reseña", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver, enabled = !isLoading) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Button(
                    onClick = {
                        val resena = Resena(
                            userId = "user_id_actual", // TODO: Reemplazar con ID real
                            userName = userName,
                            rating = rating,
                            comment = comentario,
                            juego = juego,
                            timestamp = Date(),
                            isVerified = true
                        )
                        viewModel.addResena(resena).invokeOnCompletion {
                            if (it == null) { // Si no hubo excepción
                                showResenaNotification(
                                    context,
                                    "¡Reseña Publicada!",
                                    "Tu reseña para $juego ha sido enviada."
                                )
                                onResenaAgregada()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = rating > 0 && comentario.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Publicar Reseña")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.clearError() }) {
                    Text("Entendido")
                }
            }

            // Calificación con RatingBar
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tu Calificación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    RatingBar(rating = rating, onRatingChange = { if (!isLoading) rating = it }, editable = !isLoading)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (rating > 0) "Has seleccionado $rating estrellas" else "Por favor, selecciona una calificación",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            OutlinedTextField(
                value = juego,
                onValueChange = { juego = it },
                label = { Text("Nombre del juego (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Escribe tu experiencia...") },
                placeholder = { Text("Describe qué te gustó o no te gustó.") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                enabled = !isLoading
            )
        }
    }
}
