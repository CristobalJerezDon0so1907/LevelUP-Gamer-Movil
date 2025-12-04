package com.example.levelup_gamer.ui.screens.carrito

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.levelup_gamer.viewmodel.CarritoViewModel
import org.junit.Rule
import org.junit.Test

class CarritoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun cuando_el_carrito_esta_vacio_debe_mostrar_mensaje_vacio() {
        val viewModel = CarritoViewModel() // carrito vacío por defecto

        composeTestRule.setContent {
            CarritoScreen(
                correoUsuario = "test@correo.com",
                onVolverAlCatalogo = {},
                onCompraExitosa = {},
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithText("El carrito está vacío").assertExists()
    }

    @Test
    fun cuando_se_muestra_carrito_debe_mostrar_titulo() {
        val viewModel = CarritoViewModel()

        composeTestRule.setContent {
            CarritoScreen(
                correoUsuario = "test@correo.com",
                onVolverAlCatalogo = {},
                onCompraExitosa = {},
                viewModel = viewModel
            )
        }

        composeTestRule.onNodeWithText("Mi Carrito").assertExists()
    }
}
