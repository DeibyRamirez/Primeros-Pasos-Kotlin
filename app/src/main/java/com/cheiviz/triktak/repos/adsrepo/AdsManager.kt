package com.cheiviz.triktak.repos.adsrepo

import android.content.Context
import com.google.android.gms.ads.MobileAds

object AdsManager {

    fun init(context: Context){
        MobileAds.initialize(context)
    }

    // Y utilizo en la actividad adecuada el AdsManager.init(this)
}