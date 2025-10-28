package com.example.levelup_gamer.navegation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.levelup_gamer.repository.ResenaRepository
import com.example.levelup_gamer.ui.screens.carrito.CarritoScreen
import com.example.levelup_gamer.ui.screens.login.LoginScreen
import com.example.levelup_gamer.ui.screens.pago.PagoConfirmacionScreen
import com.example.levelup_gamer.ui.screens.perfil.PerfilAdminScreen
import com.example.levelup_gamer.ui.screens.perfil.PerfilClienteScreen
import com.example.levelup_gamer.ui.screens.registro.RegistroScreen
import com.example.levelup_gamer.ui.screens.resenas.AgregarResenaScreen
import com.example.levelup_gamer.ui.screens.reseña.ReseñaScreen
import com.example.levelup_gamer.viewmodel.CarritoViewModel
import com.example.levelup_gamer.viewmodel.ResenaViewModel
import com.example.levelup_gamer.viewmodel.ResenaViewModelFactory
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val carritoViewModel: CarritoViewModel = viewModel()
    val repository = ResenaRepository()
    val factory = ResenaViewModelFactory(repository)
    val resenaViewModel: ResenaViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate("register")
                },
                onLoginSuccess = { user ->
                    when (user.rol) {
                        "admin" -> navController.navigate("perfil_admin/${user.nombre}")
                        else -> navController.navigate("perfil_cliente/${user.nombre}")
                    }
                }
            )
        }

        composable("register") {
            RegistroScreen(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // === PERFIL ADMIN ===
        composable(
            "perfil_admin/{nombre}",
            arguments = listOf(navArgument("nombre") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: "Administrador"
            PerfilAdminScreen(
                nombre = nombre,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onVerResenas = { navController.navigate("resenas") },
                onGestionUsuarios = { navController.navigate("gestion_usuarios") },
                onVerReportes = { navController.navigate("reportes") },
                onConfiguraciones = { navController.navigate("configuracion") },
                onSoporte = { navController.navigate("soporte") }
            )
        }

        // === PERFIL CLIENTE ===
        composable(
            "perfil_cliente/{nombre}",
            arguments = listOf(navArgument("nombre") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: "Cliente"
            PerfilClienteScreen(
                nombre = nombre,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onVerCarrito = { navController.navigate("carrito") },
                onVerResenas = { navController.navigate("resenas") },
                onAgregarResena = { navController.navigate("agregar_resena") },
                viewModel = carritoViewModel
            )
        }

        // === CARRITO ===
        composable("carrito") {
            CarritoScreen(
                onVolverAlCatalogo = { navController.popBackStack() },
                onConfirmarPago = {
                    navController.navigate("pago") {
                        popUpTo("perfil_cliente") { inclusive = false }
                    }
                },
                viewModel = carritoViewModel
            )
        }

        // === PAGO ===
        composable("pago") {
            PagoConfirmacionScreen(
                nombreUsuario = "Cliente",
                onVolverAlPerfil = {
                    navController.navigate("perfil_cliente/Cliente") {
                        popUpTo("login") { inclusive = false }
                    }
                }
            )
        }

        // === RESEÑAS ===
        composable("resenas") {
            ReseñaScreen(
                onVolver = { navController.popBackStack() },
                onAgregarResena = { navController.navigate("agregar_resena") },
                viewModel = resenaViewModel
            )
        }

        composable("agregar_resena") {
            AgregarResenaScreen(
                onVolver = { navController.popBackStack() },
                onResenaAgregada = { navController.popBackStack() },
                viewModel = resenaViewModel
            )
        }

        // === GESTIÓN DE USUARIOS ===
        composable("gestion_usuarios") {
            // Pantalla de gestión de usuarios (ejemplo temporal)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Pantalla de gestión de usuarios")
            }
        }

        // === REPORTES ===
        composable("reportes") {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Pantalla de reportes")
            }
        }

        // === CONFIGURACIÓN ===
        composable("configuracion") {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Pantalla de configuración")
            }
        }

        // === SOPORTE ===
        composable("soporte") {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Pantalla de soporte técnico")
            }
        }
    }
}
