package com.cyberfanta.desafiobetterfly.presenters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import com.cyberfanta.desafiobetterfly.R
import com.cyberfanta.desafiobetterfly.views.DeviceUtils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdsManager {
    companion object {
        @Suppress("PrivatePropertyName", "unused")
        private val TAG = this::class.java.simpleName

        private var bannerAd: AdView? = null
        private var currentBannerAd: AdView? = null
        @SuppressLint("StaticFieldLeak")
        private var currentBannerAdFl: FrameLayout? = null
        private var interstitialAd: InterstitialAd? = null

        /**
         * Show Interstitial Ad on adView
         */
        @Suppress("unused")
        fun attachBannerAd (adView: AdView) {
            currentBannerAd?.removeView(bannerAd)
            if (adView.adSize == null)
                adView.adSize = bannerAd?.adSize
            adView.addView(bannerAd)
            adView.post { bannerAd!!.loadAd(AdRequest.Builder().build()) }
            currentBannerAd = adView
        }

        /**
         * Show Banner Ad on frameLayout
         */
        @Suppress("unused")
        fun attachBannerAd (frameLayout: FrameLayout) {
            currentBannerAdFl?.removeView(bannerAd)
            frameLayout.addView(bannerAd)
            frameLayout.post { bannerAd!!.loadAd(AdRequest.Builder().build()) }
            currentBannerAdFl = frameLayout
        }

        /**
         * Show Interstitial Ad
         */
        @Suppress("unused")
        fun showInterstitialAd (activity: Activity) {
            interstitialAd?.show(activity)
        }

        /**
         * Manage the loading of Banner Ads
         */
        @Suppress("unused")
        fun loadBannerAds(context: Context, deviceWidth: Float) {
            if (bannerAd == null) {
                bannerAd = AdView(context)
                bannerAd!!.adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context,
                    DeviceUtils.convertPxToDp(context, deviceWidth)
                )
                bannerAd!!.adUnitId = context.resources.getString(R.string.ads_banner)
                bannerAd!!.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.i(TAG, "Banner Ad Loaded")
                        super.onAdLoaded()
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        @SuppressLint("DefaultLocale")
                        val error = String.format(
                            "domain: %s, code: %d, message: %s",
                            loadAdError.domain, loadAdError.code, loadAdError.message
                        )
                        Log.e(
                            TAG,
                            "The previous banner ad failed to load with error: "
                                    + error
                                    + ". Attempting to"
                                    + " load the next banner ad in the items list."
                        )
                    }
                }
            }
        }

        /**
         * Manage the loading of Interstitial Ads
         */
        @Suppress("unused")
        fun loadInterstitialAds(context: Context) {
            MobileAds.initialize(context) {
                val adRequest = AdRequest.Builder().build()
                InterstitialAd.load(context,
                    context.resources.getString(R.string.ads_interstitial),
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(mInterstitialAd: InterstitialAd) {
                            Log.i(TAG, "Interstitial Ad Loaded")
                            interstitialAd = mInterstitialAd
                            interstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() { interstitialAd = null }

                                override fun onAdFailedToShowFullScreenContent(adError: AdError) { interstitialAd = null }

                                override fun onAdShowedFullScreenContent() {}
                            }
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            Log.i(TAG, loadAdError.message)
                            interstitialAd = null
                            @SuppressLint("DefaultLocale")
                            val error = String.format(
                                "domain: %s, code: %d, message: %s",
                                loadAdError.domain, loadAdError.code, loadAdError.message
                            )
                            Log.e(TAG,
                                "The previous interstitial ad failed to load with error: "
                                        + error
                                        + ". Attempting to"
                                        + " load the next interstitial ad in the items list."
                            )
                        }
                    }
                )
            }
        }
    }
}