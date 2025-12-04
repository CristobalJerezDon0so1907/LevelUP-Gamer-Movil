package com.example.levelup_gamer.viewmodel

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class LoginBusinessLogicTest : BehaviorSpec({

    given("credenciales de usuario") {

        `when`("correo y contraseña son correctos") {
            val correo = "test@test.com"
            val password = "123456"

            then("las credenciales son válidas") {
                (correo.isNotBlank() && password.length >= 6) shouldBe true
            }
        }

        `when`("la contraseña es muy corta") {
            val password = "123"

            then("las credenciales son inválidas") {
                (password.length >= 6) shouldBe false
            }
        }
    }
})
