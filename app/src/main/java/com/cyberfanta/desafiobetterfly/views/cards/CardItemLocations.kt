package com.cyberfanta.desafiobetterfly.views.cards

import com.cyberfanta.desafiobetterfly.models.location.LocationDetail

class CardItemLocations(locationDetail: LocationDetail) {
    var name: String? = null
    var id: String? = null

    init {
        name = locationDetail.name
        id = locationDetail.id.toString()
    }
}