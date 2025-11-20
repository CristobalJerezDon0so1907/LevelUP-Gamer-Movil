package com.example.levelup_gamer.ui.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.navigation.NavController
import com.example.levelup_gamer.model.Producto



@Composable
fun PerfilClienteScreen(
    nombre: String,
    onLogout: () -> Unit
) {
    val productos = listOf(
        Producto(
            nombre = "Mouse Gamer RGB",
            precio = "$19.990",
            descripcion = "Mouse gamer con 7 botones programables y luces RGB.",
            imagenUrl = "https://images.pexels.com/photos/845434/pexels-photo-845434.jpeg"
        ),
        Producto(
            nombre = "Teclado Mecánico",
            precio = "$39.990",
            descripcion = "Teclado mecánico con switches rojos, ideal para gaming.",
            imagenUrl = "https://images.pexels.com/photos/2115257/pexels-photo-2115257.jpeg"
        ),
        Producto(
            nombre = "Monitor 27\" 144Hz",
            precio = "$189.990",
            descripcion = "Monitor Full HD de 27 pulgadas con tasa de refresco de 144Hz.",
            imagenUrl = "https://images.pexels.com/photos/1779487/pexels-photo-1779487.jpeg"
        ),
        Producto(
            nombre = "PC Gamer RTX",
            precio = "$899.990",
            descripcion = "PC gamer con RTX, 16GB RAM y SSD de 1TB.",
            imagenUrl = "https://images.pexels.com/photos/3165335/pexels-photo-3165335.jpeg"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🔹 Título
        Text(
            text = "Bienvenido, $nombre",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Título del catálogo
        Text(
            text = "Catálogo de productos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Lista de productos
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(productos) { producto ->
                ProductoCard(producto)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Botón de cerrar sesión
        Button(onClick = onLogout) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
fun ProductoCard(producto: Producto) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = producto.imagenUrl,
                contentDescription = producto.nombre,
                modifier = Modifier.size(90.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = producto.precio,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = producto.descripcion,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
