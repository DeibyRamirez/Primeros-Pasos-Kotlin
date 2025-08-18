package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PaginaBienvenida : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_bienvenida)

        val botonlocal = findViewById<Button>(R.id.botonlocal)
        val botononline = findViewById<Button>(R.id.botononline)



        botonlocal.setOnClickListener {
            val intent = Intent(this, PaginaJuego::class.java)
            intent.putExtra("modo_juego", "local")
            startActivity(intent)
        }

        botononline.setOnClickListener {
            val intent = Intent(this, PaginaPrincipal::class.java)
            startActivity(intent)
        }


    }
}