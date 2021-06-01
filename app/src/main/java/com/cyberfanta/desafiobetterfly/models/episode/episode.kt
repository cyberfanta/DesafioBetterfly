package com.cyberfanta.desafiobetterfly.models.episode

import com.google.gson.annotations.SerializedName

data class Episode(

	@field:SerializedName("results")
	val results: List<ResultsItem8?>? = null,

	@field:SerializedName("info")
	val info: Info8? = null
)

data class ResultsItem8(

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

data class Info8(

	@field:SerializedName("next")
	val next: String? = null,

	@field:SerializedName("pages")
	val pages: Int? = null,

	@field:SerializedName("prev")
	val prev: String? = null,

	@field:SerializedName("count")
	val count: Int? = null
)
