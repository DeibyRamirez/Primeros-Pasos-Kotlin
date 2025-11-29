package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.viewfinder.core.ScaleType
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.desafio.ControladorDesafio
import com.example.myapplication.desafio.MotorIA
import com.example.myapplication.desafio.Temporizador
import com.google.firebase.database.*
import kotlinx.coroutines.launch

class PaginaJuego : BaseActivity() {

    // Variables del juego (locales y online)
    private var turnoJugador = "X"
    private var jugadas = Array(3) { arrayOfNulls<String>(3) }
    private var juegoTerminado = false

    // Variables para determinar el modo de juego
    private var modoJuego = "online" // "local" o "online"

    // Variables Firebase (solo para modo online)
    private lateinit var database: FirebaseDatabase
    private lateinit var partidaRef: DatabaseReference
    private var codigoPartida: String = ""
    private var yoSoy: String = "x" // "x" o "o"
    private var gameListener: ValueEventListener? = null

    // Variables UI
    private lateinit var textoTurno: TextView
    private lateinit var textoNivel: TextView
    private lateinit var textoTiempo: TextView
    private lateinit var grid: GridLayout



    // Skin de X y O
    private lateinit var prefs: UserPrefs
    private var skinX: String = ""
    private var skinO: String = ""

    // importaciones Desafio
    private var controladorDesafio = ControladorDesafio()
    private var motorIA = MotorIA()
    private var temporizador: Temporizador? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_juego)

        // Inicializar preferencias
        prefs = UserPrefs(this)

        lifecycleScope.launch {
            skinX = prefs.leerSkinX()        // puede ser "ic_x" o una ruta interna
            skinO = prefs.leerSkinO()        // puede ser "ic_c" o una ruta interna
        }


        // Obtener el modo de juego del Intent
        modoJuego = intent.getStringExtra("modo_juego") ?: "online"

        Log.d("PaginaJuego", "onCreate - Modo de juego: $modoJuego")

        // Configurar las vistas
        textoTurno = findViewById(R.id.textoTurno)
        textoTiempo = findViewById(R.id.textoTiempo)
        textoNivel = findViewById(R.id.textoNivel)
        grid = findViewById(R.id.grid)

        val botonReiniciar = findViewById<Button>(R.id.buttonReiniciar)
        val botonVolver = findViewById<Button>(R.id.botonVolver)

        // Configurar según el modo de juego
        if (modoJuego == "local") {
            configurarModoLocal()
            // Ocultar elementos del modo desafío
            textoTiempo.visibility = View.GONE
            textoNivel.visibility = View.GONE
            botonReiniciar.visibility = View.VISIBLE
        }
        if (modoJuego == "desafio"){
            configurarModoDesafio()


            // Mostrar elementos del modo desafío
            textoTiempo.visibility = View.VISIBLE
            textoNivel.visibility = View.VISIBLE
            botonReiniciar.visibility = View.GONE
            textoTurno.visibility = View.GONE

        }
        if (modoJuego == "online"){
            configurarModoOnline()

            // Ocultar elementos del modo desafío
            textoTiempo.visibility = View.GONE
            textoNivel.visibility = View.GONE
            botonReiniciar.visibility = View.GONE
        }

        // Configurar botones
        botonVolver.setOnClickListener {
            val intent = Intent(this, PaginaBienvenida::class.java)
            startActivity(intent)
            finish()
        }

        botonReiniciar.setOnClickListener {
             if (modoJuego == "local") {
                reiniciarJuegoLocal()
            }
        }
    }

    // --- MODO  DESAFIO --- //

    private fun configurarModoDesafio() {

        Log.d("PaginaJuego", "Configurando modo DESAFÍO")

        juegoTerminado = false
        temporizador?.detener()

        controladorDesafio.configurarNivel()
        inicializarTableroDesafio(controladorDesafio.tableroTamano)

        textoNivel.text = "Nivel ${controladorDesafio.nivelActual}"

        iniciarTemporizador()

    }

    private fun inicializarTableroDesafio(tamano: Int) {
        grid.columnCount = tamano
        grid.rowCount = tamano
        grid.removeAllViews()  // Eliminar los botones anteriores

        jugadas = Array(tamano) { arrayOfNulls<String>(tamano) }

        for (i in 0 until tamano) {
            for (j in 0 until tamano) {
                // Crear botón programáticamente
                val boton = ImageButton(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        setMargins(4, 4, 4, 4)
                    }
                    setBackgroundResource(R.drawable.green_button)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    contentDescription = "Casilla $i-$j"
                }

                grid.addView(boton)

                val fila = i
                val col = j

                boton.setOnClickListener { clickJugadorDesafio(fila, col, boton) }
            }
        }
    }


    private fun clickJugadorDesafio(fila: Int, col: Int, boton: ImageButton) {

        if (jugadas[fila][col] != null || juegoTerminado) return

        jugadas[fila][col] = "X"
        aplicarSkin(boton, "X")

        if (chequearGanador()) {
            ganarJugadorDesafio()
            return
        }

        if (tableroLleno()) {
            juegoTerminado = true
            temporizador?.detener()
            Toast.makeText(this, "¡Empate! Intenta de nuevo", Toast.LENGTH_LONG).show()
            configurarModoDesafio() // Reiniciar mismo nivel
            return
        }

        moverIA()
    }

    private fun moverIA() {
        val jugada = motorIA.elegirJugadaIA(jugadas, controladorDesafio.probabilidadIA)
            ?: return

        val (fila, col) = jugada
        val boton = grid.getChildAt(fila * controladorDesafio.tableroTamano + col) as ImageButton

        jugadas[fila][col] = "O"
        aplicarSkin(boton, "O")

        if (chequearGanador()) {
            perderJugadorDesafio()
            return
        }

        // ✅ AGREGAR: Verificar empate después del movimiento de IA
        if (tableroLleno()) {
            juegoTerminado = true
            temporizador?.detener()
            Toast.makeText(this, "¡Empate! Intenta de nuevo", Toast.LENGTH_LONG).show()
            configurarModoDesafio() // Reiniciar mismo nivel
        }
    }

    private fun iniciarTemporizador() {

        temporizador?.detener()

        temporizador = Temporizador(
            controladorDesafio.tiempoRestante * 1000L,
            onTick = { segundos ->
                runOnUiThread {
                    textoTiempo.text = "Tiempo restante: $segundos s"
                }

            },
            onFinish = {
                runOnUiThread {
                    perderJugadorDesafio()
                }

            }
        )
        temporizador?.iniciar()
    }

    private fun ganarJugadorDesafio() {
        juegoTerminado = true
        temporizador?.detener()

        Toast.makeText(this, "¡Ganaste el nivel!", Toast.LENGTH_LONG).show()

        controladorDesafio.avanzaNivel()
        configurarModoDesafio()
    }

    private fun perderJugadorDesafio() {
        juegoTerminado = true
        temporizador?.detener()

        Toast.makeText(this, "Perdiste el desafío 😔", Toast.LENGTH_LONG).show()

        val intent = Intent(this, pagina_juego_ia::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun configurarBotonesTableroDesafio() {
        val tamano = controladorDesafio.tableroTamano

        for (i in 0 until grid.childCount) {
            val boton = grid.getChildAt(i) as ImageButton
            val fila = i / tamano
            val col = i % tamano

            boton.setOnClickListener {
                clickJugadorDesafio(fila, col, boton)
            }
        }
    }







    private fun configurarModoLocal() {
        Log.d("PaginaJuego", "Configurando modo LOCAL")

        // En modo local, siempre empezamos con X
        turnoJugador = "X"
        textoTurno.text = "Juego Local - Turno de: $turnoJugador"

        // Configurar botones del tablero para modo local
        configurarBotonesTableroLocal()
    }

    private fun configurarModoOnline() {
        Log.d("PaginaJuego", "Configurando modo ONLINE")

        // Obtener datos del Intent para modo online
        codigoPartida = intent.getStringExtra("codigo_partida") ?: ""
        yoSoy = intent.getStringExtra("yoSoy") ?: "x"

        if (codigoPartida.isEmpty()) {
            Toast.makeText(this, "Error: No se recibió código de partida", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        Log.d("PaginaJuego", "onCreate - Código partida: $codigoPartida")
        Log.d("PaginaJuego", "onCreate - Yo soy: $yoSoy")

        // Inicializar Firebase
        database = FirebaseDatabase.getInstance()
        partidaRef = database.getReference("games").child(codigoPartida)

        // Mostrar información de la partida
        textoTurno.text = "Código: $codigoPartida - Tú eres: ${yoSoy.uppercase()}"

        // Escuchar cambios en Firebase
        iniciarListenerFirebase()

        // Configurar botones del tablero para modo online
        configurarBotonesTableroOnline()
    }

    private fun configurarBotonesTableroLocal() {
        for (i in 0 until grid.childCount) {
            val boton = grid.getChildAt(i) as ImageButton
            val row = i / 3
            val col = i % 3

            boton.setOnClickListener {
                Log.d("PaginaJuego", "Click LOCAL en botón [$row,$col], turno actual: $turnoJugador")

                // Verificaciones antes de hacer jugada
                if (juegoTerminado) {
                    Toast.makeText(this, "El juego ya terminó", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (jugadas[row][col] != null) {
                    Toast.makeText(this, "Esta casilla ya está ocupada", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Realizar jugada local
                realizarJugadaLocal(row, col, boton)
            }
        }
    }

    private fun realizarJugadaLocal(row: Int, col: Int, boton: ImageButton) {
        Log.d("PaginaJuego", "Realizando jugada LOCAL [$row,$col] = $turnoJugador")

        // Actualizar matriz local
        jugadas[row][col] = turnoJugador

        // Actualizar UI del botón
        aplicarSkin(boton, turnoJugador)


        // Verificar si hay ganador o empate
        if (chequearGanador()) {
            val ganador = obtenerGanador()
            juegoTerminado = true
            textoTurno.text = "¡Ganó $ganador! 🎉"
            Toast.makeText(this, "¡Ganó $ganador!", Toast.LENGTH_LONG).show()
            return
        }

        if (tableroLleno()) {
            juegoTerminado = true
            textoTurno.text = "¡EMPATE! 🤝"
            Toast.makeText(this, "¡Empate!", Toast.LENGTH_LONG).show()
            return
        }

        // Cambiar turno
        turnoJugador = if (turnoJugador == "X") "O" else "X"
        textoTurno.text = "Juego Local - Turno de: $turnoJugador"

        Log.d("PaginaJuego", "Turno cambiado a: $turnoJugador")
    }

    private fun reiniciarJuegoLocal() {
        Log.d("PaginaJuego", "Reiniciando juego LOCAL")

        // Limpiar estado del juego
        for (i in jugadas.indices) {
            for (j in jugadas[i].indices) {
                jugadas[i][j] = null
            }
        }

        // Limpiar UI
        for (i in 0 until grid.childCount) {
            val boton = grid.getChildAt(i) as ImageButton
            boton.setImageResource(0)
            boton.setBackgroundResource(R.drawable.green_button)
        }

        // Resetear variables de juego
        juegoTerminado = false
        turnoJugador = "X"
        textoTurno.text = "Juego Local - Turno de: $turnoJugador"

        Toast.makeText(this, "Juego reiniciado", Toast.LENGTH_SHORT).show()
    }

    // ================== CÓDIGO PARA MODO ONLINE (sin cambios) ==================

    private fun configurarBotonesTableroOnline() {
        for (i in 0 until grid.childCount) {
            val boton = grid.getChildAt(i) as ImageButton
            val row = i / 3
            val col = i % 3

            boton.setOnClickListener {
                Log.d("PaginaJuego", "Click ONLINE en botón [$row,$col], turno actual: $turnoJugador, yo soy: $yoSoy")

                // Verificaciones antes de hacer jugada
                if (juegoTerminado) {
                    Toast.makeText(this, "El juego ya terminó", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (jugadas[row][col] != null) {
                    Toast.makeText(this, "Esta casilla ya está ocupada", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Verificar si es mi turno
                if (turnoJugador.lowercase() != yoSoy) {
                    Toast.makeText(this, "No es tu turno. Espera a tu oponente", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Realizar jugada en Firebase
                enviarJugadaAFirebase(row, col)
            }
        }
    }

    private fun enviarJugadaAFirebase(row: Int, col: Int) {
        Log.d("PaginaJuego", "Enviando jugada [$row,$col] = $yoSoy a Firebase")

        // Calcular próximo turno
        val proximoTurno = if (yoSoy == "x") "o" else "x"

        val updates = hashMapOf<String, Any>(
            "tablero/${row}_${col}" to yoSoy.uppercase(),
            "turno" to proximoTurno
        )

        partidaRef.updateChildren(updates)
            .addOnSuccessListener {
                Log.d("PaginaJuego", "Jugada enviada exitosamente")
            }
            .addOnFailureListener { error ->
                Log.e("PaginaJuego", "Error al enviar jugada: ${error.message}")
                Toast.makeText(this, "Error al enviar jugada: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun iniciarListenerFirebase() {
        Log.d("PaginaJuego", "Iniciando listener de Firebase")

        gameListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("PaginaJuego", "onDataChange - Datos actualizados desde Firebase")

                if (!snapshot.exists()) {
                    Log.e("PaginaJuego", "La partida no existe en Firebase")
                    return
                }

                // Actualizar turno
                val turnoFirebase = snapshot.child("turno").getValue(String::class.java) ?: "x"
                turnoJugador = turnoFirebase.uppercase()

                // Actualizar texto del turno
                val esMiTurno = turnoFirebase == yoSoy
                textoTurno.text = if (juegoTerminado) {
                    textoTurno.text // Mantener mensaje de fin de juego
                } else if (esMiTurno) {
                    "¡Tu turno! (${yoSoy.uppercase()})"
                } else {
                    "Turno del oponente (${turnoFirebase.uppercase()})"
                }

                Log.d("PaginaJuego", "Turno actualizado: $turnoFirebase, Es mi turno: $esMiTurno")

                // Actualizar tablero desde Firebase
                actualizarTableroDesdeFirebase(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PaginaJuego", "Error en Firebase listener: ${error.message}")
                Toast.makeText(this@PaginaJuego, "Error de conexión: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        partidaRef.addValueEventListener(gameListener!!)
    }

    private fun actualizarTableroDesdeFirebase(snapshot: DataSnapshot) {
        Log.d("PaginaJuego", "Actualizando tablero desde Firebase")

        val tableroFirebase = snapshot.child("tablero")
        var cambiosHechos = false

        // Recorrer todas las posiciones del tablero
        for (i in 0..2) {
            for (j in 0..2) {
                val key = "${i}_${j}"
                val valorFirebase = tableroFirebase.child(key).getValue(String::class.java) ?: ""

                Log.d("PaginaJuego", "Posición [$i,$j]: Firebase='$valorFirebase', Local='${jugadas[i][j]}'")

                // Si Firebase tiene un valor y local no, actualizar
                if (valorFirebase.isNotEmpty() && jugadas[i][j] == null) {
                    Log.d("PaginaJuego", "Actualizando posición [$i,$j] con $valorFirebase")

                    // Actualizar matriz local
                    jugadas[i][j] = valorFirebase

                    // Actualizar UI
                    val boton = grid.getChildAt(i * 3 + j) as ImageButton
                    if (valorFirebase.uppercase() == "X") {
                        aplicarSkin(boton, "X")
                    } else {
                        aplicarSkin(boton, "O")
                    }

                    cambiosHechos = true
                }
            }
        }

        // Si se hicieron cambios, verificar ganador
        if (cambiosHechos) {
            verificarEstadoJuegoOnline()
        }
    }

    private fun verificarEstadoJuegoOnline() {
        if (chequearGanador()) {
            val ganador = obtenerGanador()
            juegoTerminado = true

            val mensaje = if (ganador?.lowercase() == yoSoy) {
                "¡GANASTE! 🎉"
            } else {
                "Perdiste 😔"
            }

            textoTurno.text = "$mensaje - Ganó $ganador"
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

        } else if (tableroLleno()) {
            juegoTerminado = true
            textoTurno.text = "¡EMPATE! 🤝"
            Toast.makeText(this, "¡Empate!", Toast.LENGTH_LONG).show()
        }
    }

    private fun reiniciarJuegoFirebase() {
        Log.d("PaginaJuego", "Reiniciando juego en Firebase")

        // Limpiar estado local
        for (i in jugadas.indices) {
            for (j in jugadas[i].indices) {
                jugadas[i][j] = null
            }
        }

        // Limpiar UI
        for (i in 0 until grid.childCount) {
            val boton = grid.getChildAt(i) as ImageButton
            boton.setImageResource(0)
            boton.setBackgroundResource(R.drawable.green_button)
        }

        juegoTerminado = false
        turnoJugador = "X"

        // Reiniciar en Firebase
        val datosReinicio = hashMapOf<String, Any>(
            "turno" to "x",
            "tablero" to hashMapOf(
                "0_0" to "", "0_1" to "", "0_2" to "",
                "1_0" to "", "1_1" to "", "1_2" to "",
                "2_0" to "", "2_1" to "", "2_2" to ""
            )
        )

        partidaRef.updateChildren(datosReinicio)
            .addOnSuccessListener {
                Log.d("PaginaJuego", "Juego reiniciado exitosamente en Firebase")
                Toast.makeText(this, "Juego reiniciado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Log.e("PaginaJuego", "Error al reiniciar: ${error.message}")
                Toast.makeText(this, "Error al reiniciar: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ================== FUNCIONES AUXILIARES COMPARTIDAS ==================

    private fun obtenerGanador(): String? {
        val tamano = jugadas.size
        val ganar = 3  // ✅ SIEMPRE buscar 3 en raya

        // 1️⃣ Verificar todas las filas
        for (i in 0 until tamano) {
            for (startCol in 0..tamano - ganar) {
                val primero = jugadas[i][startCol]
                if (primero == null) continue

                var todosIguales = true
                for (k in 1 until ganar) {
                    if (jugadas[i][startCol + k] != primero) {
                        todosIguales = false
                        break
                    }
                }
                if (todosIguales) return primero
            }
        }

        // 2️⃣ Verificar todas las columnas
        for (j in 0 until tamano) {
            for (startRow in 0..tamano - ganar) {
                val primero = jugadas[startRow][j]
                if (primero == null) continue

                var todosIguales = true
                for (k in 1 until ganar) {
                    if (jugadas[startRow + k][j] != primero) {
                        todosIguales = false
                        break
                    }
                }
                if (todosIguales) return primero
            }
        }

        // 3️⃣ Verificar diagonales principales (\)
        for (i in 0..tamano - ganar) {
            for (j in 0..tamano - ganar) {
                val primero = jugadas[i][j]
                if (primero == null) continue

                var todosIguales = true
                for (k in 1 until ganar) {
                    if (jugadas[i + k][j + k] != primero) {
                        todosIguales = false
                        break
                    }
                }
                if (todosIguales) return primero
            }
        }

        // 4️⃣ Verificar diagonales inversas (/)
        for (i in 0..tamano - ganar) {
            for (j in ganar - 1 until tamano) {
                val primero = jugadas[i][j]
                if (primero == null) continue

                var todosIguales = true
                for (k in 1 until ganar) {
                    if (jugadas[i + k][j - k] != primero) {
                        todosIguales = false
                        break
                    }
                }
                if (todosIguales) return primero
            }
        }

        return null
    }

    private fun tableroLleno(): Boolean {
        for (i in jugadas.indices) {
            for (j in jugadas[i].indices) {
                if (jugadas[i][j] == null) {
                    return false
                }
            }
        }
        return true
    }

    private fun chequearGanador(): Boolean {
        val tamano = jugadas.size
        val ganar = 3

        // Verificar todas filas
        for (i in 0 until tamano){
            for (startCol in 0..tamano - ganar){
                val primero = jugadas[i][startCol]
                if (primero == null) continue

                var gano = true
                for (k in 1 until ganar){
                    if (jugadas[i][startCol + k] != primero){
                        gano = false
                        break
                    }
                }
                if (gano) return true

            }

        }

        // 2️⃣ Verificar todas las columnas
        for (j in 0 until tamano) {
            for (startRow in 0..tamano - ganar) {
                val primero = jugadas[startRow][j]
                if (primero == null) continue

                var gano = true
                for (k in 1 until ganar) {
                    if (jugadas[startRow + k][j] != primero) {
                        gano = false
                        break
                    }
                }
                if (gano) return true
            }
        }

        // 3️⃣ Verificar diagonales principales (\)
        for (i in 0..tamano - ganar) {
            for (j in 0..tamano - ganar) {
                val primero = jugadas[i][j]
                if (primero == null) continue

                var gano = true
                for (k in 1 until ganar) {
                    if (jugadas[i + k][j + k] != primero) {
                        gano = false
                        break
                    }
                }
                if (gano) return true
            }
        }

        // 4️⃣ Verificar diagonales inversas (/)
        for (i in 0..tamano - ganar) {
            for (j in ganar - 1 until tamano) {
                val primero = jugadas[i][j]
                if (primero == null) continue

                var gano = true
                for (k in 1 until ganar) {
                    if (jugadas[i + k][j - k] != primero) {
                        gano = false
                        break
                    }
                }
                if (gano) return true
            }
        }
        return false
    }


    private fun aplicarSkin(boton: ImageButton, tipo: String) {
        val ruta = if (tipo == "X") skinX else skinO

        if (ruta.endsWith(".png")) {
            // Imagen personalizada (ruta interna)
            boton.setImageURI(Uri.parse(ruta))
        } else {
            // Imagen desde drawable
            val id = resources.getIdentifier(ruta, "drawable", packageName)
            boton.setImageResource(id)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Solo limpiar listener si estamos en modo online
        if (modoJuego == "online") {
            gameListener?.let { partidaRef.removeEventListener(it) }
            Log.d("PaginaJuego", "Listener removido al destruir actividad")
        }
    }


}