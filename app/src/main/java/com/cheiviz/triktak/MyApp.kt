// MyApp.kt
package com.cheiviz.triktak

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MyApp : Application(), Application.ActivityLifecycleCallbacks {

    private var startedActivities = 0
    private var userUid: String? = null

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)

        // Observa cambios de auth (por si inicia/cierra sesión mientras la app corre)
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            userUid = auth.currentUser?.uid
        }
    }

    private fun setEnLinea(value: Boolean) {
        val uid = userUid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("usuarios/$uid/enLinea")
        if (value) {
            // marcar en linea y asegurar onDisconnect
            ref.onDisconnect().setValue(false)
            ref.setValue(true)
        } else {
            ref.setValue(false)
        }
    }

    // ActivityLifecycleCallbacks
    override fun onActivityStarted(activity: Activity) {
        startedActivities++
        if (startedActivities == 1) {
            // app pasó a foreground
            setEnLinea(true)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        startedActivities--
        if (startedActivities <= 0) {
            startedActivities = 0
            // app pasó a background
            setEnLinea(false)
        }
    }

    // No nos interesan los otros callbacks, pero hay que implementarlos
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
