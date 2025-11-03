package com.example.levelup_gamer.ui.screens.resena

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.levelup_gamer.model.Resena
import com.example.levelup_gamer.utils.showResenaNotification
import com.example.levelup_gamer.viewmodel.ResenaViewModel
import kotlinx.coroutines.launch // <-- 1. Importa 'launch'
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarResenaScreen(
    onVolver: () -> Unit,
    onResenaAgregada: () -> Unit,
    viewModel: ResenaViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // <-- 2. Obtén el alcance de la corrutina

    var rating by remember { mutableStateOf(0f) }
    var comentario by remember { mutableStateOf("") }
    var juego by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("Usuario") } // TODO: Obtener del usuario logueado

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Escribir Reseña", fontWeight = FontWeight.Bold) },
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
                            val resena = Resena(
                                userId = "user_id_actual", // TODO: Reemplazar con ID real
                                userName = userName,
                                rating = rating,
                                comment = comentario,
                                juego = juego,
                                timestamp = Date(),
                                isVerified = true
                            )
                            // <-- 3. Lanza una corrutina para la operación asíncrona
                            coroutineScope.launch {
                                try {
                                    // Llama a tu función suspend del ViewModel
                                    viewModel.addResena(resena)

                                    // Si la llamada anterior no lanzó una excepción, la operación fue exitosa
                                    showResenaNotification(
                                        context,
                                        "¡Reseña Publicada!",
                                        "Tu reseña de $juego ha sido enviada con $rating estrellas. ¡Gracias!"
                                    )
                                    onResenaAgregada()
                                } catch (e: Exception) {
                                    // Opcional pero recomendado: Maneja posibles errores
                                    // Por ejemplo, puedes mostrar un Toast o un Snackbar informando el error.
                                    // Log.e("AgregarResenaScreen", "Error al agregar reseña", e)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
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
            // TODO: Implementar o añadir componente RatingBar
            Text("Aquí va el componente para la calificación (RatingBar)")

            OutlinedTextField(
                value = juego,
                onValueChange = { juego = it },
                label = { Text("Nombre del juego (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Escribe tu experiencia...") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )
        }
    }
}
