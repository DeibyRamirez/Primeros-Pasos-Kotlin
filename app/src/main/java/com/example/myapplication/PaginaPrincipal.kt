package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PaginaPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_principal)

        val botonUnirse = findViewById<Button>(R.id.botonUnirse)
        val botonCrear = findViewById<Button>(R.id.botonCrear)
        val botonPerfil = findViewById<ImageButton>(R.id.btnPerfil)
        var modo = intent.getStringExtra("modo_juego")

        if (modo == "online") {

            // Faltaban estos cambios de texto
            botonUnirse.text = getString(R.string.Unirse)
            botonCrear.text = getString(R.string.Crear)

            botonCrear.setOnClickListener {
                val intent = Intent(this, PaginaCodigo::class.java)
                intent.putExtra("modo", "crear")
                startActivity(intent)
            }

            botonUnirse.setOnClickListener {
                val intent = Intent(this, PaginaCodigo::class.java)
                intent.putExtra("modo", "unirse")
                startActivity(intent)
            }

        }
        else {

            // Cambiar textos para modo LOCAL
            botonUnirse.text = getString(R.string.local_AI)   // VS IA
            botonCrear.text = getString(R.string.local_2p)    // 2 jugadores

            botonCrear.setOnClickListener {
                val intent = Intent(this, PaginaJuego::class.java)
                intent.putExtra("modo_juego", "local")
                startActivity(intent)
            }

            botonUnirse.setOnClickListener {
                val intent = Intent(this, pagina_juego_ia::class.java)
                startActivity(intent)
            }
        }
        // Este boton da ingreso a la pantalla de Perfil del usuario.
        botonPerfil.setOnClickListener {
            val intent = Intent(this, Perfil::class.java)
            startActivity(intent)
        }


    }
}