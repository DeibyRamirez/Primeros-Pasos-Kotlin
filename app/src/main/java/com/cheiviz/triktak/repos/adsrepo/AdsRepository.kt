package com.cheiviz.triktak.repos.adsrepo

import com.google.firebase.database.FirebaseDatabase

object AdsRepository {

    fun setRemoveAds(uid: String, value: Boolean) {
        FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uid)
            .child("removeAds")
            .setValue(value)
    }

    fun observeRemoveAds(uid: String, callback: (Boolean) -> Unit) {
        FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uid)
            .child("removeAds")
            .get()
            .addOnSuccessListener {
                callback(it.getValue(Boolean::class.java) ?: false)
            }
    }

}