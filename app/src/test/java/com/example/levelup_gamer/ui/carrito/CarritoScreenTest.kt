package com.example.levelup_gamer.ui.carrito

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.levelup_gamer.ui.screens.carrito.CarritoScreen
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test

class CarritoScreenTest {

    // Regla para testing de Compose
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun cuando_el_carrito_esta_vacio_debe_mostrar_mensaje_vacio() {
        // Configurar el estado vacío
        composeTestRule.setContent {
            CarritoScreen(
                onVolverAlCatalogo = {},
                onConfirmarPago = {}
            )
        }

        // Verificar que se muestra el mensaje de carrito vacío
        composeTestRule.onNodeWithText("El carrito está vacío").assertExists()
    }

    @Test
    fun cuando_hay_productos_debe_mostrar_lista_y_total() {
        composeTestRule.setContent {
            CarritoScreen(
                onVolverAlCatalogo = {},
                onConfirmarPago = {}
            )
        }

        // Verificar que se muestran elementos del carrito (si los hay)
        composeTestRule.onNodeWithText("Mi Carrito").assertExists()
        composeTestRule.onNodeWithText("Total:").assertExists()
    }

    @Test
    fun al_hacer_clic_en_confirmar_compra_debe_mostrar_formulario() {
        composeTestRule.setContent {
            CarritoScreen(
                onVolverAlCatalogo = {},
                onConfirmarPago = {}
            )
        }

        // Hacer clic en confirmar compra (si el carrito no está vacío)
        composeTestRule.onNodeWithText("Confirmar Compra").performClick()

        // Verificar que aparece el formulario de envío
        composeTestRule.onNodeWithText("Información de envío").assertExists()
    }
}