package com.example.levelup_gamer.model

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class ItemCarritoTest : BehaviorSpec({

    // Test para el modelo ItemCarrito
    given("un ItemCarrito") {
        val producto = Producto(
            id = "1",
            nombre = "Producto Test",
            precio = 10.0,
            imagenUrl = "imagen.jpg",
            stock = 5
        )

        `when`("se crea con cantidad por defecto") {
            val itemCarrito = ItemCarrito(producto = producto)

            then("debe tener cantidad 1") {
                itemCarrito.cantidad shouldBe 1
                itemCarrito.producto shouldBe producto
            }
        }

        `when`("se crea con cantidad específica") {
            val itemCarrito = ItemCarrito(producto = producto, cantidad = 3)

            then("debe tener la cantidad especificada") {
                itemCarrito.cantidad shouldBe 3
            }
        }

        `when`("se modifica la cantidad") {
            val itemCarrito = ItemCarrito(producto = producto)
            itemCarrito.cantidad = 5

            then("la cantidad debe actualizarse") {
                itemCarrito.cantidad shouldBe 5
            }
        }
    }
})