package com.cyberfanta.desafiobetterfly.presenters

import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import com.google.gson.Gson

class ModelFromConnection {
    @Suppress("PrivatePropertyName", "unused")
    private val TAG = this::class.java.simpleName

    private val connectionToApi = ConnectionToApi()
    private val gson = Gson()

    @Throws(ConnectionException::class)
    fun <T> getObject(type: Class<T>?, url: String?): T {
        val string = connectionToApi.getJson(url!!)
        return gson.fromJson(string, type)
    }

    @Suppress("unused")
    @Throws(ConnectionException::class)
    fun <T> postObject(type: Class<T>?, url: String?, size: Int, offset: Int): T {
        val string = connectionToApi.postJson(url!!, size, offset)
        return gson.fromJson(string, type)
    }

}