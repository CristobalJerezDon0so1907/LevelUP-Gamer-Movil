package com.example.levelup_gamer.viewmodel

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class CarritoViewModelTest : BehaviorSpec({

    given("un ViewModel de carrito") {
        val viewModel = CarritoViewModel()

        `when`("se consulta el total") {
            val total = viewModel.obtenerTotal()

            then("el total debe ser 0 al iniciar") {
                total shouldBe 0.0
            }
        }
    }
})
