package com.cheiviz.triktak

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

        var idUsuario = intent.getStringExtra("id_usuario") ?: return


        botonAceptar.setOnClickListener {
            val intent = Intent(this, PaginaJuego::class.java)
            intent.putExtra("modo_juego", "desafio")
            intent.putExtra("id_usuario", idUsuario)
            startActivity(intent)
        }

        
    }
}