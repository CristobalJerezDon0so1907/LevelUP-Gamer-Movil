package com.example.levelup_gamer.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.levelup_gamer.model.Resena
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ResenaCard(resena: Resena) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Nombre del usuario
            Text(
                text = resena.userName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rating
            Text("⭐ ${resena.rating}")

            Spacer(modifier = Modifier.height(8.dp))

            // Comentario
            Text(resena.comment)

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha formateada
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            Text(
                text = dateFormat.format(resena.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
