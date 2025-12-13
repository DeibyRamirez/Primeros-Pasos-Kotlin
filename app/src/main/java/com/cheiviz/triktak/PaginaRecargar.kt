package com.cheiviz.triktak

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.cheiviz.triktak.modelos.Paquete
import com.cheiviz.triktak.repos.adsrepo.CoinsRepo

class PaginaRecargar : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_recargar)

        val contenedorPaquetes = findViewById<LinearLayout>(R.id.contenedorPaquetes)
        val botonQuitarAnuncios = findViewById<Button>(R.id.btnQuitarAnuncios)

        val uidUsuario  = intent.getStringExtra("id_usuario").toString()

        if (uidUsuario == null) {
            Toast.makeText(this, "Error: No se encontró el ID de usuario", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Lista de paquetes
        val paquetes = listOf(
            Paquete(100, 2000),
            Paquete(250, 4000),
            Paquete(500, 6000),
            Paquete(1200, 10000)
        )

        // Cargar paquetes
        cargarPaquetes(paquetes, contenedorPaquetes, uidUsuario)

        // Botón quitar anuncios
        botonQuitarAnuncios.setOnClickListener {
            Toast.makeText(this, "Función de quitar anuncios pendiente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarPaquetes(
        paquetes: List<Paquete>,
        contenedor: LinearLayout,
        idUsuario: String
    ) {
        for (paquete in paquetes) {
            try {
                // Inflar la vista del paquete
                val vista = LayoutInflater.from(this).inflate(
                    R.layout.item_paquete,
                    contenedor,
                    false
                )

                // Buscar los elementos
                val txtMonedas = vista.findViewById<TextView>(R.id.txtMonedasPaquete)
                val txtPrecio = vista.findViewById<TextView>(R.id.txtPrecioPaquete)
                val btnComprar = vista.findViewById<Button>(R.id.botonComprarPaquete)

                // Configurar textos
                txtMonedas.text = "${paquete.monedas} Monedas"
                txtPrecio.text = "$${paquete.precio}"

                // Configurar botón
                btnComprar.setOnClickListener {
                    comprarPaquete(paquete, idUsuario)
                }

                // Agregar al contenedor
                contenedor.addView(vista)

            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Error al cargar paquete: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Verificación: ¿Se agregaron las vistas?
        if (contenedor.childCount == 0) {
            Toast.makeText(
                this,
                "ERROR: No se cargaron los paquetes",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this,
                "Se cargaron ${contenedor.childCount} paquetes",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun comprarPaquete(paquete: Paquete, idUsuario: String) {
        CoinsRepo.addCoins(idUsuario, paquete.monedas) { exitoso ->
            if (exitoso) {
                Toast.makeText(
                    this,
                    "¡Compraste ${paquete.monedas} monedas por $${paquete.precio}!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Error al agregar monedas. Intenta de nuevo.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}