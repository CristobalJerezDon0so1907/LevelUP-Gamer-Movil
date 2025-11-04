package com.example.levelup_gamer.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.levelup_gamer.R

const val CHANNEL_ID = "payment_channel"
const val CHANNEL_NAME = "Notificaciones de Pago"
const val NOTIFICATION_ID = 1

/**
 * Crea el canal de notificaciones necesario para Android 8.0 (API 26) y superior.
 * Si el canal ya existe, no se realiza ninguna acción.
 */
fun createNotificationChannel(context: Context) {
    // Las notificaciones solo necesitan un canal en versiones de Android Oreo o superiores.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Canal para notificaciones de pagos y confirmaciones."
        }

        // Registra el canal en el sistema.
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

/**
 * Construye y muestra una notificación simple para confirmar un pago exitoso.
 */
fun showPaymentSuccessNotification(context: Context) {
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de tener este drawable
        .setContentTitle("¡Pago Exitoso!")
        .setContentText("Gracias por tu compra en Level UP Gamer.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true) // La notificación se cierra al tocarla.

    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    // Muestra la notificación.
    notificationManager.notify(NOTIFICATION_ID, builder.build())
}
