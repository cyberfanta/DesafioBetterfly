package com.cyberfanta.desafiobetterfly.presenters

import android.util.Log
import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import com.cyberfanta.desafiobetterfly.models.character.*
import com.cyberfanta.desafiobetterfly.models.episode.*
import com.cyberfanta.desafiobetterfly.models.location.*
import junit.framework.TestCase
import org.junit.Assert

class ModelFromConnectionTest : TestCase() {
    @Suppress("PrivatePropertyName")
    private val TAG = this::class.java.simpleName

    private var modelFromConnection = ModelFromConnection()
    private lateinit var url: Array<String>

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
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
            Assert.assertSame(
                modelFromConnection.getObject(Character::class.java, url[0])::class.java,
                Character::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }

        try {
            Assert.assertSame(
                modelFromConnection.getObject(CharacterDetail::class.java, url[1])::class.java,
                CharacterDetail::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }

        try {
            Assert.assertSame(
                modelFromConnection.getObject(CharacterFilter::class.java, url[2])::class.java,
                CharacterFilter::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }

//        try {
//            Assert.assertSame(
//                modelFromConnection!!.getObject(CharacterMultiple::class.java, url[3])::class.java,
//                CharacterMultiple::class.java
//            )
//        } catch (e: ConnectionException) {
//            Log.i(TAG, e.toString())
//            Assert.fail()
//        }

        try {
            Assert.assertSame(
                modelFromConnection!!.getObject(Character::class.java, url[4])::class.java,
                Character::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

    fun testGetLocationOk() {
        try {
            Assert.assertSame(
                modelFromConnection!!.getObject(Location::class.java, url[5])::class.java,
                Location::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }

        try {
            Assert.assertSame(
                modelFromConnection!!.getObject(LocationDetail::class.java, url[6])::class.java,
                LocationDetail::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }

        try {
            Assert.assertSame(
                modelFromConnection!!.getObject(LocationFilter::class.java, url[7])::class.java,
                LocationFilter::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }

//        try {
//            Assert.assertSame(
//                modelFromConnection!!.getObject(LocationMultiple::class.java, url[8])::class.java,
//                LocationMultiple::class.java
//            )
//        } catch (e: ConnectionException) {
//            Log.i(TAG, e.toString())
//            Assert.fail()
//        }

        try {
            Assert.assertSame(
                modelFromConnection!!.getObject(Location::class.java, url[9])::class.java,
                Location::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

    fun testGetEpisodeOk() {
        try {
            Assert.assertSame(
                modelFromConnection!!.getObject(Episode::class.java, url[10])::class.java,
                Episode::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }

        try {
            Assert.assertSame(
                modelFromConnection!!.getObject(EpisodeDetail::class.java, url[11])::class.java,
                EpisodeDetail::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }

        try {
            Assert.assertSame(
                modelFromConnection!!.getObject(EpisodeFilter::class.java, url[12])::class.java,
                EpisodeFilter::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }

//        try {
//            Assert.assertSame(
//                modelFromConnection!!.getObject(EpisodeMultiple::class.java, url[13])::class.java,
//                EpisodeMultiple::class.java
//            )
//        } catch (e: ConnectionException) {
//            Log.i(TAG, e.toString())
//            Assert.fail()
//        }

        try {
            Assert.assertSame(
                modelFromConnection!!.getObject(Episode::class.java, url[14])::class.java,
                Episode::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }
}