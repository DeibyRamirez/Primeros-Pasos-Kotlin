package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random
import kotlin.random.nextInt

class Logica_Basica_Kotlin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_logica_basica_kotlin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

fun main() {

    /*Actividad 1*/

    val nombre = "Deiby "
    val apellidos = "Ramirez "
    val telefono = "311767....."
    val altura = 1.80
    val inicialApellido = 'R'
    var edad = 20
    var estudiante: Boolean = true
    var trabajdador: Boolean = true
    var apodo = "Cheiviz"

    print(
        """Hola mi nombre es ${nombre}  ${apellidos},
        | tengo ${edad} años, mido ${altura} cm, 
        | mi apodo es ${apodo}, la inicial de mi apellido es ${inicialApellido}, 
        | mi telefono laboral es ${telefono}.
        | 
        | Es Estudiante? ${estudiante}
        | Es Trabajador? ${trabajdador} """
    )

    /*Actividad 2*/

    println("Ingresa tu nombre: ")
    val nombreIngresado = readln()


    println("Ingresa tu edad: ")
    /*El toInt() convierte un string a entero*/
    val edadIngresada = readln()?.toInt()

    println("Ingresa tu Carrera Universitaria: ")
    val carreraU = readln()

    var tipoMusica = 0
    while (tipoMusica < 1 || tipoMusica > 6) {
        println(
            """Ingresa tu Tipo de musica Favorita: 
        |1. Pop
        |2. Salsa
        |3. Regueton
        |4. Ponk
        |5. Electronica
        |6. Vallenato
        |
        |Respuesta solo numerica: 
    """
        )
        tipoMusica = readln().toInt()


    }
    tipoMusica = tipoMusica.toInt() - 1


    var musica = listOf<String>("Pop", "Salsa", "Regueton", "Ponk", "Electronica", "Vallenato")

    println(
        "Hola soy ${nombreIngresado}, mi edad es ${edadIngresada}, años " +
                "estudio en la ${carreraU}, " +
                "mi genero de musica favorita es ${
                    musica.get(tipoMusica)
                }"
    )

    /*Actividad 3 y 4*/

    println("\nNUMERO INCOGNITO...adivina el numero entre 1 y 10: ")
    val numeroIncognito = Random.nextInt(1, 11).toInt()
    var numeroEncontrado = false

    for (i in 1..5) {
        /* > es mayor que
        * < es menor que */

        println("Ingresa un numero: ")
        val numero = readLine()?.toInt() ?: 0

        if (numeroIncognito == numero) {
            println("El numero es que ingresaste es igual al numero Incognito... Lo encontraste")
            numeroEncontrado == true
            break
        }
        else if (numero < 0 && numeroIncognito < 5)
            println("El numero es que ingresaste es negativo, Pero el numero Incognito es menor a 5")
        else if (numero > 0 && numeroIncognito > 5)
            println("El numero es que ingresaste es positivo, Pero el numero Incognito es mayor a 5")
        else if (numero > 0 && numeroIncognito < 3)
            println("El numero es que ingresaste es positivo, Pero el numero Incognito es menor a 3")
        else if (numero < 0 && numeroIncognito > 7)
            println("El numero es que ingresaste es negativo, Pero el numero Incognito es mayor a 7")
        else if (numero == 0)
            println("El numero es que ingresaste es 0")
        else
            println("No encontraste el numero Incognito")
    }


    /*Actividad 4*/

    println("Contador con un blucle for...")
    for (i in 1..20) {
        println(i)
    }


    println("Contador con un blucle while...")
    var i = 10
    while (i >= 1) {
        println(i)
    i--
    }

    /*Actividad 5*/

    println("Dias de la semana...|Lunes|Martes|Miercoles|Jueves|Viernes|Sabado|Domingo|")

    val dia = readLine()?.toInt() ?: 0

    when (dia) {
        1 -> println("Lunes")
        2 -> println("Martes")
        3 -> println("Miercoles")
        4 -> println("Jueves")
        5 -> println("Viernes")
        6 -> println("Sabado")
        7 -> println("Domingo")
        else -> println("No es un dia de la semana")
    }

    /*Actividad 6*/

    fun Saludar(nombre: String) {
        println("Hola ${nombre} desde el lenguje de programación Kotlin")

    }

    Saludar("Alejandro Galvis")

    fun Operaciones_matematicas(a:Int, b:Int, opcion:Int ):Int {

        when (opcion)
        {
            1 -> return a + b
            2 -> return a - b
            3 -> return a * b
            4 -> return a / b
            else -> return 0
        }
    }

    println(Operaciones_matematicas(3,5,1))

    /*Actividad 7*/

    val nombres = arrayOf("Stiven","Andres","Daniela","Sofia","Maicol","Nicol")

    for (nombre in nombres){
            println("Mi nombre es ${nombre} y soy feliz..")
    }

    /*Mapas funcionan con clave, valor, es como el json en api*/
    val motos = mapOf(
        "NKD" to "Honda",
        "XTZ_150" to "Yamaha",
        "XRR" to "Suzuki",
        "Victory_one" to "Victory"
    )


    for ((clave, valor) in motos) {
        println("La moto ${clave} es una ${valor}")

    }

}