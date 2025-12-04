package com.example.levelup_gamer.ui.login

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.levelup_gamer.ui.screens.login.LoginScreen
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun muestra_pantalla_de_login() {
        composeRule.setContent {
            LoginScreen(
                onLoginSuccess = {}
            )
        }

        composeRule.onNodeWithText("Iniciar Sesión").assertExists()
    }
}
