package com.cheiviz.triktak

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.cheiviz.triktak.modelos.Usuario
import com.cheiviz.triktak.repos.UserRepository
import com.cheiviz.triktak.repos.adsrepo.CoinsRepo
import com.google.firebase.auth.FirebaseAuth
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

        uidActual = intent.getStringExtra("uid_actual") ?: ""


        // Evitar crash si faltan IDs
        if (uidUsuario.isEmpty() || uidActual.isEmpty()) {
            Toast.makeText(this, "Error al abrir el chat.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }



        // Inicializar vistas
        contenedorChat = findViewById(R.id.contenedorChat)
        editMensaje = findViewById(R.id.edtMensaje)
        botonEnviar = findViewById(R.id.botonEnviarMensaje)

        val nombreUsuario = findViewById<TextView>(R.id.txtNombreUsuario)
        val btnPerfil = findViewById<ImageButton>(R.id.btnPerfil)
        val botonCrearPartida = findViewById<Button>(R.id.botonCrearPartida)



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
            CoinsRepo.subtractCoins( uidUsuario,50) { ok, msg ->
                if (ok) {
                    enviarMensaje()
                } else {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        botonCrearPartida.setOnClickListener {
            val intent = Intent(this, PaginaCodigo::class.java)
            intent.putExtra("modo", "crear")
            intent.putExtra("id_usuario", uidActual)
            startActivity(intent)
        }
    }

    /***
     * Genera un ID único para un chat entre dos personas
     */
    private fun generarChatId(uid1: String, uid2: String): String {
        return if (uid1.compareTo(uid2) < 0) "${uid1}_${uid2}" else "${uid2}_${uid1}"
    }

    /***
     * Enviar mensaje al chat correspondiente
     */
    private fun enviarMensaje() {
        val texto = editMensaje.text.toString().trim()
        if (texto.isEmpty()) return

        val refChat = FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(chatId)

        val mensaje = hashMapOf(
            "id_usuario" to uidActual,
            "mensaje" to texto,
            "timestamp" to System.currentTimeMillis()
        )

        // Guardar el mensaje
        refChat.push().setValue(mensaje)

        // Registrar último mensaje PARA EL ADAPTER
        refChat.child("ultimoMensaje").setValue(texto)

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
