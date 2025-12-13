package com.cheiviz.triktak.repos.notificacionesRepo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cheiviz.triktak.R
import com.cheiviz.triktak.UserPrefs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        serviceScope.launch {
            val tipo = message.data["tipo"] ?: ""
            val prefs = UserPrefs(this@MyFirebaseMessagingService)

            // Llamar funciones suspend correctamente
            val permitirGlobal = prefs.leerNotifGlobal()
            val permitirPrivado = prefs.leerNotifPrivado()

            // Validar tipo de notificación
            if (tipo == "chat_global" && !permitirGlobal) return@launch
            if (tipo == "chat_privado" && !permitirPrivado) return@launch

            // Mostrar notificación (esto sí puede ser fuera de suspends)
            mostrarNotificacion(
                title = message.data["titulo"] ?: "Mensaje",
                body = message.data["mensaje"] ?: ""
            )
        }
    }

    private fun mostrarNotificacion(title: String, body: String) {
        val channelId = "chat_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones de Chat",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.icon)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), notification)
    }
}

