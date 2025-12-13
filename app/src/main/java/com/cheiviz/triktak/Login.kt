package com.cheiviz.triktak

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.cheiviz.triktak.databinding.ActivityLoginBinding
import com.cheiviz.triktak.modelos.Usuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Login : BaseActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*binding.iniciarSesion.setOnClickListener{
            val correo = binding.correo.text.toString()
            val contraseña = binding.contrasena.text.toString()

            if (correo.isEmpty() || contraseña.isEmpty()){
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this, PaginaBienvenida::class.java)
            startActivity(intent)
        }*/

        binding.iniciarSesion.setOnClickListener {
            val correo = binding.correo.text.toString()
            val contraseña = binding.contrasena.text.toString()

            if (correo.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }

            val dbref = FirebaseDatabase.getInstance().getReference("usuarios")
            val consulta = dbref.orderByChild("correo").equalTo(correo)
            consulta.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(this@Login, "Usuario  no encontrado", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }
                    for (childSnapshot in snapshot.children) {
                        val usuario = childSnapshot.getValue(Usuario::class.java)
                        if (usuario?.contraseña == contraseña) {
                            Toast.makeText(this@Login, "Bienvenido", Toast.LENGTH_SHORT).show()

                            val uidUsuario = childSnapshot.key ?: return

                            // 🔥 Marcar en línea al iniciar sesión
                            val ref = FirebaseDatabase.getInstance()
                                .getReference("usuarios/$uidUsuario/enLinea")

                            ref.setValue(true)
                            ref.onDisconnect().setValue(false)

                            val intent = Intent(this@Login, PaginaBienvenida::class.java)

                            intent.putExtra("id_usuario", childSnapshot.key)  // 👈 Guardamos el ID
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            return
                        }
                    }
                    Toast.makeText(this@Login, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Login, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()

                }
            })

        }

        binding.botonIrRegistro.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)

        }


    }


}