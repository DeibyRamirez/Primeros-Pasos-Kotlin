package com.cheiviz.triktak

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.cheiviz.triktak.repos.adsrepo.CoinsRepo


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