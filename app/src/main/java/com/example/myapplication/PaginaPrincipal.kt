package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.modelos.Usuario
import com.google.firebase.auth.AuthResult
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

        val botonUnirse = findViewById<Button>(R.id.botonUnirse)
        val botonCrear = findViewById<Button>(R.id.botonCrear)
        val botonPerfil = findViewById<ImageButton>(R.id.btnPerfil)


        var modo = intent.getStringExtra("modo_juego")
        val idUsuario = intent.getStringExtra("id_usuario")
        val txtNombreUsuario = findViewById<TextView>(R.id.txtNombreUsuario)

        cargarPartidasOnline()


        if (idUsuario != null) {
            cargarDatosUsuario(
                idUsuario,
                txtNombreUsuario,
                botonPerfil
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
                val intent = Intent(this, pagina_juego_ia::class.java)
                intent.putExtra("id_usuario", idUsuario)
                startActivity(intent)
            }
        }
        // Este boton da ingreso a la pantalla de Perfil del usuario.
        botonPerfil.setOnClickListener {
            datosUsuario?.let { usuario ->
                val intent = Intent(this, Perfil::class.java)
                intent.putExtra("id", usuario.id)
                intent.putExtra("nombre", usuario.nombre)
                intent.putExtra("correo", usuario.correo)
                intent.putExtra("contraseña", usuario.contraseña)
                intent.putExtra("avatar", usuario.avatar)
                startActivity(intent)
            }

        }


    }
    // Funcion para cargar los datos del usurio (nombre e imagen de perfil)
    private fun cargarDatosUsuario(
        id: String,
        txtNombre: TextView,
        botonPerfil: ImageButton,
        callback: (Usuario) -> Unit
    ) {
        val dbref = FirebaseDatabase.getInstance().getReference("usuarios").child(id)

        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val nombre = snapshot.child("nombre").value.toString()
                    val correo = snapshot.child("correo").value.toString()
                    val contraseña = snapshot.child("contraseña").value.toString()
                    val avatar = snapshot.child("avatar").value.toString()

                    val idImg = resources.getIdentifier(avatar, "drawable", packageName)
                    botonPerfil.setImageResource(idImg)

                    txtNombre.text = nombre   // 👈 mostrar nombre en pantalla

                    // Enviar datos al callback
                    // El callback es una función que se ejecuta cuando se cargan los datos.
                    // sirve para que el callback pueda acceder a los datos del usuario.
                    // funciona como una variable que se ejecuta cuando se cargan los datos.
                    callback(
                        Usuario(
                            id = id,
                            nombre = nombre,
                            correo = correo,
                            contraseña = contraseña,
                            avatar = avatar
                        )
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                txtNombre.text = "Usuario"
            }
        })
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
                    val soloUnJugador = (jugador1 != null && jugador1.isNotEmpty()) &&
                            (jugador2 == null || jugador2.isEmpty())

                    if (!soloUnJugador) continue

                    val item = LayoutInflater.from(this@PaginaPrincipal)
                        .inflate(R.layout.item_partida, partidasLayout, false)

                    val txtCodigo = item.findViewById<TextView>(R.id.txtCodigoPartida)
                    val btnUnirse = item.findViewById<Button>(R.id.btnUnirsePartida)

                    txtCodigo.text = "Código: $codigo"

                    btnUnirse.setOnClickListener {
                        unirseAPartida(codigo)
                    }

                    partidasLayout.addView(item)
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun unirseAPartida(codigo: String) {

        val partidaRef = FirebaseDatabase.getInstance().getReference("games").child(codigo)

        partidaRef.get().addOnSuccessListener { snapshot ->

            if (!snapshot.exists()) {
                Toast.makeText(this, "Código inválido", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            val jugador1 = snapshot.child("jugador1").value as? String
            val jugador2 = snapshot.child("jugador2").value as? String

            // Si el segundo jugador ya existe, la partida está llena
            if (jugador2 != null && jugador2.isNotEmpty()) {
                Toast.makeText(this, "La partida está llena", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            // Registrar jugador2
            partidaRef.child("jugador2").setValue("o").addOnSuccessListener {

                val intent = Intent(this, PaginaJuego::class.java)
                intent.putExtra("codigo_partida", codigo)
                intent.putExtra("yoSoy", "o")
                intent.putExtra("modo_juego", "online")
                startActivity(intent)
            }
        }
    }

}