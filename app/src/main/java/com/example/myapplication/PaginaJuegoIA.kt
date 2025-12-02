package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge

class PaginaJuegoIA : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_juego_ia)

        val botonAceptar = findViewById<Button>(R.id.botonaceptar)

        botonAceptar.setOnClickListener {
            val intent = Intent(this, PaginaJuego::class.java)
            intent.putExtra("modo_juego", "desafio")
            startActivity(intent)
        }

        
    }
}