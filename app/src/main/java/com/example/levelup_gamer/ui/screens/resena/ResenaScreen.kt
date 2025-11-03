package com.example.levelup_gamer.ui.screens.resenas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.levelup_gamer.components.RatingBar
import com.example.levelup_gamer.model.Resena
import com.example.levelup_gamer.viewmodel.ResenaViewModel
import java.util.*
import androidx.compose.ui.platform.LocalContext
import com.example.levelup_gamer.utils.showResenaNotification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarResenaScreen(
    onVolver: () -> Unit,
    onResenaAgregada: () -> Unit,
    viewModel: ResenaViewModel
) {
    // Obtiene el contexto para usar en la función de notificación
    val context = LocalContext.current

    var rating by remember { mutableStateOf(0f) }
    var comentario by remember { mutableStateOf("") }
    var juego by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("Usuario") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Escribir Reseña",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = {
                        if (rating > 0 && comentario.isNotBlank()) {
                            val resena = Reseñas(
                                userId = "user_id_actual",
                                userName = userName,
                                rating = rating,
                                comment = comentario,
                                juego = juego,
                                timestamp = Date(),
                                isVerified = true
                            )
                            viewModel.addResena(resena) {

                                // **********************************************
                                // DISPARAR LA NOTIFICACIÓN EN CASO DE ÉXITO
                                // **********************************************
                                showResenaNotification(
                                    context,
                                    "¡Reseña Publicada!",
                                    "Tu reseña de $juego ha sido enviada con $rating estrellas. ¡Gracias!"
                                )

                                onResenaAgregada()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = rating > 0 && comentario.isNotBlank()
                ) {
                    Text("Publicar Reseña")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calificación
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Calificación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    RatingBar(
                        rating = rating,
                        onRatingChange = { rating = it },
                        editable = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (rating > 0) "Tu calificación: $rating estrellas"
                        else "Selecciona una calificación",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Juego (opcional)
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Juego (opcional)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = juego,
                        onValueChange = { juego = it },
                        label = { Text("Nombre del juego") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // Comentario
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Tu Reseña",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text("Escribe tu experiencia...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )
                    Text(
                        text = "${comentario.length}/500 caracteres",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}
