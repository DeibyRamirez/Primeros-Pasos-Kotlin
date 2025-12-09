package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.modelos.Usuario
import com.example.myapplication.repos.adsrepo.CoinsRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class PaginaDesafioGanado : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_desafio_ganado)

        val botonContinuar = findViewById<Button>(R.id.buttonContinuar)

        val idUsuario = intent.getStringExtra("id_usuario") ?: return

        botonContinuar.setOnClickListener {
            CoinsRepo.addCoins(idUsuario,300) {
                Toast.makeText(this, "Recompensa recibida", Toast.LENGTH_SHORT).show()
                finish()
            }



        }


    }

}