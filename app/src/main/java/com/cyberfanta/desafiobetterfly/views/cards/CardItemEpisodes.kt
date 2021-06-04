package com.cyberfanta.desafiobetterfly.views.cards

import com.cyberfanta.desafiobetterfly.models.episode.EpisodeDetail

class CardItemEpisodes(episodeDetail: EpisodeDetail) {
    var name: String? = null
    var id: String? = null

    init {
        name = episodeDetail.name
        id = episodeDetail.id.toString()
    }
}