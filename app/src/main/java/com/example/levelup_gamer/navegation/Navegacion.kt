package com.example.levelup_gamer.navigation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.levelup_gamer.ui.screens.login.LoginScreen
import com.example.levelup_gamer.ui.screens.registro.RegistroScreen
import com.example.levelup_gamer.ui.screens.perfil.PerfilAdminScreen
import com.example.levelup_gamer.ui.screens.perfil.PerfilClienteScreen
import com.example.levelup_gamer.ui.screens.carrito.CarritoScreen
import com.example.levelup_gamer.ui.screens.pago.PagoConfirmacionScreen
import com.example.levelup_gamer.ui.screens.perfil.GestorPerfilScreen
import com.example.levelup_gamer.ui.screens.perfil.MisPedidosScreen
import com.example.levelup_gamer.ui.screens.resenas.RegistroResenasScreen
import com.example.levelup_gamer.viewmodel.CarritoViewModel
import com.example.levelup_gamer.viewmodel.LoginViewModel
import com.example.levelup_gamer.viewmodel.ResenasViewModel
import com.example.levelup_gamer.ui.screens.gestion.GestionProductosScreen
import com.example.levelup_gamer.ui.screens.gestion.GestionUsuarioScreen
import com.example.levelup_gamer.ui.screens.gestion.GestionPedidosScreen
import com.example.levelup_gamer.ui.screens.gestion.ProductoFormScreen
import com.example.levelup_gamer.ui.screens.gestion.UsuarioFormScreen
import com.example.levelup_gamer.viewmodel.GestionProductosViewModel
import com.example.levelup_gamer.viewmodel.GestionUsuariosViewModel


@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(context, "Permiso de cámara otorgado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                context,
                "Necesitas permiso de cámara para usar esta función",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ViewModels compartidos
    val gestionProductosViewModel: GestionProductosViewModel = viewModel()
    val gestionUsuariosViewModel: GestionUsuariosViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    val carritoViewModel: CarritoViewModel = viewModel()
    val resenasViewModel: ResenasViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(
                onRegisterClick = { navController.navigate("register") },
                onLoginSuccess = { user ->
                    when (user.rol) {
                        "admin" -> navController.navigate("perfil_admin")
                        else -> navController.navigate("perfil_cliente")
                    }
                },
                viewModel = loginViewModel
            )
        }

        composable("register") {
            RegistroScreen(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = { navController.popBackStack() }
            )
        }

        //PERFIL ADMIN
        composable("perfil_admin") {
            PerfilAdminScreen(
                loginViewModel = loginViewModel,
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                },
                onGestionProductos = { navController.navigate("gestionProductos") },
                onGestionUsuarios = { navController.navigate("gestionUsuarios") },
                onGestionPedidos = { navController.navigate("gestionPedidos") }
            )
        }


        composable("gestionPedidos") {
            GestionPedidosScreen(onBack = { navController.popBackStack() })
        }

        //PERFIL CLIENTE
        composable("perfil_cliente") {
            val userState by loginViewModel.user.collectAsState()
            val nombre = userState?.nombre ?: "Cliente"

            PerfilClienteScreen(
                nombre = nombre,
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                },
                onVerCarrito = { navController.navigate("carrito") },
                onAgregarResena = { navController.navigate("registroResenas") },
                onVerPedidos = { navController.navigate("misPedidos") },
                onEditarPerfil = { navController.navigate("gestor_perfil") },
                viewModel = carritoViewModel
            )
        }

        composable("carrito") {
            val userState by loginViewModel.user.collectAsState()
            val correoUsuario = userState?.correo ?: ""

            CarritoScreen(
                correoUsuario = correoUsuario,
                onVolverAlCatalogo = { navController.popBackStack() },
                onCompraExitosa = {
                    navController.navigate("pago") { popUpTo("carrito") { inclusive = true } }
                },
                viewModel = carritoViewModel
            )
        }

        composable("pago") {
            val userState by loginViewModel.user.collectAsState()
            val nombreUsuario = userState?.nombre ?: "Cliente"

            PagoConfirmacionScreen(
                nombreUsuario = nombreUsuario,
                onVolverAlPerfil = {
                    navController.navigate("perfil_cliente") {
                        popUpTo("login") { inclusive = false }
                    }
                }
            )
        }

        composable(route = "gestor_perfil") {
            GestorPerfilScreen(
                loginViewModel = loginViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("registroResenas") {
            val productos = carritoViewModel.productos.collectAsState().value

            RegistroResenasScreen(
                productos = productos,
                viewModel = resenasViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("misPedidos") {
            val userState by loginViewModel.user.collectAsState()
            val correoUsuario = userState?.correo ?: ""

            MisPedidosScreen(
                correoUsuario = correoUsuario,
                onBack = { navController.popBackStack() }
            )
        }

        composable("gestionProductos") {
            GestionProductosScreen(
                viewModel = gestionProductosViewModel,
                onBack = { navController.popBackStack() },
                onEditarProducto = { producto ->
                    gestionProductosViewModel.seleccionarProducto(producto)
                    navController.navigate("productoForm")
                }
            )
        }

        composable("productoForm") {
            val productoSeleccionado by gestionProductosViewModel.productoSeleccionado.collectAsState()

            ProductoFormScreen(
                viewModel = gestionProductosViewModel,
                producto = productoSeleccionado,
                onBack = { navController.popBackStack() }
            )
        }

        composable("gestionUsuarios") {
            GestionUsuarioScreen(
                viewModel = gestionUsuariosViewModel,
                onBack = { navController.popBackStack() },
                onEditarUsuario = { usuario ->
                    gestionUsuariosViewModel.seleccionarUsuario(usuario)
                    navController.navigate("usuarioForm")
                }
            )
        }

        composable("usuarioForm") {
            val usuarioSeleccionado by gestionUsuariosViewModel.usuarioSeleccionado.collectAsState()

            UsuarioFormScreen(
                viewModel = gestionUsuariosViewModel,
                usuario = usuarioSeleccionado,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
