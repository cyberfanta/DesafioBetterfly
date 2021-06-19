package com.cyberfanta.desafiobetterfly.presenters

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.cyberfanta.desafiobetterfly.R
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

class RateAppManager (context: Context) {
    companion object {
        @Suppress("PrivatePropertyName", "unused")
        private val TAG = this::class.java.simpleName

        var reviewManager : ReviewManager? = null

        fun requestReview (context: Context) {
            val request = reviewManager?.requestReviewFlow()
            request?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseManager.logEvent("Menu: Rate App - Success", "Open_Menu")
                    Toast.makeText(
                        context,
                        R.string.item_rate_success,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i(TAG, "Rate App Result: " + task.result.toString())
                } else {
                    FirebaseManager.logEvent("Menu: Rate App - Fail", "Open_Menu")
                    Toast.makeText(context, R.string.item_rate_fail, Toast.LENGTH_SHORT)
                        .show()
                    Log.i(TAG, "Rate App Result: " + task.exception.toString())
                }
            }
        }

    }
    init {
        if (reviewManager == null)
            reviewManager = ReviewManagerFactory.create(context)
    }
}