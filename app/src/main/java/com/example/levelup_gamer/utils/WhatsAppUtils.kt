package com.example.levelup_gamer.utils


import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object WhatsAppUtils {

    fun shareReviewViaWhatsApp(
        context: Context,
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        reviewText: String,
        phoneNumber: String? = null
    ) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                setPackage("com.whatsapp")
                putExtra(Intent.EXTRA_TEXT, reviewText)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "WhatsApp no está instalado",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    "Error al compartir: ${e.message}",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    fun openWhatsAppChat(context: Context, phoneNumber: String, message: String = "") {
        try {
            val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}