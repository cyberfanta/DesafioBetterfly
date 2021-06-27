package com.cyberfanta.desafiobetterfly.models.episode

import com.google.gson.annotations.SerializedName

data class Episode(

	@field:SerializedName("results")
	val results: List<EpisodeDetail?>? = null,

	@field:SerializedName("info")
	val info: Info8? = null
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
