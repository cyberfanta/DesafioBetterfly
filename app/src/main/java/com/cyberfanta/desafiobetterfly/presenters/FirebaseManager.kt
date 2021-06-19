package com.cyberfanta.desafiobetterfly.presenters

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent

class FirebaseManager (context: Context) {
    companion object {
        @Suppress("PrivatePropertyName", "unused")
        private val TAG = this::class.java.simpleName

        var mFirebaseAnalytics: FirebaseAnalytics? = null

        @Suppress("unused")
        fun getFirebaseAnalytics(): FirebaseAnalytics? {
            return mFirebaseAnalytics
        }

        private fun logRawEvent (itemName: String, eventType: String) {
            //Firebase Data Collection
            mFirebaseAnalytics!!.logEvent(eventType) {
                param("Item_Name", itemName)
            }

        }

        fun logEvent (itemName: String, eventType: String) {
            when(eventType) {
                "SELECT_ITEM" -> {
                    logRawEvent(itemName, FirebaseAnalytics.Event.SELECT_ITEM)
                }
                "SELECT_CONTENT" -> {
                    logRawEvent(itemName, FirebaseAnalytics.Event.SELECT_CONTENT)
                }
                "APP_OPEN" -> {
                    logRawEvent(itemName, FirebaseAnalytics.Event.APP_OPEN)
                }
                "SEARCH" -> {
                    logRawEvent(itemName, FirebaseAnalytics.Event.SEARCH)
                }
                "LOGIN" -> {
                    logRawEvent(itemName, FirebaseAnalytics.Event.LOGIN)
                }
                "AD_IMPRESSION" -> {
                    logRawEvent(itemName, FirebaseAnalytics.Event.AD_IMPRESSION)
                }
                "SHARE" -> {
                    logRawEvent(itemName, FirebaseAnalytics.Event.SHARE)
                }
                else -> {
                    logRawEvent(itemName, eventType)
                }
            }
        }
    }

    init {
        if (mFirebaseAnalytics == null)
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }
}