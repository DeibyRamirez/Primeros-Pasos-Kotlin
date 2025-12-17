package com.cheiviz.triktak

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.cheiviz.triktak.repos.UserRepository
import com.cheiviz.triktak.repos.adsrepo.AdsRepository
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch


open class BaseActivity : AppCompatActivity() {

    private val prefs by lazy { UserPrefs(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observarFondo()
    }

    override fun onResume() {
        super.onResume()

        val uid = UserRepository.miId
        if (uid.isEmpty()) return

        AdsRepository.observeRemoveAds(uid) { removeAds ->
            if (removeAds) ocultarAnuncios()
            else mostrarAnuncios()
        }
    }

    private fun observarFondo() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                prefs.fondo.collect { fondo ->
                    val root = findViewById<View>(android.R.id.content)

                    when {
                        fondo.isNullOrEmpty() -> {
                            root.setBackgroundResource(R.drawable.f2)
                        }

                        resources.getIdentifier(fondo, "drawable", packageName) != 0 -> {
                            root.setBackgroundResource(
                                resources.getIdentifier(fondo, "drawable", packageName)
                            )
                        }

                        else -> {
                            val bitmap = BitmapFactory.decodeFile(fondo)
                            if (bitmap != null) {
                                root.background = BitmapDrawable(resources, bitmap)
                            } else {
                                root.setBackgroundResource(R.drawable.f2)
                            }
                        }
                    }
                }
            }
        }
    }

    protected open fun ocultarAnuncios() {}
    protected open fun mostrarAnuncios() {}
}
