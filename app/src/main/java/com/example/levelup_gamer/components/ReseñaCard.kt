package com.example.levelup_gamer.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.levelup_gamer.model.Reseñas
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ResenaCard(
    resena: Reseñas,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con nombre, fecha y rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = resena.userName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(resena.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                RatingBar(rating = resena.rating)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Juego reseñado (si existe)
            if (resena.juego.isNotEmpty()) {
                Text(
                    text = "Sobre: ${resena.juego}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Comentario
            Text(
                text = resena.comment,
                style = MaterialTheme.typography.bodyMedium
            )

            // Verificación
            if (resena.isVerified) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✓ Compra verificada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}