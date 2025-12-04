package com.example.levelup_gamer.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UsuarioTest : FunSpec({

    test("el usuario se crea correctamente") {

        val usuario = Usuario(
            id = "1",
            nombre = "Cris",
            correo = "cris@test.cl",
            rol = "cliente"
        )

        usuario.nombre shouldBe "Cris"
        usuario.rol shouldBe "cliente"
    }
})
