package com.example.myapplication.repos.adsrepo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object CoinsRepo {

    private fun userRef(uid: String) =
        FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uid)

    // Obtener monedas actuales
    fun getCoins(uid: String,onResult: (Int) -> Unit) {
        userRef(uid).child("monedas").get().addOnSuccessListener {
            onResult(it.getValue(Int::class.java) ?: 0)
        }
    }

    // Agregar monedas
    fun addCoins(uid: String, amount: Int, onComplete: (Boolean) -> Unit = {}) {
        getCoins(uid) { current ->
            userRef(uid).child("monedas").setValue(current + amount)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }
    }

    // Restar monedas (para chat, skins, etc.)
    fun subtractCoins(uid: String, amount: Int, onComplete: (Boolean, String) -> Unit) {
        getCoins(uid) { current ->
            if (current < amount) {
                onComplete(false, "No tienes suficientes monedas")
                return@getCoins
            }

            userRef(uid).child("monedas").setValue(current - amount)
                .addOnSuccessListener { onComplete(true, "") }
                .addOnFailureListener { onComplete(false, "Error al descontar") }
        }
    }
}
