package com.cheiviz.triktak

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("user_settings")


class UserPrefs(private val context: Context) {

    companion object {
        val FONDO = stringPreferencesKey("fondo")
        val SKIN_X = stringPreferencesKey("skin_x")
        val SKIN_O = stringPreferencesKey("skin_o")

        val NOTIF_GLOBAL = booleanPreferencesKey("activar_global")
        val NOTIF_PRIVADO = booleanPreferencesKey("activar_privado")

    }

    val fondo: Flow<String?> = context.dataStore.data.map { it[FONDO] ?: ""}
    val skinX: Flow<String?> = context.dataStore.data.map { it[SKIN_X] ?: ""}
    val skinO: Flow<String?> = context.dataStore.data.map { it[SKIN_O] ?: ""}

    // NUEVOS FLUJOS DE NOTIFICACIONES
    val activarGlobal: Flow<Boolean> = context.dataStore.data.map { it[NOTIF_GLOBAL] ?: true }
    val activarPrivado: Flow<Boolean> = context.dataStore.data.map { it[NOTIF_PRIVADO] ?: true }

    suspend fun guardarFondo(ruta: String) {
        context.dataStore.edit { it[FONDO] = ruta }
    }

    suspend fun guardarSkinX(ruta: String) {
        context.dataStore.edit { it[SKIN_X] = ruta }
    }

    suspend fun guardarSkinO(ruta: String) {
        context.dataStore.edit { it[SKIN_O] = ruta }
    }

    suspend fun leerSkinX(): String {
        return context.dataStore.data.map { prefs ->
            prefs[SKIN_X] ?: ""
        }.first()
    }

    suspend fun leerSkinO(): String {
        return context.dataStore.data.map { prefs ->
            prefs[SKIN_O] ?: ""
        }.first()
    }


    // GUARDAR NOTIFICACIONES
    suspend fun setNotifGlobal(valor: Boolean) {
        context.dataStore.edit { it[NOTIF_GLOBAL] = valor }
    }

    suspend fun setNotifPrivado(valor: Boolean) {
        context.dataStore.edit { it[NOTIF_PRIVADO] = valor }
    }

    // LECTURAS RÁPIDAS
    suspend fun leerNotifGlobal(): Boolean {
        return activarGlobal.first()
    }

    suspend fun leerNotifPrivado(): Boolean {
        return activarPrivado.first()
    }




}