package com.example.levelup_gamer.navegacion

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.levelup_gamer.model.User
import com.example.levelup_gamer.ui.screens.admin.AdminDashboardScreen
import com.example.levelup_gamer.ui.screens.carrito.CarritoScreen
import com.example.levelup_gamer.ui.screens.detalle.DetalleProductoScreen
import com.example.levelup_gamer.ui.screens.login.LoginScreen
import com.example.levelup_gamer.ui.screens.main.MainScreen
import com.example.levelup_gamer.ui.screens.perfil.PerfilClienteScreen
import com.example.levelup_gamer.ui.screens.registro.RegistroScreen
import com.example.levelup_gamer.ui.screens.resena.AgregarResenaScreen
import com.example.levelup_gamer.ui.screens.resena.ResenaScreen
import com.example.levelup_gamer.ui.screens.scan.ScanScreen
import com.example.levelup_gamer.viewmodel.CarritoViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val carritoViewModel: CarritoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Login.route
    ) {
        composable(AppScreens.Login.route) {
            LoginScreen(
                onLoginExitoso = { user ->
                    if (user.role == "admin") {
                        navigateToAdminDashboard(navController, user)
                    } else {
                        navigateToMainScreen(navController, user)
                    }
                },
                onIrRegistro = { navController.navigate(AppScreens.Registro.route) }
            )
        }

        composable(AppScreens.Registro.route) {
            RegistroScreen(
                onRegistroExitoso = { user ->
                    navigateToMainScreen(navController, user)
                }
            )
        }

        composable(
            route = "${AppScreens.Main.route}/{userName}/{userRole}",
            arguments = listOf(
                navArgument("userName") { type = NavType.StringType },
                navArgument("userRole") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName")?.decode()
            val userRole = backStackEntry.arguments?.getString("userRole")?.decode()

            MainScreen(
                userName = userName,
                userRole = userRole,
                onCerrarSesion = { logout(navController) },
                onVerDetalleProducto = { productoId -> navController.navigate("${AppScreens.Detalle.route}/$productoId") },
                onVerCarrito = { navController.navigate(AppScreens.Carrito.route) },
                onVerResenas = { navController.navigate(AppScreens.Resenas.route) },
                onVerPerfil = { navController.navigate(AppScreens.Perfil.route) },
                onAgregarOpinion = { navController.navigate(AppScreens.Opinion.route) },
                onEscanearQr = { navController.navigate(AppScreens.Escanear.route) },
                carritoViewModel = carritoViewModel
            )
        }
        
        composable(
            route = "${AppScreens.AdminDashboard.route}/{adminName}",
            arguments = listOf(navArgument("adminName") { type = NavType.StringType })
        ) { backStackEntry ->
            val adminName = backStackEntry.arguments?.getString("adminName")?.decode()
            AdminDashboardScreen(
                adminName = adminName,
                onCerrarSesion = { logout(navController) },
                onEscanearQr = { navController.navigate(AppScreens.Escanear.route) }
            )
        }

        composable(
            route = "${AppScreens.Detalle.route}/{productoId}",
            arguments = listOf(navArgument("productoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId") ?: ""
            DetalleProductoScreen(
                productoId = productoId,
                onVolver = { navController.popBackStack() },
                onAgregarCarrito = { producto ->
                    carritoViewModel.agregarAlCarrito(producto)
                    navController.navigate(AppScreens.Carrito.route)
                }
            )
        }

        composable(AppScreens.Carrito.route) {
            CarritoScreen(
                onVolverAlCatalogo = { navController.popBackStack() },
                onConfirmarPago = {
                    carritoViewModel.vaciarCarrito()
                    navController.popBackStack()
                },
                viewModel = carritoViewModel
            )
        }

        composable(AppScreens.Resenas.route) {
            ResenaScreen(
                onVolver = { navController.popBackStack() },
                onAgregarResena = { navController.navigate(AppScreens.Opinion.route) }
            )
        }

        composable(AppScreens.Opinion.route) {
            AgregarResenaScreen(
                onVolver = { navController.popBackStack() },
                onOpinionEnviada = { navController.popBackStack() }
            )
        }

        composable(AppScreens.Escanear.route) {
            ScanScreen(onVolver = { navController.popBackStack() })
        }

        composable(AppScreens.Perfil.route) {
            PerfilClienteScreen(
                onVolverAtras = { navController.popBackStack() }
            )
        }
    }
}

private fun navigateToMainScreen(navController: NavHostController, user: User) {
    val userName = user.username.takeIf { it.isNotBlank() } ?: user.email.split("@").firstOrNull() ?: "Cliente"
    val userRole = user.role
    navController.navigate("${AppScreens.Main.route}/${userName.encode()}/${userRole.encode()}") {
        popUpTo(AppScreens.Login.route) { inclusive = true }
    }
}

private fun navigateToAdminDashboard(navController: NavHostController, user: User) {
    val adminName = user.username.takeIf { it.isNotBlank() } ?: "Admin"
    navController.navigate("${AppScreens.AdminDashboard.route}/${adminName.encode()}") {
        popUpTo(AppScreens.Login.route) { inclusive = true }
    }
}

private fun logout(navController: NavHostController) {
    navController.navigate(AppScreens.Login.route) {
        popUpTo(0) { inclusive = true }
    }
}

sealed class AppScreens(val route: String) {
    object Login : AppScreens("login")
    object Registro : AppScreens("registro")
    object Main : AppScreens("main")
    object AdminDashboard : AppScreens("admin_dashboard")
    object Detalle : AppScreens("detalle")
    object Carrito : AppScreens("carrito")
    object Resenas : AppScreens("resenas")
    object Opinion : AppScreens("opinion")
    object Escanear : AppScreens("escanear")
    object Perfil : AppScreens("perfil")
}

fun String.encode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
fun String.decode(): String = URLDecoder.decode(this, StandardCharsets.UTF_8.toString())