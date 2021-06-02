package com.cyberfanta.desafiobetterfly.presenters

import android.util.Log
import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import junit.framework.TestCase
import org.junit.Assert

class ConnectionToApiTest : TestCase() {
    @Suppress("PrivatePropertyName")
    private val TAG = this::class.java.simpleName

    private var connectionToApi: ConnectionToApi? = null
    private lateinit var url: Array<String>

    fun testGetJson() {}
    fun testPostJson() {}

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        connectionToApi = ConnectionToApi()
        url = arrayOf(
            "https://rickandmortyapi.com/api/character",
            "https://rickandmortyapi.com/api/character/19",
            "https://rickandmortyapi.com/api/character/?name=rick&status=alive",
            "https://rickandmortyapi.com/api/character/1,183",
            "https://rickandmortyapi.com/api/character/?page=19",

            "https://rickandmortyapi.com/api/location",
            "https://rickandmortyapi.com/api/location/3",
            "https://rickandmortyapi.com/api/location/?dimension=unknown",
            "https://rickandmortyapi.com/api/location/3,21",
            "https://rickandmortyapi.com/api/location/?page=2",

            "https://rickandmortyapi.com/api/episode",
            "https://rickandmortyapi.com/api/episode/28",
            "https://rickandmortyapi.com/api/episode/?episode=S03E07",
            "https://rickandmortyapi.com/api/episode/10,28",
            "https://rickandmortyapi.com/api/episode/?page=2"
        )
    }

    fun testGetCharacterOk() {
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[0]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[1]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[2]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[3]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[4]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

    fun testGetLocationOk() {
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[5]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[6]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[7]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[8]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[9]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

    fun testGetEpisodeOk() {
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[10]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[11]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[12]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[13]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
        try {
            Assert.assertNotNull(connectionToApi!!.getJson(url[14]))
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }
}