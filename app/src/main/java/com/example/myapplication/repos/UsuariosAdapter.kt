package com.example.myapplication.repos

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Perfil
import com.example.myapplication.R
import com.example.myapplication.modelos.Usuario
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase

// Esta clase sirve para cargar los datos en el recyclerView.
// Usando el layout item_usuario.xml
class UsuariosAdapter(
    private val lista: ArrayList<Usuario>,
    private val onPerfilClick: (Usuario) -> Unit,
    private val onChatClick: (Usuario) -> Unit
    // El RecycleView necesita un adapter para cargar los datos y se usa para reciclar las vistas.
) : RecyclerView.Adapter<UsuariosAdapter.ViewHolder>() {

    // Aqui definimos los datos que queremos mostrar en el recyclerView.
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPerfil = view.findViewById<ImageButton>(R.id.imgPerfil)
        val txtNombre = view.findViewById<TextView>(R.id.txtNombreJ)
        val txtEstado = view.findViewById<TextView>(R.id.txtEstado)
        val btnChat = view.findViewById<MaterialButton>(R.id.btnChatear)
    }

    // Esta funcion nos ayuda a crear la vista del recyclerView.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return ViewHolder(vista)
    }
    // Esto sirve para cargar los datos en el recyclerView.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = lista[position]

        holder.imgPerfil.setImageResource(
            holder.itemView.context.resources.getIdentifier(
                user.avatar,
                "drawable",
                holder.itemView.context.packageName
            )
        )

        holder.txtNombre.text = user.nombre

        if (user.enLinea ) {
            holder.txtEstado.text = "En línea"
            holder.txtEstado.setTextColor(Color.GREEN)
        } else {
            holder.txtEstado.text = "Desconectado"
            holder.txtEstado.setTextColor(Color.GRAY)
        }

        holder.btnChat.setOnClickListener {
            onChatClick(user)
        }

        holder.imgPerfil.setOnClickListener {
            onPerfilClick(user)
        }


    }

    override fun getItemCount(): Int = lista.size

}
