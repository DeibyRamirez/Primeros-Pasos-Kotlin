package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.modelos.Usuario
import com.example.myapplication.repos.UserRepository
import com.example.myapplication.repos.UsuariosAdapter
import com.example.myapplication.repos.adsrepo.AdsManager
import com.example.myapplication.repos.adsrepo.InterstitialAds
import com.example.myapplication.repos.adsrepo.RewardeAds
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PaginaUsuarios : BaseActivity() {

    private lateinit var listaUsuarios: RecyclerView
    private lateinit var adapter: UsuariosAdapter
    private val usuarios = ArrayList<Usuario>()
    private var datosUsuario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_usuarios)

        // Cargar Banner
        val banner = findViewById<com.google.android.gms.ads.AdView>(R.id.adViewBanner)
        val request = AdRequest.Builder().build()
        banner.loadAd(request)

        var botonPerfil = findViewById<ImageButton>(R.id.btnPerfil)
        var nombreUsuario = findViewById<TextView>(R.id.txtNombreUsuario)

        // Obtener el ID del usuario actual
        val idU  = intent.getStringExtra("uid_usuario").toString()

        UserRepository.miId = idU

        listaUsuarios = findViewById(R.id.listaUsuarios)
        // Esta linea le dice al recyclerView como se va a mostrar los datos y donde.
        listaUsuarios.layoutManager = LinearLayoutManager(this)


        adapter = UsuariosAdapter(
            usuarios,
            onPerfilClick = { usuario -> abrirPerfil(usuario.id ?: "") },
            onChatClick = { usuario -> abrirChat(usuario.id ?: "") }
        )


        listaUsuarios.adapter = adapter

        cargarUsuariosEnLinea()

        val repo = UserRepository()
        // Funcion para cargar los datos del usurio (nombre e imagen de perfil)
        repo.cargarDatosUsuario(idU) { datos ->
            if (datos != null) {
                datosUsuario = datos
                nombreUsuario.text = datos.nombre

                val idImg = resources.getIdentifier(datos.avatar, "drawable", packageName)
                botonPerfil.setImageResource(idImg)
            }
        }

        botonPerfil.setOnClickListener {
            datosUsuario?.let { usuario ->
                val intent = Intent(this, Perfil::class.java)
                intent.putExtra("uid_usuario", idU)
                startActivity(intent)
            }
        }

        

    }

    private fun cargarUsuariosEnLinea() {
        val ref = FirebaseDatabase.getInstance().getReference("usuarios")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usuarios.clear()

                for (child in snapshot.children) {
                    val u = child.getValue(Usuario::class.java)

                    if (u != null) {
                        u.id = child.key ?: ""   // 📌 Asignar UID al modelo

                        if (u.enLinea) {
                            usuarios.add(u)
                        }
                    }


                }
                // mi usuario actual no debe estar en esa lista.
                usuarios.removeAll { it.id == UserRepository.miId }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun abrirChat(uid: String) {
        val intent = Intent(this, PaginaChat::class.java)
        intent.putExtra("uid_usuario", uid)  // ✔ el id DEL USUARIO QUE TOCASTE
        startActivity(intent)
    }


    private fun abrirPerfil(uid: String) {
        val intent = Intent(this, Perfil::class.java)
        intent.putExtra("uid_usuario", uid)   // ✔ el id DEL USUARIO QUE TOCASTE
        startActivity(intent)
    }



}