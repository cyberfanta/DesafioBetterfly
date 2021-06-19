package com.cyberfanta.desafiobetterfly.presenters

import android.util.Log
import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import com.cyberfanta.desafiobetterfly.models.character.*
import com.cyberfanta.desafiobetterfly.models.episode.*
import com.cyberfanta.desafiobetterfly.models.location.*
import junit.framework.TestCase
import org.junit.Assert

class QueryManagerTest : TestCase() {
    @Suppress("PrivatePropertyName", "unused")
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
                queryManager.getCharacterPage(url[0].toInt())::class.java,
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
                queryManager.getCharacterFilterPage(mapOf(url[1] to url[2]), url[3].toInt())::class.java,
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
                queryManager.getCharacterDetail(url[4].toInt())::class.java,
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
                queryManager.getLocationPage(url[0].toInt())::class.java,
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
                queryManager.getLocationFilterPage(mapOf(url[1] to url[2]), url[3].toInt())::class.java,
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
                queryManager.getLocationDetail(url[4].toInt())::class.java,
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
                queryManager.getEpisodePage(url[0].toInt())::class.java,
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
                queryManager.getEpisodeFilterPage(mapOf(url[1] to url[2]), url[3].toInt())::class.java,
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
                queryManager.getEpisodeDetail(url[4].toInt())::class.java,
                EpisodeDetail::class.java
            )
        } catch (e: ConnectionException) {
            Log.i(TAG, e.toString())
            Assert.fail()
        }
    }

    fun test(){
        val string =
                "    <string name=\"hint_sleep_timer_duration\">Stop playback after</string>\n" +
                "    <plurals name=\"duration_minutes\">\n" +
                "        <item quantity=\"one\">minute</item>\n" +
                "        <item quantity=\"other\">minutes</item>\n" +
                "    </plurals>\n" +
                "    <string name=\"defaultContentDescription\" translatable=\"false\">defaultContentDescription</string>\n" +
                "    <string name=\"error_invalid_duration\">Invalid duration. Must be an integer &gt; 0</string>\n" +
                "    <string name=\"repeating_sleep_timer\">Repeat sleep timer</string>\n" +
                "    <string name=\"repeating_sleep_timer_desc\">Starts the sleep timer automatically for the next playback session</string>\n" +
                "    <!-- Contributors -->\n" +
                "    <string name=\"lead_developer\">Lead developers</string>\n" +
                "    <string name=\"github\">Github</string>\n"

        val test1 = string.split("\n")

        val test2a = test1[1].split("<")
        val test2b = test2a[1].split(">")
//        Assert.assertEquals("", "---" + test2a[0] + "---" + test2b[0] + "---" + test2b[1] + "/---")
        Assert.assertEquals(2, test2a.size)
        Assert.assertEquals(2, test2b.size)

        val test3a = test1[5].split("<")
        val test3b = test3a[1].split(">")
        val test3c = test3a[2].split(">")
//        Assert.assertEquals(3, test3a.size)
//        Assert.assertEquals(2, test3b.size)
//        Assert.assertEquals(2, test3c.size)
        val test3d = test3b[0].split(" ")
        val test3e = test3d[1].split("=")
        val test3f = test3e[1].split("\"")
        val test3g = test3d[2].split("=")
        val test3h = test3g[1].split("\"")

//        Assert.assertEquals("", "---" + test3a[0] + "---" + test3a[1] + "---" + test3a[2] + "/---")
        Assert.assertEquals("",
            "---" + test3a[0] + "/---"
                    + test3d[0] + "/---"
                    + test3e[0] + "/---"
                    + test3f[1] + "/---"
                    + test3g[0] + "/---"
                    + test3h[1] + "/---"
                    + test3b[1] + "/---"
                    + test3c[0] + "/---")
    }
}