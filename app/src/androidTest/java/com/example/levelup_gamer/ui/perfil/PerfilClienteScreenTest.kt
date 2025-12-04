package com.example.levelup_gamer.ui.screens.perfil

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.levelup_gamer.viewmodel.CarritoViewModel
import org.junit.Rule
import org.junit.Test

class PerfilClienteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun muestra_nombre_y_rol_cliente() {
        val carritoViewModel = CarritoViewModel()

        composeTestRule.setContent {
            PerfilClienteScreen(
                nombre = "Daniel",
                onLogout = {},
                onVerCarrito = {},
                onAgregarResena = {},
                onEditarPerfil = {},
                viewModel = carritoViewModel
            )
        }

        // Verifica que se muestre el saludo con el nombre
        composeTestRule.onNodeWithText("Bienvenido Daniel").assertExists()

        // Verifica que se muestre el rol
        composeTestRule.onNodeWithText("Rol: Cliente").assertExists()
    }

    @Test
    fun muestra_botones_principales() {
        val carritoViewModel = CarritoViewModel()

        composeTestRule.setContent {
            PerfilClienteScreen(
                nombre = "Daniel",
                onLogout = {},
                onVerCarrito = {},
                onAgregarResena = {},
                onEditarPerfil = {},
                viewModel = carritoViewModel
            )
        }

        // Botón Ver Carrito
        composeTestRule.onNodeWithText("Ver Carrito").assertExists()

        // Botón Agregar reseña
        composeTestRule.onNodeWithText("Agregar reseña").assertExists()

        // Botón Cerrar Sesión
        composeTestRule.onNodeWithText("Cerrar Sesión").assertExists()
    }
}
