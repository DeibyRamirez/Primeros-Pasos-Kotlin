package com.cheiviz.triktak.desafio

class MotorIA {

    /**
     * Elige la jugada de la IA según su nivel de dificultad
     * @param tablero Estado actual del juego
     * @param probabilidadInteligente Probabilidad de jugar bien (30%, 70%, 99%)
     * @return Par de coordenadas (fila, columna) donde jugar
     */
    fun elegirJugadaIA(
        tablero: Array<Array<String?>>,
        probabilidadInteligente: Int
    ): Pair<Int, Int>? {

        return jugadaInteligente(tablero)
    }

    val ControladorDesafio = ControladorDesafio()



    /**
     * IA INTELIGENTE - Usa estrategia real de Tres en Raya
     * Prioridades:
     * 1. Ganar si puede (completar su propio 3 en raya)
     * 2. Bloquear al jugador si está por ganar
     * 3. Crear amenazas (poner 2 en raya)
     * 4. Bloquear amenazas del jugador (bloquear su 2 en raya)
     * 5. Jugar en el centro (estratégicamente mejor)
     * 6. Jugar aleatoriamente
     */
    private fun jugadaInteligente(tablero: Array<Array<String?>>, nivel: Int = 1): Pair<Int, Int>? {

        // 🎯 PRIORIDAD 1: Ganar si puede (buscar completar 3 en raya propio)
        buscarGanar(tablero, "O")?.let { return it }

        // 🛡️ PRIORIDAD 2: Bloquear victoria inminente del jugador
        buscarGanar(tablero, "X")?.let { return it }

        // ⚔️ PRIORIDAD 3: Crear amenaza (hacer 2 en raya propio)
        buscarCrearAmenaza(tablero, "O")?.let { return it }

        // 🛡️ PRIORIDAD 4: Bloquear amenaza del jugador (bloquear su 2 en raya)
        buscarCrearAmenaza(tablero, "X")?.let { return it }

        // 🎲 PRIORIDAD 5: Jugar en posición estratégica (centro o esquinas)
        buscarPosicionEstrategica(tablero, nivel = ControladorDesafio.nivelActual)?.let { return it }

        // 🎲 ÚLTIMA OPCIÓN: Jugar aleatoriamente
        return jugadaAleatoria(tablero)
    }

    /**
     * Busca si puede ganar o necesita bloquear (2 en raya con espacio libre)
     * @param simbolo "O" para ganar, "X" para bloquear al jugador
     */
    private fun buscarGanar(tablero: Array<Array<String?>>, simbolo: String): Pair<Int, Int>? {
        val tamano = tablero.size
        val ganar = 3

        // 🔍 Verificar todas las filas
        for (i in 0 until tamano) {
            for (startCol in 0..tamano - ganar) {
                val resultado = verificarLinea(tablero, i, startCol, 0, 1, simbolo, ganar)
                if (resultado != null) return resultado
            }
        }

        // 🔍 Verificar todas las columnas
        for (j in 0 until tamano) {
            for (startRow in 0..tamano - ganar) {
                val resultado = verificarLinea(tablero, startRow, j, 1, 0, simbolo, ganar)
                if (resultado != null) return resultado
            }
        }

        // 🔍 Verificar diagonales principales (\)
        for (i in 0..tamano - ganar) {
            for (j in 0..tamano - ganar) {
                val resultado = verificarLinea(tablero, i, j, 1, 1, simbolo, ganar)
                if (resultado != null) return resultado
            }
        }

        // 🔍 Verificar diagonales inversas (/)
        for (i in 0..tamano - ganar) {
            for (j in ganar - 1 until tamano) {
                val resultado = verificarLinea(tablero, i, j, 1, -1, simbolo, ganar)
                if (resultado != null) return resultado
            }
        }

        return null
    }

    /**
     * Verifica una línea específica buscando 2 del mismo símbolo + 1 vacío
     * @param filaInicio Fila inicial
     * @param colInicio Columna inicial
     * @param deltaFila Incremento de fila (0 para horizontal, 1 para vertical/diagonal)
     * @param deltaCol Incremento de columna (1 para horizontal, 0 para vertical, ±1 para diagonal)
     * @param simbolo Símbolo a buscar ("X" o "O")
     * @param longitud Longitud de la línea a verificar (3)
     * @return Coordenadas de la casilla vacía si encuentra 2 + vacío, null si no
     */
    private fun verificarLinea(
        tablero: Array<Array<String?>>,
        filaInicio: Int,
        colInicio: Int,
        deltaFila: Int,
        deltaCol: Int,
        simbolo: String,
        longitud: Int
    ): Pair<Int, Int>? {

        var conteo = 0
        var vacio: Pair<Int, Int>? = null

        for (k in 0 until longitud) {
            val fila = filaInicio + k * deltaFila
            val col = colInicio + k * deltaCol

            when (tablero[fila][col]) {
                simbolo -> conteo++
                null -> vacio = fila to col
                else -> return null // Hay símbolo contrario, esta línea no sirve
            }
        }

        // Si hay exactamente 2 del símbolo buscado y 1 vacío, devolver el vacío
        return if (conteo == 2 && vacio != null) vacio else null
    }

    /**
     * Busca crear o bloquear una amenaza (1 símbolo + 2 vacíos en línea)
     */
    private fun buscarCrearAmenaza(tablero: Array<Array<String?>>, simbolo: String): Pair<Int, Int>? {
        val tamano = tablero.size
        val ganar = 3

        // Verificar filas
        for (i in 0 until tamano) {
            for (startCol in 0..tamano - ganar) {
                val resultado = verificarAmenaza(tablero, i, startCol, 0, 1, simbolo, ganar)
                if (resultado != null) return resultado
            }
        }

        // Verificar columnas
        for (j in 0 until tamano) {
            for (startRow in 0..tamano - ganar) {
                val resultado = verificarAmenaza(tablero, startRow, j, 1, 0, simbolo, ganar)
                if (resultado != null) return resultado
            }
        }

        // Verificar diagonales principales
        for (i in 0..tamano - ganar) {
            for (j in 0..tamano - ganar) {
                val resultado = verificarAmenaza(tablero, i, j, 1, 1, simbolo, ganar)
                if (resultado != null) return resultado
            }
        }

        // Verificar diagonales inversas
        for (i in 0..tamano - ganar) {
            for (j in ganar - 1 until tamano) {
                val resultado = verificarAmenaza(tablero, i, j, 1, -1, simbolo, ganar)
                if (resultado != null) return resultado
            }
        }

        return null
    }

    /**
     * Verifica si hay 1 símbolo + 2 vacíos en una línea (potencial amenaza)
     */
    private fun verificarAmenaza(
        tablero: Array<Array<String?>>,
        filaInicio: Int,
        colInicio: Int,
        deltaFila: Int,
        deltaCol: Int,
        simbolo: String,
        longitud: Int
    ): Pair<Int, Int>? {

        var conteo = 0
        val vacios = mutableListOf<Pair<Int, Int>>()

        for (k in 0 until longitud) {
            val fila = filaInicio + k * deltaFila
            val col = colInicio + k * deltaCol

            when (tablero[fila][col]) {
                simbolo -> conteo++
                null -> vacios.add(fila to col)
                else -> return null
            }
        }

        // Si hay 1 símbolo y 2 vacíos, jugar en el primer vacío
        return if (conteo == 1 && vacios.size == 2) vacios.random() else null
    }

    /**
     * Busca posiciones estratégicas (centro > esquinas > bordes)
     */
    private fun buscarPosicionEstrategica(tablero: Array<Array<String?>>, nivel: Int): Pair<Int, Int>? {
        val tamano = tablero.size
        val centro = tamano / 2
        val nivelInterno = nivel

        // 1️⃣ Intentar jugar en el centro
        if (tablero[centro][centro] == null && nivelInterno != 1) {
            return centro to centro
        }

        // 2️⃣ Intentar jugar en esquinas
        val esquinas = listOf(
            0 to 0,
            0 to tamano - 1,
            tamano - 1 to 0,
            tamano - 1 to tamano - 1
        )

        esquinas.shuffled().forEach { (fila, col) ->
            if (tablero[fila][col] == null) {
                return fila to col
            }
        }

        // 3️⃣ Si no hay esquinas libres, buscar cerca del centro
        for (i in -1..1) {
            for (j in -1..1) {
                val fila = centro + i
                val col = centro + j
                if (fila in tablero.indices && col in tablero.indices) {
                    if (tablero[fila][col] == null) {
                        return fila to col
                    }
                }
            }
        }

        return null
    }

    /**
     * IA TONTA - Juega aleatoriamente en cualquier casilla libre
     */
    private fun jugadaAleatoria(tablero: Array<Array<String?>>): Pair<Int, Int>? {
        val libres = mutableListOf<Pair<Int, Int>>()

        for (i in tablero.indices) {
            for (j in tablero[i].indices) {
                if (tablero[i][j] == null) {
                    libres.add(i to j)
                }
            }
        }

        return if (libres.isNotEmpty()) libres.random() else null
    }
}