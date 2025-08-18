package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
}