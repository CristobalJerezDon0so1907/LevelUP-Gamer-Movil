package com.example.levelup_gamer.services

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object FCMClient {

    private const val FCM_URL = "https://fcm.googleapis.com/fcm/send"

    // ⚠️ USA TU SERVER KEY DE FIREBASE CLOUD MESSAGING
    private const val SERVER_KEY = "AAAAxxxxxxx:XXXXXXXXXXXXX"

    private val client = OkHttpClient()

    fun enviarNotificacion(token: String, titulo: String, mensaje: String) {
        val json = JSONObject()
        val notification = JSONObject()

        notification.put("title", titulo)
        notification.put("body", mensaje)

        json.put("to", token)
        json.put("notification", notification)

        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(FCM_URL)
            .post(body)
            .addHeader("Authorization", "key=$SERVER_KEY")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute()
    }
}
