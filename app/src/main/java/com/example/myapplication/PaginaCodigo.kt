package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

    private fun crearPartida(gamesRef: DatabaseReference, codigo: String, partidaPrivada: Boolean) {
        // Registrar evento personalizado

        crashlytics.log("Creando partida con codigo: $codigo")
        crashlytics.setCustomKey("action", "crear partida")
        crashlytics.setCustomKey("codigo_partida", codigo)

        // Crear una Partida --------------------------------
        val partidaRef = gamesRef.child(codigo)

        // Estrutura inicial de la partida
        val datosIniciales = hashMapOf<String, Any?>(
            "jugador1" to "x",
            "jugador2" to null,
            "turno" to "x",
            "tablero" to mapOf(
                "0_0" to "", "0_1" to "", "0_2" to "",
                "1_0" to "", "1_1" to "", "1_2" to "",
                "2_0" to "", "2_1" to "", "2_2" to "",
            ),
            "partidaPrivada" to partidaPrivada,
            "partidaFinalizada" to false,


        )

        // Guardamos esos datos en Firebase
        partidaRef.setValue(datosIniciales).addOnSuccessListener {
            crashlytics.log("Se creo partida exitosamente")

            // Nos movemos a la pantalla del Juego
                val intent = Intent(this, PaginaJuego::class.java)
            intent.putExtra("codigo_partida", codigo)
            intent.putExtra("yoSoy", "x")
            intent.putExtra("modo_juego", "online")
            startActivity(intent)
        }.addOnFailureListener { error ->
            //Registrar el error especifico
            crashlytics.recordException(error)
            crashlytics.setCustomKey("error_type", "crear_partida_failed")
            Toast.makeText(this, "Error al crear partida: ${error.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun unirseAPartida(gamesRef: DatabaseReference, codigo: String) {
        // Registrar evento personalizado
        crashlytics.log("Unirse a partida con codigo: $codigo")
        crashlytics.setCustomKey("action", "unirse a partida")
        crashlytics.setCustomKey("codigo_partida", codigo)

        // Unirse a una partida ----------------------------------
        val partidaRef = gamesRef.child(codigo)
        partidaRef.get().addOnSuccessListener { snapshot ->

            // Verificamos que la partida exista
            if (snapshot.exists()) {
                // Si jugador2 todavia esta vacío entonces nos podemos unir
                if (snapshot.child("jugador2").value == null) {
                    partidaRef.child("jugador2").setValue("o").addOnSuccessListener {
                        crashlytics.log("Se uni a partida exitosamente")


                        val intent = Intent(this, PaginaJuego::class.java)
                        intent.putExtra("codigo_partida", codigo)
                        intent.putExtra("yoSoy", "o")
                        startActivity(intent)
                    }.addOnFailureListener { error ->
                        crashlytics.recordException(error)
                        crashlytics.setCustomKey("error_type", "unirse_partida_failed")
                        Toast.makeText(
                            this,
                            "Error al unirse a la partida: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    crashlytics.log("Intento de unirse a partida llena")
                    Toast.makeText(this, "Partida llena", Toast.LENGTH_SHORT).show()
                }

            } else {
                crashlytics.log("Intendo de unirse a partida inexistente")
                Toast.makeText(this, "Partida no encontrada", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { error ->
            crashlytics.recordException(error)
            crashlytics.setCustomKey("error_type", "unirse_partida_failed")
            Toast.makeText(
                this,
                "Error al unirse a la partida: ${error.message}",
                Toast.LENGTH_SHORT
            ).show()
        }


    }
}