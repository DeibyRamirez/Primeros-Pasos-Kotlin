package com.cheiviz.triktak

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.cheiviz.triktak.modelos.Usuario
import com.cheiviz.triktak.repos.UserRepository
import com.cheiviz.triktak.repos.adsrepo.CoinsRepo
import com.cheiviz.triktak.repos.adsrepo.RewardeAds
import com.google.firebase.database.FirebaseDatabase

class Perfil : BaseActivity() {

    private var datosUsuario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)

        val botonPerfil = findViewById<ImageView>(R.id.btnPerfil)
        val botonguardar = findViewById<Button>(R.id.botonGuardar)
        val txtNombre = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.textNombreUsuario)
        val txtCorreo = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.textCorreo)
        val txtContraseña = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.textContraseña)
        val botonRecargar = findViewById<Button>(R.id.botonRecargar)
        val botonAnuncio = findViewById<Button>(R.id.botonAnuncio)
        val txtMonedas = findViewById<TextView>(R.id.txtMonedas)
        val txtAnuncio = findViewById<TextView>(R.id.txtAnuncio)
        val botonCerrarPerfil = findViewById<Button>(R.id.botonCerrarPerfil)


        val uidUsuario  = intent.getStringExtra("uid_usuario").toString()
        val repo = UserRepository()

        // 🔥 1. Cargar datos del perfil
        repo.cargarDatosUsuario(uidUsuario) { datos ->
            if (datos != null) {
                datosUsuario = datos

                txtNombre.setText(datos.nombre)
                txtCorreo.setText(datos.correo)
                txtContraseña.setText(datos.contraseña)
                txtMonedas.text = "Monedas: ${datos.monedas}"

                val idImg = resources.getIdentifier(datos.avatar, "drawable", packageName)
                botonPerfil.setImageResource(idImg)
            }
        }

        botonguardar.setOnClickListener {
            actualizarDatosUsuario(
                uidUsuario.toString(),
                txtNombre.text.toString(),
                txtCorreo.text.toString(),
                txtContraseña.text.toString(),
                datosUsuario?.avatar.toString()
            )
        }

        botonAnuncio.setOnClickListener {
            RewardeAds.show(this) { rewardAmount ->
                CoinsRepo.addCoins(uidUsuario,25)
            }
        }

        botonCerrarPerfil.setOnClickListener {
            cerrarSesion(uidUsuario)
        }

        botonRecargar.setOnClickListener {
            val intent = Intent(this, PaginaRecargar::class.java)
            intent.putExtra("id_usuario", uidUsuario)
            startActivity(intent)
        }
    }

    private fun actualizarDatosUsuario(
        id: String,
        nombre: String,
        correo: String,
        contraseña: String,
        avatar: String
    ) {
        val database = FirebaseDatabase.getInstance().getReference("usuarios").child(id)

        val usuario = Usuario(
            id = id,
            nombre = nombre,
            correo = correo,
            contraseña = contraseña,
            avatar = avatar,
            enLinea = datosUsuario?.enLinea ?: false,
            monedas = datosUsuario?.monedas ?: 0 // 🔥 MANTENER LAS MONEDAS
        )

        database.setValue(usuario)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cerrarSesion(uid: String) {

        // 🔥 marcar offline
        FirebaseDatabase.getInstance()
            .getReference("usuarios/$uid/enLinea")
            .setValue(false)

        // 🚀 ir a Login SIN POSIBILIDAD DE VOLVER ATRÁS
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}
