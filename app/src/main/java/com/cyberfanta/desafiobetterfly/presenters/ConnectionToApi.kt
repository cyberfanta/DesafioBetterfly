package com.cyberfanta.desafiobetterfly.presenters

import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class ConnectionToApi {
    @Suppress("PrivatePropertyName", "unused")
    private val TAG = this::class.java.simpleName

    // Objects to read data from server
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Throws(ConnectionException::class)
    fun getJson(url: String): String {
        return loadJson(Request.Builder().url(url).get().header("User-Agent", "").header("Accept", "application/json").build())
    }

    @Throws(ConnectionException::class)
    fun postJson(url: String, size: Int, offset: Int): String {
        val json: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = "".toRequestBody(json)
        return loadJson(
            Request.Builder()
                .url("$url?size=$size&offset=$offset")
                .post(requestBody)
                .build()
        )
    }

    @Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
    @Throws(ConnectionException::class)
    private fun loadJson(request: Request): String {
        try {
            val response = client.newCall(request).execute()

            val answer = response.body?.string().toString()
            response.body?.close()

            if (response.code != 200)
                throw ConnectionException("ERROR_WITH_RESPONSE")

            response.close()
            return answer
        } catch (e: Exception) {
            throw ConnectionException("CONNECTION_FAIL")
        }
    }

}