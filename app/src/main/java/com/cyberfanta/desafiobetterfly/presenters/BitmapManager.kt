package com.cyberfanta.desafiobetterfly.presenters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.net.URL
import java.util.*

class BitmapManager {
    @Suppress("PrivatePropertyName", "unused")
    private val TAG = this::class.java.simpleName

    private val bitmapList = LinkedHashMap<String, Bitmap>(0)

    @Synchronized
    @Throws(IOException::class)
    fun loadBitmap(url: String) {
        if (!bitmapList.containsKey(url)) {
            val bitmapUrl = URL(url)
            val bitmap = BitmapFactory.decodeStream(bitmapUrl.openConnection().getInputStream())
            if (bitmap != null)
                bitmapList[url] = bitmap
        }
    }

    fun getBitmap(name: String): Bitmap {
        if (!bitmapList.containsKey(name))
            loadBitmap(name)
        return bitmapList[name]!!
    }
}