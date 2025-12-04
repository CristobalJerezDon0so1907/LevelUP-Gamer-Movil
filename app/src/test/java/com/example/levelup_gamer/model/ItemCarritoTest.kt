package com.example.levelup_gamer.model

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class ItemCarritoTest : BehaviorSpec({

    given("un ItemCarrito") {

        val producto = Producto(
            id = "1",
            nombre = "Producto Test",
            precio = 10.0,
            descripcion = "Desc",
            imagenUrl = "img.jpg",
            stock = 5
        )

        `when`("se crea con cantidad por defecto") {
            val item = ItemCarrito(producto)

            then("la cantidad debe ser 1") {
                item.cantidad shouldBe 1
            }
        }

        `when`("se crea con cantidad personalizada") {
            val item = ItemCarrito(producto, 3)

            then("la cantidad debe ser 3") {
                item.cantidad shouldBe 3
            }
        }
    }
})
