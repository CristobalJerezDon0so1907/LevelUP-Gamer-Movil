package com.example.levelup_gamer.viewmodel

import com.example.levelup_gamer.model.ItemCarrito
import com.example.levelup_gamer.model.Producto
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class CarritoIntegrationTest : BehaviorSpec({

    given("un flujo completo de carrito") {

        val p1 = Producto("1", "Laptop", 1000.0, "desc", "img", 5)
        val p2 = Producto("2", "Mouse", 100.0, "desc", "img", 5)

        val carrito = mutableListOf<ItemCarrito>()

        `when`("se agregan productos") {
            carrito.add(ItemCarrito(p1, 1))
            carrito.add(ItemCarrito(p2, 1))

            then("el carrito tiene 2 productos") {
                carrito.size shouldBe 2
            }

            then("el total es correcto") {
                val total = carrito.sumOf { it.producto.precio * it.cantidad }
                total shouldBe 1100.0
            }
        }

        `when`("se vacía el carrito") {
            carrito.clear()

            then("el carrito queda vacío") {
                carrito.isEmpty() shouldBe true
            }
        }
    }
})
