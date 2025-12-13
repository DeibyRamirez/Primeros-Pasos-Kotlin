package com.cheiviz.triktak.repos.adsrepo

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object RewardeAds {

    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    fun loadAd(context: Context) {
        if (isLoading || rewardedAd != null) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            "ca-app-pub-3940256099942544/5224354917", // ID prueba
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isLoading = false
                }
            }
        )
    }

    fun show(activity: Activity, rewardCallback: (Int) -> Unit) {
        val ad = rewardedAd
        if (ad == null) {
            // Recargar si no estaba listo
            loadAd(activity.applicationContext)
            return
        }

        ad.show(activity) { rewardItem ->
            rewardCallback(rewardItem.amount)
        }

        rewardedAd = null
        loadAd(activity.applicationContext) // Recargar después de usarlo
    }

}
