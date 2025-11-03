package com.tuproyecto.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.components.RatingBar
import com.example.levelup_gamer.components.ReviewCard
import com.example.levelup_gamer.model.Review
import com.example.levelup_gamer.viewmodel.ReviewViewModel
import java.util.*

@Composable
fun ReviewScreen(
    productId: String? = null,
    userId: String = "current_user_id", // deberías obtenerlo de FirebaseAuth o similar
    userName: String = "Usuario"
) {
    val viewModel: ReviewViewModel = viewModel()

    // ✅ Convertir StateFlow a estados observables de Compose
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showAddReview by remember { mutableStateOf(false) }
    var newRating by remember { mutableStateOf(0f) }
    var newComment by remember { mutableStateOf("") }

    LaunchedEffect(productId) {
        viewModel.loadReviews(productId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reseñas (${reviews.size})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Button(onClick = { showAddReview = true }) {
                Text("Escribir reseña")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de reseñas
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            reviews.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay reseñas aún",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(reviews) { review ->
                        ReviewCard(review = review)
                    }
                }
            }
        }
    }

    // Dialog para agregar reseña
    if (showAddReview) {
        AlertDialog(
            onDismissRequest = { showAddReview = false },
            title = { Text("Escribir reseña") },
            text = {
                Column {
                    Text("Calificación:")
                    Spacer(modifier = Modifier.height(8.dp))
                    RatingBar(
                        rating = newRating,
                        onRatingChange = { newRating = it },
                        editable = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        label = { Text("Tu reseña") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newRating > 0 && newComment.isNotBlank()) {
                            val review = Review(
                                userId = userId,
                                userName = userName,
                                rating = newRating,
                                comment = newComment,
                                productId = productId ?: "",
                                timestamp = Date()
                            )
                            viewModel.addReview(review) {
                                showAddReview = false
                                newRating = 0f
                                newComment = ""
                            }
                        }
                    },
                    enabled = newRating > 0 && newComment.isNotBlank()
                ) {
                    Text("Publicar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddReview = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Mostrar errores
    errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Aquí podrías usar un Snackbar o Log
            println("Error: $error")
            viewModel.clearError()
        }
    }
}
