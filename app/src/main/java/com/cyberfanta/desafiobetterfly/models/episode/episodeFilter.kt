package com.cyberfanta.desafiobetterfly.models.episode

import com.google.gson.annotations.SerializedName

data class ResultsItem9(

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

data class EpisodeFilter(

	@field:SerializedName("results")
	val results: List<EpisodeDetail?>? = null,

	@field:SerializedName("info")
	val info: Info9? = null
)

data class Info9(

	@field:SerializedName("next")
	val next: String? = null,

	@field:SerializedName("pages")
	val pages: Int? = null,

	@field:SerializedName("prev")
	val prev: String? = null,

	@field:SerializedName("count")
	val count: Int? = null
)
