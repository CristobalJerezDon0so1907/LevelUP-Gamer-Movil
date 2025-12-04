package com.example.levelup_gamer.ui.carrito

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.levelup_gamer.ui.screens.carrito.CarritoScreen
import com.example.levelup_gamer.viewmodel.CarritoViewModel
import org.junit.Rule
import org.junit.Test

class CarritoScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val fakeViewModel = CarritoViewModel()   // Se usa Android real

    @Test
    fun cuando_el_carrito_esta_vacio_debe_mostrar_mensaje() {
        composeRule.setContent {
            CarritoScreen(
                viewModel = fakeViewModel,
                correoUsuario = "test@test.com",
                onVolverAlCatalogo = {},
                onCompraExitosa = {}
            )
        }

        composeRule.onNodeWithText("El carrito está vacío").assertExists()
    }
}
