package com.cyberfanta.desafiobetterfly.models.location

import com.google.gson.annotations.SerializedName

data class LocationFilter(

	@field:SerializedName("results")
	val results: List<ResultsItem7?>? = null,

	@field:SerializedName("info")
	val info: Info7? = null
)

data class ResultsItem7(

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("residents")
	val residents: List<String?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("dimension")
	val dimension: String? = null,

	@field:SerializedName("url")
	val url: String? = null
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
