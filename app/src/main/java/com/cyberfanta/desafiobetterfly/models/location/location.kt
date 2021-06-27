package com.cyberfanta.desafiobetterfly.models.location

import com.google.gson.annotations.SerializedName

data class Info5(

	@field:SerializedName("next")
	val next: String? = null,

	@field:SerializedName("pages")
	val pages: Int? = null,

	@field:SerializedName("prev")
	val prev: String? = null,

	@field:SerializedName("count")
	val count: Int? = null
)

data class Location(

	@field:SerializedName("results")
	val results: List<LocationDetail?>? = null,

	@field:SerializedName("info")
	val info: Info5? = null
)
