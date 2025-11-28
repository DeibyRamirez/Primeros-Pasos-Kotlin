package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PaginaBienvenida : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_bienvenida)

        val botonpersonalizar = findViewById<ImageButton>(R.id.botonpersonalizar)
        val botonlocal = findViewById<Button>(R.id.botonlocal)
        val botononline = findViewById<Button>(R.id.botononline)
        var modo: String? = null




        botonlocal.setOnClickListener {
            val intent = Intent(this, PaginaPrincipal::class.java)
            intent.putExtra("modo_juego", modo)
            modo = "local"
            startActivity(intent)
        }

        botononline.setOnClickListener {
            val intent = Intent(this, PaginaPrincipal::class.java)
            modo = "online"
            intent.putExtra("modo_juego", modo)
            startActivity(intent)
        }

        botonpersonalizar.setOnClickListener {
            val intent = Intent(this, Personalizar::class.java)
            startActivity(intent)
        }

    }
}