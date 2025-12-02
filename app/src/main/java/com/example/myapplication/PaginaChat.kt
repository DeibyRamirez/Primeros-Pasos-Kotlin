package com.example.myapplication

import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.modelos.Usuario
import com.example.myapplication.repos.UserRepository
import com.google.firebase.database.*

class PaginaChat : BaseActivity() {

    private var datosUsuario: Usuario? = null
    private lateinit var chatId: String
    private lateinit var uidActual: String

    private lateinit var contenedorChat: LinearLayout
    private lateinit var editMensaje: EditText
    private lateinit var botonEnviar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_chat)

        val uidUsuario = intent.getStringExtra("uid_usuario") ?: ""
        uidActual = intent.getStringExtra("uid_usuario") ?: ""

        // Inicializar vistas
        contenedorChat = findViewById(R.id.contenedorChat)
        editMensaje = findViewById(R.id.edtMensaje)
        botonEnviar = findViewById(R.id.botonEnviarMensaje)

        val nombreUsuario = findViewById<TextView>(R.id.txtNombreUsuario)
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)

        val repo = UserRepository()

        // Cargar datos usuario receptor
        repo.cargarDatosUsuario(uidUsuario) { datos ->
            if (datos != null) {
                datosUsuario = datos
                nombreUsuario.text = datos.nombre
                val idImg = resources.getIdentifier(datos.avatar, "drawable", packageName)
                btnPerfil.setImageResource(idImg)
            }
        }

        // Generar ID único para el chat (ordenado alfabéticamente)
        chatId = generarChatId(uidActual, uidUsuario)

        // Cargar chat en tiempo real
        cargarChat()

        // Enviar mensaje
        botonEnviar.setOnClickListener {
            enviarMensaje()
        }
    }

    /***
     * Genera un ID único para un chat entre dos personas
     */
    private fun generarChatId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "${uid1}_${uid2}" else "${uid2}_${uid1}"
    }

    /***
     * Enviar mensaje al chat correspondiente
     */
    private fun enviarMensaje() {
        val texto = editMensaje.text.toString().trim()
        if (texto.isEmpty()) return

        val ref = FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(chatId)

        val mensaje = hashMapOf(
            "id_usuario" to uidActual,
            "mensaje" to texto,
            "timestamp" to System.currentTimeMillis()
        )

        ref.push().setValue(mensaje)
        editMensaje.setText("")
    }

    /***
     * Escuchar mensajes en tiempo real y mostrarlos
     */
    private fun cargarChat() {
        val ref = FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(chatId)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                contenedorChat.removeAllViews() // limpiar para recargar

                for (msgSnap in snapshot.children) {
                    val idUsuario = msgSnap.child("id_usuario").value.toString()
                    val texto = msgSnap.child("mensaje").value.toString()

                    agregarMensaje(texto, idUsuario == uidActual)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /***
     * Crear visualmente un mensaje (estilo WhatsApp)
     */
    private fun agregarMensaje(texto: String, esMio: Boolean) {
        val textoView = TextView(this)
        textoView.text = texto
        textoView.textSize = 18f
        textoView.setPadding(20, 10, 20, 10)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        if (esMio) {
            textoView.setBackgroundResource(R.drawable.bubble_mio)
            params.gravity = Gravity.END
        } else {
            textoView.setBackgroundResource(R.drawable.bubble_otro)
            params.gravity = Gravity.START
        }

        params.setMargins(0, 10, 0, 10)
        textoView.layoutParams = params

        contenedorChat.addView(textoView)
    }
}
