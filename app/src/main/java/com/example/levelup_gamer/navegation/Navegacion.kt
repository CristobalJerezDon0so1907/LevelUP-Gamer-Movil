package com.example.levelup_gamer.navegacion

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.levelup_gamer.ui.screens.login.LoginScreen
import com.example.levelup_gamer.ui.screens.registro.RegistroScreen
import com.example.levelup_gamer.ui.screens.catalogo.CatalogoScreen
import com.example.levelup_gamer.ui.screens.detalle.DetalleProductoScreen
import com.example.levelup_gamer.ui.screens.carrito.CarritoScreen
import com.example.levelup_gamer.ui.screens.perfil.PerfilClienteScreen
import com.example.levelup_gamer.ui.screens.perfil.PerfilAdminScreen
import com.example.levelup_gamer.viewmodel.CarritoViewModel

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val carritoViewModel: CarritoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // 🔐 LOGIN
        composable("login") {
            LoginScreen(
                onLoginExitoso = { rol ->
                    if (rol == "admin") {
                        navController.navigate("perfilAdmin")
                    } else {
                        navController.navigate("perfilCliente")
                    }
                },
                onIrRegistro = { navController.navigate("registro") }
            )
        }

        // 📝 REGISTRO
        composable("registro") {
            RegistroScreen(
                onRegistroExitoso = { navController.navigate("login") },
                onVolver = { navController.popBackStack() }
            )
        }

        // 👤 PERFIL CLIENTE
        composable("perfilCliente") {
            PerfilClienteScreen(
                onVerCatalogo = { navController.navigate("catalogo") },
                onVerCarrito = { navController.navigate("carrito") },
                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 🧑‍💼 PERFIL ADMIN
        composable("perfilAdmin") {
            PerfilAdminScreen(
                onVerReportes = { /* futuro */ },
                onGestionUsuarios = { /* futuro */ },
                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 🛍 CATÁLOGO
        composable("catalogo") {
            CatalogoScreen(
                onVerDetalleProducto = { productoId ->
                    navController.navigate("detalle/$productoId")
                },
                onVerCarrito = { navController.navigate("carrito") },
                viewModel = carritoViewModel
            )
        }

        // 🔎 DETALLE DE PRODUCTO
        composable(
            route = "detalle/{productoId}",
            arguments = listOf(navArgument("productoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId") ?: ""
            DetalleProductoScreen(
                productoId = productoId,
                onVolver = { navController.popBackStack() },
                onAgregarCarrito = { producto ->
                    carritoViewModel.agregarAlCarrito(producto)
                    navController.navigate("carrito")
                }
            )
        }

        // 🛒 CARRITO
        composable("carrito") {
            CarritoScreen(
                onVolverAlCatalogo = { navController.popBackStack() },
                onConfirmarPago = { navController.navigate("perfilCliente") },
                viewModel = carritoViewModel
            )
        }
    }
}
