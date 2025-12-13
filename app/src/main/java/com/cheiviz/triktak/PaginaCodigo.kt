package com.cheiviz.triktak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.cheiviz.triktak.repos.UserRepository
import com.google.android.gms.ads.AdRequest
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.random.Random

class PaginaCodigo : BaseActivity() {

    private lateinit var crashlytics: FirebaseCrashlytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_codigo)

        // Cargar Banner
        val banner = findViewById<com.google.android.gms.ads.AdView>(R.id.adViewBanner)
        val request = AdRequest.Builder().build()
        banner.loadAd(request)

        //Inicializar Crashlytics
        crashlytics = FirebaseCrashlytics.getInstance()

        //Registrar que el usuario llego a esta pantalla
        crashlytics.log("Usuario llego a PaginaCodigo...")

        val editarCodigo = findViewById<EditText>(R.id.codigoPartida)
        val botonIngresar = findViewById<Button>(R.id.botonIngresar)
        var textoModo = findViewById<TextView>(R.id.textoModo)
        var textoIntrucciones = findViewById<TextView>(R.id.textoInstrucciones)
        val checkboxPrivada = findViewById<CheckBox>(R.id.checkboxPrivada)



        val modo = intent.getStringExtra("modo") ?: "crear" // "crear" o "unirse"

        if (modo == "crear") {
            val codigoGenerado = generarCodigoPartida()
            editarCodigo.setText(codigoGenerado)
            editarCodigo.isEnabled = false //No permite editarlo
            textoModo.text = "Codigo de tu partida"
            botonIngresar.text = "Crear partida"
            textoIntrucciones.text = "Comparte este código con tu amigo para que se pueda unir a tu partida..."
            checkboxPrivada.visibility = View.VISIBLE


        }else {
            // Modo unirse - permitir ingresar código
            textoModo.text = "Ingresa el código de partida"
            botonIngresar.text = "UNIRSE A PARTIDA"
            editarCodigo.hint = "Ejemplo: ABC123"
            textoIntrucciones.text = "Usa el código de partida que te compartierón para que puedas unirte a su partida..."
            checkboxPrivada.visibility = View.GONE
        }

        val database = FirebaseDatabase.getInstance()
        val gamesRef = database.getReference("games") // Referencia a /games

        botonIngresar.setOnClickListener {
            val codigo = editarCodigo.text.toString()

            // Logica de firebase
            if (codigo.isEmpty()) {
                crashlytics.recordException(Exception("Usuario intento crear/unirse sin codigo"))
                Toast.makeText(this, "Ingresa un código", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            try {
                if (modo == "crear") {
                    crearPartida(gamesRef, codigo, checkboxPrivada.isChecked)
                } else {
                    unirseAPartida(gamesRef, codigo)

                }
            } catch (e: Exception) {
                crashlytics.recordException(e)
                Toast.makeText(this, "Error inesperado: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun generarCodigoPartida(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

        return (1..6)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")


    }

    private fun crearPartida(gamesRef: DatabaseReference, codigo: String, privada: Boolean) {

        val idUsuario = intent.getStringExtra("id_usuario") ?: return
        val repo = UserRepository()

        repo.cargarDatosUsuario(idUsuario) { datos ->

            if (datos == null) {
                Toast.makeText(this, "No se pudieron obtener los datos del usuario", Toast.LENGTH_SHORT).show()
                return@cargarDatosUsuario
            }

            val nombre = datos.nombre
            val avatar = datos.avatar  // ej: "av3"

            val datosIniciales = hashMapOf<String, Any?>(
                "jugador1" to mapOf(
                    "uid" to idUsuario,
                    "nombre" to nombre,
                    "avatar" to avatar,
                    "ficha" to "x"
                ),
                "jugador2" to null,
                "turno" to "x",
                "tablero" to mapOf(
                    "0_0" to "", "0_1" to "", "0_2" to "",
                    "1_0" to "", "1_1" to "", "1_2" to "",
                    "2_0" to "", "2_1" to "", "2_2" to "",
                ),
                "partidaPrivada" to privada,
                "partidaFinalizada" to false
            )

            gamesRef.child(codigo).setValue(datosIniciales)
                .addOnSuccessListener {
                    val intent = Intent(this, PaginaJuego::class.java)
                    intent.putExtra("codigo_partida", codigo)
                    intent.putExtra("yoSoy", "x")
                    startActivity(intent)
                }
        }
    }



    private fun unirseAPartida(gamesRef: DatabaseReference, codigo: String) {

        val idUsuario = intent.getStringExtra("id_usuario") ?: return
        val repo = UserRepository()

        repo.cargarDatosUsuario(idUsuario) { datos ->

            if (datos == null) {
                Toast.makeText(this, "No pude cargar tu perfil", Toast.LENGTH_SHORT).show()
                return@cargarDatosUsuario
            }

            val nombre = datos.nombre
            val avatar = datos.avatar

            val partidaRef = gamesRef.child(codigo)

            partidaRef.child("jugador2").setValue(
                mapOf(
                    "uid" to idUsuario,
                    "nombre" to nombre,
                    "avatar" to avatar,
                    "ficha" to "o"
                )
            ).addOnSuccessListener {
                val intent = Intent(this, PaginaJuego::class.java)
                intent.putExtra("codigo_partida", codigo)
                intent.putExtra("yoSoy", "o")
                startActivity(intent)
            }
        }
    }


}