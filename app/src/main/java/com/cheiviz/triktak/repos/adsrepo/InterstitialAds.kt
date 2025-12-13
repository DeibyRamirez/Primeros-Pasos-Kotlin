package com.cheiviz.triktak.repos.adsrepo

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialAds {

    private var interstitialAd: InterstitialAd? = null

    fun loadAd(activity: Activity){
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            activity,
            "ca-app-pub-3940256099942544/6300978111", //  ID Prueba
            adRequest,
            object : InterstitialAdLoadCallback(){
                override fun onAdLoaded(ed: InterstitialAd){
                    interstitialAd = ed
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun show(activity: Activity){
        interstitialAd?.show(activity)
        interstitialAd = null
        loadAd(activity) // Recarga para siguiente uso
    }


    // Llamar al Intersitial desde tu Activity

    // InterstitialAds.loadAd(this)
    //
    //  cuando quieras mostrar:
    // btnContinuar.setOnClickListener {
    //     InterstitialAds.show(this)
    // }

}