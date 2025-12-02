package com.example.myapplication.modelos

// Esata clase me permite crear un objeto de tipo usuario, para poderlo guardar en la base de datos Realtime...
data class Usuario (
    var id: String? = null,
    var nombre: String? = null,
    var correo: String? = null,
    var contraseña: String? = null,
    var avatar: String? = null,
    var enLinea: Boolean = false

)

