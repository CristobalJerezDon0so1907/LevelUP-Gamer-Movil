package com.example.levelup_gamer.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.levelup_gamer.R
import com.example.levelup_gamer.model.EstadoPedido

class PedidoNotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "pedidos_channel"
        const val NOTIFICATION_ID_PEDIDO = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Estado de pedidos",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones del estado de tus pedidos"
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun mostrarNotificacionPedido(estado: EstadoPedido) {
        val titulo = "Pedido ${when (estado) {
            EstadoPedido.PENDIENTE -> "pendiente"
            EstadoPedido.EN_CAMINO -> "en camino"
            EstadoPedido.ENTREGADO -> "entregado"
        }}"

        val mensaje = when (estado) {
            EstadoPedido.PENDIENTE ->
                "Tu pedido se ha comprado exitosamente. Estado actual: Pendiente."
            EstadoPedido.EN_CAMINO ->
                "Tu pedido va en camino. ¡Prepárate para recibirlo!"
            EstadoPedido.ENTREGADO ->
                "Tu pedido ha sido entregado. ¡Gracias por tu compra!"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logofeo) // pon tu propio icono
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_PEDIDO, builder.build())
        }
    }
}