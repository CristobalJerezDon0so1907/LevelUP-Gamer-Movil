package com.example.levelup_gamer.state

import com.example.levelup_gamer.model.EstadoPedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object PedidoEstadoHolder {

    private val _estadoPedido = MutableStateFlow<EstadoPedido?>(null)
    val estadoPedido: StateFlow<EstadoPedido?> = _estadoPedido

    fun actualizarEstado(nuevoEstado: EstadoPedido?) {
        _estadoPedido.value = nuevoEstado
    }

    fun limpiarEstado() {
        _estadoPedido.value = null
    }
}
