package com.example.myapplication.desafio

import android.os.CountDownTimer

class Temporizador(
    val duracion: Long,
    val onTick: (segundos: Int) -> Unit,
    val onFinish: () -> Unit
) {
    private var timer: CountDownTimer? = null

    fun iniciar(){
        timer = object : CountDownTimer(duracion, 1000){
            override fun onTick(ms: Long){
                this@Temporizador.onTick((ms/1000).toInt())
            }

            override fun onFinish(){
                this@Temporizador.onFinish()

            }
        }.start()

    }
    fun detener(){
        timer?.cancel()
    }
}