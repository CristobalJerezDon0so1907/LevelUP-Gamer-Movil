package com.example.levelup_gamer.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.ui.screens.catalogo.ProductoCard
import com.example.levelup_gamer.ui.theme.ElectricBlue
import com.example.levelup_gamer.ui.theme.NeonGreen
import com.example.levelup_gamer.ui.theme.NeonPurple
import com.example.levelup_gamer.viewmodel.CarritoViewModel
import com.example.levelup_gamer.viewmodel.CatalogoUiState
import com.example.levelup_gamer.viewmodel.CatalogoViewModel

@Composable
fun MainScreen(
    userName: String?,
    userRole: String?,
    onCerrarSesion: () -> Unit,
    onVerDetalleProducto: (String) -> Unit,
    onVerCarrito: () -> Unit,
    onVerPerfil: () -> Unit,
    onVerResenas: () -> Unit,
    onAgregarOpinion: () -> Unit,
    onEscanearQr: () -> Unit,
    catalogoViewModel: CatalogoViewModel = viewModel(),
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val catalogoState by catalogoViewModel.uiState.collectAsState()

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
        ) {
            UserHeader(
                userName = userName ?: "Usuario",
                userRole = userRole ?: "Cliente",
                onCerrarSesion = onCerrarSesion,
                onVerPerfil = onVerPerfil
            )
            ActionButtons(
                onVerCarrito = onVerCarrito,
                onVerResenas = onVerResenas,
                onAgregarOpinion = onAgregarOpinion,
                onEscanearQr = onEscanearQr
            )
            ProductCatalog(catalogoState, onVerDetalleProducto, carritoViewModel)
        }
    }
}

@Composable
fun UserHeader(userName: String, userRole: String, onCerrarSesion: () -> Unit, onVerPerfil: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Bienvenido, $userName", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text("Rol: $userRole", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row {
            TextButton(onClick = onVerPerfil) {
                Text("Mi Perfil")
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onCerrarSesion) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun ActionButtons(
    onVerCarrito: () -> Unit,
    onVerResenas: () -> Unit,
    onAgregarOpinion: () -> Unit,
    onEscanearQr: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ActionButton(text = "Carrito", icon = Icons.Default.ShoppingCart, color = NeonGreen, onClick = onVerCarrito, modifier = Modifier.weight(1f))
        ActionButton(text = "Reseñas", icon = Icons.Default.Star, color = ElectricBlue, onClick = onVerResenas, modifier = Modifier.weight(1f))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ActionButton(text = "Opinión", icon = Icons.Default.AddComment, color = NeonPurple, onClick = onAgregarOpinion, modifier = Modifier.weight(1f))
        ActionButton(text = "Escanear", icon = Icons.Default.QrCodeScanner, color = MaterialTheme.colorScheme.secondary, onClick = onEscanearQr, modifier = Modifier.weight(1f))
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = text, tint = MaterialTheme.colorScheme.onPrimary)
            Text(text, color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.sp)
        }
    }
}

@Composable
fun ProductCatalog(
    state: CatalogoUiState,
    onVerDetalleProducto: (String) -> Unit,
    carritoViewModel: CarritoViewModel
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Catálogo de Productos", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(vertical = 8.dp))

        when (state) {
            is CatalogoUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CatalogoUiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.productos) { producto ->
                        ProductoCard(
                            producto = producto,
                            onProductoClick = { onVerDetalleProducto(producto.id) },
                            onAgregarCarrito = { carritoViewModel.agregarAlCarrito(producto) }
                        )
                    }
                }
            }
            is CatalogoUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message)
                }
            }
        }
    }
}