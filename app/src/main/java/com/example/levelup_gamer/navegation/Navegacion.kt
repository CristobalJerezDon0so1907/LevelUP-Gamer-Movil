package com.example.levelup_gamer.navegation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.levelup_gamer.ui.screens.carrito.CarritoScreen
import com.example.levelup_gamer.ui.screens.login.LoginScreen
import com.example.levelup_gamer.ui.screens.pago.PagoConfirmacionScreen
import com.example.levelup_gamer.ui.screens.perfil.PerfilAdminScreen
import com.example.levelup_gamer.ui.screens.perfil.PerfilClienteScreen
import com.example.levelup_gamer.ui.screens.registro.RegistroScreen
import com.example.levelup_gamer.ui.screens.resenas.AgregarResenaScreen
import com.example.levelup_gamer.ui.screens.reseña.ReseñaScreen
import com.example.levelup_gamer.viewmodel.CarritoViewModel
import com.example.levelup_gamer.viewmodel.ReseñaViewModel

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val carritoViewModel: CarritoViewModel = viewModel()
    val resenaViewModel: ReseñaViewModel = viewModel()

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
                // Agregar navegación a reseñas para admin
                onVerResenas = {
                    navController.navigate("resenas")
                }
            )
        }

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
                onVerCarrito = {
                    navController.navigate("carrito")
                },
                // Agregar navegación a reseñas para cliente
                onVerResenas = {
                    navController.navigate("resenas")
                },
                onAgregarResena = {
                    navController.navigate("agregar_resena")
                },
                viewModel = carritoViewModel
            )
        }

        composable("carrito") {
            CarritoScreen(
                onVolverAlCatalogo = {
                    navController.popBackStack()
                },
                onConfirmarPago = {
                    navController.navigate("pago") {
                        popUpTo("perfil_cliente") { inclusive = false }
                    }
                },
                viewModel = carritoViewModel
            )
        }

        composable("pago") { backStackEntry ->
            PagoConfirmacionScreen(
                nombreUsuario = "Cliente",
                onVolverAlPerfil = {
                    navController.navigate("perfil_cliente/Cliente") {
                        popUpTo("login") { inclusive = false }
                    }
                }
            )
        }

        // Agregar pantallas de reseñas
        composable("resenas") {
            ReseñaScreen(
                onVolver = {
                    navController.popBackStack()
                },
                onAgregarResena = {
                    navController.navigate("agregar_resena")
                },
                viewModel = resenaViewModel
            )
        }

        composable("agregar_resena") {
            AgregarResenaScreen(
                onVolver = {
                    navController.popBackStack()
                },
                onResenaAgregada = {
                    navController.popBackStack() // Volver a la lista de reseñas
                },
                viewModel = resenaViewModel
            )
        }
    }
}