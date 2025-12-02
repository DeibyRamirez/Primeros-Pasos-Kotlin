package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityRegistroBinding
import com.example.myapplication.modelos.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase

class Registro : BaseActivity() {

    private var avatarSeleccionado: String = ""

    lateinit var binding: ActivityRegistroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSeleccionAvatares()


        binding.botonRegistrar.setOnClickListener {
            val nombre = binding.nombre.text.toString()
            val correo = binding.correo.text.toString()
            val contraseña = binding.contrasena.text.toString()
            val re = binding.repetirContrasena.text.toString()

            if (nombre.isEmpty() || correo.isEmpty() || contraseña.isEmpty()) {
                // El Toast es una notificación rápida que aparece en la pantalla en la parte inferior
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }

            if (contraseña.length <= 6) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
            }

            if (avatarSeleccionado.isEmpty()) {
                Toast.makeText(this, "Selecciona un avatar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contraseña != re) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                /*val database = FirebaseDatabase.getInstance().getReference("usuarios")
                val idUsuario = database.push().key!!
                val usuario = Usuario(idUsuario, nombre, correo, contraseña)

                database.child(idUsuario).setValue(usuario)
                val intent = Intent(this, Login::class.java)
                startActivity(intent)*/

                // El createWithEmailAndPassword crea un usuario en Firebase Authentication
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, contraseña)
                    // El task es un objeto que contiene la respuesta de la operación...
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Si el registro es exitoso, se obtiene el ID del usuario,
                            // El @addOnCompleteListener es una función que se ejecuta cuando la operación termina...
                            val uid = task.result?.user?.uid ?: return@addOnCompleteListener

                            val usuario = Usuario(uid, nombre, correo, contraseña, avatarSeleccionado, false)
                            // Guarda en Realtime Database el usuario creado...
                            FirebaseDatabase.getInstance().getReference("usuarios")
                                .child(uid).setValue(usuario).addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Usuario creado correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Si el registro es exitoso, se redirige a la pantalla de inicio de sesión...
                                    startActivity(Intent(this, Login::class.java))
                                    finish()
                                }

                        } else {
                            // Si falla el registro masnda este error..
                            Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }


            }


        }
        binding.botonIrLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }

    private fun configurarSeleccionAvatares() {
        val avatares = mapOf(
            binding.button1 to "av1",
            binding.button2 to "av2",
            binding.button3 to "av3",
            binding.button4 to "av4",
            binding.button5 to "av5",
            binding.button6 to "av6",
            binding.button7 to "av7",
            binding.button8 to "av8",
            binding.button9 to "av9",
            binding.button10 to "av10",
            binding.button11 to "av11",
            binding.button12 to "av12"
        )

        avatares.forEach { (boton, nombreAvatar) ->
            boton.setOnClickListener {
                avatarSeleccionado = nombreAvatar

                // marca visual (opcional)
                limpiarSelecciones()
                boton.setBackgroundResource(R.drawable.border_selected)
            }
        }
    }

    private fun limpiarSelecciones() {
        listOf(
            binding.button1, binding.button2, binding.button3, binding.button4, binding.button5, binding.button6,
            binding.button7, binding.button8, binding.button9, binding.button10, binding.button11, binding.button12
        ).forEach {
            it.setBackgroundResource(R.drawable.green_button)
        }
    }

}