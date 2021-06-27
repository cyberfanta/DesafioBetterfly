package com.cyberfanta.desafiobetterfly.models.episode

import com.google.gson.annotations.SerializedName

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
