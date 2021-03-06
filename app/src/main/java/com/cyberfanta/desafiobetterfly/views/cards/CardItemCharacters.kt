package com.cyberfanta.desafiobetterfly.views.cards

import android.graphics.Bitmap
import com.cyberfanta.desafiobetterfly.models.character.CharacterDetail

class CardItemCharacters(characterDetail: CharacterDetail) {
    var image: Bitmap? = null
    var name: String? = null
    var id: String? = null

    init {
        name = characterDetail.name
        id = characterDetail.id.toString()
    }
}