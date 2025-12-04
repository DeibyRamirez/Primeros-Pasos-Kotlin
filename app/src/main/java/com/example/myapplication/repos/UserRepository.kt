package com.example.myapplication.repos

import com.example.myapplication.modelos.Usuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepository {

    companion object {
        var miId: String = ""   // <--- ESTA ES LA VARIABLE QUE FALTABA
    }
    private val db = FirebaseDatabase.getInstance().getReference("usuarios")

    fun cargarDatosUsuario(
        id: String,
        callback: (Usuario?) -> Unit
    ) {
        db.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (!snapshot.exists()) {
                    callback(null)
                    return
                }

                val usuario = Usuario(
                    id = id,
                    nombre = snapshot.child("nombre").value.toString(),
                    correo = snapshot.child("correo").value.toString(),
                    contraseña = snapshot.child("contraseña").value.toString(),
                    avatar = snapshot.child("avatar").value.toString(),
                )

                callback(usuario)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

}