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
import androidx.compose.runtime.collectAsState

@Composable
fun ReviewScreen(
    productId: String? = null,
    userId: String = "current_user_id", // Debes obtener esto de tu auth system
    userName: String = "Usuario"
) {
    val viewModel: ReviewViewModel = viewModel()
    var showAddReview by remember { mutableStateOf(false) }
    var newRating by remember { mutableStateOf(0f) }
    var newComment by remember { mutableStateOf("") }

    LaunchedEffect(key1 = productId) {
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
                text = "Reseñas (${viewModel.reviews.value.size})",
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
            viewModel.isLoading.value -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            viewModel.reviews.value.isEmpty() -> {
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
                    items(viewModel.reviews.value) { review ->
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
    viewModel.errorMessage.collectAsState<String?>().value?.let { error ->
        LaunchedEffect(error) {
            // Puedes mostrar un snackbar aquí
            println("Error: $error")
            viewModel.clearError()
        }
    }
}