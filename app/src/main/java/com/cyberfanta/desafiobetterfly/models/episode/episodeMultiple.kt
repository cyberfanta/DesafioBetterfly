package com.cyberfanta.desafiobetterfly.models.episode

import com.google.gson.annotations.SerializedName

data class EpisodeMultipleItem(

	@field:SerializedName("air_date")
	val airDate: String? = null,

	@field:SerializedName("characters")
	val characters: List<String?>? = null,

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("episode")
	val episode: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class EpisodeMultiple(

	@field:SerializedName("episodeMultiple")
	val episodeMultiple: List<EpisodeMultipleItem?>? = null
)
