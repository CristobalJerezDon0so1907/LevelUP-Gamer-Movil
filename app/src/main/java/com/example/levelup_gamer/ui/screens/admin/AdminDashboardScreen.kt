package com.example.levelup_gamer.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminDashboardScreen(
    adminName: String?,
    onCerrarSesion: () -> Unit,
    onEscanearQr: () -> Unit
) {
    MaterialTheme(colorScheme = lightColorScheme()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Panel de Administrador",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Bienvenido, ${adminName ?: "Administrador"}",
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Rol: Administrador",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onEscanearQr,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.width(220.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear QR", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Escanear QR", color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onCerrarSesion,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier.width(220.dp)
                ) {
                    Text("Cerrar Sesión", color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}
