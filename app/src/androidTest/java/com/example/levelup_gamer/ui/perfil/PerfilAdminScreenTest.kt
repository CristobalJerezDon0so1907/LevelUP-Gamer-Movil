package com.example.levelup_gamer.ui.screens.perfil

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test


class PerfilAdminScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun muestra_titulo_y_panel_de_gestion() {
        composeTestRule.setContent {
            PerfilAdminScreen(
                nombre = "Admin Test",
                onLogout = {},
                onGestionProductos = {},
                onGestionUsuarios = {},
                onGestionPedidos = {}
            )
        }

        // TopBar
        composeTestRule.onNodeWithText("Panel administrador").assertExists()

        // Nombre del admin
        composeTestRule.onNodeWithText("Admin Test").assertExists()

        // Título del panel
        composeTestRule.onNodeWithText("Panel de gestión").assertExists()

        // Botones del panel
        composeTestRule.onNodeWithText("Gestión de productos").assertExists()
        composeTestRule.onNodeWithText("Gestión de usuarios").assertExists()
        composeTestRule.onNodeWithText("Gestión de pedidos").assertExists()
    }


    //Detecta si el logout fue ejecutado
    @Test
    fun al_presionar_cerrar_sesion_se_llama_callback() {
        var logoutLlamado = false

        composeTestRule.setContent {
            PerfilAdminScreen(
                nombre = "Admin Test",
                onLogout = { logoutLlamado = true },
                onGestionProductos = {},
                onGestionUsuarios = {},
                onGestionPedidos = {}
            )
        }

        // Click en "Cerrar sesión"
        composeTestRule.onNodeWithText("Cerrar sesión").performClick()

        // Verificamos que el callback se ejecutó
        assertTrue(logoutLlamado)
    }
}
