package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class Personalizar : BaseActivity() {

    private lateinit var prefs: UserPrefs

    private var fondoSeleccionado: String = ""
    private var rutaSkinX: String = ""
    private var rutaSkinO: String = ""

    private lateinit var fondo1: ImageView
    private lateinit var fondo2: ImageView
    private lateinit var fondo3: ImageView
    private lateinit var fondo4: ImageView

    private lateinit var fichaX1: ImageView
    private lateinit var fichaX2: ImageView
    private lateinit var fichaX3: ImageView
    private lateinit var fichaX4: ImageView
    private lateinit var fichaXpersonalizada: ImageView

    private lateinit var fichaO1: ImageView
    private lateinit var fichaO2: ImageView
    private lateinit var fichaO3: ImageView
    private lateinit var fichaO4: ImageView
    private lateinit var fichaOpersonalizada: ImageView

    private var tipoSeleccion = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personalizar)

        prefs = UserPrefs(this)

        fondo1 = findViewById(R.id.fondo1)
        fondo2 = findViewById(R.id.fondo2)
        fondo3 = findViewById(R.id.fondo3)
        fondo4 = findViewById(R.id.fondo4)

        fichaX1 = findViewById(R.id.fichaX1)
        fichaX2 = findViewById(R.id.fichaX2)
        fichaX3 = findViewById(R.id.fichaX3)
        fichaX4 = findViewById(R.id.fichaX4)
        fichaXpersonalizada = findViewById(R.id.fichaXpersonalizada)

        fichaO1 = findViewById(R.id.fichaO1)
        fichaO2 = findViewById(R.id.fichaO2)
        fichaO3 = findViewById(R.id.fichaO3)
        fichaO4 = findViewById(R.id.fichaO4)
        fichaOpersonalizada = findViewById(R.id.fichaOpersonalizada)

        val botonGuardar = findViewById<Button>(R.id.botonGuardar)

        // -------- SELECCIÓN DE FONDOS ----------
        fondo1.setOnClickListener {
            tipoSeleccion = "fondo"
            fondoSeleccionado = "f1"
            marcarFondo(fondo1)
        }

        fondo2.setOnClickListener {
            tipoSeleccion = "fondo"
            fondoSeleccionado = "f2"
            marcarFondo(fondo2)
        }

        fondo3.setOnClickListener {
            tipoSeleccion = "fondo"
            fondoSeleccionado = "f3"
            marcarFondo(fondo3)
        }
        fondo4.setOnClickListener {
            tipoSeleccion = "fondoPersonalizado"
            seleccionarImagen.launch("image/*")
        }

        // -------- SELECCIÓN DE SKINS ----------

        // Falta agregar mas skins

        fichaX1.setOnClickListener {
            tipoSeleccion = "skinX"
            rutaSkinX = "ic_x"
            marcarSkinX(fichaX1)

        }
        fichaX2.setOnClickListener {
            tipoSeleccion = "skinX"
            rutaSkinX = "ic_x1"
            marcarSkinX(fichaX2)

        }
        fichaX3.setOnClickListener {
            tipoSeleccion = "skinX"
            rutaSkinX = "ic_x2"
            marcarSkinX(fichaX3)

        }
        fichaX4.setOnClickListener {
            tipoSeleccion = "skinX"
            rutaSkinX = "ic_x3"
            marcarSkinX(fichaX4)

        }
        fichaXpersonalizada.setOnClickListener {
            tipoSeleccion = "skinX"
            seleccionarImagen.launch("image/*")
        }


        fichaO1.setOnClickListener {
            tipoSeleccion = "skinO"
            rutaSkinO = "ic_c"
            marcarSkinO(fichaO1)

        }
        fichaO2.setOnClickListener {
            tipoSeleccion = "skinO"
            rutaSkinO = "ic_c1"
            marcarSkinO(fichaO2)

        }
        fichaO3.setOnClickListener {
            tipoSeleccion = "skinO"
            rutaSkinO = "ic_c2"
            marcarSkinO(fichaO3)

        }
        fichaO4.setOnClickListener {
            tipoSeleccion = "skinO"
            rutaSkinO = "ic_c3"
            marcarSkinO(fichaO4)

        }
        fichaOpersonalizada.setOnClickListener {
            tipoSeleccion = "skinO"
            seleccionarImagen.launch("image/*")
        }

        // -------- GUARDAR EN DATASTORE ----------
        botonGuardar.setOnClickListener {
            lifecycleScope.launch {
                prefs.guardarSkinX(rutaSkinX)
                prefs.guardarSkinO(rutaSkinO)
                prefs.guardarFondo(fondoSeleccionado)

                Toast.makeText(this@Personalizar, "Preferencias guardadas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Selector de imágenes
    private val seleccionarImagen = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val ruta = guardarImagenEnInterno(it)

            when (tipoSeleccion) {
                "fondo" -> {
                    fondoSeleccionado = ruta
                    fondo1.setImageURI(uri) // Vista previa
                }
                "skinX" -> {
                    rutaSkinX = ruta
                    fichaX1.setImageURI(uri)
                }
                "skinO" -> {
                    rutaSkinO = ruta
                    fichaO1.setImageURI(uri)
                }
                "fondoPersonalizado" -> {
                    val ruta = guardarImagenEnInterno(it)
                    fondoSeleccionado = ruta
                    fondo4.setImageURI(uri)
                    marcarFondo(fondo4)
                }
            }
        }
    }

    // Guardar imagen en almacenamiento interno
    private fun guardarImagenEnInterno(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(filesDir, "img_${System.currentTimeMillis()}.png")
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        return file.absolutePath
    }

    private fun marcarFondo(view: ImageView) {
        val listaFondos = listOf(fondo1, fondo2, fondo3, fondo4)
        listaFondos.forEach { it.alpha = 0.5f }
        view.alpha = 1f
    }

    private fun marcarSkinX(view: ImageView) {
        val listaX = listOf(fichaX1, fichaX2, fichaX3, fichaXpersonalizada)
        listaX.forEach { it.alpha = 0.5f }
        view.alpha = 1f
    }

    private fun marcarSkinO(view: ImageView) {
        val listaO = listOf(fichaO1, fichaO2, fichaO3, fichaOpersonalizada)
        listaO.forEach { it.alpha = 0.5f }
        view.alpha = 1f
    }

}
