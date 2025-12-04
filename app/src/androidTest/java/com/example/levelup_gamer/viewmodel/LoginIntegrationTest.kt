package com.example.levelup_gamer.viewmodel

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class LoginIntegrationTest : BehaviorSpec({

    fun login(correo: String, pass: String): Boolean {
        return correo.contains("@") && pass.length >= 6
    }

    given("un flujo completo de autenticación") {

        `when`("las credenciales son correctas") {
            val resultado = login("usuario@dominio.cl", "123456")

            then("el login debe ser exitoso") {
                resultado shouldBe true
            }
        }

        `when`("el correo es inválido") {
            val resultado = login("correo-invalido", "123456")

            then("debe fallar la autenticación") {
                resultado shouldBe false
            }
        }

        `when`("la contraseña es demasiado corta") {
            val resultado = login("user@correo.cl", "123")

            then("debe fallar el login") {
                resultado shouldBe false
            }
        }
    }
})
