package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.modelos.Usuario
import com.example.myapplication.repos.UserRepository
import com.example.myapplication.repos.adsrepo.AdsManager
import com.example.myapplication.repos.adsrepo.CoinsRepo
import com.example.myapplication.repos.adsrepo.InterstitialAds
import com.example.myapplication.repos.adsrepo.RewardeAds
import com.google.android.gms.ads.AdRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PaginaBienvenida : BaseActivity() {

    private var datosUsuario: Usuario? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_bienvenida)

        val botonpersonalizar = findViewById<ImageButton>(R.id.botonpersonalizar)
        val botonlocal = findViewById<Button>(R.id.botonlocal)
        val botononline = findViewById<Button>(R.id.botononline)
        var modo: String? = null
        val idUsuario = intent.getStringExtra("id_usuario") ?: return
        val botonEnviar = findViewById<ImageButton>(R.id.btnEnviar)

        // Inicializar AdMod
        AdsManager.init(this)

        // Cargar Interstitial
        InterstitialAds.loadAd(this)

        // Cargar Rewarde
        RewardeAds.loadAd(this)

        // Cargar Banner
        val banner = findViewById<com.google.android.gms.ads.AdView>(R.id.adViewBanner)
        val request = AdRequest.Builder().build()
        banner.loadAd(request)


        // Uso el repo para cargar los datos del usuario con UserRepository.kt
        val repo = UserRepository()

        repo.cargarDatosUsuario(idUsuario) { datos ->
            datosUsuario = datos
        }

        cargarChatGlobal()

        botonlocal.setOnClickListener {
            val intent = Intent(this, PaginaPrincipal::class.java)
            modo = "local"
            intent.putExtra("modo_juego", modo)
            intent.putExtra("id_usuario", idUsuario)
            startActivity(intent)
        }

        botononline.setOnClickListener {
            val intent = Intent(this, PaginaPrincipal::class.java)
            modo = "online"
            intent.putExtra("modo_juego", modo)
            intent.putExtra("id_usuario", idUsuario)
            startActivity(intent)
        }

        botonpersonalizar.setOnClickListener {
            val intent = Intent(this, Personalizar::class.java)
            startActivity(intent)
        }

        botonEnviar.setOnClickListener {
            CoinsRepo.subtractCoins(idUsuario,25) { ok, msg ->
                if (ok) {
                    enviarMensajeGlobal()
                } else {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() // Muestra el mensaje de toast
                }
            }


        }

    }

    // Funcion para enviar mensajes al chat global
    private fun enviarMensajeGlobal() {
        val edtMensaje = findViewById<EditText>(R.id.edtMensaje)
        val mensajeTexto = edtMensaje.text.toString().trim()

        if (mensajeTexto.isEmpty()) return

        val usuario = datosUsuario ?: return // ya lo tienes cargado

        val mensaje = hashMapOf(
            "id_usuario" to usuario.id,
            "nombre" to usuario.nombre,
            "mensaje" to mensajeTexto,
            "timestamp" to System.currentTimeMillis()
        )
        // Creo la collección de mensajes (chat_global) en la base de datos
        val chatRef = FirebaseDatabase.getInstance().getReference("chat_global")

        chatRef.push().setValue(mensaje)

        edtMensaje.setText("") // limpiar texto
    }

    // Funcion para cargar mensajes del chat global
    private fun cargarChatGlobal() {

        val chatLayout = findViewById<LinearLayout>(R.id.contenedorChat)
        val chatRef = FirebaseDatabase.getInstance().getReference("chat_global")

        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                chatLayout.removeAllViews() // 🔥 IMPORTANTE

                for (mensajeSnap in snapshot.children) {

                    val nombre = mensajeSnap.child("nombre").value.toString()
                    val mensaje = mensajeSnap.child("mensaje").value.toString()

                    val item = layoutInflater.inflate(R.layout.item_mensaje, chatLayout, false)

                    val txtNombre = item.findViewById<TextView>(R.id.txtNombreMensaje)
                    val txtMensaje = item.findViewById<TextView>(R.id.txtContenidoMensaje)

                    txtNombre.text = nombre
                    txtMensaje.text = mensaje

                    chatLayout.addView(item)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}