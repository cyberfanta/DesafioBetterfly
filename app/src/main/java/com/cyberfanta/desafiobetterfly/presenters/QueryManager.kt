package com.cyberfanta.desafiobetterfly.presenters

import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import com.cyberfanta.desafiobetterfly.models.character.Character
import com.cyberfanta.desafiobetterfly.models.character.CharacterDetail
import com.cyberfanta.desafiobetterfly.models.character.CharacterFilter
import com.cyberfanta.desafiobetterfly.models.episode.Episode
import com.cyberfanta.desafiobetterfly.models.episode.EpisodeDetail
import com.cyberfanta.desafiobetterfly.models.episode.EpisodeFilter
import com.cyberfanta.desafiobetterfly.models.location.Location
import com.cyberfanta.desafiobetterfly.models.location.LocationDetail
import com.cyberfanta.desafiobetterfly.models.location.LocationFilter
import com.cyberfanta.desafiobetterfly.views.BitmapMessage
import java.util.*
import kotlin.collections.LinkedHashMap

class QueryManager {
    @Suppress("PrivatePropertyName", "unused")
    private val TAG = this::class.java.simpleName

    private val url = arrayOf(
        "https://rickandmortyapi.com/api/character/",
        "https://rickandmortyapi.com/api/location/",
        "https://rickandmortyapi.com/api/episode/"
    )
    private val modelFromConnection = ModelFromConnection()
    private val bitmapManager = BitmapManager()

    companion object {
        private val characterPageList : Queue<Int> = LinkedList()
        private val characterFilterPageList = LinkedHashMap<String, CharacterFilter>(0)
        private val characterDetailList = LinkedHashMap<Int, CharacterDetail>(0)

        private val locationPageList : Queue<Int> = LinkedList()
        private val locationFilterPageList = LinkedHashMap<String, LocationFilter>(0)
        private val locationDetailList = LinkedHashMap<Int, LocationDetail>(0)

        private val episodePageList : Queue<Int> = LinkedList()
        private val episodeFilterPageList = LinkedHashMap<String, EpisodeFilter>(0)
        private val episodeDetailList = LinkedHashMap<Int, EpisodeDetail>(0)
    }

    //-- Character
    @Throws(ConnectionException::class)
    fun getCharacterPage(page: Int): Character {
        val characterPage : Character
        if (!characterPageList.contains(page)) {
            characterPage = modelFromConnection.getObject(Character::class.java, url[0] + "?page=" + page)

            for (characterDetail in characterPage.results!!)
                characterDetailList[characterDetail?.id!!] = characterDetail

            characterPageList.add(page)
        } else {
            val list = mutableListOf<CharacterDetail>()
            try {
                for (i in (1+((page-1)*20))..(20+((page-1)*20)))
                    if (characterDetailList.containsKey(i))
                        list.add(characterDetailList[i]!!)
            } catch (ignored : IndexOutOfBoundsException) {}
            characterPage = Character(list, null)
        }

        return characterPage
    }

    @Synchronized
    fun getCharacterAvatar(id: Int): BitmapMessage {
        if (!characterDetailList.containsKey(id))
            getCharacterDetail(id)

        return BitmapMessage(id, characterDetailList[id]?.image?.let { bitmapManager.getBitmap(it) })
    }

    @Throws(ConnectionException::class)
    fun getCharacterFilterPage(filters: Map<String, String>, page: Int): CharacterFilter {
        var filterList = "?"
        for (filter in filters)
            filterList += filter.key + "=" + filter.value + "&"
        val filter = "$filterList?page=$page"
        val characterPage : CharacterFilter

        if (!characterFilterPageList.containsKey(filter)) {
            characterPage = modelFromConnection.getObject(CharacterFilter::class.java, url[0] + filter)

            for (characterDetail in characterPage.results!!)
                if (!characterDetailList.containsKey(characterDetail?.id))
                    characterDetailList[characterDetail?.id!!] = characterDetail

            characterFilterPageList[filter] = characterPage
        } else {
            val list = mutableListOf<CharacterDetail>()
            try {
                for (i in (1+((page-1)*20))..(20+((page-1)*20)))
                    list.add(characterDetailList[i]!!)
            } catch (ignored : IndexOutOfBoundsException) {}
            characterPage = CharacterFilter(list, null)
        }

        return characterPage
    }

    @Throws(ConnectionException::class)
    fun getCharacterDetail(id: Int): CharacterDetail {
        if (!characterDetailList.containsKey(id))
            characterDetailList[id] = modelFromConnection.getObject(CharacterDetail::class.java, url[0] + id)

        return characterDetailList[id]!!
    }

    //-- Location
    @Throws(ConnectionException::class)
    fun getLocationPage(page: Int): Location {
        val locationPage : Location
        if (!locationPageList.contains(page)) {
            locationPage = modelFromConnection.getObject(Location::class.java, url[1] + "?page=" + page)

            for (locationDetail in locationPage.results!!)
                locationDetailList[locationDetail?.id!!] = locationDetail

            locationPageList.add(page)
        } else {
            val list = mutableListOf<LocationDetail>()
            try {
                for (i in (1+((page-1)*20))..(20+((page-1)*20)))
                    if (locationDetailList.containsKey(i))
                        list.add(locationDetailList[i]!!)
            } catch (ignored : IndexOutOfBoundsException) {}
            locationPage = Location(list, null)
        }

        return locationPage
    }

    @Throws(ConnectionException::class)
    fun getLocationFilterPage(filters: Map<String, String>, page: Int): LocationFilter {
        var filterList = "?"
        for (filter in filters)
            filterList += filter.key + "=" + filter.value + "&"
        val filter = "$filterList?page=$page"
        val locationPage : LocationFilter

        if (!locationFilterPageList.containsKey(filter)) {
            locationPage = modelFromConnection.getObject(LocationFilter::class.java, url[1] + filter)

            for (locationDetail in locationPage.results!!)
                locationDetailList[locationDetail?.id!!] = locationDetail

            locationFilterPageList[filter] = locationPage
        } else {
            val list = mutableListOf<LocationDetail>()
            try {
                for (i in (1+((page-1)*20))..(20+((page-1)*20)))
                    list.add(locationDetailList[i]!!)
            } catch (ignored : IndexOutOfBoundsException) {}
            locationPage = LocationFilter(list, null)
        }

        return locationPage
    }

    @Throws(ConnectionException::class)
    fun getLocationDetail(id: Int): LocationDetail {
        if (!locationDetailList.containsKey(id))
            locationDetailList[id] = modelFromConnection.getObject(LocationDetail::class.java, url[1] + id)

        return locationDetailList[id]!!
    }

    //-- Episode
    @Throws(ConnectionException::class)
    fun getEpisodePage(page: Int): Episode {
        val episodePage : Episode
        if (!episodePageList.contains(page)) {
            episodePage = modelFromConnection.getObject(Episode::class.java, url[2] + "?page=" + page)

            for (episodeDetail in episodePage.results!!)
                episodeDetailList[episodeDetail?.id!!] = episodeDetail

            episodePageList.add(page)
        } else {
            val list = mutableListOf<EpisodeDetail>()
            try {
                for (i in (1+((page-1)*20))..(20+((page-1)*20)))
                    if (episodeDetailList.containsKey(i))
                        list.add(episodeDetailList[i]!!)
            } catch (ignored : IndexOutOfBoundsException) {}
            episodePage = Episode(list, null)
        }

        return episodePage
    }

    @Throws(ConnectionException::class)
    fun getEpisodeFilterPage(filters: Map<String, String>, page: Int): EpisodeFilter {
        var filterList = "?"
        for (filter in filters)
            filterList += filter.key + "=" + filter.value + "&"
        val filter = "$filterList?page=$page"
        val episodePage : EpisodeFilter

        if (!episodeFilterPageList.containsKey(filter)) {
            episodePage = modelFromConnection.getObject(EpisodeFilter::class.java, url[2] + filter)

            for (episodeDetail in episodePage.results!!)
                episodeDetailList[episodeDetail?.id!!] = episodeDetail

            episodeFilterPageList[filter] = episodePage
        } else {
            val list = mutableListOf<EpisodeDetail>()
            try {
                for (i in (1+((page-1)*20))..(20+((page-1)*20)))
                    list.add(episodeDetailList[i]!!)
            } catch (ignored : IndexOutOfBoundsException) {}
            episodePage = EpisodeFilter(list, null)
        }

        return episodePage
    }

    @Throws(ConnectionException::class)
    fun getEpisodeDetail(id: Int): EpisodeDetail {
        if (!episodeDetailList.containsKey(id))
            episodeDetailList[id] = modelFromConnection.getObject(EpisodeDetail::class.java, url[2] + id)

        return episodeDetailList[id]!!
    }

}