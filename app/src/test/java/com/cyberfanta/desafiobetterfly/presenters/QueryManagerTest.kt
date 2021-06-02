package com.cyberfanta.desafiobetterfly.presenters

import android.util.Log
import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import com.cyberfanta.desafiobetterfly.models.character.*
import com.cyberfanta.desafiobetterfly.models.episode.*
import com.cyberfanta.desafiobetterfly.models.location.*
import junit.framework.TestCase
import org.junit.Assert

class QueryManagerTest : TestCase() {
    @Suppress("PrivatePropertyName")
    private val TAG = this::class.java.simpleName

    private var queryManager = QueryManager()
    private lateinit var url: Array<String>

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        url = arrayOf(
            "1",
            "status",
            "dead",
            "1",
            "1",
            "1",
            "type",
            "Dimension",
            "1",
            "1",
            "1",
            "episode",
            "S03E07",
            "1",
            "1",
        )
    }

    //-- Character
    fun testGetCharacterPage() {
        try {
            Assert.assertSame(
                queryManager.getCharacterPage(url[0].toInt())!!::class.java,
                Character::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

    fun testGetCharacterFilterPage() {
        try {
            Assert.assertSame(
                queryManager.getCharacterFilterPage(mapOf(url[1] to url[2]), url[3].toInt())!!::class.java,
                CharacterFilter::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

    fun testGetCharacterDetail() {
        try {
            Assert.assertSame(
                queryManager.getCharacterDetail(url[4].toInt())!!::class.java,
                CharacterDetail::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

    //-- Location
    fun testGetLocationPage() {
        try {
            Assert.assertSame(
                queryManager.getLocationPage(url[0].toInt())!!::class.java,
                Location::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

    fun testGetLocationFilterPage() {
        try {
            Assert.assertSame(
                queryManager.getLocationFilterPage(mapOf(url[1] to url[2]), url[3].toInt())!!::class.java,
                LocationFilter::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

    fun testGetLocationDetail() {
        try {
            Assert.assertSame(
                queryManager.getLocationDetail(url[4].toInt())!!::class.java,
                LocationDetail::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

    //-- Episode
    fun testGetEpisodePage() {
        try {
            Assert.assertSame(
                queryManager.getEpisodePage(url[0].toInt())!!::class.java,
                Episode::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

    fun testGetEpisodeFilterPage() {
        try {
            Assert.assertSame(
                queryManager.getEpisodeFilterPage(mapOf(url[1] to url[2]), url[3].toInt())!!::class.java,
                EpisodeFilter::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

    fun testGetEpisodeDetail() {
        try {
            Assert.assertSame(
                queryManager.getEpisodeDetail(url[4].toInt())!!::class.java,
                EpisodeDetail::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

}