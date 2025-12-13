package com.cheiviz.triktak

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.cheiviz.triktak.modelos.Usuario
import com.cheiviz.triktak.repos.UserRepository
import com.google.android.gms.ads.AdRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PaginaPrincipal : BaseActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var partidaRef: DatabaseReference
    private var datosUsuario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_principal)

        // Cargar Banner
        val banner = findViewById<com.google.android.gms.ads.AdView>(R.id.adViewBanner)
        val request = AdRequest.Builder().build()
        banner.loadAd(request)

        val botonUnirse = findViewById<Button>(R.id.botonUnirse)
        val botonCrear = findViewById<Button>(R.id.botonCrear)
        val botonPerfil = findViewById<ImageButton>(R.id.btnPerfil)
        val botonChatear = findViewById<Button>(R.id.botonChatear)
        val textoPartidas = findViewById<TextView>(R.id.txtPartidas)
        val partidasLayout = findViewById<LinearLayout>(R.id.contenedorPartidas)



        var modo = intent.getStringExtra("modo_juego")
        val idUsuario = intent.getStringExtra("id_usuario") ?: return
        val txtNombreUsuario = findViewById<TextView>(R.id.txtNombreUsuario)


        val repo = UserRepository()
        // Funcion para cargar los datos del usurio (nombre e imagen de perfil)
        repo.cargarDatosUsuario(idUsuario) { datos ->
            if (datos != null) {
                datosUsuario = datos
                txtNombreUsuario.text = datos.nombre

                val idImg = resources.getIdentifier(datos.avatar, "drawable", packageName)
                botonPerfil.setImageResource(idImg)
            }
        }

        cargarPartidasOnline()


        if (idUsuario != null) {
            repo.cargarDatosUsuario(
                idUsuario
            ) { datos ->
                datosUsuario = datos
            }
        }


        if (modo == "online") {

            // Faltaban estos cambios de texto
            botonUnirse.text = getString(R.string.Unirse)
            botonCrear.text = getString(R.string.Crear)

            botonCrear.setOnClickListener {
                val intent = Intent(this, PaginaCodigo::class.java)
                intent.putExtra("modo", "crear")
                intent.putExtra("id_usuario", idUsuario)
                startActivity(intent)
            }

            botonUnirse.setOnClickListener {
                val intent = Intent(this, PaginaCodigo::class.java)
                intent.putExtra("modo", "unirse")
                intent.putExtra("id_usuario", idUsuario)
                startActivity(intent)
            }

            botonChatear.visibility = View.VISIBLE

        }
        else {

            // Cambiar textos para modo LOCAL
            botonUnirse.text = getString(R.string.local_AI)   // VS IA
            botonCrear.text = getString(R.string.local_2p)    // 2 jugadores

            botonCrear.setOnClickListener {
                val intent = Intent(this, PaginaJuego::class.java)
                intent.putExtra("modo_juego", "local")
                intent.putExtra("id_usuario", idUsuario)
                startActivity(intent)
            }

            botonUnirse.setOnClickListener {
                val intent = Intent(this, PaginaJuegoIA::class.java)
                intent.putExtra("id_usuario", idUsuario)
                startActivity(intent)
            }

            botonChatear.visibility = View.GONE
            textoPartidas.visibility = View.GONE
            partidasLayout.visibility = View.GONE

        }
        // Este boton da ingreso a la pantalla de Perfil del usuario.
        botonPerfil.setOnClickListener {
            datosUsuario?.let { usuario ->
                val intent = Intent(this, Perfil::class.java)
                intent.putExtra("uid_usuario", usuario.id)
                startActivity(intent)
            }

        }

        botonChatear.setOnClickListener {
            val intent = Intent(this, PaginaUsuarios::class.java)
            intent.putExtra("uid_usuario", idUsuario)
            startActivity(intent)
        }

    }


    private fun cargarPartidasOnline() {

        val partidasLayout = findViewById<LinearLayout>(R.id.contenedorPartidas)
        partidasLayout.removeAllViews()

        val db = FirebaseDatabase.getInstance().getReference("games")


        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                partidasLayout.removeAllViews()

                for (partidaSnap in snapshot.children) {

                    val codigo = partidaSnap.key ?: continue

                    val jugador1 = partidaSnap.child("jugador1").value?.toString()
                    val jugador2 = partidaSnap.child("jugador2").value?.toString()

                    // Solo mostrar partidas donde SOLO hay un jugador
                    val soloUnJugador = jugador1 != null && jugador1.isNotEmpty() &&
                            (jugador2 == null || jugador2.isEmpty())

                    // correcta lectura
                    val partidaPrivada = partidaSnap.child("partidaPrivada").value as? Boolean ?: false
                    val partidaFinalizada = partidaSnap.child("partidaFinalizada").value as? Boolean ?: false

                    // Solo mostrar si:
                    // 1. NO es privada
                    // 2. NO está finalizada
                    // 3. Solo un jugador
                    if (partidaPrivada || partidaFinalizada || !soloUnJugador) continue

                    val item = LayoutInflater.from(this@PaginaPrincipal)
                        .inflate(R.layout.item_partida, partidasLayout, false)

                    val txtCodigo = item.findViewById<TextView>(R.id.txtCodigoPartida)
                    val btnUnirse = item.findViewById<Button>(R.id.btnUnirsePartida)

                    txtCodigo.text = "Código: $codigo"

                    btnUnirse.setOnClickListener {
                        unirseAPartida(db, codigo)
                    }

                    partidasLayout.addView(item)
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })
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

            // Antes de unirte, refrescar JUGADOR1 desde Firebase
            partidaRef.child("jugador1").get().addOnSuccessListener { snap1 ->
                if (snap1.exists()) {
                    // OK, jugador1 válido
                }

                // Unirse como jugador2
                partidaRef.child("jugador2").setValue(
                    mapOf(
                        "uid" to idUsuario,
                        "nombre" to nombre,
                        "avatar" to avatar,
                        "ficha" to "o"
                    )
                ).addOnSuccessListener {

                    // Ir al juego
                    val intent = Intent(this, PaginaJuego::class.java)
                    intent.putExtra("codigo_partida", codigo)
                    intent.putExtra("yoSoy", "o")
                    startActivity(intent)
                }
            }
        }
    }

}