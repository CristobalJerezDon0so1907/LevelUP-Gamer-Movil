package com.example.levelup_gamer.ui.screens.resena

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.levelup_gamer.components.ResenaCard
import com.example.levelup_gamer.viewmodel.ResenaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResenaScreen(
    onVolver: () -> Unit,
    onAgregarResena: () -> Unit,
    viewModel: ResenaViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadResenas()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reseñas de la Comunidad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onAgregarResena) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar reseña")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregarResena) {
                Icon(Icons.Default.Add, contentDescription = "Agregar reseña")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val loading = viewModel.isLoading.collectAsState().value
            val listaResenas = viewModel.resenas.collectAsState().value

            when {
                loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }

                listaResenas.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No hay reseñas aún", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onAgregarResena) { Text("¡Sé el primero en opinar!") }
                    }
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listaResenas) { resena ->
                        ResenaCard(resena = resena)
                    }
                }
            }
        }
    }
}
