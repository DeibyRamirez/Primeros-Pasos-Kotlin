package com.cheiviz.triktak

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.cheiviz.triktak.billing.BillingManager
import com.cheiviz.triktak.modelos.Paquete
import com.cheiviz.triktak.repos.UserRepository
import com.cheiviz.triktak.repos.adsrepo.AdsRepository
import com.cheiviz.triktak.repos.adsrepo.CoinsRepo
import com.google.firebase.database.FirebaseDatabase

class PaginaRecargar : BaseActivity() {

    private lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_recargar)

        val contenedorPaquetes = findViewById<LinearLayout>(R.id.contenedorPaquetes)
        val botonQuitarAnuncios = findViewById<Button>(R.id.btnQuitarAnuncios)

        val uidUsuario = UserRepository.miId

        if (uidUsuario.isEmpty()) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_LONG).show()
            finish()
            return
        }


        // Lista de paquetes
        val paquetes = listOf(
            Paquete(100, "coins_100"),
            Paquete(250, "coins_250"),
            Paquete(500, "coins_500"),
            Paquete(1200, "coins_1200")
        )

        billingManager = BillingManager(this) { productId ->
            when (productId) {
                "coins_100" -> CoinsRepo.addCoins(uidUsuario, 100)
                "coins_250" -> CoinsRepo.addCoins(uidUsuario, 250)
                "coins_500" -> CoinsRepo.addCoins(uidUsuario, 500)
                "coins_1200" -> CoinsRepo.addCoins(uidUsuario, 1200)

                "remove_ads" -> {
                    AdsRepository.setRemoveAds(uidUsuario, true)
                }
            }

            Toast.makeText(this, "Compra exitosa", Toast.LENGTH_SHORT).show()
        }


        billingManager.start()


        // Cargar paquetes
        cargarPaquetes(paquetes, contenedorPaquetes, uidUsuario)

        // Botón quitar anuncios
        botonQuitarAnuncios.setOnClickListener {
            billingManager.buy("remove_ads")
        }

        AdsRepository.observeRemoveAds(uidUsuario) { removeAds ->
            if (removeAds) {
                botonQuitarAnuncios.visibility = View.GONE
            }
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
                txtPrecio.text = "Comprar"


                // Configurar botón
                btnComprar.setOnClickListener {
                    billingManager.buy(paquete.productoId)
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

}