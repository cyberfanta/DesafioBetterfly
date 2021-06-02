package com.cyberfanta.desafiobetterfly.presenters

import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import com.cyberfanta.desafiobetterfly.models.character.Character
import com.cyberfanta.desafiobetterfly.models.episode.Episode
import com.cyberfanta.desafiobetterfly.models.location.Location
import java.io.IOException

class QueryManager {
    private val url = arrayOf(
        "https://rickandmortyapi.com/api/character",
        "https://rickandmortyapi.com/api/location",
        "https://rickandmortyapi.com/api/episode"
    )
    private val modelFromConnection = ModelFromConnection()

    private val characterList: Map<Int, Character> = LinkedHashMap(0)
    private val locationList: Map<String, Location> = LinkedHashMap(0)
    private val episodeList: Map<String, Episode> = LinkedHashMap(0)

    //-- Character
    @Throws(ConnectionException::class)
    fun getCharacterPage(name: String): Character? {
        if (!characterList.containsKey(name)){
            val character: Character = modelFromConnection.getObject(Character::class.java, url[0] + name)
            for (i in 0..19) {
                val intValue = character.results?.get(i)?.id
                characterList[intValue].copy() [intValue] =  character
            }
        }

        return characterList.get(name)
    }

}