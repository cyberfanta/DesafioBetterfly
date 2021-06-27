package com.cyberfanta.desafiobetterfly.models.location

import com.google.gson.annotations.SerializedName

data class LocationFilter(

	@field:SerializedName("results")
	val results: List<LocationDetail?>? = null,

	@field:SerializedName("info")
	val info: Info7? = null
)

data class Info7(

	@field:SerializedName("next")
	val next: String? = null,

	@field:SerializedName("pages")
	val pages: Int? = null,

	@field:SerializedName("prev")
	val prev: String? = null,

	@field:SerializedName("count")
	val count: Int? = null
)
