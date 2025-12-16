package com.example.levelup_gamer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.levelup_gamer.model.EstadoPedido
import com.example.levelup_gamer.notifications.PedidoNotificationHelper

class PerfilViewModel(
    context: Context
) : ViewModel() {

    private val notificationHelper = PedidoNotificationHelper(context)

    fun notificarEstadoPedido(estado: EstadoPedido) {
        notificationHelper.mostrarNotificacionPedido(estado)
    }
}
