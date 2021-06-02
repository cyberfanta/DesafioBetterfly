package com.cyberfanta.desafiobetterfly.presenters

import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import com.cyberfanta.desafiobetterfly.models.character.*
import com.cyberfanta.desafiobetterfly.models.episode.*
import com.cyberfanta.desafiobetterfly.models.location.*

class QueryManager {
    private val url = arrayOf(
        "https://rickandmortyapi.com/api/character/",
        "https://rickandmortyapi.com/api/location/",
        "https://rickandmortyapi.com/api/episode/"
    )
    private val modelFromConnection = ModelFromConnection()

    private val characterPageList = LinkedHashMap<Int, Character>(0)
    private val characterFilterPageList = LinkedHashMap<String, CharacterFilter>(0)
    private val characterDetailList = LinkedHashMap<Int, CharacterDetail>(0)

    private val locationPageList = LinkedHashMap<Int, Location>(0)
    private val locationFilterPageList = LinkedHashMap<String, LocationFilter>(0)
    private val locationDetailList = LinkedHashMap<Int, LocationDetail>(0)

    private val episodePageList = LinkedHashMap<Int, Episode>(0)
    private val episodeFilterPageList = LinkedHashMap<String, EpisodeFilter>(0)
    private val episodeDetailList = LinkedHashMap<Int, EpisodeDetail>(0)

    //-- Character
    @Throws(ConnectionException::class)
    fun getCharacterPage(page: Int): Character? {
        if (!characterPageList.containsKey(page))
            characterPageList[page] = modelFromConnection.getObject(Character::class.java, url[0] + "?page=" + page)

        return characterPageList[page]
    }

    @Throws(ConnectionException::class)
    fun getCharacterFilterPage(filters: Map<String, String>, page: Int): CharacterFilter? {
        var filterList = "?"
        for (filter in filters)
            filterList += filter.key + "=" + filter.value + "&"
        val filter = "$filterList?page=$page"

        if (!characterFilterPageList.containsKey(filter))
            characterFilterPageList[filter] = modelFromConnection.getObject(CharacterFilter::class.java, url[0] + filter)

        return characterFilterPageList[filter]
    }

    @Throws(ConnectionException::class)
    fun getCharacterDetail(id: Int): CharacterDetail? {
        if (!characterDetailList.containsKey(id))
            characterDetailList[id] = modelFromConnection.getObject(CharacterDetail::class.java, url[0] + id)

        return characterDetailList[id]
    }

    //-- Location
    @Throws(ConnectionException::class)
    fun getLocationPage(page: Int): Location? {
        if (!locationPageList.containsKey(page))
            locationPageList[page] = modelFromConnection.getObject(Location::class.java, url[1] + "?page=" + page)

        return locationPageList[page]
    }

    @Throws(ConnectionException::class)
    fun getLocationFilterPage(filters: Map<String, String>, page: Int): LocationFilter? {
        var filterList = "?"
        for (filter in filters)
            filterList += filter.key + "=" + filter.value + "&"
        val filter = "$filterList?page=$page"

        if (!locationFilterPageList.containsKey(filter))
            locationFilterPageList[filter] = modelFromConnection.getObject(LocationFilter::class.java, url[1] + filter)

        return locationFilterPageList[filter]
    }

    @Throws(ConnectionException::class)
    fun getLocationDetail(id: Int): LocationDetail? {
        if (!locationDetailList.containsKey(id))
            locationDetailList[id] = modelFromConnection.getObject(LocationDetail::class.java, url[1] + id)

        return locationDetailList[id]
    }

    //-- Episode
    @Throws(ConnectionException::class)
    fun getEpisodePage(page: Int): Episode? {
        if (!episodePageList.containsKey(page))
            episodePageList[page] = modelFromConnection.getObject(Episode::class.java, url[2] + "?page=" + page)

        return episodePageList[page]
    }

    @Throws(ConnectionException::class)
    fun getEpisodeFilterPage(filters: Map<String, String>, page: Int): EpisodeFilter? {
        var filterList = "?"
        for (filter in filters)
            filterList += filter.key + "=" + filter.value + "&"
        val filter = "$filterList?page=$page"

        if (!episodeFilterPageList.containsKey(filter))
            episodeFilterPageList[filter] = modelFromConnection.getObject(EpisodeFilter::class.java, url[2] + filter)

        return episodeFilterPageList[filter]
    }

    @Throws(ConnectionException::class)
    fun getEpisodeDetail(id: Int): EpisodeDetail? {
        if (!episodeDetailList.containsKey(id))
            episodeDetailList[id] = modelFromConnection.getObject(EpisodeDetail::class.java, url[2] + id)

        return episodeDetailList[id]
    }

}