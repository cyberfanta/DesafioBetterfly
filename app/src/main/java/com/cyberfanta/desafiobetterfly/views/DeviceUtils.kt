package com.cyberfanta.desafiobetterfly.views

import android.content.Context

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
    }
}