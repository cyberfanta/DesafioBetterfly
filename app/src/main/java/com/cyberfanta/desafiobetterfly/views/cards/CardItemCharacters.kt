package com.cyberfanta.desafiobetterfly.views.cards

import android.graphics.Bitmap
import com.cyberfanta.desafiobetterfly.models.character.CharacterDetail
import com.cyberfanta.desafiobetterfly.presenters.BitmapManager

//class CardItemCharacters(characterDetail: CharacterDetail, bitmapManager: BitmapManager) {
class CardItemCharacters(characterDetail: CharacterDetail) {
    var image: Bitmap? = null
    var name: String? = null
    var id: String? = null

    init {
//        image = name?.let { bitmapManager.getBitmap(it) }
        name = characterDetail.name
        id = characterDetail.id.toString()
    }
}