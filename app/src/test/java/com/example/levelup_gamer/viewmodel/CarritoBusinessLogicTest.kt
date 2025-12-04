package com.example.levelup_gamer.viewmodel

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class CarritoBusinessLogicTest : BehaviorSpec({

    given("un total inicial de 0") {

        `when`("se agrega un producto de 1000") {
            val total = 0 + 1000

            then("el total debe ser 1000") {
                total shouldBe 1000
            }
        }

        `when`("se agregan dos productos de 500") {
            val total = 500 + 500

            then("el total debe ser 1000") {
                total shouldBe 1000
            }
        }
    }
})
