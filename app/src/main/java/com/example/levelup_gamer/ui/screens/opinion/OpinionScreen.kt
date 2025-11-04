package com.example.levelup_gamer.ui.screens.opinion

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.repository.ResenaRepository
import com.example.levelup_gamer.ui.theme.NeonGreen
import com.example.levelup_gamer.viewmodel.OpinionUiState
import com.example.levelup_gamer.viewmodel.OpinionViewModel
import com.example.levelup_gamer.viewmodel.OpinionViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpinionScreen(
    onVolver: () -> Unit,
    onOpinionEnviada: () -> Unit
) {
    val context = LocalContext.current
    val factory = OpinionViewModelFactory(ResenaRepository())
    val viewModel: OpinionViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()

    var juego by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is OpinionUiState.Success -> {
                Toast.makeText(context, "¡Gracias por tu opinión!", Toast.LENGTH_LONG).show()
                viewModel.resetState()
                onOpinionEnviada()
            }
            is OpinionUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Deja tu Opinión") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = juego,
                onValueChange = { juego = it },
                label = { Text("Nombre del Juego") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Calificación", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            InteractiveRatingBar(rating = rating, onRatingChanged = { rating = it })
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Tu opinión cuenta...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { viewModel.addResena(juego, rating, comment) },
                enabled = uiState !is OpinionUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
            ) {
                if (uiState is OpinionUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Enviar Opinión", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
fun InteractiveRatingBar(rating: Float, onRatingChanged: (Float) -> Unit, maxRating: Int = 5) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..maxRating) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating $i",
                tint = if (i <= rating) Color.Yellow else Color.Gray,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingChanged(i.toFloat()) }
            )
        }
    }
}