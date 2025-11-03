package com.example.levelup_gamer.navegation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.model.Usuario
import com.example.levelup_gamer.repository.ProductoRepository
import com.example.levelup_gamer.repository.ResenaRepository
import com.example.levelup_gamer.ui.screens.carrito.CarritoScreen
import com.example.levelup_gamer.ui.screens.detalle.DetalleProductoScreen
import com.example.levelup_gamer.ui.screens.login.LoginScreen
import com.example.levelup_gamer.ui.screens.pago.PagoConfirmacionScreen
import com.example.levelup_gamer.ui.screens.perfil.PerfilAdminScreen
import com.example.levelup_gamer.ui.screens.perfil.PerfilClienteScreen
import com.example.levelup_gamer.ui.screens.qr.QRScannerScreen
import com.example.levelup_gamer.ui.screens.registro.RegistroScreen
import com.example.levelup_gamer.ui.screens.resena.AgregarResenaScreen
import com.example.levelup_gamer.ui.screens.resena.ResenaScreen
import com.example.levelup_gamer.viewmodel.CarritoViewModel
import com.example.levelup_gamer.viewmodel.CarritoViewModelFactory
import com.example.levelup_gamer.viewmodel.ResenaViewModel
import com.example.levelup_gamer.viewmodel.ResenaViewModelFactory

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    // Creación correcta de los ViewModels con sus factorías
    val resenaRepository = remember { ResenaRepository() }
    val productoRepository = remember { ProductoRepository() }

    val resenaViewModel: ResenaViewModel = viewModel(factory = ResenaViewModelFactory(resenaRepository))
    val carritoViewModel: CarritoViewModel = viewModel(factory = CarritoViewModelFactory(productoRepository))

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onRegisterClick = { navController.navigate("register") },
                onLoginSuccess = { user: Usuario ->
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
                onRegisterSuccess = { navController.popBackStack() }
            )
        }

        composable(
            "perfil_admin/{nombre}",
            arguments = listOf(navArgument("nombre") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: "Administrador"
            PerfilAdminScreen(
                nombre = nombre,
                onLogout = { navController.navigate("login") { popUpTo(0) } },
                onVerResenas = { navController.navigate("resenas") },
            )
        }

        composable(
            "perfil_cliente/{nombre}",
            arguments = listOf(navArgument("nombre") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: "Cliente"
            PerfilClienteScreen(
                nombre = nombre,
                onLogout = { navController.navigate("login") { popUpTo(0) } },
                onVerCarrito = { navController.navigate("carrito") },
                onVerResenas = { navController.navigate("resenas") },
                onAgregarResena = { navController.navigate("agregar_resena") },
                onEscanearProducto = { navController.navigate("qr_scan") },
                viewModel = carritoViewModel
            )
        }

        composable("carrito") {
            CarritoScreen(
                onVolverAlCatalogo = { navController.popBackStack() },
                onConfirmarPago = { navController.navigate("pago") },
                viewModel = carritoViewModel
            )
        }

        composable("pago") {
            PagoConfirmacionScreen(
                nombreUsuario = "Cliente", // TODO: Pasar nombre real
                onVolverAlPerfil = { navController.navigate("perfil_cliente/Cliente") { popUpTo("login") } }
            )
        }

        composable("resenas") {
            ResenaScreen(
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

        composable("qr_scan") {
            QRScannerScreen(navController)
        }

        composable(
            "detalle_producto/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DetalleProductoScreen(
                productoId = id,
                onVolver = { navController.popBackStack() },
                onAgregarCarrito = { producto: Producto ->
                    carritoViewModel.agregarAlCarrito(producto)
                    navController.popBackStack()
                }
            )
        }
    }
}
