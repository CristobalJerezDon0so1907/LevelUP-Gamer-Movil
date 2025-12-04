package com.example.levelup_gamer.viewmodel

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class LoginViewModelTest : BehaviorSpec({

    given("login inicial") {
        val correo = ""
        val password = ""

        `when`("no hay datos") {
            then("no es válido") {
                (correo.isNotBlank() && password.length >= 6) shouldBe false
            }
        }
    }
})
