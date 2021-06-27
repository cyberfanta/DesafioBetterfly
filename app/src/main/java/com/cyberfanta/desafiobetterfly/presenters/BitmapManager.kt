package com.cyberfanta.desafiobetterfly.presenters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import java.net.URL
import java.util.*

class BitmapManager {
    @Suppress("PrivatePropertyName", "unused")
    private val TAG = this::class.java.simpleName

    companion object {
        private val bitmapList = LinkedHashMap<String, Bitmap>(0)
    }

    @Synchronized
    @Throws(ConnectionException::class)
    private fun loadBitmap(url: String) {
        try {
            if (!bitmapList.containsKey(url)) {
                val bitmapUrl = URL(url)
                val bitmap = BitmapFactory.decodeStream(bitmapUrl.openConnection().getInputStream())
                if (bitmap != null)
                    bitmapList[url] = bitmap
            }
            Log.i(TAG, "Bitmap Loaded: $url")
        } catch (e: Exception) {
            throw ConnectionException("CONNECTION_FAIL")
        }
    }

    @Throws(ConnectionException::class)
    fun getBitmap(name: String): Bitmap {
        if (!bitmapList.containsKey(name))
            loadBitmap(name)
        return bitmapList[name]!!
    }
}