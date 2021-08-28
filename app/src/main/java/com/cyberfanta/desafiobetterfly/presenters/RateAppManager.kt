package com.cyberfanta.desafiobetterfly.presenters

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.cyberfanta.desafiobetterfly.R
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

class RateAppManager (activity: Activity) {
    init {
        if (reviewManager == null)
            reviewManager = ReviewManagerFactory.create(activity)
    }

    companion object {
        @Suppress("PrivatePropertyName", "unused")
        private val TAG = this::class.java.simpleName

        var reviewManager : ReviewManager? = null

        fun requestReview (activity: Activity) {
            FirebaseManager.logEvent("Menu: Rate App - Open", "Rate_Menu")
            val request = reviewManager?.requestReviewFlow()
            request?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    reviewManager?.launchReviewFlow(activity, reviewInfo)
                        ?.addOnFailureListener {
                            FirebaseManager.logEvent("Menu: Rate App - Fail", "Rate_Menu")
                            Toast.makeText(activity, R.string.item_rate_fail, Toast.LENGTH_SHORT).show()
                            Log.i(TAG, "Rate App Result: " + task.result.toString())
                        }
                        ?.addOnSuccessListener {
                            FirebaseManager.logEvent("Menu: Rate App - Success", "Rate_Menu")
                            Toast.makeText(activity, R.string.item_rate_success, Toast.LENGTH_SHORT).show()
                            Log.i(TAG, "Rate App Result: " + task.result.toString())
                        }
//                        ?.addOnCompleteListener {  }
                } else {
                    FirebaseManager.logEvent("Menu: Rate App - Exception - " + task.exception.toString(), "Rate_Menu")
                    Toast.makeText(activity, R.string.item_rate_fail, Toast.LENGTH_SHORT).show()
                    Log.i(TAG, "Rate App Result: " + task.exception.toString())

                }
            }
        }
    }
}