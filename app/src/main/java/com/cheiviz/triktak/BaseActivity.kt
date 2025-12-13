package com.cheiviz.triktak

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {

    private val prefs by lazy { UserPrefs(this) }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            prefs.fondo.collect { fondo ->

                val root = findViewById<View>(android.R.id.content)

                if (fondo.isNullOrEmpty()) {
                    root.setBackgroundResource(R.drawable.f2)
                    return@collect
                }

                val idDrawable = resources.getIdentifier(fondo, "drawable", packageName)
                if (idDrawable != 0) {
                    root.setBackgroundResource(idDrawable)
                    return@collect
                }

                val bitmap = BitmapFactory.decodeFile(fondo)
                if (bitmap != null) {
                    root.background = BitmapDrawable(resources, bitmap)
                }
            }
        }
    }
}
