package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.modelos.Usuario
import com.google.firebase.database.FirebaseDatabase

class Perfil : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)

        val id = intent.getStringExtra("id")
        val nombre = intent.getStringExtra("nombre")
        val correo = intent.getStringExtra("correo")
        val contraseña = intent.getStringExtra("contraseña")
        val avatar = intent.getStringExtra("avatar")

        val idImg = resources.getIdentifier(avatar, "drawable", packageName)
        val botonPerfil = findViewById<ImageView>(R.id.btnPerfil)
        val botonguardar = findViewById<Button>(R.id.botonGuardar)
        val txtNombre = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.textNombreUsuario)
        val txtCorreo = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.textCorreo)
        val txtContraseña = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.textContraseña)

        // Mostrar los datos en los campos de texto
        txtNombre.setText(nombre)
        txtCorreo.setText(correo)
        txtContraseña.setText(contraseña)

        botonPerfil.setImageResource(idImg)

        // Actualizar los datos en la base de datos Firebase
        botonguardar.setOnClickListener {
            actualizarDatosUsuario(
                id.toString(),
                txtNombre.text.toString(),
                txtCorreo.text.toString(),
                txtContraseña.text.toString(),
                avatar.toString())
        }
    }

    private fun actualizarDatosUsuario(
        id: String,
        nombre : String,
        correo : String,
        contraseña : String,
        avatar : String
    ) {
        val database = FirebaseDatabase.getInstance().getReference("usuarios").child(id)

        val usuario = Usuario(id, nombre, correo, contraseña, avatar)

        database.setValue(usuario)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show()
            }

    }
}