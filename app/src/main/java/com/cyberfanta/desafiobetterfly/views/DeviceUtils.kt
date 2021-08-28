package com.cyberfanta.desafiobetterfly.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Insets
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.WindowMetrics
import android.widget.Toast

@Suppress("unused")
class DeviceUtils {
    companion object {
        /**
         * This method converts dp unit to equivalent pixels, depending on device density.
         *
         * @param context Context to get resources and device specific display metrics
         * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
         * @return A int value to represent px equivalent to dp depending on device density
         */
        fun convertDpToPx(context: Context, dp: Float): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }

        /**
         * This method converts device specific pixels to density independent pixels.
         *
         * @param context Context to get resources and device specific display metrics
         * @param px      A value in px (pixels) unit. Which we need to convert into db
         * @return A int value to represent dp equivalent to px value
         */
        fun convertPxToDp(context: Context, px: Float): Int {
            return (px / context.resources.displayMetrics.density).toInt()
        }

        /**
         * Make a simple animation on a view
         *
         * @param view View to be animated
         * @param propertyName String the exactly name of the feature to be animated
         * @param duration Long animation duration
         * @param repeat Boolean infinite repeat
         * @param value1 Float initial value
         * @param value2 Float ending value
         */
        @Suppress("SameParameterValue")
        fun setAnimation(
            view: View,
            propertyName: String,
            duration: Long,
            repeat: Boolean,
            value1: Float,
            value2: Float
        ) {
            val objectAnimator = ObjectAnimator.ofFloat(view, propertyName, value1, value2)
            val animator = AnimatorSet()
            animator.play(objectAnimator)
            animator.duration = duration
            if (repeat) {
                animator.addListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            animator.start()
                        }
                    }
                )
            }
            animator.start()
        }

        /**
         * Calculate the device dimension
         *
         * @param activity Activity to get the device dimension
         * @return A IntArray where position [0] is the width and [1] is the height
         */
        fun calculateDeviceDimensions(activity: Activity): IntArray {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
                val insets: Insets = windowMetrics.windowInsets
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                deviceSize[0] = windowMetrics.bounds.width() - insets.left - insets.right
                deviceSize[1] = windowMetrics.bounds.height() - insets.top - insets.bottom
            } else {
                val displayMetrics = DisplayMetrics()
                @Suppress("DEPRECATION")
                activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                deviceSize[0] = displayMetrics.widthPixels
                deviceSize[1] = displayMetrics.heightPixels
            }
            return deviceSize
        }

        //Current Device Size
        private var deviceSize = intArrayOf(0, 0)

        /**
         * Get curent Device Width
         *
         * @return An Int
         */
        fun getDeviceWidth(): Int {
            return deviceSize[0]
        }

        /**
         * Get curent Device Height
         *
         * @return An Int
         */
        fun getDeviceHeight(): Int {
            return deviceSize[1]
        }

        /**
         * Get curent Device Size
         *
         * @return A IntArray where position [0] is the width and [1] is the height
         */
        fun getDeviceDimensions(): IntArray {
            return deviceSize
        }

        /**
         * Open URL on intent
         *
         * @param activity Activity that make the call
         * @param url String url to be opened
         */
        fun openURL(activity: Activity, url: String) {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            activity.startActivity(intent)
        }

        /**
         * Send Email to author via intent
         *
         * @param activity Activity that make the call
         * @param email String email taeget
         * @param subject String email subject
         * @param body String email body
         * @param chooserMessage String kind of chooser for this message
         */
        @Suppress("SameParameterValue")
        fun sendAuthorEmail(
            activity: Activity,
            email: String,
            subject: String,
            body: String,
            chooserMessage: String
        ) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.data = Uri.parse("mailto:")
            intent.type = "plain/text"
            intent.putExtra(Intent.EXTRA_TEXT, body)
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            activity.startActivity(Intent.createChooser(intent, chooserMessage))
        }

        /**
         * Get a random number between 2 numbers
         *
         * @param starting Int lower bound
         * @param ending Int upper bound
         * @return A IntArray where position [0] is the width and [1] is the height
         */
        fun getRandomNumber(starting: Int, ending: Int): Int {
            val list = (starting..ending).filter { it % 2 == 0 }
            return list.random()
        }

        /**
         * Show toast
         *
         * @param context Context for the toast
         * @param message String for the toast
         */
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}