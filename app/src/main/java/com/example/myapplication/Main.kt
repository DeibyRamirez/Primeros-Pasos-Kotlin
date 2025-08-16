package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Main : AppCompatActivity() {

    // Declaro las variables del juego
    private var turnoJugador = "X"
    private val jugadas = Array(3) { arrayOfNulls<String>(3) }
    private var juegoTerminado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Configuro las vistas
        val textoTurno = findViewById<TextView>(R.id.textoTurno)
        val grid = findViewById<GridLayout>(R.id.grid)
        val botonReiniciar = findViewById<Button>(R.id.buttonReiniciar)

        for (i in 0 until grid.childCount) {
            val boton = grid.getChildAt(i) as ImageButton  // Cambio a ImageButton
            val row = i / 3
            val col = i % 3

            boton.setOnClickListener {
                if (jugadas[row][col] == null && !juegoTerminado) {

                    // 1. Poner la imagen correspondiente
                    if (turnoJugador == "X") {
                        boton.setImageResource(R.drawable.ic_x)  // Usar setImageResource para ImageButton
                    } else {
                        boton.setImageResource(R.drawable.ic_c)  // Usar setImageResource para ImageButton
                    }

                    // 2. Guardamos la jugada
                    jugadas[row][col] = turnoJugador

                    // 3. Comprobamos si ganó
                    if (chequearGanador()) {
                        textoTurno.text = "Ganó $turnoJugador!"
                        juegoTerminado = true

                    } else if (tableroLleno()) {
                        textoTurno.text = "¡Empate!"
                        juegoTerminado = true
                    } else {
                        // 4. Cambiamos al otro jugador
                        turnoJugador = if (turnoJugador == "X") "O" else "X"
                        textoTurno.text = "Turno de $turnoJugador"
                    }
                }
            }
        }

        // Reiniciar Juego
        botonReiniciar.setOnClickListener {
            reiniciarJuego(grid, textoTurno)
        }
    }

    private fun reiniciarJuego(grid: GridLayout, textoTurno: TextView) {
        for (i in 0 until grid.childCount) {
            val boton = grid.getChildAt(i) as ImageButton  // Cambio a ImageButton
            // Limpiar la imagen
            boton.setImageResource(0)  // Quitar imagen
            boton.setBackgroundResource(R.drawable.green_button)  // Restaurar fondo verde
        }

        // Limpiar matriz de jugadas
        for (i in jugadas.indices) {
            for (j in jugadas[i].indices) {
                jugadas[i][j] = null
            }
        }

        turnoJugador = "X"
        textoTurno.text = "Turno de X"
        juegoTerminado = false
    }

    // Función para verificar si el tablero está lleno (empate)
    private fun tableroLleno(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (jugadas[i][j] == null) {
                    return false
                }
            }
        }
        return true
    }

    private fun chequearGanador(): Boolean {
        // Filas y Columnas
        for (i in 0..2) {
            // Tenemos una fila con 3 iguales
            if (jugadas[i][0] != null &&
                jugadas[i][0] == jugadas[i][1] &&
                jugadas[i][0] == jugadas[i][2]
            ) return true

            // Tenemos una columna con 3 iguales
            if (jugadas[0][i] != null &&
                jugadas[0][i] == jugadas[1][i] &&
                jugadas[0][i] == jugadas[2][i]
            ) return true
        }

        // Diagonales
        // Diagonal principal (izquierda-derecha)
        if (jugadas[0][0] != null &&
            jugadas[0][0] == jugadas[1][1] &&
            jugadas[1][1] == jugadas[2][2]
        ) return true

        // Diagonal secundaria (derecha-izquierda)
        if (jugadas[0][2] != null &&
            jugadas[0][2] == jugadas[1][1] &&
            jugadas[1][1] == jugadas[2][0]
        ) return true

        return false
    }
}