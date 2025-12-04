package com.example.levelup_gamer.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.levelup_gamer.R
import com.example.levelup_gamer.model.EstadoPedido
import com.example.levelup_gamer.notifications.PedidoNotificationHelper
import com.example.levelup_gamer.state.PedidoEstadoHolder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM", "From: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body

        val pedidoNotificationHelper = PedidoNotificationHelper(this)

        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM_DATA", "Data Payload: ${remoteMessage.data}")

            val tipoNotificacion = remoteMessage.data["tipoNotificacion"]

            when (tipoNotificacion) {

                // 🔹 Notificación con estado de pedido actualizado
                "ESTADO_PEDIDO" -> {
                    val estadoString = remoteMessage.data["estado"] ?: "PENDIENTE"
                    val idPedido = remoteMessage.data["idPedido"] ?: "N/A"

                    Log.d("FCM_PEDIDO", "idPedido=$idPedido, estado=$estadoString")

                    val estado = when (estadoString.uppercase()) {
                        "PENDIENTE" -> EstadoPedido.PENDIENTE
                        "EN_CAMINO", "EN CAMINO" -> EstadoPedido.EN_CAMINO
                        "ENTREGADO" -> EstadoPedido.ENTREGADO
                        else -> EstadoPedido.PENDIENTE
                    }

                    PedidoEstadoHolder.actualizarEstado(estado)

                    //Mostrar notificación con estado del pedido
                    pedidoNotificationHelper.mostrarNotificacionPedido(estado)
                }

                else -> {
                    // Notificación genérica
                    showNotification(title, body)
                }
            }
        } else {
            showNotification(title, body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token FCM: $token")
    }


    private fun showNotification(title: String?, body: String?) {
        val channelId = "default_channel_id"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones generales",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logofeo)
            .setContentTitle(title ?: "LevelUP Gamer")
            .setContentText(body ?: "Tienes una nueva notificación")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notificationBuilder.build()
        )
    }
}
