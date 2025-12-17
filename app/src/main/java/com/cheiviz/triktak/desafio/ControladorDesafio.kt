package com.cheiviz.triktak.desafio

class ControladorDesafio {


    var nivelActual = 1
    var tiempoRestante = 20
    var tableroTamano = 3
    var probabilidadIA = 100
    var nivelMaximo = 4


    fun configurarNivel(){
        when (nivelActual){
            1 -> {
                tableroTamano = 3
                probabilidadIA = 60
                tiempoRestante = 20

            }

            2 -> {
                tableroTamano = 3
                probabilidadIA = 70
                tiempoRestante = 15

            }

            3 -> {
                tableroTamano = 4
                probabilidadIA = 80
                tiempoRestante = 10

            }

            4 -> {
                tableroTamano = 5
                probabilidadIA = 90
                tiempoRestante = 5

            }

        }
    }

    fun avanzaNivel(){
        nivelActual++
        if (nivelActual > 4) nivelActual = 1
        configurarNivel()
    }

    fun reiniciarDesafio(){
        nivelActual = 1
        configurarNivel()

    }
    fun esUltimoNivel(): Boolean {
        return nivelActual >= nivelMaximo
    }

}